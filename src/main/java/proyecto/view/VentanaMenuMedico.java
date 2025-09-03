package proyecto.view;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
import java.util.Date;
import proyecto.model.*;

public class VentanaMenuMedico extends JFrame {
    private Medico medicoLogueado;

    private JTextField txtFechaRetiro;
    private JButton btnBuscarPaciente, btnAgregarMedicamento;
    private JTable tablaMedicamentos;
    private JButton btnGuardar, btnLimpiar, btnDescartar, btnDetalles;
    private JLabel lblPaciente;
    private Paciente pacienteSeleccionado;

    public VentanaMenuMedico(Medico medicoLogueado) {
        this.medicoLogueado = medicoLogueado;
        SDF_DMY.setLenient(false);
        init();
    }

    // ------------------ INIT ------------------
    private void init() {
        setTitle("Recetas - " + medicoLogueado.getId() + " (" + medicoLogueado.getRol() + ")");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setIconImage(cargarIcono("/imagenes/medico logo.png", 32, 32).getImage());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Prescribir", cargarIcono("/imagenes/medicamentos logos.png", 20, 20), crearPanelPrescribir());
        tabbedPane.addTab("Dashboard", cargarIcono("/imagenes/dashbord logo.png", 20, 20), new JPanel());
        tabbedPane.addTab("Histórico", cargarIcono("/imagenes/historico logo.png", 20, 20), new JPanel());
        tabbedPane.addTab("Acerca de...", cargarIcono("/imagenes/Acerca de logo.png", 20, 20), crearPanelAcercaDe());
        add(tabbedPane);
    }

    private final SimpleDateFormat SDF_DMY = new SimpleDateFormat("dd/MM/yyyy");

    private Date parseFechaFlexible(String s) {
        if (s == null) return null;
        String t = s.trim();
        try { return SDF_DMY.parse(t); } catch (Exception ignore) {}
        return null;
    }
    private Date soloFecha(Date d) throws Exception {
        return SDF_DMY.parse(SDF_DMY.format(d));
    }
    private String generarSiguienteIdReceta(Document doc) {
        NodeList nodes = doc.getElementsByTagName("receta");
        int max = 0;
        for (int i = 0; i < nodes.getLength(); i++) {
            Element r = (Element) nodes.item(i);
            String id = r.getAttribute("id");
            if (id != null && id.startsWith("R")) {
                try {
                    int n = Integer.parseInt(id.substring(1));
                    if (n > max) max = n;
                } catch (Exception ignored) {}
            }
        }
        return String.format("R%03d", max + 1);
    }

    // ------------------ UTILS ------------------
    private ImageIcon cargarIcono(String ruta, int ancho, int alto) {
        java.net.URL location = getClass().getResource(ruta);
        if (location == null) {
            System.err.println("No se encontró la imagen: " + ruta);
            return null;
        }
        ImageIcon icono = new ImageIcon(location);
        Image imagen = icono.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        return new ImageIcon(imagen);
    }

    // ------------------ PANEL PRESCRIBIR ------------------
    private JPanel crearPanelPrescribir() {
        JPanel panelPrescribir = new JPanel(null);

        // Control
        JLabel lblControl = new JLabel("Control");
        lblControl.setFont(new Font("Arial", Font.BOLD, 12));
        lblControl.setBounds(10, 5, 100, 20);
        panelPrescribir.add(lblControl);

        btnBuscarPaciente = new JButton("Buscar Paciente", cargarIcono("/imagenes/Lupa de buscar logo.png", 20, 20));
        btnBuscarPaciente.setBounds(10, 25, 150, 30);
        btnBuscarPaciente.addActionListener(e -> mostrarVentanaBuscarPaciente());
        panelPrescribir.add(btnBuscarPaciente);

        btnAgregarMedicamento = new JButton("Agregar Medicamento", cargarIcono("/imagenes/medicamentos logos.png", 20, 20));
        btnAgregarMedicamento.setBounds(170, 25, 180, 30);
        btnAgregarMedicamento.addActionListener(e -> mostrarVentanaAgregarMedicamento());
        panelPrescribir.add(btnAgregarMedicamento);

        // Receta
        JLabel lblReceta = new JLabel("Receta Médica");
        lblReceta.setFont(new Font("Arial", Font.BOLD, 12));
        lblReceta.setBounds(10, 70, 120, 20);
        panelPrescribir.add(lblReceta);

        JLabel lblFechaRetiro = new JLabel("Fecha de Retiro");
        lblFechaRetiro.setForeground(Color.RED);
        lblFechaRetiro.setBounds(20, 95, 100, 25);
        panelPrescribir.add(lblFechaRetiro);

        txtFechaRetiro = new JTextField();
        txtFechaRetiro.setBounds(130, 95, 150, 25);
        panelPrescribir.add(txtFechaRetiro);

        JButton btnCalendario = new JButton("📅");
        btnCalendario.setBounds(300, 95, 50, 25);
        panelPrescribir.add(btnCalendario);
        new DatePickerConNavegacion(txtFechaRetiro, btnCalendario);

        lblPaciente = new JLabel("");
        lblPaciente.setFont(new Font("Arial", Font.BOLD, 12));
        lblPaciente.setForeground(new Color(0, 70, 140));
        lblPaciente.setBounds(20, 130, 300, 25);
        panelPrescribir.add(lblPaciente);

        // Tabla
        String[] columnas = {"Medicamento", "Presentación", "Cantidad", "Indicaciones", "Duración"};
        DefaultTableModel modeloTabla = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 2 || col == 3 || col == 4;
            }
        };
        tablaMedicamentos = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tablaMedicamentos);
        scroll.setBounds(20, 160, 740, 150);
        panelPrescribir.add(scroll);

        // Ajustar Prescripción
        JLabel lblAjustar = new JLabel("Ajustar Prescripción");
        lblAjustar.setFont(new Font("Arial", Font.BOLD, 12));
        lblAjustar.setBounds(10, 320, 150, 20);
        panelPrescribir.add(lblAjustar);

        btnGuardar = new JButton("Guardar", cargarIcono("/imagenes/Guardar logo.png", 20, 20));
        btnGuardar.setBounds(20, 345, 120, 30);
        btnGuardar.addActionListener(e -> guardarRecetaEnXML("recetas.xml"));
        panelPrescribir.add(btnGuardar);

        btnLimpiar = new JButton("Limpiar", cargarIcono("/imagenes/Limpiar logo.png", 20, 20));
        btnLimpiar.setBounds(150, 345, 120, 30);
        btnLimpiar.addActionListener(e -> {
            tablaMedicamentos.clearSelection();
            txtFechaRetiro.setText("");
            lblPaciente.setText("");
        });
        panelPrescribir.add(btnLimpiar);

        btnDescartar = new JButton("Descartar Medicamento", cargarIcono("/imagenes/Borrar logo.png", 20, 20));
        btnDescartar.setBounds(280, 345, 200, 30);
        btnDescartar.addActionListener(e -> {
            int[] filas = tablaMedicamentos.getSelectedRows();
            if (filas.length == 0) {
                JOptionPane.showMessageDialog(this, "No hay medicamentos seleccionados para descartar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            DefaultTableModel modelo = (DefaultTableModel) tablaMedicamentos.getModel();
            for (int i = filas.length - 1; i >= 0; i--) modelo.removeRow(filas[i]);
        });
        panelPrescribir.add(btnDescartar);

        btnDetalles = new JButton("Detalles", cargarIcono("/imagenes/Check logo.png", 20, 20));
        btnDetalles.setBounds(490, 345, 120, 30);
        btnDetalles.addActionListener(e -> mostrarVentanaDetalles());
        panelPrescribir.add(btnDetalles);

        return panelPrescribir;
    }

    // ------------------ PANEL ACERCA DE ------------------
    private JPanel crearPanelAcercaDe() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(230, 233, 237));

        JLabel lblTitulo = new JLabel("Prescripcion y Despacho de Recetas", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(Color.BLUE);
        panel.add(lblTitulo, BorderLayout.NORTH);

        JLabel lblImagen = new JLabel();
        lblImagen.setHorizontalAlignment(SwingConstants.CENTER);
        lblImagen.setIcon(cargarIcono("/imagenes/hospital imagen principal.jpg", 700, 300));
        panel.add(lblImagen, BorderLayout.CENTER);

        JPanel subPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        subPanel.setBackground(new Color(230, 233, 237));
        JLabel lblInfo = new JLabel("Total Soft Inc.   @totalsoft   Tel. 67197691");
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 14));
        subPanel.add(lblInfo);
        panel.add(subPanel, BorderLayout.SOUTH);

        return panel;
    }

    // ------------------ PACIENTES ------------------
    private void mostrarVentanaBuscarPaciente() {
        List<Paciente> pacientes = cargarPacientesDesdeXML("pacientes.xml");

        JDialog dialog = new JDialog(this, "Pacientes", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel panelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> comboFiltro = new JComboBox<>(new String[]{"nombre", "id"});
        JTextField txtFiltro = new JTextField(20);
        panelFiltro.add(new JLabel("Filtrar por:"));
        panelFiltro.add(comboFiltro);
        panelFiltro.add(txtFiltro);
        dialog.add(panelFiltro, BorderLayout.NORTH);

        String[] columnas = {"Id", "Nombre", "Telefono", "Fec. Nac."};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(modelo);
        dialog.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        JButton btnOK = new JButton("OK");
        JButton btnCancel = new JButton("Cancel");
        panelBotones.add(btnOK);
        panelBotones.add(btnCancel);
        dialog.add(panelBotones, BorderLayout.SOUTH);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        Runnable cargarTabla = () -> {
            modelo.setRowCount(0);
            String criterio = comboFiltro.getSelectedItem().toString();
            String filtroTexto = txtFiltro.getText().trim().toLowerCase();
            for (Paciente p : pacientes) {
                String valor = criterio.equals("nombre") ? p.getNombre() : p.getId();
                if (valor.toLowerCase().contains(filtroTexto)) {
                    modelo.addRow(new String[]{p.getId(), p.getNombre(), p.getTelefono(), sdf.format(p.getFechaNacimiento())});
                }
            }
        };
        cargarTabla.run();

        txtFiltro.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { cargarTabla.run(); }
        });
        comboFiltro.addActionListener(e -> cargarTabla.run());

        btnOK.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila != -1) {
                String idSel = (String) modelo.getValueAt(fila, 0);
                pacienteSeleccionado = pacientes.stream().filter(p -> p.getId().equals(idSel)).findFirst().orElse(null);
                if (pacienteSeleccionado != null) lblPaciente.setText("Paciente: " + pacienteSeleccionado.getNombre());
                dialog.dispose();
            } else JOptionPane.showMessageDialog(dialog, "Seleccione un paciente.", "Aviso", JOptionPane.WARNING_MESSAGE);
        });

        btnCancel.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    private List<Paciente> cargarPacientesDesdeXML(String rutaArchivo) {
        List<Paciente> lista = new ArrayList<>();
        try {
            File archivo = new File(rutaArchivo);
            if (!archivo.exists()) return lista;

            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = dBuilder.parse(archivo);
            NodeList nList = doc.getElementsByTagName("paciente");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            for (int i = 0; i < nList.getLength(); i++) {
                Element elem = (Element) nList.item(i);
                lista.add(new Paciente(
                        elem.getAttribute("id"),
                        elem.getAttribute("nombre"),
                        sdf.parse(elem.getAttribute("fechaNac")),
                        elem.getAttribute("telefono")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return lista;
    }

    // ------------------ MEDICAMENTOS ------------------
    private void mostrarVentanaAgregarMedicamento() {
        List<Medicamento> medicamentos = cargarMedicamentosDesdeXML("medicamentos.xml");

        JDialog dialog = new JDialog(this, "Agregar Medicamento", true);
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel panelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> comboFiltro = new JComboBox<>(new String[]{"nombre", "codigo"});
        JTextField txtFiltro = new JTextField(20);
        panelFiltro.add(new JLabel("Filtrar por:"));
        panelFiltro.add(comboFiltro);
        panelFiltro.add(txtFiltro);
        dialog.add(panelFiltro, BorderLayout.NORTH);

        String[] columnas = {"Código", "Nombre", "Presentación"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(modelo);
        dialog.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        JButton btnOK = new JButton("Agregar");
        JButton btnCancel = new JButton("Cancelar");
        panelBotones.add(btnOK); panelBotones.add(btnCancel);
        dialog.add(panelBotones, BorderLayout.SOUTH);

        Runnable cargarTabla = () -> {
            modelo.setRowCount(0);
            String criterio = comboFiltro.getSelectedItem().toString();
            String filtroTexto = txtFiltro.getText().trim().toLowerCase();
            for (Medicamento m : medicamentos) {
                String valor = criterio.equals("nombre") ? m.getNombre() : m.getCodigo();
                if (valor.toLowerCase().contains(filtroTexto)) {
                    modelo.addRow(new Object[]{m.getCodigo(), m.getNombre(), m.getPresentacion()});
                }
            }
        };
        cargarTabla.run();

        txtFiltro.addKeyListener(new KeyAdapter() { @Override public void keyReleased(KeyEvent e) { cargarTabla.run(); }});
        comboFiltro.addActionListener(e -> cargarTabla.run());

        btnOK.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila != -1) {
                DefaultTableModel modeloTabla = (DefaultTableModel) tablaMedicamentos.getModel();
                modeloTabla.addRow(new Object[]{
                        modelo.getValueAt(fila, 1), modelo.getValueAt(fila, 2), "", "", ""
                });
                dialog.dispose();
            } else JOptionPane.showMessageDialog(dialog, "Seleccione un medicamento.", "Aviso", JOptionPane.WARNING_MESSAGE);
        });

        btnCancel.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    private List<Medicamento> cargarMedicamentosDesdeXML(String rutaArchivo) {
        List<Medicamento> lista = new ArrayList<>();
        try {
            File archivo = new File(rutaArchivo);
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

    private void guardarRecetaEnXML(String rutaArchivo) {
        if (pacienteSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un paciente.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (tablaMedicamentos.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Debe agregar al menos un medicamento.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // validar que todos los campos de cada medicamento estén completos
        for (int i = 0; i < tablaMedicamentos.getRowCount(); i++) {
            for (int j = 0; j < tablaMedicamentos.getColumnCount(); j++) {
                Object v = tablaMedicamentos.getValueAt(i, j);
                if (v == null || v.toString().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Debe llenar todos los campos de los medicamentos (fila " + (i + 1) + ").",
                            "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
        }
        // fecha de retiro obligatoria y válida
        Date fechaRetiro = parseFechaFlexible(txtFechaRetiro.getText());
        if (fechaRetiro == null) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido (use dd/MM/yyyy).",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Date fechaConfeccion = soloFecha(new Date());
            fechaRetiro = soloFecha(fechaRetiro);

            // la fecha de retiro no puede ser anterior a la fecha de confección
            if (fechaRetiro.before(fechaConfeccion)) {
                JOptionPane.showMessageDialog(this,
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

            String nuevoId = generarSiguienteIdReceta(doc);

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

            JOptionPane.showMessageDialog(this, "Receta guardada exitosamente. ID: " + nuevoId);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al guardar receta.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ------------------ DETALLES ------------------
    private void mostrarVentanaDetalles() {
        JDialog dialog = new JDialog(this, "Detalles de la Prescripción", true);
        dialog.setSize(600, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel panelInfo = new JPanel();
        panelInfo.setBorder(BorderFactory.createTitledBorder("Información"));
        panelInfo.add(new JLabel("Paciente: " + lblPaciente.getText()));
        panelInfo.add(new JLabel("Fecha de Retiro: " + txtFechaRetiro.getText()));
        dialog.add(panelInfo, BorderLayout.NORTH);

        String[] columnas = {"Medicamento", "Presentación", "Cantidad", "Indicaciones", "Duración"};
        DefaultTableModel modeloDetalles = new DefaultTableModel(columnas, 0);
        DefaultTableModel modeloOriginal = (DefaultTableModel) tablaMedicamentos.getModel();
        for (int i = 0; i < modeloOriginal.getRowCount(); i++) {
            Object[] fila = new Object[modeloOriginal.getColumnCount()];
            for (int j = 0; j < modeloOriginal.getColumnCount(); j++) fila[j] = modeloOriginal.getValueAt(i, j);
            modeloDetalles.addRow(fila);
        }
        dialog.add(new JScrollPane(new JTable(modeloDetalles)), BorderLayout.CENTER);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dialog.dispose());
        JPanel panelBoton = new JPanel();
        panelBoton.add(btnCerrar);
        dialog.add(panelBoton, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}
