package view.paneles.generales;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import control.ControlDashboard;
import model.Medicamento;
import model.Receta;
import java.awt.*;
import java.util.*;
import java.util.List;

public class DashboardPanel extends JPanel {

    private final List<Receta> recetas;
    private final List<Medicamento> medicamentos;
    private final ControlDashboard control;

    private JSpinner spAnioDesde, spAnioHasta;
    private JComboBox<String> cbMesDesde, cbMesHasta;
    private JComboBox<String> cbMedicamentos;
    private DefaultListModel<String> modeloSeleccion;
    private JList<String> listaSeleccion;
    private DefaultTableModel modeloTabla;
    private PanelLineas graficoLineas;
    private PanelPastel graficoPastel;

    public DashboardPanel() {
        this.control = new ControlDashboard();
        this.recetas = control.getRecetas();
        this.medicamentos = control.getMedicamentos();
        
        initUI();
        cargarInicial();
    }

    private void initUI() {
        setLayout(new BorderLayout(8,8));
        setBackground(new Color(235,235,238));

        JPanel izq = new JPanel();
        izq.setBackground(new Color(245,245,247));
        izq.setPreferredSize(new Dimension(230, 420));
        izq.setLayout(new BorderLayout(8,8));
        izq.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(8,8,8,8),
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(180,180,180)), "Datos",
                        TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 12))
        ));

        JPanel filtros = new JPanel(null);
        filtros.setPreferredSize(new Dimension(210, 240));
        filtros.setBackground(new Color(245,245,247));

        int y = 10;
        JLabel lDesde = new JLabel("Desde");
        lDesde.setBounds(10, y, 80, 20);
        filtros.add(lDesde);

        spAnioDesde = new JSpinner(new SpinnerNumberModel(2025, 1990, 2100, 1));
        spAnioDesde.setBounds(60, y, 60, 22);
        filtros.add(spAnioDesde);

        cbMesDesde = new JComboBox<>(meses());
        cbMesDesde.setSelectedIndex(7);
        cbMesDesde.setBounds(125, y, 80, 22);
        filtros.add(cbMesDesde);

        y += 34;
        JLabel lHasta = new JLabel("Hasta");
        lHasta.setBounds(10, y, 80, 20);
        filtros.add(lHasta);

        spAnioHasta = new JSpinner(new SpinnerNumberModel(2025, 1990, 2100, 1));
        spAnioHasta.setBounds(60, y, 60, 22);
        filtros.add(spAnioHasta);

        cbMesHasta = new JComboBox<>(meses());
        cbMesHasta.setSelectedIndex(9);
        cbMesHasta.setBounds(125, y, 80, 22);
        filtros.add(cbMesHasta);

        y += 34;
        JLabel lMedic = new JLabel("Medicamentos");
        lMedic.setBounds(10, y, 110, 20);
        filtros.add(lMedic);

        cbMedicamentos = new JComboBox<>(nombresMedicamentos());
        cbMedicamentos.setBounds(10, y + 22, 195, 22);
        filtros.add(cbMedicamentos);

        JButton btnAgregar = new JButton(icono("/imagenes/Check logo.png", 18, 18));
        btnAgregar.setToolTipText("Agregar a selección");
        btnAgregar.setBounds(10, y + 50, 30, 26);
        filtros.add(btnAgregar);

        JButton btnQuitar = new JButton(icono("/imagenes/Borrar logo.png", 18, 18));
        btnQuitar.setToolTipText("Quitar de selección");
        btnQuitar.setBounds(45, y + 50, 30, 26);
        filtros.add(btnQuitar);

        modeloSeleccion = new DefaultListModel<>();
        listaSeleccion = new JList<>(modeloSeleccion);
        JScrollPane spSel = new JScrollPane(listaSeleccion);
        spSel.setBounds(10, y + 80, 195, 70);
        filtros.add(spSel);

        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBackground(new Color(245,245,247));
        panelTabla.setBorder(BorderFactory.createTitledBorder("Resumen"));
        String[] col = {"Medic.", "Mes1", "Mes2", "Mes3"};
        modeloTabla = new DefaultTableModel(col, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(modeloTabla);
        tabla.setRowHeight(20);
        panelTabla.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel panelBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 4));
        panelBtns.setBackground(new Color(245,245,247));
        JButton btnAplicar = new JButton(icono("/imagenes/Doble check.png", 20, 20));
        btnAplicar.setToolTipText("Aplicar filtros");
        JButton btnLimpiar = new JButton(icono("/imagenes/Limpiar logo.png", 20, 20));
        btnLimpiar.setToolTipText("Limpiar");
        JButton btnActualizar = new JButton("🔄");
        btnActualizar.setToolTipText("Actualizar datos");
        panelBtns.add(btnAplicar);
        panelBtns.add(btnLimpiar);
        panelBtns.add(btnActualizar);

        izq.add(filtros, BorderLayout.NORTH);
        izq.add(panelTabla, BorderLayout.CENTER);
        izq.add(panelBtns, BorderLayout.SOUTH);

        graficoLineas = new PanelLineas();
        graficoLineas.setBorder(BorderFactory.createTitledBorder("Medicamentos"));
        graficoLineas.setPreferredSize(new Dimension(50, 150));
        graficoPastel = new PanelPastel();
        graficoPastel.setBorder(BorderFactory.createTitledBorder("Recetas"));
        graficoPastel.setPreferredSize(new Dimension(50, 150));

        JPanel centro = new JPanel(new GridLayout(1,2,8,8));
        centro.add(graficoLineas);
        centro.add(graficoPastel);

        add(izq, BorderLayout.WEST);
        add(centro, BorderLayout.CENTER);

        btnAgregar.addActionListener(e -> {
            String sel = (String) cbMedicamentos.getSelectedItem();
            if (sel != null && !modeloSeleccion.contains(sel)) modeloSeleccion.addElement(sel);
        });
        
        btnQuitar.addActionListener(e -> {
            List<String> seleccion = listaSeleccion.getSelectedValuesList();
            for (String s : seleccion) modeloSeleccion.removeElement(s);
        });
        
        btnLimpiar.addActionListener(e -> {
            modeloSeleccion.clear();
            modeloTabla.setRowCount(0);
            actualizarDatosYGraficos();
        });
        
        btnAplicar.addActionListener(e -> actualizarGraficos());
        
        btnActualizar.addActionListener(e -> actualizarDatosYGraficos());
    }

    private void cargarInicial() {
        Set<String> defaults = new LinkedHashSet<>(Arrays.asList("Acetaminofen", "Amoxicilina", "Acetaminofén"));
        for (int i = 0; i < cbMedicamentos.getItemCount() && modeloSeleccion.size() < 2; i++) {
            String nombre = cbMedicamentos.getItemAt(i);
            if (defaults.contains(nombre) || modeloSeleccion.isEmpty())
                modeloSeleccion.addElement(nombre);
        }
        actualizarGraficos();
    }

    private void actualizarDatosYGraficos() {
        control.actualizarDatos();
        
        cbMedicamentos.removeAllItems();
        for (String nombre : nombresMedicamentos()) {
            cbMedicamentos.addItem(nombre);
        }
        
        actualizarGraficos();
        JOptionPane.showMessageDialog(this, "✅ Datos actualizados");
    }

    private void actualizarGraficos() {
        int anio = getAnioDesde();
        List<Integer> meses = mesesRango();

        Map<String, Map<Integer, Integer>> series = new LinkedHashMap<>();
        if (!meses.isEmpty()) {
            for (int i = 0; i < modeloSeleccion.size(); i++) {
                String med = modeloSeleccion.get(i);
                Map<Integer, Integer> serie = control.medicamentoPorMes(
                        med, anio, meses.get(0), meses.get(meses.size()-1));
                series.put(med, serie);
            }
        }
        graficoLineas.setDatos(anio, meses, series);

        modeloTabla.setRowCount(0);
        for (Map.Entry<String, Map<Integer, Integer>> e : series.entrySet()) {
            String med = e.getKey();
            Map<Integer,Integer> mapa = e.getValue();
            Object[] fila = new Object[1 + meses.size()];
            fila[0] = med;
            for (int i = 0; i < meses.size(); i++) fila[1 + i] = mapa.getOrDefault(meses.get(i), 0);
            modeloTabla.addRow(fila);
        }

        graficoPastel.setDatos(control.recetasPorEstado());
        graficoLineas.revalidate();
        graficoLineas.repaint();
        graficoPastel.revalidate();
        graficoPastel.repaint();
    }

    private int getAnioDesde() { return ((Number) spAnioDesde.getValue()).intValue(); }
    private int getMesDesde() { return cbMesDesde.getSelectedIndex() + 1; }
    private int getAnioHasta() { return ((Number) spAnioHasta.getValue()).intValue(); }
    private int getMesHasta() { return cbMesHasta.getSelectedIndex() + 1; }

    private List<Integer> mesesRango() {
        int ai = getAnioDesde(), af = getAnioHasta();
        int mi = getMesDesde(), mf = getMesHasta();
        List<Integer> meses = new ArrayList<>();
        if (ai != af) af = ai;
        for (int m = mi; m <= mf; m++) meses.add(m);
        return meses;
    }

    private String[] meses() {
        return new String[]{"1-Enero","2-Febrero","3-Marzo","4-Abril","5-Mayo","6-Junio",
                "7-Julio","8-Agosto","9-Septiembre","10-Octubre","11-Noviembre","12-Diciembre"};
    }
    
    private String[] nombresMedicamentos() {
        List<String> n = new ArrayList<>();
        for (Medicamento m : control.getMedicamentos()) n.add(m.getNombre());
        return n.toArray(new String[0]);
    }
    
    private ImageIcon icono(String ruta, int w, int h) {
        java.net.URL url = getClass().getResource(ruta);
        if (url == null) return new ImageIcon(new java.awt.image.BufferedImage(
                w,h,java.awt.image.BufferedImage.TYPE_INT_ARGB));
        Image img = new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
}