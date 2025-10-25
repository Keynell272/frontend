package view;

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
import javax.swing.table.DefaultTableModel;

import control.ControlLogin;
import control.ControlReceta;
import model.Medicamento;
import model.Medico;
import model.Paciente;
import model.Receta;
import view.paneles.generales.DashboardPanel;
import view.paneles.generales.DatePickerConNavegacion;
import view.paneles.generales.PanelAcercaDe;
import view.paneles.generales.PanelHistorico;
import view.paneles.generales.PanelUsuariosActivos;

public class VentanaMenuMedico extends JFrame {
    private Medico medicoLogueado;
    private ControlReceta controlReceta;
    private ControlLogin controlLogin;
    private List<Receta> recetas;
    private JTextField txtFechaRetiro;
    private JButton btnBuscarPaciente, btnAgregarMedicamento;
    private JTable tablaMedicamentos;
    private JButton btnGuardar, btnLimpiar, btnDescartar, btnDetalles, btnCerrarSesion;
    private JLabel lblPaciente;
    private Paciente pacienteSeleccionado;
    private List<Medicamento> cargarMedicamentos;
    private PanelUsuariosActivos panelUsuariosActivos;

    public VentanaMenuMedico(Medico medicoLogueado, ControlReceta controlReceta) {
        this.medicoLogueado = medicoLogueado;
        this.controlReceta = controlReceta;
        this.controlLogin = new ControlLogin();
        this.recetas = controlReceta.getRecetas();
        this.cargarMedicamentos = controlReceta.getMedicamentos();
        init();
    }

    private void init() {
        setTitle("Sistema de Recetas - Dr(a). " + medicoLogueado.getNombre() + " (" + medicoLogueado.getRol() + ")");
        setSize(1050, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setIconImage(cargarIcono("/imagenes/medico logo.png", 32, 32).getImage());

        JPanel panelPrincipal = new JPanel(new BorderLayout());
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Prescribir", cargarIcono("/imagenes/medicamentos logos.png", 20, 20), crearPanelPrescribir());
        tabbedPane.addTab("Dashboard", cargarIcono("/imagenes/dashbord logo.png", 20, 20), new DashboardPanel());
        tabbedPane.addTab("Histórico", cargarIcono("/imagenes/historico logo.png", 20, 20), crearPanelHistorico());
        tabbedPane.addTab("Acerca de...", cargarIcono("/imagenes/Acerca de logo.png", 20, 20), crearPanelAcercaDe());
        
        panelPrincipal.add(tabbedPane, BorderLayout.CENTER);
        
        panelUsuariosActivos = new PanelUsuariosActivos(medicoLogueado);
        panelPrincipal.add(panelUsuariosActivos, BorderLayout.EAST);
        
        add(panelPrincipal);
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                cerrarSesion();
            }
        });
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

    private JPanel crearPanelPrescribir() {
        JPanel panelPrescribir = new JPanel(null);

        JLabel lblControl = new JLabel("Control");
        lblControl.setFont(new Font("Arial", Font.BOLD, 12));
        lblControl.setBounds(10, 5, 100, 20);
        panelPrescribir.add(lblControl);

        btnBuscarPaciente = new JButton("Buscar Paciente", cargarIcono("/imagenes/Lupa de buscar logo.png", 20, 20));
        btnBuscarPaciente.setBounds(10, 25, 160, 30);
        btnBuscarPaciente.addActionListener(e -> mostrarVentanaBuscarPaciente());
        panelPrescribir.add(btnBuscarPaciente);

        btnAgregarMedicamento = new JButton("Agregar Medicamento", cargarIcono("/imagenes/medicamentos logos.png", 20, 20));
        btnAgregarMedicamento.setBounds(180, 25, 190, 30);
        btnAgregarMedicamento.addActionListener(e -> mostrarVentanaAgregarMedicamento());
        panelPrescribir.add(btnAgregarMedicamento);

        btnCerrarSesion = new JButton("Cerrar Sesión");
        btnCerrarSesion.setBounds(420, 25, 130, 30);
        btnCerrarSesion.setBackground(new Color(192, 57, 43));
        btnCerrarSesion.setForeground(Color.black);
        btnCerrarSesion.setFocusPainted(false);
        btnCerrarSesion.addActionListener(e -> cerrarSesion());
        panelPrescribir.add(btnCerrarSesion);

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
        txtFechaRetiro.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        panelPrescribir.add(txtFechaRetiro);

        JButton btnCalendario = new JButton("📅");
        btnCalendario.setBounds(290, 95, 50, 25);
        panelPrescribir.add(btnCalendario);
        new DatePickerConNavegacion(txtFechaRetiro, btnCalendario);

        lblPaciente = new JLabel("No se ha seleccionado paciente");
        lblPaciente.setFont(new Font("Arial", Font.BOLD, 12));
        lblPaciente.setForeground(new Color(0, 70, 140));
        lblPaciente.setBounds(20, 130, 400, 25);
        panelPrescribir.add(lblPaciente);

        String[] columnas = {"Medicamento", "Presentación", "Cantidad", "Indicaciones", "Duración (días)"};
        DefaultTableModel modeloTabla = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tablaMedicamentos = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tablaMedicamentos);
        scroll.setBounds(20, 160, 530, 180);
        panelPrescribir.add(scroll);

        JLabel lblAjustar = new JLabel("Ajustar Prescripción");
        lblAjustar.setFont(new Font("Arial", Font.BOLD, 12));
        lblAjustar.setBounds(10, 350, 150, 20);
        panelPrescribir.add(lblAjustar);

        btnGuardar = new JButton("Guardar", cargarIcono("/imagenes/Guardar logo.png", 20, 20));
        btnGuardar.setBounds(20, 375, 130, 35);
        btnGuardar.addActionListener(e -> guardarReceta());
        panelPrescribir.add(btnGuardar);

        btnLimpiar = new JButton("Limpiar", cargarIcono("/imagenes/Limpiar logo.png", 20, 20));
        btnLimpiar.setBounds(160, 375, 130, 35);
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        panelPrescribir.add(btnLimpiar);

        btnDescartar = new JButton("Descartar Med.", cargarIcono("/imagenes/Borrar logo.png", 20, 20));
        btnDescartar.setBounds(300, 375, 130, 35);
        btnDescartar.addActionListener(e -> descartarMedicamento());
        panelPrescribir.add(btnDescartar);

        btnDetalles = new JButton("Detalles", cargarIcono("/imagenes/Check logo.png", 20, 20));
        btnDetalles.setBounds(440, 375, 110, 35);
        btnDetalles.addActionListener(e -> mostrarVentanaDetalles());
        panelPrescribir.add(btnDetalles);

        return panelPrescribir;
    }

    private void guardarReceta() {
        try {
            controlReceta.guardarReceta(
                pacienteSeleccionado, 
                tablaMedicamentos, 
                txtFechaRetiro, 
                medicoLogueado.getId()
            );
            
            limpiarFormulario();
            controlReceta.refrescarDatos();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error al guardar la receta: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFormulario() {
        DefaultTableModel modelo = (DefaultTableModel) tablaMedicamentos.getModel();
        modelo.setRowCount(0);
        txtFechaRetiro.setText("");
        lblPaciente.setText("No se ha seleccionado paciente");
        pacienteSeleccionado = null;
    }

    private void descartarMedicamento() {
        int[] filas = tablaMedicamentos.getSelectedRows();
        if (filas.length == 0) {
            JOptionPane.showMessageDialog(this, 
                "Debe seleccionar al menos un medicamento para descartar.", 
                "Advertencia", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        DefaultTableModel modelo = (DefaultTableModel) tablaMedicamentos.getModel();
        for (int i = filas.length - 1; i >= 0; i--) {
            modelo.removeRow(filas[i]);
        }
        
        JOptionPane.showMessageDialog(this, 
            "Medicamento(s) descartado(s) correctamente.", 
            "Información", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void cerrarSesion() {
        int opcion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro que desea cerrar sesión?",
            "Confirmar cierre de sesión",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (opcion == JOptionPane.YES_OPTION) {
            controlLogin.logout(medicoLogueado.getId());
            dispose();
            new VentanaLogin().setVisible(true);
        }
    }

    private JPanel crearPanelAcercaDe() {
        return new PanelAcercaDe();
    }

    private void mostrarVentanaBuscarPaciente() {
        List<Paciente> pacientes = controlReceta.getPacientes();

        JDialog dialog = new JDialog(this, "Buscar Paciente", true);
        dialog.setSize(650, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel panelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelFiltro.setBorder(BorderFactory.createTitledBorder("Filtrar"));
        JComboBox<String> comboFiltro = new JComboBox<>(new String[]{"Nombre", "ID"});
        JTextField txtFiltro = new JTextField(25);
        panelFiltro.add(new JLabel("Buscar por:"));
        panelFiltro.add(comboFiltro);
        panelFiltro.add(txtFiltro);
        dialog.add(panelFiltro, BorderLayout.NORTH);

        String[] columnas = {"ID", "Nombre", "Teléfono", "Fecha Nacimiento"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override 
            public boolean isCellEditable(int r, int c) { 
                return false; 
            }
        };
        JTable tabla = new JTable(modelo);
        tabla.getTableHeader().setReorderingAllowed(false);
        dialog.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnSeleccionar = new JButton("Seleccionar");
        btnSeleccionar.setFocusPainted(false);
        
        JButton btnCancelar = new JButton("Cancelar");
        panelBotones.add(btnSeleccionar);
        panelBotones.add(btnCancelar);
        dialog.add(panelBotones, BorderLayout.SOUTH);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        Runnable cargarTabla = () -> {
            modelo.setRowCount(0);
            String criterio = comboFiltro.getSelectedItem().toString().toLowerCase();
            String filtroTexto = txtFiltro.getText().trim().toLowerCase();
            
            for (Paciente p : pacientes) {
                String valor = criterio.equals("nombre") ? p.getNombre() : p.getId();
                if (valor.toLowerCase().contains(filtroTexto)) {
                    modelo.addRow(new Object[]{
                        p.getId(), 
                        p.getNombre(), 
                        p.getTelefono(), 
                        sdf.format(p.getFechaNacimiento())
                    });
                }
            }
        };
        cargarTabla.run();

        txtFiltro.addKeyListener(new KeyAdapter() {
            @Override 
            public void keyReleased(KeyEvent e) { 
                cargarTabla.run(); 
            }
        });
        
        comboFiltro.addActionListener(e -> cargarTabla.run());

        btnSeleccionar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila != -1) {
                String idSel = (String) modelo.getValueAt(fila, 0);
                pacienteSeleccionado = pacientes.stream()
                    .filter(p -> p.getId().equals(idSel))
                    .findFirst()
                    .orElse(null);
                    
                if (pacienteSeleccionado != null) {
                    lblPaciente.setText("Paciente: " + pacienteSeleccionado.getNombre() + 
                                       " (ID: " + pacienteSeleccionado.getId() + ")");
                }
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, 
                    "Debe seleccionar un paciente de la tabla.", 
                    "Advertencia", 
                    JOptionPane.WARNING_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());
        
        dialog.setVisible(true);
    }

    private void mostrarVentanaAgregarMedicamento() {
        List<Medicamento> medicamentos = cargarMedicamentos;

        JDialog dialog = new JDialog(this, "Agregar Medicamento", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel panelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelFiltro.setBorder(BorderFactory.createTitledBorder("Filtrar"));
        JComboBox<String> comboFiltro = new JComboBox<>(new String[]{"Nombre", "Código"});
        JTextField txtFiltro = new JTextField(25);
        panelFiltro.add(new JLabel("Buscar por:"));
        panelFiltro.add(comboFiltro);
        panelFiltro.add(txtFiltro);
        dialog.add(panelFiltro, BorderLayout.NORTH);

        String[] columnas = {"Código", "Nombre", "Presentación"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override 
            public boolean isCellEditable(int r, int c) { 
                return false; 
            }
        };
        JTable tabla = new JTable(modelo);
        tabla.getTableHeader().setReorderingAllowed(false);
        dialog.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnAgregar = new JButton("Agregar");
        btnAgregar.setFocusPainted(false);
        
        JButton btnCancelar = new JButton("Cancelar");
        panelBotones.add(btnAgregar);
        panelBotones.add(btnCancelar);
        dialog.add(panelBotones, BorderLayout.SOUTH);

        Runnable cargarTabla = () -> {
            modelo.setRowCount(0);
            String criterio = comboFiltro.getSelectedItem().toString().toLowerCase();
            String filtroTexto = txtFiltro.getText().trim().toLowerCase();
            
            for (Medicamento m : medicamentos) {
                String valor = criterio.equals("nombre") ? m.getNombre() : m.getCodigo();
                if (valor.toLowerCase().contains(filtroTexto)) {
                    modelo.addRow(new Object[]{
                        m.getCodigo(), 
                        m.getNombre(), 
                        m.getPresentacion()
                    });
                }
            }
        };
        cargarTabla.run();

        txtFiltro.addKeyListener(new KeyAdapter() { 
            @Override 
            public void keyReleased(KeyEvent e) { 
                cargarTabla.run(); 
            }
        });
        
        comboFiltro.addActionListener(e -> cargarTabla.run());

        btnAgregar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila != -1) {
                DefaultTableModel modeloTabla = (DefaultTableModel) tablaMedicamentos.getModel();
                modeloTabla.addRow(new Object[]{
                    modelo.getValueAt(fila, 1),
                    modelo.getValueAt(fila, 2),
                    "",
                    "",
                    ""
                });
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, 
                    "Debe seleccionar un medicamento de la tabla.", 
                    "Advertencia", 
                    JOptionPane.WARNING_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());
        
        dialog.setVisible(true);
    }

    private JPanel crearPanelHistorico() {
        return new PanelHistorico(controlReceta);
    }
    
    private void mostrarVentanaDetalles() {
        if (tablaMedicamentos.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "No hay medicamentos agregados para mostrar detalles.",
                "Información",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Detalles de la Prescripción", true);
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel panelInfo = new JPanel();
        panelInfo.setBorder(BorderFactory.createTitledBorder("Información de la Receta"));
        panelInfo.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panelInfo.add(new JLabel("📋 " + lblPaciente.getText()));
        panelInfo.add(new JLabel(" | "));
        panelInfo.add(new JLabel("📅 Fecha de Retiro: " + txtFechaRetiro.getText()));
        dialog.add(panelInfo, BorderLayout.NORTH);

        String[] columnas = {"Medicamento", "Presentación", "Cantidad", "Indicaciones", "Duración (días)"};
        DefaultTableModel modeloDetalles = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
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

        tablaDetalles.getModel().addTableModelListener(e -> {
            int fila = e.getFirstRow();
            int columna = e.getColumn();
            if (fila >= 0 && columna >= 0 && (columna == 2 || columna == 3 || columna == 4)) {
                Object nuevoValor = tablaDetalles.getValueAt(fila, columna);
                tablaMedicamentos.setValueAt(nuevoValor, fila, columna);
            }
        });

        dialog.add(new JScrollPane(tablaDetalles), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnGuardarCambios = new JButton("Guardar Cambios");
        btnGuardarCambios.setFocusPainted(false);
        
        JButton btnCerrar = new JButton("Cerrar");
        
        btnGuardarCambios.addActionListener(e -> {
            JOptionPane.showMessageDialog(dialog,
                "Los cambios se han guardado correctamente.",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        btnCerrar.addActionListener(e -> dialog.dispose());
        
        panelBotones.add(btnGuardarCambios);
        panelBotones.add(btnCerrar);
        dialog.add(panelBotones, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}