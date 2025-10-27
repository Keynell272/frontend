package view;

import javax.swing.*;

import control.ControlAdmin;
import control.ControlLogin;
import control.ControlReceta;
import model.Farmaceuta;
import model.Medicamento;
import model.Medico;
import model.Paciente;
import model.Receta;
import model.Usuario;
import view.paneles.admin.PanelFarmaceutas;
import view.paneles.admin.PanelMedicamentos;
import view.paneles.admin.PanelMedicos;
import view.paneles.admin.PanelPacientes;
import view.paneles.generales.DashboardPanel;
import view.paneles.generales.PanelAcercaDe;
import view.paneles.generales.PanelHistorico;
import view.paneles.generales.PanelUsuariosActivos;

import java.awt.*;
import java.net.URL;
import java.util.List;

public class VentanaMenuAdmin extends JFrame {
    private Usuario usuarioLogueado;
    private PanelUsuariosActivos panelUsuariosActivos;
    
    private ControlAdmin controlAdmin;
    private ControlReceta controlReceta;
    private ControlLogin controlLogin;

    private List<Medico> medicos;
    private List<Farmaceuta> farmaceutas;
    private List<Paciente> pacientes;
    private List<Medicamento> medicamentos;
    private List<Receta> recetas;

    public VentanaMenuAdmin(Usuario usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;

        this.controlAdmin = new ControlAdmin();
        this.controlReceta = new ControlReceta();
        this.controlLogin = new ControlLogin();

        this.medicos = controlAdmin.getMedicos();
        this.farmaceutas = controlAdmin.getFarmaceutas();
        this.pacientes = controlAdmin.getPacientes();
        this.medicamentos = controlAdmin.getMedicamentos();
        this.recetas = controlReceta.getRecetas();

        init();
    }

    private void init() {
        setTitle(usuarioLogueado.getNombre() + " (" + usuarioLogueado.getRol() + ")");
        setSize(1150, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setIconImage(new ImageIcon(getClass().getResource("/imagenes/Recetas logo.png")).getImage());
        
        // Panel principal con BorderLayout
        JPanel panelPrincipal = new JPanel(new BorderLayout());

        // Panel superior con logout
        JPanel panelSuperior = crearPanelSuperior();
        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Crear paneles
        JPanel panelMedicos = crearPanelMedicos();
        JPanel panelFarmaceutas = crearPanelFarmaceutas();
        JPanel panelPacientes = crearPanelPacientes();
        JPanel panelMedicamentos = crearPanelMedicamentos();
        JPanel panelDashboard = crearPanelDashboard();
        JPanel panelHistorico = crearPanelHistorico();
        JPanel panelAcerca = crearPanelAcercaDe();

        // Tabs
        tabbedPane.addTab("Médicos", cargarIcono("/imagenes/medico logo.png", 24, 24), panelMedicos);
        tabbedPane.addTab("Farmaceutas", cargarIcono("/imagenes/farmaceuta logo.png", 24, 24), panelFarmaceutas);
        tabbedPane.addTab("Pacientes", cargarIcono("/imagenes/pacientes logo.png", 24, 24), panelPacientes);
        tabbedPane.addTab("Medicamentos", cargarIcono("/imagenes/medicamentos logos.png", 24, 24), panelMedicamentos);
        tabbedPane.addTab("Dashboard", cargarIcono("/imagenes/dashbord logo.png", 24, 24), panelDashboard);
        tabbedPane.addTab("Histórico", cargarIcono("/imagenes/historico logo.png", 24, 24), panelHistorico);
        tabbedPane.addTab("Acerca de...", cargarIcono("/imagenes/Acerca de logo.png", 24, 24), panelAcerca);

        // Panel central con tabs y usuarios activos
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.add(tabbedPane, BorderLayout.CENTER);
        
        // Panel de usuarios activos al lado derecho
        panelUsuariosActivos = new PanelUsuariosActivos(usuarioLogueado);
        panelCentral.add(panelUsuariosActivos, BorderLayout.EAST);
        
        panelPrincipal.add(panelCentral, BorderLayout.CENTER);
        add(panelPrincipal);
        
        // Listener para cerrar sesión al cerrar la ventana
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                cerrarSesion();
            }
        });
    }

    // ===================== PANEL SUPERIOR CON LOGOUT =====================
    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));

        JLabel lblUsuario = new JLabel("Usuario: " + usuarioLogueado.getNombre());
        lblUsuario.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblUsuario);

        JButton btnLogout = new JButton("Cerrar Sesión", cargarIcono("/imagenes/X logo.png", 16, 16));
        btnLogout.setFont(new Font("Arial", Font.PLAIN, 11));
        btnLogout.setBackground(new Color(220, 53, 69));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.addActionListener(e -> cerrarSesion());
        
        panel.add(btnLogout);

        return panel;
    }

    // ===================== CERRAR SESIÓN =====================
    private void cerrarSesion() {
        int opcion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro que desea cerrar sesión?",
            "Confirmar cierre de sesión",
            JOptionPane.YES_NO_OPTION);
        
        if (opcion == JOptionPane.YES_OPTION) {
            controlLogin.logout(usuarioLogueado.getId());
            dispose();
            new VentanaLogin().setVisible(true);
        }
    }

    // ===================== PANELS =====================
    private JPanel crearPanelMedicos() {
        return new PanelMedicos(medicos, controlAdmin);
    }

    private JPanel crearPanelFarmaceutas() {
        return new PanelFarmaceutas(farmaceutas, controlAdmin);
    }

    private JPanel crearPanelPacientes() {
        return new PanelPacientes(pacientes, controlAdmin);
    }

    private JPanel crearPanelMedicamentos() {
        return new PanelMedicamentos(medicamentos, controlAdmin);
    }
    
    private JPanel crearPanelDashboard() {
        return new DashboardPanel();
    }
    
    private JPanel crearPanelHistorico() {
        return new PanelHistorico(controlReceta); 
    }
    
    private JPanel crearPanelAcercaDe() {
        return new PanelAcercaDe();
    }

    // ===================== UTILS =====================
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