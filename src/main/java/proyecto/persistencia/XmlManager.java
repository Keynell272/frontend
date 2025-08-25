package proyecto.persistencia;

import proyecto.model.*;
import java.io.File;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

public class XmlManager {

    private static Document createDocument() throws ParserConfigurationException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        return dBuilder.newDocument();
    }
    private static Document loadDocument(String ruta) throws Exception {
        File archivo = new File(ruta);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(archivo);
    }

    private static void saveDocument(Document doc, String ruta) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(ruta));
            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------- USUARIOS ----------------
    public static void guardarUsuarios(List<Usuario> usuarios, String ruta) {
        try {
            Document doc = createDocument();
            Element root = doc.createElement("usuarios");
            doc.appendChild(root);

            for (Usuario u : usuarios) {
                Element user = doc.createElement("usuario");
                user.setAttribute("tipo", u.getClass().getSimpleName());
                user.setAttribute("id", u.getId());
                user.setAttribute("clave", u.getClave());
                user.setAttribute("nombre", u.getNombre());

                if (u instanceof Medico) {
                    user.setAttribute("especialidad", ((Medico) u).getEspecialidad());
                }
                root.appendChild(user);
            }

            saveDocument(doc, ruta);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Usuario> cargarUsuarios(String ruta) {
        List<Usuario> usuarios = new ArrayList<>();
        try {
            File file = new File(ruta);
            if (!file.exists()) return usuarios;

            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            NodeList lista = doc.getElementsByTagName("usuario");

            for (int i = 0; i < lista.getLength(); i++) {
                Element u = (Element) lista.item(i);
                String tipo = u.getAttribute("tipo");
                String id = u.getAttribute("id");
                String clave = u.getAttribute("clave");
                String nombre = u.getAttribute("nombre");

                switch (tipo) {
                    case "Medico":
                        usuarios.add(new Medico(id, clave, nombre, u.getAttribute("especialidad")));
                        break;
                    case "Farmaceuta":
                        usuarios.add(new Farmaceuta(id, clave, nombre));
                        break;
                    case "Administrador":
                        usuarios.add(new Administrador(id, clave, nombre));
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    // ---------------- MEDICOS -----------------
    public static void guardarMedicos(List<Medico> medicos, String ruta) {
        try {
            Document doc = createDocument();
            Element root = doc.createElement("medicos");
            doc.appendChild(root);

            for (Medico m : medicos) {
                Element medico = doc.createElement("medico");
                medico.setAttribute("id", m.getId());
                medico.setAttribute("clave", m.getClave());
                medico.setAttribute("nombre", m.getNombre());
                medico.setAttribute("especialidad", m.getEspecialidad());
                root.appendChild(medico);
            }

            saveDocument(doc, ruta);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Medico> cargarMedicos(String ruta) {
        List<Medico> medicos = new ArrayList<>();
        try {
            Document doc = loadDocument(ruta);
            NodeList lista = doc.getElementsByTagName("medico");

            for (int i = 0; i < lista.getLength(); i++) {
                Element el = (Element) lista.item(i);
                String id = el.getAttribute("id");
                String clave = el.getAttribute("clave");
                String nombre = el.getAttribute("nombre");
                String especialidad = el.getAttribute("especialidad");
                medicos.add(new Medico(id, clave, nombre, especialidad));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return medicos;
    }

    // ---------------- FARMACEUTAS -----------------

    public static void guardarFarmaceutas(List<Farmaceuta> farmaceutas, String ruta) {
        try {
            Document doc = createDocument();
            Element root = doc.createElement("farmaceutas");
            doc.appendChild(root);
            for (Farmaceuta f : farmaceutas) {
                Element farmaceuta = doc.createElement("farmaceuta");
                farmaceuta.setAttribute("id", f.getId());
                farmaceuta.setAttribute("clave", f.getClave());
                farmaceuta.setAttribute("nombre", f.getNombre());
                root.appendChild(farmaceuta);
            }
            saveDocument(doc, ruta);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static List<Farmaceuta> cargarFarmaceutas(String ruta) {
        List<Farmaceuta> farmaceutas = new ArrayList<>();
        try {
            Document doc = loadDocument(ruta);
            NodeList lista = doc.getElementsByTagName("farmaceuta");
            for (int i = 0; i < lista.getLength(); i++) {
                Element el = (Element) lista.item(i);
                String id = el.getAttribute("id");
                String clave = el.getAttribute("clave");
                String nombre = el.getAttribute("nombre");
                farmaceutas.add(new Farmaceuta(id, clave, nombre));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return farmaceutas;
    }

    // ---------------- PACIENTES ----------------
    public static void guardarPacientes(List<Paciente> pacientes, String ruta) {
        try {
            Document doc = createDocument();
            Element root = doc.createElement("pacientes");
            doc.appendChild(root);

            for (Paciente p : pacientes) {
                Element el = doc.createElement("paciente");
                el.setAttribute("id", p.getId());
                el.setAttribute("nombre", p.getNombre());
                el.setAttribute("fechaNac", String.valueOf(p.getFechaNacimiento().getTime()));
                el.setAttribute("telefono", p.getTelefono());
                root.appendChild(el);
            }

            saveDocument(doc, ruta);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Paciente> cargarPacientes(String ruta) {
        List<Paciente> pacientes = new ArrayList<>();
        try {
            File file = new File(ruta);
            if (!file.exists()) return pacientes;

            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            NodeList lista = doc.getElementsByTagName("paciente");

            for (int i = 0; i < lista.getLength(); i++) {
                Element p = (Element) lista.item(i);
                String id = p.getAttribute("id");
                String nombre = p.getAttribute("nombre");
                Date fecha = new Date(Long.parseLong(p.getAttribute("fechaNac")));
                String tel = p.getAttribute("telefono");
                pacientes.add(new Paciente(id, nombre, fecha, tel));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pacientes;
    }

    // ---------------- MEDICAMENTOS ----------------
    public static void guardarMedicamentos(List<Medicamento> medicamentos, String ruta) {
        try {
            Document doc = createDocument();
            Element root = doc.createElement("medicamentos");
            doc.appendChild(root);

            for (Medicamento m : medicamentos) {
                Element el = doc.createElement("medicamento");
                el.setAttribute("codigo", m.getCodigo());
                el.setAttribute("nombre", m.getNombre());
                el.setAttribute("presentacion", m.getPresentacion());
                root.appendChild(el);
            }

            saveDocument(doc, ruta);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Medicamento> cargarMedicamentos(String ruta) {
        List<Medicamento> medicamentos = new ArrayList<>();
        try {
            File file = new File(ruta);
            if (!file.exists()) return medicamentos;

            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            NodeList lista = doc.getElementsByTagName("medicamento");

            for (int i = 0; i < lista.getLength(); i++) {
                Element m = (Element) lista.item(i);
                medicamentos.add(new Medicamento(
                        m.getAttribute("codigo"),
                        m.getAttribute("nombre"),
                        m.getAttribute("presentacion")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return medicamentos;
    }

    // ---------------- RECETAS ----------------
    public static void guardarRecetas(List<Receta> recetas, String ruta) {
        try {
            Document doc = createDocument();
            Element root = doc.createElement("recetas");
            doc.appendChild(root);

            for (Receta r : recetas) {
                Element el = doc.createElement("receta");
                el.setAttribute("id", r.getId());
                el.setAttribute("fecha", String.valueOf(r.getFecha().getTime()));
                el.setAttribute("fechaRetiro", String.valueOf(r.getFechaRetiro().getTime()));
                el.setAttribute("estado", r.getEstado());

                for (DetalleReceta d : r.getDetalles()) {
                    Element det = doc.createElement("detalle");
                    det.setAttribute("codigoMed", d.getMedicamento().getCodigo());
                    det.setAttribute("cantidad", String.valueOf(d.getCantidad()));
                    det.setAttribute("indicaciones", d.getIndicaciones());
                    det.setAttribute("duracion", String.valueOf(d.getDuracionDias()));
                    el.appendChild(det);
                }
                root.appendChild(el);
            }
            saveDocument(doc, ruta);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Receta> cargarRecetas(String ruta, List<Medicamento> medicamentos) {
        List<Receta> recetas = new ArrayList<>();
        try {
            File file = new File(ruta);
            if (!file.exists()) return recetas;

            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            NodeList lista = doc.getElementsByTagName("receta");

            for (int i = 0; i < lista.getLength(); i++) {
                Element r = (Element) lista.item(i);
                Receta receta = new Receta(
                        r.getAttribute("id"),
                        new Date(Long.parseLong(r.getAttribute("fecha"))),
                        new Date(Long.parseLong(r.getAttribute("fechaRetiro"))),
                        r.getAttribute("estado")
                );

                NodeList dets = r.getElementsByTagName("detalle");
                for (int j = 0; j < dets.getLength(); j++) {
                    Element d = (Element) dets.item(j);
                    String codMed = d.getAttribute("codigoMed");
                    Medicamento med = medicamentos.stream()
                            .filter(m -> m.getCodigo().equals(codMed))
                            .findFirst().orElse(null);

                    if (med != null) {
                        DetalleReceta detalle = new DetalleReceta(
                                med,
                                Integer.parseInt(d.getAttribute("cantidad")),
                                d.getAttribute("indicaciones"),
                                Integer.parseInt(d.getAttribute("duracion"))
                        );
                        receta.agregarDetalle(detalle);
                    }
                }
                recetas.add(receta);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return recetas;
    }
}
