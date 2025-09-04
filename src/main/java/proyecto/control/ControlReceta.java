package proyecto.control;

import proyecto.model.*;
import proyecto.persistencia.XmlManager;

import java.io.File;
import java.util.*;

import javax.swing.JOptionPane;

import java.text.SimpleDateFormat;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class ControlReceta {
    private List<Receta> recetas;

    public ControlReceta(List<Receta> recetas) {
        this.recetas = recetas;
    }
    
    private static final SimpleDateFormat SDF_DMY = new SimpleDateFormat("dd/MM/yyyy");
    private Date soloFecha(Date d) throws Exception {
        return SDF_DMY.parse(SDF_DMY.format(d));
    }
    private String generarSiguienteIdReceta() {
        int maxId = 0;
        for (Receta r : recetas) {
            int id = Integer.parseInt(r.getId().substring(1));
            if (id > maxId) maxId = id;
        }
        return "R" + String.format("%03d", maxId + 1);
    }
    private Date parseFechaFlexible(String s) {
        if (s == null) return null;
        String t = s.trim();
        try { return SDF_DMY.parse(t); } catch (Exception ignore) {}
        return null;
    }
    public void guardarReceta(Paciente pacienteSeleccionado, javax.swing.JTable tablaMedicamentos, javax.swing.JTextField txtFechaRetiro) {
        String rutaArchivo = "recetas.xml";
        
        if (pacienteSeleccionado == null) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar un paciente.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (tablaMedicamentos.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Debe agregar al menos un medicamento.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // validar que todos los campos de cada medicamento estén completos
        for (int i = 0; i < tablaMedicamentos.getRowCount(); i++) {
            for (int j = 0; j < tablaMedicamentos.getColumnCount(); j++) {
                Object v = tablaMedicamentos.getValueAt(i, j);
                if (v == null || v.toString().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null,
                            "Debe llenar todos los campos de los medicamentos (fila " + (i + 1) + ").",
                            "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
        }
        // fecha de retiro obligatoria y válida
        Date fechaRetiro = parseFechaFlexible(txtFechaRetiro.getText());
        if (fechaRetiro == null) {
            JOptionPane.showMessageDialog(null, "Formato de fecha inválido (use dd/MM/yyyy).",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Date fechaConfeccion = soloFecha(new Date());
            fechaRetiro = soloFecha(fechaRetiro);

            // la fecha de retiro no puede ser anterior a la fecha de confección
            if (fechaRetiro.before(fechaConfeccion)) {
                JOptionPane.showMessageDialog(null,
                        "La fecha de retiro no puede ser anterior a la fecha de confección (" +
                        SDF_DMY.format(fechaConfeccion) + ").",
                        "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc;
            File archivo = new File(rutaArchivo);
            Element raiz;

            if (archivo.exists()) {
                doc = builder.parse(archivo);
                raiz = doc.getDocumentElement();
            } else {
                doc = builder.newDocument();
                raiz = doc.createElement("recetas");
                doc.appendChild(raiz);
            }

            String nuevoId = generarSiguienteIdReceta();

            Element recetaElem = doc.createElement("receta");
            recetaElem.setAttribute("id", nuevoId);

            // Estado inicial
            Element estadoElem = doc.createElement("estado");
            estadoElem.setTextContent("confeccionada");
            recetaElem.appendChild(estadoElem);

            // Fechas
            Element fechaConfElem = doc.createElement("fechaConfeccion");
            fechaConfElem.setTextContent(SDF_DMY.format(fechaConfeccion));
            recetaElem.appendChild(fechaConfElem);

            Element fechaRetElem = doc.createElement("fechaRetiro");
            fechaRetElem.setTextContent(SDF_DMY.format(fechaRetiro));
            recetaElem.appendChild(fechaRetElem);

            Element fechaProcElem = doc.createElement("fechaProceso");
            fechaProcElem.setTextContent(""); // vacío al inicio
            recetaElem.appendChild(fechaProcElem);

            Element fechaListaElem = doc.createElement("fechaLista");
            fechaListaElem.setTextContent(""); // vacío al inicio
            recetaElem.appendChild(fechaListaElem);

            Element fechaEntElem = doc.createElement("fechaEntregada");
            fechaEntElem.setTextContent(""); // vacío al inicio
            recetaElem.appendChild(fechaEntElem);

            // Paciente
            Element pacienteElem = doc.createElement("paciente");
            pacienteElem.setAttribute("id", pacienteSeleccionado.getId());
            pacienteElem.setAttribute("nombre", pacienteSeleccionado.getNombre());
            pacienteElem.setAttribute("telefono", pacienteSeleccionado.getTelefono());
            recetaElem.appendChild(pacienteElem);

            // Medicamentos
            for (int i = 0; i < tablaMedicamentos.getRowCount(); i++) {
                Element medElem = doc.createElement("medicamento");
                medElem.setAttribute("nombre", tablaMedicamentos.getValueAt(i, 0).toString());
                medElem.setAttribute("presentacion", tablaMedicamentos.getValueAt(i, 1).toString());
                medElem.setAttribute("cantidad", tablaMedicamentos.getValueAt(i, 2).toString());
                medElem.setAttribute("indicaciones", tablaMedicamentos.getValueAt(i, 3).toString());
                medElem.setAttribute("duracion", tablaMedicamentos.getValueAt(i, 4).toString());
                recetaElem.appendChild(medElem);
            }

            raiz.appendChild(recetaElem);

            javax.xml.transform.TransformerFactory tf = javax.xml.transform.TransformerFactory.newInstance();
            javax.xml.transform.Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            javax.xml.transform.dom.DOMSource source = new javax.xml.transform.dom.DOMSource(doc);
            javax.xml.transform.stream.StreamResult result = new javax.xml.transform.stream.StreamResult(archivo);
            transformer.transform(source, result);

            JOptionPane.showMessageDialog(null, "Receta guardada exitosamente. ID: " + nuevoId);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al guardar receta.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void guardarRecetas(String ruta) {
        XmlManager.guardarRecetas(recetas, ruta);
    }

    public void agregarMedicamento(Receta receta, Medicamento med, int cantidad, String indicaciones, int dias) {
        DetalleReceta detalle = new DetalleReceta(med, cantidad, indicaciones, dias);
        receta.agregarDetalle(detalle);
    }

    public List<Receta> buscarPorPaciente(String pacienteId) {
        List<Receta> result = new ArrayList<>();
        for (Receta r : recetas) {
            if (r.getPaciente() != null && r.getPaciente().getId().equals(pacienteId)) {
                result.add(r);
            }
        }
        return result;
    }

    public List<Receta> getRecetas() {
        return recetas;
    }

    public List<Medicamento> getMedicamentos() {
        List<Medicamento> lista = new ArrayList<>();
        try {
            File archivo = new File("medicamentos.xml");
            if (!archivo.exists()) return lista;

            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = dBuilder.parse(archivo);
            NodeList nList = doc.getElementsByTagName("medicamento");

            for (int i = 0; i < nList.getLength(); i++) {
                Element elem = (Element) nList.item(i);
                lista.add(new Medicamento(
                        elem.getAttribute("codigo"),
                        elem.getAttribute("nombre"),
                        elem.getAttribute("presentacion")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return lista;
    }
    
    public List<Paciente> getPacientes() {
        List<Paciente> lista = XmlManager.cargarPacientes("pacientes.xml");
        return lista;
    }

    public void guardarCambios(String ruta) {
        XmlManager.guardarRecetas(recetas, ruta);
    }
    
    public String validarFechaRetiro(Receta receta) {
        Date hoy = new Date();
        Date fechaRetiro = receta.getFechaRetiro();

        if (hoy.before(fechaRetiro)) {
            return "anticipado"; 
        } else if (hoy.after(fechaRetiro)) {
            return "atrasado";   
        } else {
            return "normal";    
        }
    }
    public Receta buscarPorId(String id) {
        for (Receta r : recetas) {
            if (r.getId().equals(id)) {
                return r;
            }
        }
        return null;
    }

}
