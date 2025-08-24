package proyecto.view;

import proyecto.control.ControlAdmin;
import proyecto.model.Farmaceuta;
import proyecto.model.Medicamento;
import proyecto.model.Medico;
import proyecto.model.Paciente;
import proyecto.model.Usuario;
import proyecto.persistencia.XmlManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URL;
import java.util.List;

public class VentanaMenuAdmin extends JFrame {
    private Usuario usuarioLogueado;
    private ControlAdmin controlAdmin;

    public VentanaMenuAdmin(Usuario usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;

        // Cargar listas desde XML
        List<Medico> medicos = XmlManager.cargarMedicos("medicos.xml");
        List<Farmaceuta> farmaceutas = XmlManager.cargarUsuarios("usuarios.xml").stream()
                .filter(u -> u instanceof Farmaceuta).map(u -> (Farmaceuta) u).toList();
        List<Paciente> pacientes = XmlManager.cargarPacientes("pacientes.xml");
        List<Medicamento> medicamentos = XmlManager.cargarMedicamentos("medicamentos.xml");

        // Crear instancia de ControlAdmin
        this.controlAdmin = new ControlAdmin(medicos, farmaceutas, pacientes, medicamentos);

        init();
    }

    private void init() {
        setTitle("Recetas - " + usuarioLogueado.getNombre() + " (" + usuarioLogueado.getRol() + ")");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel lblIcono = new JLabel();
        lblIcono.setHorizontalAlignment(SwingConstants.CENTER);
        setIconImage(new ImageIcon(getClass().getResource("/imagenes/Recetas logo.png")).getImage());


        JTabbedPane tabbedPane = new JTabbedPane();

        // Cargar iconos para cada pestaña (24x24)
        ImageIcon iconMedicos = cargarIcono("/imagenes/medico logo.png", 24, 24);
        ImageIcon iconFarmaceutas = cargarIcono("/imagenes/farmaceuta logo.png", 24, 24);
        ImageIcon iconPacientes = cargarIcono("/imagenes/pacientes logo.png", 24, 24);
        ImageIcon iconMedicamentos = cargarIcono("/imagenes/medicamentos logos.png", 24, 24);
        ImageIcon iconDashboard = cargarIcono("/imagenes/dashbord logo.png", 24, 24);
        ImageIcon iconHistorico = cargarIcono("/imagenes/historico logo.png", 24, 24);
        ImageIcon iconAcerca = cargarIcono("/imagenes/Acerca de logo.png", 24, 24);

        // Panel para Médicos con contenido complejo
        JPanel panelMedicos = crearPanelMedicos();

        // Paneles simples para las otras pestañas
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

        // Agregar pestañas con título e icono
        tabbedPane.addTab("Médicos", iconMedicos, panelMedicos);
        tabbedPane.addTab("Farmaceutas", iconFarmaceutas, panelFarmaceutas);
        tabbedPane.addTab("Pacientes", iconPacientes, panelPacientes);
        tabbedPane.addTab("Medicamentos", iconMedicamentos, panelMedicamentos);
        tabbedPane.addTab("Dashboard", iconDashboard, panelDashboard);
        tabbedPane.addTab("Histórico", iconHistorico, panelHistorico);
        tabbedPane.addTab("Acerca de...", iconAcerca, panelAcerca);

        add(tabbedPane, BorderLayout.CENTER);
    }

    // Método para crear el panel de Médicos con contenido detallado
    private JPanel crearPanelMedicos() {
        JPanel panel = new JPanel();
        panel.setLayout(null); // Usamos null layout para posicionar manualmente como en la imagen

        // Etiquetas y campos
        JLabel lblMedico = new JLabel("Médico");
        lblMedico.setFont(new Font("Arial", Font.BOLD, 12));
        lblMedico.setBounds(10, 10, 100, 20);
        panel.add(lblMedico);

        JLabel lblId = new JLabel("Id");
        lblId.setBounds(10, 40, 50, 20);
        panel.add(lblId);

        JTextField txtId = new JTextField("MED-111");
        txtId.setBounds(100, 40, 150, 25);
        panel.add(txtId);

        JLabel lblNombre = new JLabel("Nombre");
        lblNombre.setBounds(10, 70, 50, 20);
        panel.add(lblNombre);

        JTextField txtNombre = new JTextField("David");
        txtNombre.setBounds(100, 70, 150, 25);
        panel.add(txtNombre);

        JLabel lblEspecialidad = new JLabel("Especialidad");
        lblEspecialidad.setBounds(10, 100, 100, 20);
        panel.add(lblEspecialidad);

        JTextField txtEspecialidad = new JTextField("Pediatría");
        txtEspecialidad.setBounds(100, 100, 150, 25);
        panel.add(txtEspecialidad);

        // Botones Guardar, Limpiar, Borrar con iconos
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setBounds(320, 40, 100, 30);
        btnGuardar.setIcon(cargarIcono("/imagenes/Guardar logo.png", 20, 20));
        panel.add(btnGuardar);
        
        btnGuardar.addActionListener(e -> {
            String id = txtId.getText().trim();
            String nombre = txtNombre.getText().trim();
            String especialidad = txtEspecialidad.getText().trim();

            Medico nuevoMedico = new Medico(id, "default", nombre, especialidad);
            controlAdmin.agregarMedico(nuevoMedico);

            JOptionPane.showMessageDialog(panel, "Médico guardado correctamente.");

        });

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBounds(430, 40, 100, 30);
        btnLimpiar.setIcon(cargarIcono("/imagenes/Limpiar logo.png", 20, 20));
        panel.add(btnLimpiar);

        btnLimpiar.addActionListener(e -> {
            txtId.setText("");
            txtNombre.setText("");
            txtEspecialidad.setText("");
        });

        JButton btnBorrar = new JButton("Borrar");
        btnBorrar.setBounds(540, 40, 100, 30);
        btnBorrar.setIcon(cargarIcono("/imagenes/X logo.png", 20, 20));
        panel.add(btnBorrar);

        // Sección Búsqueda
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

        // Sección Listado (Tabla)
        JLabel lblListado = new JLabel("Listado");
        lblListado.setFont(new Font("Arial", Font.BOLD, 12));
        lblListado.setBounds(10, 230, 100, 20);
        panel.add(lblListado);

        String[] columnas = {"Id", "Nombre", "Especialidad"};
        Object[][] datos = {
                {"MED-111", "David", "Pediatría"},
                {"MED-222", "Miguel", "Neurocirugía"}
        };

        DefaultTableModel modeloTabla = new DefaultTableModel(datos, columnas);
        JTable tabla = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBounds(10, 260, 650, 300);
        panel.add(scrollPane);

        return panel;
    }

    // Método para cargar iconos desde resources y escalar
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