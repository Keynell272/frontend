package proyecto.view;

import proyecto.model.Farmaceuta;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VentanaMenuFarmaceuta extends JFrame {
    private Farmaceuta farmaceutaLogueado;

    private JTextField txtFechaRetiro;
    private JButton btnFecha, btnBuscarPaciente, btnAgregarMedicamento;
    private JTable tablaMedicamentos;
    private JButton btnGuardar, btnLimpiar, btnDescartar, btnDetalles;

    public VentanaMenuFarmaceuta(Farmaceuta farmaceutaLogueado) {
        this.farmaceutaLogueado = farmaceutaLogueado;
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
        setTitle("Recetas - " + farmaceutaLogueado.getId() + " (" + farmaceutaLogueado.getRol() + ")");
        setSize(700, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setIconImage(cargarIcono("/imagenes/farmaceuta logo.png", 32, 32).getImage());

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

        JLabel lblPaciente = new JLabel("");
        lblPaciente.setFont(new Font("Arial", Font.BOLD, 12));
        lblPaciente.setForeground(new Color(0, 70, 140));
        lblPaciente.setBounds(20, 130, 200, 25);
        panelPrescribir.add(lblPaciente);

        String[] columnas = {"Medicamento", "Presentación", "Cantidad", "Indicaciones", "Duración"};
        DefaultTableModel modeloTabla = new DefaultTableModel(null, columnas) {
            @Override
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
        btnDescartar.setEnabled(true);
        btnDescartar.setBounds(280, 345, 200, 30);
        panelPrescribir.add(btnDescartar);

        btnDetalles = new JButton("Detalles");
        btnDetalles.setIcon(cargarIcono("/imagenes/Check logo.png", 20, 20));
        btnDetalles.setEnabled(true);
        btnDetalles.setBounds(490, 345, 120, 30);
        panelPrescribir.add(btnDetalles);

        return panelPrescribir;
    }

    private JPanel crearPanelAcercaDe() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(230, 233, 237));

        JLabel lblTitulo = new JLabel("Prescripcion y Despacho de Recetas", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(Color.BLUE);
        panel.add(lblTitulo, BorderLayout.NORTH);

        JLabel lblImagen = new JLabel();
        lblImagen.setHorizontalAlignment(SwingConstants.CENTER);
        lblImagen.setIcon(cargarIcono("/imagenes/hospital imagen principal.jpg", 700, 400));
        panel.add(lblImagen, BorderLayout.CENTER);

        JPanel subPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        subPanel.setBackground(new Color(230, 233, 237));

        JLabel lblInfo = new JLabel("Total Soft Inc.   @totalsoft   Tel. 67197691");
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 14));
        subPanel.add(lblInfo);

        panel.add(subPanel, BorderLayout.SOUTH);

        return panel;
    }
}
