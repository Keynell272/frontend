package proyecto.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import proyecto.view.DashboardPanel;
import proyecto.model.*;
import proyecto.control.*;

public class VentanaMenuFarmaceuta extends JFrame {
    private Farmaceuta farmaceutaLogueado;
    private ControlReceta controlReceta;
    private ControlAdmin controlAdmin;
    private List<Receta> recetas;
    private List<Medicamento> medicamentos;
    private JTable tablaRecetas;
    private DefaultTableModel modeloTabla;

    public VentanaMenuFarmaceuta(Farmaceuta farmaceutaLogueado, ControlReceta controlReceta) {
        this.farmaceutaLogueado = farmaceutaLogueado;
        this.controlReceta = controlReceta;
        this.controlAdmin = new ControlAdmin();
        this.recetas = controlReceta.getRecetas();
        this.medicamentos = controlAdmin.getMedicamentos();
        init();
    }

    private void init() {
        setTitle("Recetas - " + farmaceutaLogueado.getId() + " (FARM)");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setIconImage(cargarIcono("/imagenes/farmaceuta logo.png", 32, 32).getImage());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Administrar Recetas", cargarIcono("/imagenes/medicamentos logos.png", 20, 20), crearPanelFarmaceuta());
        tabbedPane.addTab("Dashboard", cargarIcono("/imagenes/dashbord logo.png", 20, 20), new DashboardPanel(recetas, medicamentos));
        tabbedPane.addTab("Histórico", cargarIcono("/imagenes/historico logo.png", 20, 20), crearPanelHistorico());
        tabbedPane.addTab("Acerca de...", cargarIcono("/imagenes/Acerca de logo.png", 20, 20), crearPanelAcercaDe());

        add(tabbedPane);
    }

    // ------------------ PANEL FARMACEUTA ------------------
    private JPanel crearPanelFarmaceuta() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnas = {"ID", "Paciente", "Estado", "Fecha Retiro"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaRecetas = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tablaRecetas);

        JPanel panelBotones = new JPanel(new FlowLayout());
        JButton btnProceso = new JButton("Pasar a Proceso");
        JButton btnLista = new JButton("Marcar como Lista");
        JButton btnEntregar = new JButton("Entregar");
        JButton btnDetalles = new JButton("Detalles");

        panelBotones.add(btnProceso);
        panelBotones.add(btnLista);
        panelBotones.add(btnEntregar);
        panelBotones.add(btnDetalles);

        btnProceso.addActionListener(this::accionPasarProceso);
        btnLista.addActionListener(this::accionMarcarLista);
        btnEntregar.addActionListener(this::accionEntregar);
        btnDetalles.addActionListener(this::accionDetalles);

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(panelBotones, BorderLayout.SOUTH);

        cargarRecetasEnTabla();

        return panel;
    }

    // ------------------ PANEL HISTORICO ------------------
    private JPanel crearPanelHistorico() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnas = {
            "ID", "Paciente", "Estado",
            "Fecha Confección", "Fecha Retiro", "Fecha Proceso", "Fecha Lista", "Fecha Entregada"
        };
        DefaultTableModel modeloHistorico = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable tablaHistorico = new JTable(modeloHistorico);
        JScrollPane scroll = new JScrollPane(tablaHistorico);

        panel.add(scroll, BorderLayout.CENTER);

        // cargar recetas en la tabla
        cargarHistorico(controlReceta.getRecetas(), modeloHistorico);

        return panel;
    }


    // ------------------ PANEL ACERCA DE ------------------
    private JPanel crearPanelAcercaDe() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(230, 233, 237));

        JLabel lblTitulo = new JLabel("Gestión de Recetas (Farmaceuta)", SwingConstants.CENTER);
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

    // ------------------ PANEL DASHBOARD ------------------
    private JPanel crearPanelDashboard() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel lblTitulo = new JLabel("Dashboard - Próximamente", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(Color.BLUE);
        panel.add(lblTitulo, BorderLayout.CENTER);
        return panel;
    }

    // ------------------ ACCIONES ------------------
    private void accionPasarProceso(ActionEvent e) {
        int fila = tablaRecetas.getSelectedRow();
        if (fila != -1) {
            String id = modeloTabla.getValueAt(fila, 0).toString();
            Receta r = buscarRecetaPorId(id);
            if (r != null && "confeccionada".equals(r.getEstado())) {
                r.cambiarEstado("proceso");
                r.setFechaProceso(new java.util.Date());
                controlReceta.guardarCambios("recetas.xml");
                JOptionPane.showMessageDialog(this, "Receta " + id + " pasó a PROCESO.");
                cargarRecetasEnTabla();
            }
        }
    }

    private void accionMarcarLista(ActionEvent e) {
        int fila = tablaRecetas.getSelectedRow();
        if (fila != -1) {
            String id = modeloTabla.getValueAt(fila, 0).toString();
            Receta r = buscarRecetaPorId(id);
            if (r != null && "proceso".equals(r.getEstado())) {
                r.cambiarEstado("lista");
                r.setFechaLista(new java.util.Date());
                controlReceta.guardarCambios("recetas.xml"); 
                JOptionPane.showMessageDialog(this, "Receta " + id + " marcada como LISTA.");
                cargarRecetasEnTabla();
            }
        }
    }

    private void accionEntregar(ActionEvent e) {
        int fila = tablaRecetas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una receta.");
            return;
        }

        String idReceta = modeloTabla.getValueAt(fila, 0).toString();
        Receta receta = controlReceta.buscarPorId(idReceta);

        if (receta != null) {
            String resultado = controlReceta.validarFechaRetiro(receta);

            switch (resultado) {
                case "anticipado":
                    JOptionPane.showMessageDialog(this,
                        "⏳ El paciente se presentó antes de la fecha de retiro: " +
                        new SimpleDateFormat("dd/MM/yyyy").format(receta.getFechaRetiro()) +
                        ". No se permite la entrega.");
                    break;

                case "atrasado":
                    JOptionPane.showMessageDialog(this,
                        "⚠️ El paciente se presentó después de la fecha de retiro: " +
                        new SimpleDateFormat("dd/MM/yyyy").format(receta.getFechaRetiro()) +
                        ". Se entregará fuera de tiempo.");
                    receta.cambiarEstado("entregada");
                    receta.setFechaEntrega(new Date());
                    controlReceta.guardarCambios("recetas.xml");
                    break;

                case "normal":
                    JOptionPane.showMessageDialog(this, "✅ Retiro en fecha correcta.");
                    receta.cambiarEstado("entregada");
                    receta.setFechaEntrega(new Date());
                    controlReceta.guardarCambios("recetas.xml");
                    break;
            }
        }
    }

    private void accionDetalles(ActionEvent e) {
        int fila = tablaRecetas.getSelectedRow();
        if (fila != -1) {
            String id = modeloTabla.getValueAt(fila, 0).toString();
            Receta r = buscarRecetaPorId(id);
            if (r != null) {
                JDialog dialog = new JDialog(this, "Detalles Receta " + id, true);
                dialog.setSize(500, 400);
                dialog.setLocationRelativeTo(this);

                JTextArea txtDetalles = new JTextArea();
                txtDetalles.setEditable(false);
                txtDetalles.append("Paciente: " + r.getPaciente().getNombre() + "\n");
                txtDetalles.append("Estado: " + r.getEstado() + "\n");
                txtDetalles.append("Medicamentos:\n");
                for (DetalleReceta d : r.getDetalles()) {
                    txtDetalles.append("- " + d.getMedicamento().getNombre() +
                            " (" + d.getCantidad() + "): " +
                            d.getIndicaciones() + "\n");
                }
                dialog.add(new JScrollPane(txtDetalles));
                dialog.setVisible(true);
            }
        }
    }

    // ------------------ UTILS ------------------
    private final SimpleDateFormat SDF_DMY = new SimpleDateFormat("dd/MM/yyyy");

    private String formatearFecha(Date d) {
        return (d != null) ? SDF_DMY.format(d) : "";
    }

    private void cargarRecetasEnTabla() {
        modeloTabla.setRowCount(0);
        for (Receta r : controlReceta.getRecetas()) {
            modeloTabla.addRow(new Object[]{
                    r.getId(),
                    r.getPaciente().getNombre(),
                    r.getEstado(),
                    formatearFecha(r.getFechaRetiro())
            });
        }
    }

    private Receta buscarRecetaPorId(String id) {
        return controlReceta.getRecetas().stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    private ImageIcon cargarIcono(String ruta, int ancho, int alto) {
        java.net.URL location = getClass().getResource(ruta);
        if (location == null) return null;
        ImageIcon icono = new ImageIcon(location);
        Image img = icono.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private void cargarHistorico(List<Receta> recetas, DefaultTableModel modelo) {
        modelo.setRowCount(0); // limpiar tabla
        for (Receta r : recetas) {
            modelo.addRow(new Object[]{
                r.getId(),
                (r.getPaciente() != null ? r.getPaciente().getNombre() : ""),
                r.getEstado(),
                formatearFecha(r.getFechaConfeccion()),
                formatearFecha(r.getFechaRetiro()),
                formatearFecha(r.getFechaProceso()),
                formatearFecha(r.getFechaLista()),
                formatearFecha(r.getFechaEntrega())
            });
        }
}

}
