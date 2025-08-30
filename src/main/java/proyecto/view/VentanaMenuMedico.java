package proyecto.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import proyecto.model.Medico;
import proyecto.model.Paciente;

public class VentanaMenuMedico extends JFrame {
    private Medico medicoLogueado;

    private JTextField txtFechaRetiro;
    private JButton btnFecha, btnBuscarPaciente, btnAgregarMedicamento;
    private JTable tablaMedicamentos;
    private JButton btnGuardar, btnLimpiar, btnDescartar, btnDetalles;
    private JLabel lblPaciente; // <-- Necesario para mostrar el paciente seleccionado
    private Paciente pacienteSeleccionado; // <-- Guardar el objeto seleccionado

    public VentanaMenuMedico(Medico medicoLogueado) {
        this.medicoLogueado = medicoLogueado;
        init();
    }

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

    private void init() {
        setTitle("Recetas - " + medicoLogueado.getId() + " (" + medicoLogueado.getRol() + ")");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setIconImage(cargarIcono("/imagenes/medico logo.png", 32, 32).getImage());

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel panelPrescribir = crearPanelPrescribir();
        tabbedPane.addTab("Prescribir", cargarIcono("/imagenes/medicamentos logos.png", 20, 20), panelPrescribir);

        JPanel panelDashboard = new JPanel();
        tabbedPane.addTab("Dashboard", cargarIcono("/imagenes/dashbord logo.png", 20, 20), panelDashboard);

        JPanel panelHistorico = new JPanel();
        tabbedPane.addTab("Histórico", cargarIcono("/imagenes/historico logo.png", 20, 20), panelHistorico);

        JPanel panelAcerca = crearPanelAcercaDe();
        tabbedPane.addTab("Acerca de...", cargarIcono("/imagenes/Acerca de logo.png", 20, 20), panelAcerca);

        add(tabbedPane);
    }

    private JPanel crearPanelPrescribir() {
        JPanel panelPrescribir = new JPanel();
        panelPrescribir.setLayout(null);

        JLabel lblControl = new JLabel("Control");
        lblControl.setFont(new Font("Arial", Font.BOLD, 12));
        lblControl.setBounds(10, 5, 100, 20);
        panelPrescribir.add(lblControl);

        btnBuscarPaciente = new JButton("Buscar Paciente");
        btnBuscarPaciente.setBounds(10, 25, 150, 30);
        btnBuscarPaciente.setIcon(cargarIcono("/imagenes/Lupa de buscar logo.png", 20, 20));
        panelPrescribir.add(btnBuscarPaciente);
        btnBuscarPaciente.addActionListener(e -> mostrarVentanaBuscarPaciente());

        btnAgregarMedicamento = new JButton("Agregar Medicamento");
        btnAgregarMedicamento.setBounds(170, 25, 180, 30);
        btnAgregarMedicamento.setIcon(cargarIcono("/imagenes/medicamentos logos.png", 20, 20));
        panelPrescribir.add(btnAgregarMedicamento);

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

        btnFecha = new JButton("...");
        btnFecha.setBounds(290, 95, 40, 25);
        panelPrescribir.add(btnFecha);

        lblPaciente = new JLabel("");
        lblPaciente.setFont(new Font("Arial", Font.BOLD, 12));
        lblPaciente.setForeground(new Color(0, 70, 140));
        lblPaciente.setBounds(20, 130, 300, 25);
        panelPrescribir.add(lblPaciente);

        String[] columnas = {"Medicamento", "Presentación", "Cantidad", "Indicaciones", "Duración"};
        DefaultTableModel modeloTabla = new DefaultTableModel(null, columnas) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tablaMedicamentos = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tablaMedicamentos);
        scroll.setBounds(20, 160, 740, 150);
        panelPrescribir.add(scroll);

        JLabel lblAjustar = new JLabel("Ajustar Prescripción");
        lblAjustar.setFont(new Font("Arial", Font.BOLD, 12));
        lblAjustar.setBounds(10, 320, 150, 20);
        panelPrescribir.add(lblAjustar);

        btnGuardar = new JButton("Guardar");
        btnGuardar.setIcon(cargarIcono("/imagenes/Guardar logo.png", 20, 20));
        btnGuardar.setBounds(20, 345, 120, 30);
        panelPrescribir.add(btnGuardar);

        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setIcon(cargarIcono("/imagenes/Limpiar logo.png", 20, 20));
        btnLimpiar.setBounds(150, 345, 120, 30);
        panelPrescribir.add(btnLimpiar);

        btnDescartar = new JButton("Descartar Medicamento");
        btnDescartar.setIcon(cargarIcono("/imagenes/Borrar logo.png", 20, 20));
        btnDescartar.setBounds(280, 345, 200, 30);
        panelPrescribir.add(btnDescartar);

        btnDetalles = new JButton("Detalles");
        btnDetalles.setIcon(cargarIcono("/imagenes/Check logo.png", 20, 20));
        btnDetalles.setBounds(490, 345, 120, 30);
        panelPrescribir.add(btnDetalles);

        return panelPrescribir;
    }

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

    // ------------------------------ MÉTODO BUSCAR PACIENTE ------------------------------

    private void mostrarVentanaBuscarPaciente() {
        List<Paciente> pacientes = cargarPacientesDesdeXML("pacientes.xml");

        JDialog dialog = new JDialog(this, "Pacientes", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Panel de filtro con combo para elegir campo (nombre o id) y campo texto
        JPanel panelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> comboFiltro = new JComboBox<>(new String[]{"nombre", "id"});
        JTextField txtFiltro = new JTextField(20);
        panelFiltro.add(new JLabel("Filtrar por:"));
        panelFiltro.add(comboFiltro);
        panelFiltro.add(txtFiltro);
        dialog.add(panelFiltro, BorderLayout.NORTH);

        String[] columnas = {"Id", "Nombre", "Telefono", "Fec. Nac."};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tabla = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabla);
        dialog.add(scroll, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        JButton btnOK = new JButton("OK");
        JButton btnCancel = new JButton("Cancel");
        panelBotones.add(btnOK);
        panelBotones.add(btnCancel);
        dialog.add(panelBotones, BorderLayout.SOUTH);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // Método para cargar lista en la tabla
        Runnable cargarTabla = () -> {
            modelo.setRowCount(0);
            String criterio = comboFiltro.getSelectedItem().toString();
            String filtroTexto = txtFiltro.getText().trim().toLowerCase();

            for (Paciente p : pacientes) {
                String valor = criterio.equals("nombre") ? p.getNombre() : p.getId();
                if (valor.toLowerCase().contains(filtroTexto)) {
                    modelo.addRow(new String[]{
                            p.getId(),
                            p.getNombre(),
                            p.getTelefono(),
                            sdf.format(p.getFechaNacimiento())
                    });
                }
            }
        };

        // Carga inicial completa
        cargarTabla.run();

        // Actualiza tabla al escribir en filtro o cambiar criterio
        txtFiltro.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                cargarTabla.run();
            }
        });

        comboFiltro.addActionListener(e -> cargarTabla.run());

        btnOK.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila != -1) {
                String idSeleccionado = (String) modelo.getValueAt(fila, 0);
                pacienteSeleccionado = pacientes.stream()
                        .filter(p -> p.getId().equals(idSeleccionado))
                        .findFirst()
                        .orElse(null);

                if (pacienteSeleccionado != null) {
                    lblPaciente.setText("Paciente: " + pacienteSeleccionado.getNombre());
                }
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Seleccione un paciente.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

private List<Paciente> cargarPacientesDesdeXML(String rutaArchivo) {
    List<Paciente> lista = new ArrayList<>();
    try {
        File archivo = new File(rutaArchivo);
        if (!archivo.exists()) {
            JOptionPane.showMessageDialog(this, "No se encontró el archivo pacientes.xml", "Archivo no encontrado", JOptionPane.ERROR_MESSAGE);
            return lista;
        }

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(archivo);
        doc.getDocumentElement().normalize();

        NodeList nList = doc.getElementsByTagName("paciente");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = 0; i < nList.getLength(); i++) {
            Element elem = (Element) nList.item(i);

            String id = elem.getAttribute("id");
            String nombre = elem.getAttribute("nombre");
            String telefono = elem.getAttribute("telefono");
            String fechaNacStr = elem.getAttribute("fechaNacimiento");

            Date fechaNacimiento = sdf.parse(fechaNacStr);
            lista.add(new Paciente(id, nombre, fechaNacimiento, telefono));
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al cargar pacientes.xml", "Error", JOptionPane.ERROR_MESSAGE);
    }
    return lista;
}

}
