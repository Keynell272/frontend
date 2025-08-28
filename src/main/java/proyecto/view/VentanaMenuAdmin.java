package proyecto.view;

import proyecto.model.Medico;
import proyecto.model.Usuario;
import proyecto.model.Farmaceuta;
import proyecto.model.Medicamento;
import proyecto.model.Paciente;
import proyecto.persistencia.XmlManager;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;


import proyecto.model.DatePickerConNavegacion;

public class VentanaMenuAdmin extends JFrame {
    private Usuario usuarioLogueado;
    private DefaultTableModel modeloTabla;
    private List<Medico> medicos;
    private JTable tabla;
    private JTextField txtBusquedaNombre;
    private JButton btnRestaurar;
    private DefaultTableModel modeloTablaFarm;
    private List<Farmaceuta> farmaceutas;  // si tenés clase Farmaceuta, usala aquí
    private JTable tablaFarm;
    private JTextField txtIdFarm, txtNombreFarm, txtBusquedaNombreFarm;
    
    private DefaultTableModel modeloTablaPac;
    private List<Paciente> pacientes;
    private JTable tablaPac;
    private JTextField txtIdPac, txtNombrePac, txtTelefonoPac, txtBusquedaNombrePac;
    private List<Medicamento> medicamentos;

    // Campos de texto (para usarlos en guardar/limpiar)
    private JTextField txtId, txtNombre, txtEspecialidad;

    private String formatearFecha(Date fecha) {
        if (fecha == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(fecha);
    }

    public VentanaMenuAdmin(Usuario usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;
        this.medicos = XmlManager.cargarMedicos("medicos.xml"); // 🔹 cargar médicos desde XML
        if (this.medicos == null) {
            this.medicos = new ArrayList<>();
        }
        this.farmaceutas = XmlManager.cargarFarmaceutas("farmaceutas.xml");
        if (this.farmaceutas == null) {
            this.farmaceutas = new ArrayList<>();
        }
        this.pacientes = XmlManager.cargarPacientes("pacientes.xml");
        if (this.pacientes == null) {
            this.pacientes = new ArrayList<>();
        }
        this.medicamentos = XmlManager.cargarMedicamentos("medicamentos.xml");
        if (this.medicamentos == null) {
            this.medicamentos = new ArrayList<>();
        }

        init();
    }

    private void init() {
        setTitle("Recetas - " + usuarioLogueado.getNombre() + " (" + usuarioLogueado.getRol() + ")");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setIconImage(new ImageIcon(getClass().getResource("/imagenes/Recetas logo.png")).getImage());

        JTabbedPane tabbedPane = new JTabbedPane();

        // Panel médicos con toda la interfaz
        JPanel panelMedicos = crearPanelMedicos();

        // Otros paneles simples
        JPanel panelFarmaceutas = crearPanelFarmaceutas();
        panelFarmaceutas.add(new JLabel("Farmaceutas"));

        JPanel panelPacientes = crearPanelPacientes();
        panelPacientes.add(new JLabel("Pacientes"));

        JPanel panelMedicamentos = crearPanelMedicamentos();
        panelMedicamentos.add(new JLabel("Medicamentos"));

        JPanel panelDashboard = new JPanel();
        panelDashboard.add(new JLabel("Dashboard"));

        JPanel panelHistorico = new JPanel();
        panelHistorico.add(new JLabel("Histórico"));

        JPanel panelAcerca = crearPanelAcercaDe();

        // Tabs
        tabbedPane.addTab("Médicos", cargarIcono("/imagenes/medico logo.png", 24, 24), panelMedicos);
        tabbedPane.addTab("Farmaceutas", cargarIcono("/imagenes/farmaceuta logo.png", 24, 24), panelFarmaceutas);
        tabbedPane.addTab("Pacientes", cargarIcono("/imagenes/pacientes logo.png", 24, 24), panelPacientes);
        tabbedPane.addTab("Medicamentos", cargarIcono("/imagenes/medicamentos logos.png", 24, 24), panelMedicamentos);
        tabbedPane.addTab("Dashboard", cargarIcono("/imagenes/dashbord logo.png", 24, 24), panelDashboard);
        tabbedPane.addTab("Histórico", cargarIcono("/imagenes/historico logo.png", 24, 24), panelHistorico);
        tabbedPane.addTab("Acerca de...", cargarIcono("/imagenes/Acerca de logo.png", 24, 24), panelAcerca);

        add(tabbedPane, BorderLayout.CENTER);
    }

    // ===================== PANEL MÉDICOS =====================
    private JPanel crearPanelMedicos() {
        JPanel panel = new JPanel();
        panel.setLayout(null);

        // Etiquetas y campos
        JLabel lblMedico = new JLabel("Médico");
        lblMedico.setFont(new Font("Arial", Font.BOLD, 12));
        lblMedico.setBounds(10, 10, 100, 20);
        panel.add(lblMedico);

        JLabel lblId = new JLabel("Id");
        lblId.setBounds(10, 40, 50, 20);
        panel.add(lblId);

        txtId = new JTextField();
        txtId.setBounds(100, 40, 150, 25);
        panel.add(txtId);

        JLabel lblNombre = new JLabel("Nombre");
        lblNombre.setBounds(10, 70, 50, 20);
        panel.add(lblNombre);

        txtNombre = new JTextField();
        txtNombre.setBounds(100, 70, 150, 25);
        panel.add(txtNombre);

        JLabel lblEspecialidad = new JLabel("Especialidad");
        lblEspecialidad.setBounds(10, 100, 100, 20);
        panel.add(lblEspecialidad);

        txtEspecialidad = new JTextField();
        txtEspecialidad.setBounds(100, 100, 150, 25);
        panel.add(txtEspecialidad);

        // Botones Guardar, Limpiar, Borrar
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setBounds(320, 40, 100, 30);
        btnGuardar.setIcon(cargarIcono("/imagenes/Guardar logo.png", 20, 20));
        panel.add(btnGuardar);

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBounds(430, 40, 100, 30);
        btnLimpiar.setIcon(cargarIcono("/imagenes/Limpiar logo.png", 20, 20));
        panel.add(btnLimpiar);

        btnLimpiar.addActionListener(e -> limpiarCampos());

        JButton btnBorrar = new JButton("Borrar");
        btnBorrar.setBounds(540, 40, 100, 30);
        btnBorrar.setIcon(cargarIcono("/imagenes/X logo.png", 20, 20));
        panel.add(btnBorrar);

        // Sección búsqueda
        JLabel lblBusqueda = new JLabel("Búsqueda");
        lblBusqueda.setFont(new Font("Arial", Font.BOLD, 12));
        lblBusqueda.setBounds(10, 150, 100, 20);
        panel.add(lblBusqueda);

        JLabel lblBusquedaNombre = new JLabel("Nombre");
        lblBusquedaNombre.setBounds(10, 180, 60, 20);
        panel.add(lblBusquedaNombre);

        txtBusquedaNombre = new JTextField();
        txtBusquedaNombre.setBounds(80, 180, 150, 25);
        panel.add(txtBusquedaNombre);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(240, 180, 100, 30);
        btnBuscar.setIcon(cargarIcono("/imagenes/Lupa de buscar logo.png", 20, 20));
        panel.add(btnBuscar);

        btnRestaurar = new JButton("⟳"); // pequeño
        btnRestaurar.setBounds(350, 180, 50, 30);
        panel.add(btnRestaurar);


        JButton btnReporte = new JButton("Reporte");
        btnReporte.setBounds(410, 180, 100, 30);
        btnReporte.setIcon(cargarIcono("/imagenes/Reporte logo.png", 20, 20));
        panel.add(btnReporte);

        // Tabla de médicos
        JLabel lblListado = new JLabel("Listado");
        lblListado.setFont(new Font("Arial", Font.BOLD, 12));
        lblListado.setBounds(10, 230, 100, 20);
        panel.add(lblListado);

        String[] columnas = {"Id", "Nombre", "Especialidad"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tabla = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBounds(10, 260, 650, 300);
        panel.add(scrollPane);

        txtId.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        txtNombre.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        txtEspecialidad.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        txtBusquedaNombre.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        btnGuardar.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        btnLimpiar.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        btnBorrar.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));        
        btnBuscar.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        btnRestaurar.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        btnReporte.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));

        // 🔹 Cargar médicos desde XML al abrir
        for (Medico m : medicos) {
            modeloTabla.addRow(new Object[]{m.getId(), m.getNombre(), m.getEspecialidad()});
        }

        // Acción Guardar
        btnGuardar.addActionListener(e -> {
            String id = txtId.getText().trim();
            String nombre = txtNombre.getText().trim();
            String especialidad = txtEspecialidad.getText().trim();

            if (id.isEmpty() || nombre.isEmpty() || especialidad.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe llenar todos los campos");
                return;
            }

            Medico m = new Medico(id, "123", nombre, especialidad); // clave por defecto
            medicos.add(m);

            // Tabla
            modeloTabla.addRow(new Object[]{m.getId(), m.getNombre(), m.getEspecialidad()});

            // Guardar en XML
            XmlManager.guardarMedicos(medicos, "medicos.xml");

            JOptionPane.showMessageDialog(this, "✅ Médico guardado con éxito");
            limpiarCampos();
        });

        btnBorrar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un médico en la tabla para borrar");
                return;
            }

            String id = (String) modeloTabla.getValueAt(fila, 0);

            // quitar de la lista
            medicos.removeIf(m -> m.getId().equals(id));

            // quitar de la tabla
            modeloTabla.removeRow(fila);

            // actualizar XML
            XmlManager.guardarMedicos(medicos, "medicos.xml");

            JOptionPane.showMessageDialog(this, "✅ Médico eliminado");
        });

        btnBuscar.addActionListener(e -> {
            String nombreBuscar = txtBusquedaNombre.getText().trim().toLowerCase();
            if (nombreBuscar.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese un nombre para buscar");
                return;
            }

            // limpiar tabla
            modeloTabla.setRowCount(0);

            // mostrar coincidencias
            for (Medico m : medicos) {
                if (m.getNombre().toLowerCase().contains(nombreBuscar)) {
                    modeloTabla.addRow(new Object[]{m.getId(), m.getNombre(), m.getEspecialidad()});
                }
            }
        });

        btnRestaurar.addActionListener(e -> {
            // limpiar tabla
            modeloTabla.setRowCount(0);
            // volver a cargar todos los médicos
            for (Medico m : medicos) {
                modeloTabla.addRow(new Object[]{m.getId(), m.getNombre(), m.getEspecialidad()});
            }
        });
        
        btnReporte.addActionListener(e -> {
            if (medicos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay médicos para el reporte");
                return;
            }

            StringBuilder sb = new StringBuilder("=== Reporte de Médicos ===\n\n");
            for (Medico m : medicos) {
                sb.append("ID: ").append(m.getId())
                .append(" | Nombre: ").append(m.getNombre())
                .append(" | Especialidad: ").append(m.getEspecialidad())
                .append("\n");
            }

            JTextArea area = new JTextArea(sb.toString());
            area.setEditable(false);
            JScrollPane scroll = new JScrollPane(area);
            scroll.setPreferredSize(new Dimension(500, 300));

            JOptionPane.showMessageDialog(this, scroll, "Reporte de Médicos", JOptionPane.INFORMATION_MESSAGE);
        });


        return panel;
    }

    private JPanel crearPanelFarmaceutas() {
        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel lblFarm = new JLabel("Farmacéuta");
        lblFarm.setFont(new Font("Arial", Font.BOLD, 12));
        lblFarm.setBounds(10, 10, 100, 20);
        panel.add(lblFarm);

        JLabel lblId = new JLabel("Id");
        lblId.setBounds(10, 40, 50, 20);
        panel.add(lblId);

        txtIdFarm = new JTextField();
        txtIdFarm.setBounds(100, 40, 150, 25);
        panel.add(txtIdFarm);

        JLabel lblNombre = new JLabel("Nombre");
        lblNombre.setBounds(10, 70, 50, 20);
        panel.add(lblNombre);

        txtNombreFarm = new JTextField();
        txtNombreFarm.setBounds(100, 70, 150, 25);
        panel.add(txtNombreFarm);

        // Botones
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setBounds(320, 40, 100, 30);
        btnGuardar.setIcon(cargarIcono("/imagenes/Guardar logo.png", 20, 20));
        panel.add(btnGuardar);

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBounds(430, 40, 100, 30);
        btnLimpiar.setIcon(cargarIcono("/imagenes/Limpiar logo.png", 20, 20));
        panel.add(btnLimpiar);

        JButton btnBorrar = new JButton("Borrar");
        btnBorrar.setBounds(540, 40, 100, 30);
        btnBorrar.setIcon(cargarIcono("/imagenes/X logo.png", 20, 20));
        panel.add(btnBorrar);

        // Búsqueda
        JLabel lblBusqueda = new JLabel("Búsqueda");
        lblBusqueda.setFont(new Font("Arial", Font.BOLD, 12));
        lblBusqueda.setBounds(10, 150, 100, 20);
        panel.add(lblBusqueda);

        JLabel lblBusquedaNombre = new JLabel("Nombre");
        lblBusquedaNombre.setBounds(10, 180, 60, 20);
        panel.add(lblBusquedaNombre);

        txtBusquedaNombreFarm = new JTextField();
        txtBusquedaNombreFarm.setBounds(80, 180, 150, 25);
        panel.add(txtBusquedaNombreFarm);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(240, 180, 100, 30);
        btnBuscar.setIcon(cargarIcono("/imagenes/Lupa de buscar logo.png", 20, 20));
        panel.add(btnBuscar);
        
        JButton btnRestaurar = new JButton("⟳");
        btnRestaurar.setBounds(350, 180, 50, 30);
        panel.add(btnRestaurar);

        JButton btnReporte = new JButton("Reporte");
        btnReporte.setBounds(410, 180, 100, 30);
        btnReporte.setIcon(cargarIcono("/imagenes/Reporte logo.png", 20, 20));
        panel.add(btnReporte);

        

        // Tabla
        JLabel lblListado = new JLabel("Listado");
        lblListado.setFont(new Font("Arial", Font.BOLD, 12));
        lblListado.setBounds(10, 230, 100, 20);
        panel.add(lblListado);

        String[] columnas = {"Id", "Nombre"};
        modeloTablaFarm = new DefaultTableModel(columnas, 0);
        tablaFarm = new JTable(modeloTablaFarm);
        JScrollPane scrollPane = new JScrollPane(tablaFarm);
        scrollPane.setBounds(10, 260, 650, 300);
        panel.add(scrollPane);

        txtIdFarm.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        txtNombreFarm.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        txtBusquedaNombreFarm.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        btnGuardar.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        btnLimpiar.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        btnBorrar.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));        
        btnBuscar.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        btnRestaurar.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        btnReporte.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));

        // Cargar farmaceutas desde XML
        for (Farmaceuta f : farmaceutas) {
            modeloTablaFarm.addRow(new Object[]{f.getId(), f.getNombre()});
        }

        // Acciones
        btnGuardar.addActionListener(e -> {
            String id = txtIdFarm.getText().trim();
            String nombre = txtNombreFarm.getText().trim();

            if (id.isEmpty() || nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe llenar todos los campos");
                return;
            }

            Farmaceuta f = new Farmaceuta(id, "123", nombre);
            farmaceutas.add(f);
            modeloTablaFarm.addRow(new Object[]{f.getId(), f.getNombre()});
            XmlManager.guardarFarmaceutas(farmaceutas, "farmaceutas.xml");

            JOptionPane.showMessageDialog(this, "✅ Farmacéuta guardado con éxito");
            txtIdFarm.setText(""); txtNombreFarm.setText(""); 
        });

        btnLimpiar.addActionListener(e -> {
            txtIdFarm.setText(""); txtNombreFarm.setText("");
        });

        btnBorrar.addActionListener(e -> {
            int fila = tablaFarm.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un farmacéuta");
                return;
            }
            String id = (String) modeloTablaFarm.getValueAt(fila, 0);
            farmaceutas.removeIf(f -> f.getId().equals(id));
            modeloTablaFarm.removeRow(fila);
            XmlManager.guardarFarmaceutas(farmaceutas, "farmaceutas.xml");
            JOptionPane.showMessageDialog(this, "✅ Farmacéuta eliminado");
        });

        btnBuscar.addActionListener(e -> {
            String buscar = txtBusquedaNombreFarm.getText().trim().toLowerCase();
            modeloTablaFarm.setRowCount(0);
            for (Farmaceuta f : farmaceutas) {
                if (f.getNombre().toLowerCase().contains(buscar)) {
                    modeloTablaFarm.addRow(new Object[]{f.getId(), f.getNombre()});
                }
            }
        });

        btnRestaurar.addActionListener(e -> {
            modeloTablaFarm.setRowCount(0);
            for (Farmaceuta f : farmaceutas) {
                modeloTablaFarm.addRow(new Object[]{f.getId(), f.getNombre()});
            }
        });

        btnReporte.addActionListener(e -> {
            StringBuilder sb = new StringBuilder("=== Reporte de Farmacéutas ===\n\n");
            for (Farmaceuta f : farmaceutas) {
                sb.append("ID: ").append(f.getId())
                .append(" | Nombre: ").append(f.getNombre())
                .append("\n");
            }
            JTextArea area = new JTextArea(sb.toString());
            area.setEditable(false);
            JScrollPane sp = new JScrollPane(area);
            sp.setPreferredSize(new Dimension(500, 300));
            JOptionPane.showMessageDialog(this, sp, "Reporte de Farmacéutas", JOptionPane.INFORMATION_MESSAGE);
        });

        return panel;
    }

    private JPanel crearPanelPacientes() {
        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel lblPac = new JLabel("Paciente");
        lblPac.setFont(new Font("Arial", Font.BOLD, 12));
        lblPac.setBounds(10, 10, 100, 20);
        panel.add(lblPac);

        JLabel lblId = new JLabel("Id");
        lblId.setBounds(10, 40, 50, 20);
        panel.add(lblId);

        txtIdPac = new JTextField();
        txtIdPac.setBounds(100, 40, 150, 25);
        panel.add(txtIdPac);

        JLabel lblNombre = new JLabel("Nombre");
        lblNombre.setBounds(10, 70, 50, 20);
        panel.add(lblNombre);

        txtNombrePac = new JTextField();
        txtNombrePac.setBounds(100, 70, 150, 25);
        panel.add(txtNombrePac);

        JTextField txtFechaNac = new JTextField();
        txtFechaNac.setBounds(100, 100, 150, 25);
        panel.add(txtFechaNac);

        JLabel lblFechaNac = new JLabel("Fecha Nac.");
        lblFechaNac.setBounds(10, 100, 100, 20);
        panel.add(lblFechaNac);

        JButton btnCalendario = new JButton("📅");
        btnCalendario.setBounds(260, 100, 50, 25);
        panel.add(btnCalendario);
        
        DatePickerConNavegacion datePicker = new DatePickerConNavegacion(txtFechaNac, btnCalendario);

        JLabel lblTel = new JLabel("Teléfono");
        lblTel.setBounds(10, 130, 100, 20);
        panel.add(lblTel);

        txtTelefonoPac = new JTextField();
        txtTelefonoPac.setBounds(100, 130, 150, 25);
        panel.add(txtTelefonoPac);

        // Botones
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setBounds(320, 40, 100, 30);
        btnGuardar.setIcon(cargarIcono("/imagenes/Guardar logo.png", 20, 20));
        panel.add(btnGuardar);

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBounds(430, 40, 100, 30);
        btnLimpiar.setIcon(cargarIcono("/imagenes/Limpiar logo.png", 20, 20));
        panel.add(btnLimpiar);

        JButton btnBorrar = new JButton("Borrar");
        btnBorrar.setBounds(540, 40, 100, 30);
        btnBorrar.setIcon(cargarIcono("/imagenes/X logo.png", 20, 20));
        panel.add(btnBorrar);

        // Búsqueda
        JLabel lblBusqueda = new JLabel("Búsqueda");
        lblBusqueda.setFont(new Font("Arial", Font.BOLD, 12));
        lblBusqueda.setBounds(10, 170, 100, 20);
        panel.add(lblBusqueda);

        JLabel lblBusquedaNombre = new JLabel("Nombre");
        lblBusquedaNombre.setBounds(10, 200, 60, 20);
        panel.add(lblBusquedaNombre);

        txtBusquedaNombrePac = new JTextField();
        txtBusquedaNombrePac.setBounds(80, 200, 150, 25);
        panel.add(txtBusquedaNombrePac);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(240, 200, 100, 30);
        btnBuscar.setIcon(cargarIcono("/imagenes/Lupa de buscar logo.png", 20, 20));
        panel.add(btnBuscar);

        JButton btnRestaurar = new JButton("⟳");
        btnRestaurar.setBounds(350, 200, 50, 30);
        panel.add(btnRestaurar);

        JButton btnReporte = new JButton("Reporte");
        btnReporte.setBounds(410, 200, 100, 30);
        btnReporte.setIcon(cargarIcono("/imagenes/Reporte logo.png", 20, 20));
        panel.add(btnReporte);

        // Tabla
        JLabel lblListado = new JLabel("Listado");
        lblListado.setFont(new Font("Arial", Font.BOLD, 12));
        lblListado.setBounds(10, 250, 100, 20);
        panel.add(lblListado);

        String[] columnas = {"Id", "Nombre", "Fecha Nac.", "Teléfono"};
        modeloTablaPac = new DefaultTableModel(columnas, 0);
        tablaPac = new JTable(modeloTablaPac);
        JScrollPane scrollPane = new JScrollPane(tablaPac);
        scrollPane.setBounds(10, 280, 650, 280);
        panel.add(scrollPane);

        // Cargar pacientes al abrir
        for (Paciente p : pacientes) {
            modeloTablaPac.addRow(new Object[]{p.getId(), p.getNombre(), formatearFecha(p.getFechaNacimiento()), p.getTelefono()});
        }

        // Acciones
        btnGuardar.addActionListener(e -> {
            String id = txtIdPac.getText().trim();
            String nombre = txtNombrePac.getText().trim();
            String tel = txtTelefonoPac.getText().trim();
            Date fecha = null;
            try {
                if (!txtFechaNac.getText().isEmpty()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    fecha = sdf.parse(txtFechaNac.getText());
                } else {
                    JOptionPane.showMessageDialog(this, "Debe seleccionar la fecha de nacimiento");
                    return;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Formato de fecha inválido");
                return;
            }

            if (id.isEmpty() || nombre.isEmpty() || tel.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe llenar todos los campos");
                return;
            }

            Paciente p = new Paciente(id, nombre, fecha, tel);
            pacientes.add(p);
            modeloTablaPac.addRow(new Object[]{p.getId(), p.getNombre(), formatearFecha(p.getFechaNacimiento()), p.getTelefono()});
            XmlManager.guardarPacientes(pacientes, "pacientes.xml");

            JOptionPane.showMessageDialog(this, "✅ Paciente guardado con éxito");
            txtIdPac.setText(""); txtNombrePac.setText(""); txtTelefonoPac.setText("");
        });

        btnLimpiar.addActionListener(e -> {
            txtIdPac.setText(""); txtNombrePac.setText(""); txtTelefonoPac.setText("");
        });

        btnBorrar.addActionListener(e -> {
            int fila = tablaPac.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un paciente");
                return;
            }
            String id = (String) modeloTablaPac.getValueAt(fila, 0);
            pacientes.removeIf(p -> p.getId().equals(id));
            modeloTablaPac.removeRow(fila);
            XmlManager.guardarPacientes(pacientes, "pacientes.xml");
            JOptionPane.showMessageDialog(this, "✅ Paciente eliminado");
        });

        btnBuscar.addActionListener(e -> {
            String buscar = txtBusquedaNombrePac.getText().trim().toLowerCase();
            modeloTablaPac.setRowCount(0);
            for (Paciente p : pacientes) {
                if (p.getNombre().toLowerCase().contains(buscar)) {
                    modeloTablaPac.addRow(new Object[]{p.getId(), p.getNombre(), formatearFecha(p.getFechaNacimiento()), p.getTelefono()});
                }
            }
        });

        btnRestaurar.addActionListener(e -> {
            modeloTablaPac.setRowCount(0);
            for (Paciente p : pacientes) {
                modeloTablaPac.addRow(new Object[]{p.getId(), p.getNombre(), formatearFecha(p.getFechaNacimiento()), p.getTelefono()});
            }
        });

        btnReporte.addActionListener(e -> {
            StringBuilder sb = new StringBuilder("=== Reporte de Pacientes ===\n\n");
            for (Paciente p : pacientes) {
                sb.append("ID: ").append(p.getId())
                .append(" | Nombre: ").append(p.getNombre())
                .append(" | Fecha Nac.: ").append(formatearFecha(p.getFechaNacimiento()))
                .append(" | Tel: ").append(p.getTelefono())
                .append("\n");
            }
            JTextArea area = new JTextArea(sb.toString());
            area.setEditable(false);
            JScrollPane sp = new JScrollPane(area);
            sp.setPreferredSize(new Dimension(500, 300));
            JOptionPane.showMessageDialog(this, sp, "Reporte de Pacientes", JOptionPane.INFORMATION_MESSAGE);
        });

        return panel;
    }

    private JPanel crearPanelAcercaDe() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(230, 233, 237));

        // Título arriba
        JLabel lblTitulo = new JLabel("Prescripcion y Despacho de Recetas", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(Color.BLUE);

        panel.add(lblTitulo, BorderLayout.NORTH);
        
        // Imagen central
        JLabel lblImagen = new JLabel();
        lblImagen.setHorizontalAlignment(SwingConstants.CENTER);
        lblImagen.setIcon(cargarIcono("/imagenes/hospital imagen principal.jpg", 700, 400)); 
        // ⚠️ Asegurate que hospital.png esté en tu carpeta resources/imagenes
        panel.add(lblImagen, BorderLayout.CENTER);
        
        // Subtítulo
        JPanel subPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        subPanel.setBackground(new Color(230, 233, 237));

        JLabel lblInfo = new JLabel("Total Soft Inc.   @totalsoft   Tel. 67197691");
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 14));
        subPanel.add(lblInfo);

        panel.add(subPanel, BorderLayout.SOUTH);

        

        return panel;
    }

    private JPanel crearPanelMedicamentos() {
        JPanel panel = new JPanel();
        panel.setLayout(null);

        // Etiquetas y campos
        JLabel lblTitulo = new JLabel("Medicamento");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 12));
        lblTitulo.setBounds(10, 10, 100, 20);
        panel.add(lblTitulo);

        JLabel lblCodigo = new JLabel("Código");
        lblCodigo.setBounds(10, 40, 50, 20);
        panel.add(lblCodigo);

        JTextField txtCodigo = new JTextField();
        txtCodigo.setBounds(100, 40, 150, 25);
        panel.add(txtCodigo);

        JLabel lblNombre = new JLabel("Nombre");
        lblNombre.setBounds(10, 70, 50, 20);
        panel.add(lblNombre);

        JTextField txtNombre = new JTextField();
        txtNombre.setBounds(100, 70, 150, 25);
        panel.add(txtNombre);

        JLabel lblPresentacion = new JLabel("Presentación");
        lblPresentacion.setBounds(10, 100, 100, 20);
        panel.add(lblPresentacion);

        JTextField txtPresentacion = new JTextField();
        txtPresentacion.setBounds(100, 100, 150, 25);
        panel.add(txtPresentacion);

        // Botones
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setIcon(cargarIcono("/imagenes/Guardar logo.png", 20, 20));
        btnGuardar.setBounds(320, 40, 100, 30);
        panel.add(btnGuardar);

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setIcon(cargarIcono("/imagenes/Limpiar logo.png", 20, 20));
        btnLimpiar.setBounds(430, 40, 100, 30);
        panel.add(btnLimpiar);

        JButton btnBorrar = new JButton("Borrar");
        btnBorrar.setIcon(cargarIcono("/imagenes/X logo.png", 20, 20));
        btnBorrar.setBounds(540, 40, 100, 30);
        panel.add(btnBorrar);

        // Búsqueda
        JLabel lblBusqueda = new JLabel("Búsqueda");
        lblBusqueda.setFont(new Font("Arial", Font.BOLD, 12));
        lblBusqueda.setBounds(10, 150, 100, 20);
        panel.add(lblBusqueda);

        JLabel lblBusquedaNombre = new JLabel("Nombre");
        lblBusquedaNombre.setBounds(10, 180, 60, 20);
        panel.add(lblBusquedaNombre);

        JTextField txtBusquedaNombre = new JTextField();
        txtBusquedaNombre.setBounds(80, 180, 150, 25);
        panel.add(txtBusquedaNombre);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setIcon(cargarIcono("/imagenes/Lupa de buscar logo.png", 20, 20));
        btnBuscar.setBounds(240, 180, 100, 30);
        panel.add(btnBuscar);

        JButton btnRestaurar = new JButton("⟳");
        btnRestaurar.setBounds(350, 180, 50, 30);
        panel.add(btnRestaurar);

        JButton btnReporte = new JButton("Reporte");
        btnReporte.setIcon(cargarIcono("/imagenes/Reporte logo.png", 20, 20));
        btnReporte.setBounds(410, 180, 100, 30);
        panel.add(btnReporte);

        // Tabla de medicamentos
        JLabel lblListado = new JLabel("Listado");
        lblListado.setFont(new Font("Arial", Font.BOLD, 12));
        lblListado.setBounds(10, 230, 100, 20);
        panel.add(lblListado);

        String[] columnas = {"Código", "Nombre", "Presentación"};
        DefaultTableModel modeloTabla = new DefaultTableModel(columnas, 0);
        JTable tabla = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBounds(10, 260, 650, 300);
        panel.add(scrollPane);

        // Cargar medicamentos desde XML
        for (Medicamento med : medicamentos) {
            modeloTabla.addRow(new Object[]{med.getCodigo(), med.getNombre(), med.getPresentacion()});
        }

        // Funcionalidades
        btnGuardar.addActionListener(e -> {
            String codigo = txtCodigo.getText().trim();
            String nombre = txtNombre.getText().trim();
            String presentacion = txtPresentacion.getText().trim();

            if (codigo.isEmpty() || nombre.isEmpty() || presentacion.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe llenar todos los campos");
                return;
            }

            Medicamento med = new Medicamento(codigo, nombre, presentacion);
            medicamentos.add(med);

            modeloTabla.addRow(new Object[]{med.getCodigo(), med.getNombre(), med.getPresentacion()});

            XmlManager.guardarMedicamentos(medicamentos, "medicamentos.xml");

            JOptionPane.showMessageDialog(this, "✅ Medicamento guardado con éxito");
            txtCodigo.setText("");
            txtNombre.setText("");
            txtPresentacion.setText("");
        });

        btnLimpiar.addActionListener(e -> {
            txtCodigo.setText("");
            txtNombre.setText("");
            txtPresentacion.setText("");
        });

        btnBorrar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un medicamento en la tabla para borrar");
                return;
            }

            String codigo = (String) modeloTabla.getValueAt(fila, 0);
            medicamentos.removeIf(m -> m.getCodigo().equals(codigo));
            modeloTabla.removeRow(fila);

            XmlManager.guardarMedicamentos(medicamentos, "medicamentos.xml");

            JOptionPane.showMessageDialog(this, "✅ Medicamento eliminado");
        });

        btnBuscar.addActionListener(e -> {
            String nombreBuscar = txtBusquedaNombre.getText().trim().toLowerCase();
            if (nombreBuscar.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese un nombre para buscar");
                return;
            }

            modeloTabla.setRowCount(0);

            for (Medicamento m : medicamentos) {
                if (m.getNombre().toLowerCase().contains(nombreBuscar)) {
                    modeloTabla.addRow(new Object[]{m.getCodigo(), m.getNombre(), m.getPresentacion()});
                }
            }
        });

        btnRestaurar.addActionListener(e -> {
            modeloTabla.setRowCount(0);
            for (Medicamento m : medicamentos) {
                modeloTabla.addRow(new Object[]{m.getCodigo(), m.getNombre(), m.getPresentacion()});
            }
        });

        btnReporte.addActionListener(e -> {
            if (medicamentos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay medicamentos para el reporte");
                return;
            }

            StringBuilder sb = new StringBuilder("=== Reporte de Medicamentos ===\n\n");
            for (Medicamento m : medicamentos) {
                sb.append("Código: ").append(m.getCodigo())
                .append(" | Nombre: ").append(m.getNombre())
                .append(" | Presentación: ").append(m.getPresentacion())
                .append("\n");
            }

            JTextArea area = new JTextArea(sb.toString());
            area.setEditable(false);
            JScrollPane scroll = new JScrollPane(area);
            scroll.setPreferredSize(new Dimension(500, 300));

            JOptionPane.showMessageDialog(this, scroll, "Reporte de Medicamentos", JOptionPane.INFORMATION_MESSAGE);
        });

        return panel;
    }

    private void limpiarCampos() {
        txtId.setText("");
        txtNombre.setText("");
        txtEspecialidad.setText("");
    }

    private ImageIcon cargarIcono(String ruta, int ancho, int alto) {
        URL url = getClass().getResource(ruta);
        if (url == null) {
            System.err.println("No se encontró la imagen: " + ruta);
            return null;
        }
        ImageIcon iconoOriginal = new ImageIcon(url);
        Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        return new ImageIcon(imagenEscalada);
    }
}