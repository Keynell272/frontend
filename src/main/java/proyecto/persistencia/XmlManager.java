package proyecto.persistencia;

import proyecto.model.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

public class XmlManager {

    // ---------------- UTILIDADES ----------------

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

    public static void guardarUsuario(Usuario usuario) {
        List<Usuario> usuarios = cargarUsuarios("usuarios.xml");
        boolean encontrado = false;
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getId().equals(usuario.getId())) {
                usuarios.set(i, usuario);
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            usuarios.add(usuario);
        }
        guardarUsuarios(usuarios, "usuarios.xml");
    }

    public static Usuario buscarUsuario(String id) {
        List<Usuario> usuarios = cargarUsuarios("usuarios.xml");
        for (Usuario u : usuarios) {
            if (u.getId().equals(id)) {
                return u;
            }
        }
        return null;
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

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // formato legible

            for (Paciente p : pacientes) {
                Element el = doc.createElement("paciente");
                el.setAttribute("id", p.getId());
                el.setAttribute("nombre", p.getNombre());
                el.setAttribute("fechaNac", sdf.format(p.getFechaNacimiento())); // 🔹 fecha legible
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

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            for (int i = 0; i < lista.getLength(); i++) {
                Element p = (Element) lista.item(i);
                String id = p.getAttribute("id");
                String nombre = p.getAttribute("nombre");
                Date fecha = sdf.parse(p.getAttribute("fechaNac")); // 🔹 parseo legible
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

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            for (Receta r : recetas) {
                Element el = doc.createElement("receta");
                el.setAttribute("id", r.getId());
                
                // Estado
                Element estadoEl = doc.createElement("estado");
                estadoEl.setTextContent(r.getEstado());
                el.appendChild(estadoEl);

                // Fechas
                Element fConf = doc.createElement("fechaConfeccion");
                fConf.setTextContent(sdf.format(r.getFechaConfeccion()));
                el.appendChild(fConf);

                Element fRet = doc.createElement("fechaRetiro");
                fRet.setTextContent(sdf.format(r.getFechaRetiro()));
                el.appendChild(fRet);

                if (r.getFechaProceso() != null) {
                    Element fProc = doc.createElement("fechaProceso");
                    fProc.setTextContent(sdf.format(r.getFechaProceso()));
                    el.appendChild(fProc);
                }

                if (r.getFechaLista() != null) {
                    Element fLis = doc.createElement("fechaLista");
                    fLis.setTextContent(sdf.format(r.getFechaLista()));
                    el.appendChild(fLis);
                }

                if (r.getFechaEntrega() != null) {
                    Element fEnt = doc.createElement("fechaEntregada");
                    fEnt.setTextContent(sdf.format(r.getFechaEntrega()));
                    el.appendChild(fEnt);
                }

                // Paciente
                if (r.getPaciente() != null) {
                    Element pacienteEl = doc.createElement("paciente");
                    pacienteEl.setAttribute("id", r.getPaciente().getId());
                    pacienteEl.setAttribute("nombre", r.getPaciente().getNombre());
                    pacienteEl.setAttribute("telefono", r.getPaciente().getTelefono());
                    el.appendChild(pacienteEl);
                }

                // Medicamentos
                for (DetalleReceta d : r.getDetalles()) {
                    Element medEl = doc.createElement("medicamento");
                    medEl.setAttribute("nombre", d.getMedicamento().getNombre());
                    medEl.setAttribute("presentacion", d.getMedicamento().getPresentacion());
                    medEl.setAttribute("cantidad", String.valueOf(d.getCantidad()));
                    medEl.setAttribute("indicaciones", d.getIndicaciones());
                    medEl.setAttribute("duracion", String.valueOf(d.getDuracionDias()));
                    el.appendChild(medEl);
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

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        try {
            File file = new File(ruta);
            if (!file.exists()) return recetas;

            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            NodeList lista = doc.getElementsByTagName("receta");

            for (int i = 0; i < lista.getLength(); i++) {
                Element r = (Element) lista.item(i);

                String id = r.getAttribute("id");
                String estado = r.getElementsByTagName("estado").item(0).getTextContent();

                Date fechaConf = sdf.parse(r.getElementsByTagName("fechaConfeccion").item(0).getTextContent());
                Date fechaRet = sdf.parse(r.getElementsByTagName("fechaRetiro").item(0).getTextContent());

                Receta receta = new Receta(id, fechaConf, fechaRet, null); // paciente lo cargo abajo
                receta.setEstado(estado);

                if (r.getElementsByTagName("fechaProceso").getLength() > 0)
                    if (r.getElementsByTagName("fechaProceso").item(0).getTextContent() != null && !r.getElementsByTagName("fechaProceso").item(0).getTextContent().isEmpty()){
                        receta.setFechaProceso(sdf.parse(r.getElementsByTagName("fechaProceso").item(0).getTextContent()));
                    }else 
                        receta.setFechaProceso(null);

                if (r.getElementsByTagName("fechaLista").getLength() > 0)
                    if (r.getElementsByTagName("fechaLista").item(0).getTextContent() != null && !r.getElementsByTagName("fechaLista").item(0).getTextContent().isEmpty())
                        receta.setFechaLista(sdf.parse(r.getElementsByTagName("fechaLista").item(0).getTextContent()));
                    else
                        receta.setFechaLista(null);

                if (r.getElementsByTagName("fechaEntregada").getLength() > 0)
                    if (r.getElementsByTagName("fechaEntregada").item(0).getTextContent() != null && !r.getElementsByTagName("fechaEntregada").item(0).getTextContent().isEmpty())
                        receta.setFechaEntrega(sdf.parse(r.getElementsByTagName("fechaEntregada").item(0).getTextContent()));
                    else
                        receta.setFechaEntrega(null);

                // Paciente
                NodeList pacNodes = r.getElementsByTagName("paciente");
                if (pacNodes.getLength() > 0) {
                    Element p = (Element) pacNodes.item(0);
                    Paciente paciente = new Paciente(
                            p.getAttribute("id"),
                            p.getAttribute("nombre"),
                            null, // fechaNac no la tienes en receta
                            p.getAttribute("telefono")
                    );
                    receta.setPaciente(paciente);
                }

                // Medicamentos
                NodeList meds = r.getElementsByTagName("medicamento");
                for (int j = 0; j < meds.getLength(); j++) {
                    Element d = (Element) meds.item(j);

                    // Buscar medicamento por nombre o código
                    Medicamento med = medicamentos.stream()
                            .filter(m -> m.getNombre().equals(d.getAttribute("nombre")))
                            .findFirst().orElse(null);

                    if (med == null) {
                        med = new Medicamento("", d.getAttribute("nombre"), d.getAttribute("presentacion"));
                    }

                    DetalleReceta detalle = new DetalleReceta(
                            med,
                            Integer.parseInt(d.getAttribute("cantidad")),
                            d.getAttribute("indicaciones"),
                            Integer.parseInt(d.getAttribute("duracion"))
                    );
                    receta.agregarDetalle(detalle);
                }

                recetas.add(receta);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return recetas;
    }

}
