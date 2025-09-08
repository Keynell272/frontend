package proyecto.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.BorderFactory;
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

import proyecto.control.ControlReceta;
import proyecto.control.ControlAdmin;
import proyecto.model.Receta;
import proyecto.model.DatePickerConNavegacion;
import proyecto.model.Medicamento;
import proyecto.model.Medico;
import proyecto.model.Paciente;
import proyecto.view.DashboardPanel;

public class VentanaMenuMedico extends JFrame {
    private Medico medicoLogueado;
    private ControlReceta controlReceta;
    private List<Receta> recetas;

    private JTextField txtFechaRetiro;
    private JButton btnBuscarPaciente, btnAgregarMedicamento;
    private JTable tablaMedicamentos;
    private JButton btnGuardar, btnLimpiar, btnDescartar, btnDetalles;
    private JLabel lblPaciente;
    private Paciente pacienteSeleccionado;
    private List<Medicamento> cargarMedicamentos;

    public VentanaMenuMedico(Medico medicoLogueado, ControlReceta controlReceta) {
        this.medicoLogueado = medicoLogueado;
        this.controlReceta = controlReceta;
        this.recetas = controlReceta.getRecetas();
        this.cargarMedicamentos = controlReceta.getMedicamentos();
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
        tabbedPane.addTab("Dashboard", cargarIcono("/imagenes/dashbord logo.png", 20, 20), new DashboardPanel(recetas, cargarMedicamentos));
        tabbedPane.addTab("Histórico", cargarIcono("/imagenes/historico logo.png", 20, 20), new JPanel());
        tabbedPane.addTab("Acerca de...", cargarIcono("/imagenes/Acerca de logo.png", 20, 20), crearPanelAcercaDe());
        add(tabbedPane);
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
                return false;
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
        btnGuardar.addActionListener(e -> controlReceta.guardarReceta(pacienteSeleccionado, tablaMedicamentos, txtFechaRetiro));
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
        List<Paciente> pacientes = controlReceta.getPacientes();

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

    // ------------------ MEDICAMENTOS ------------------
    private void mostrarVentanaAgregarMedicamento() {
        List<Medicamento> medicamentos = cargarMedicamentos;

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
        DefaultTableModel modeloDetalles = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                // Solo columnas Cantidad, Indicaciones y Duración son editables
                return col == 2 || col == 3 || col == 4;
            }
        };

        DefaultTableModel modeloOriginal = (DefaultTableModel) tablaMedicamentos.getModel();
        for (int i = 0; i < modeloOriginal.getRowCount(); i++) {
            Object[] fila = new Object[modeloOriginal.getColumnCount()];
            for (int j = 0; j < modeloOriginal.getColumnCount(); j++) {
                fila[j] = modeloOriginal.getValueAt(i, j);
            }
            modeloDetalles.addRow(fila);
        }

        JTable tablaDetalles = new JTable(modeloDetalles);

        // Listener para sincronizar cambios con tabla principal
        tablaDetalles.getModel().addTableModelListener(e -> {
            int fila = e.getFirstRow();
            int columna = e.getColumn();
            if (fila >= 0 && (columna == 2 || columna == 3 || columna == 4)) {
                Object nuevoValor = tablaDetalles.getValueAt(fila, columna);
                tablaMedicamentos.setValueAt(nuevoValor, fila, columna);
            }
        });

        dialog.add(new JScrollPane(tablaDetalles), BorderLayout.CENTER);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dialog.dispose());
        JPanel panelBoton = new JPanel();
        panelBoton.add(btnCerrar);
        dialog.add(panelBoton, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}
