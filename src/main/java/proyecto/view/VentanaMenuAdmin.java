package proyecto.view;

import proyecto.model.Medico;
import proyecto.model.Usuario;
import proyecto.view.paneles.generales.DashboardPanel;
import proyecto.view.paneles.generales.PanelHistorico;
import proyecto.view.paneles.generales.PanelAcercaDe;
import proyecto.view.paneles.admin.PanelMedicos;
import proyecto.view.paneles.admin.PanelFarmaceutas;
import proyecto.view.paneles.admin.PanelPacientes;
import proyecto.view.paneles.admin.PanelMedicamentos;
import proyecto.model.Farmaceuta;
import proyecto.model.Medicamento;
import proyecto.model.Paciente;
import proyecto.control.ControlAdmin;
import proyecto.control.ControlReceta;
import proyecto.model.Receta;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.List;

public class VentanaMenuAdmin extends JFrame {
    private Usuario usuarioLogueado;

    private ControlAdmin controlAdmin;
    private ControlReceta controlReceta;

    private List<Medico> medicos;
    private List<Farmaceuta> farmaceutas;
    private List<Paciente> pacientes;
    private List<Medicamento> medicamentos;
    private List<Receta> recetas;

    public VentanaMenuAdmin(Usuario usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;

        this.controlAdmin = new ControlAdmin();
        this.controlReceta = new ControlReceta();

        this.medicos = controlAdmin.getMedicos();
        this.farmaceutas = controlAdmin.getFarmaceutas();
        this.pacientes = controlAdmin.getPacientes();
        this.medicamentos = controlAdmin.getMedicamentos();
        this.recetas = controlReceta.getRecetas();

        init();
    }

    private void init() {
        setTitle(usuarioLogueado.getNombre() + " (" + usuarioLogueado.getRol() + ")");
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

        add(tabbedPane, BorderLayout.CENTER);
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