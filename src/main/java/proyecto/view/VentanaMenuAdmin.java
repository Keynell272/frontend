package proyecto.view;

import proyecto.model.Medico;
import proyecto.model.Usuario;
import proyecto.persistencia.XmlManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class VentanaMenuAdmin extends JFrame {
    private Usuario usuarioLogueado;
    private DefaultTableModel modeloTabla;
    private List<Medico> medicos;
    private JTable tabla;

    // Campos de texto (para usarlos en guardar/limpiar)
    private JTextField txtId, txtNombre, txtEspecialidad;

    public VentanaMenuAdmin(Usuario usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;
        this.medicos = XmlManager.cargarMedicos("medicos.xml"); // 🔹 cargar médicos desde XML
        if (this.medicos == null) {
            this.medicos = new ArrayList<>();
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
        JPanel panelFarmaceutas = new JPanel();
        panelFarmaceutas.add(new JLabel("Farmaceutas"));

        JPanel panelPacientes = new JPanel();
        panelPacientes.add(new JLabel("Pacientes"));

        JPanel panelMedicamentos = new JPanel();
        panelMedicamentos.add(new JLabel("Medicamentos"));

        JPanel panelDashboard = new JPanel();
        panelDashboard.add(new JLabel("Dashboard"));

        JPanel panelHistorico = new JPanel();
        panelHistorico.add(new JLabel("Histórico"));

        JPanel panelAcerca = new JPanel();
        panelAcerca.add(new JLabel("Acerca de..."));

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

        JTextField txtBusquedaNombre = new JTextField();
        txtBusquedaNombre.setBounds(80, 180, 150, 25);
        panel.add(txtBusquedaNombre);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(240, 180, 100, 30);
        btnBuscar.setIcon(cargarIcono("/imagenes/Lupa de buscar logo.png", 20, 20));
        panel.add(btnBuscar);

        JButton btnReporte = new JButton("Reporte");
        btnReporte.setBounds(350, 180, 100, 30);
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
