package proyecto.view;

import java.awt.FlowLayout;
import java.awt.Image;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import proyecto.model.Medico;

public class VentanaMenuMedico extends JFrame {
    private Medico medicoLogueado;

    // Componentes para la pestaña "Prescribir"
    private JTextField txtFechaRetiro;
    private JButton btnFecha, btnBuscarPaciente, btnAgregarMedicamento;
    private JTable tablaMedicamentos;
    private JButton btnGuardar, btnLimpiar, btnDescartar, btnDetalles;

    public VentanaMenuMedico(Medico medicoLogueado) {
        this.medicoLogueado = medicoLogueado;
        init();
    }

    // Método para cargar imágenes escaladas
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
        setTitle("Recetas - " + medicoLogueado.getId() + " (" + medicoLogueado.getNombre() + ")");
        setSize(700, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Icono de la ventana
        setIconImage(cargarIcono("/imagenes/medico logo.png", 32, 32).getImage());

        // Panel principal con pestañas
        JTabbedPane tabbedPane = new JTabbedPane();

        // ============================
        // Panel "Prescribir"
        // ============================
        JPanel panelPrescribir = new JPanel();
        panelPrescribir.setLayout(null);

        // Botón Buscar Paciente
        btnBuscarPaciente = new JButton("Buscar Paciente");
        btnBuscarPaciente.setBounds(10, 10, 150, 30);
        btnBuscarPaciente.setIcon(cargarIcono("/imagenes/Lupa de buscar logo.png", 20, 20));
        panelPrescribir.add(btnBuscarPaciente);

        // Botón Agregar Medicamento
        btnAgregarMedicamento = new JButton("Agregar Medicamento");
        btnAgregarMedicamento.setBounds(170, 10, 170, 30);
        btnAgregarMedicamento.setIcon(cargarIcono("/imagenes/medicamentos logos.png", 20, 20));
        panelPrescribir.add(btnAgregarMedicamento);

        // Panel Receta Médica
        JPanel panelReceta = new JPanel();
        panelReceta.setBorder(BorderFactory.createTitledBorder("Receta Médica"));
        panelReceta.setLayout(null);
        panelReceta.setBounds(10, 50, 660, 300);
        panelPrescribir.add(panelReceta);

        // Campo Fecha de Retiro
        JLabel lblFechaRetiro = new JLabel("Fecha de Retiro");
        lblFechaRetiro.setBounds(10, 25, 100, 25);
        panelReceta.add(lblFechaRetiro);

        txtFechaRetiro = new JTextField("18 de agosto de 2025");
        txtFechaRetiro.setBounds(120, 25, 150, 25);
        panelReceta.add(txtFechaRetiro);

        btnFecha = new JButton("...");
        btnFecha.setBounds(280, 25, 40, 25);
        panelReceta.add(btnFecha);

        // Tabla Medicamentos (vacía)
        String[] columnas = {"Medicamento", "Presentación", "Cantidad", "Indicaciones", "Duración"};
        DefaultTableModel modeloTabla = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaMedicamentos = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tablaMedicamentos);
        scrollTabla.setBounds(10, 60, 640, 180);
        panelReceta.add(scrollTabla);

        // Panel de botones Ajustar Prescripción
        JPanel panelAjustar = new JPanel();
        panelAjustar.setBorder(BorderFactory.createTitledBorder("Ajustar Prescripción"));
        panelAjustar.setBounds(10, 250, 660, 80);
        panelAjustar.setLayout(new FlowLayout(FlowLayout.LEFT));

        btnGuardar = new JButton("Guardar");
        btnGuardar.setIcon(cargarIcono("/imagenes/Guardar logo.png", 20, 20));
        panelAjustar.add(btnGuardar);

        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setIcon(cargarIcono("/imagenes/Limpiar logo.png", 20, 20));
        panelAjustar.add(btnLimpiar);

        btnDescartar = new JButton("Descartar Medicamento");
        btnDescartar.setIcon(cargarIcono("/imagenes/Borrar logo.png", 20, 20));
        btnDescartar.setEnabled(false);
        panelAjustar.add(btnDescartar);

        btnDetalles = new JButton("Detalles");
        btnDetalles.setIcon(cargarIcono("/imagenes/Check logo.png", 20, 20));
        btnDetalles.setEnabled(false);
        panelAjustar.add(btnDetalles);

        panelReceta.add(panelAjustar);

        // Agregar pestaña con ícono
        tabbedPane.addTab("Prescribir", cargarIcono("/imagenes/medicamentos logos.png", 20, 20), panelPrescribir);

        // ============================
        // Panel "Dashboard"
        // ============================
        JPanel panelDashboard = new JPanel();
        tabbedPane.addTab("Dashboard", cargarIcono("/imagenes/dashbord logo.png", 20, 20), panelDashboard);

        // ============================
        // Panel "Histórico"
        // ============================
        JPanel panelHistorico = new JPanel();
        tabbedPane.addTab("Histórico", cargarIcono("/imagenes/historico logo.png", 20, 20), panelHistorico);

        // ============================
        // Panel "Acerca de..."
        // ============================
        JPanel panelAcercaDe = new JPanel();
        tabbedPane.addTab("Acerca de...", cargarIcono("/imagenes/Acerca de logo.png", 20, 20), panelAcercaDe);

        // Agregar todo al JFrame
        add(tabbedPane);
    }
}

