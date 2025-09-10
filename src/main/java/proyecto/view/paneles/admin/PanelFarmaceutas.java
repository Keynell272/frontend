package proyecto.view.paneles.admin;

import proyecto.model.Farmaceuta;
import proyecto.control.ControlAdmin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelFarmaceutas extends JPanel {
    private JTextField txtIdFarm, txtNombreFarm, txtBusquedaNombreFarm;
    private JTable tablaFarm;
    private DefaultTableModel modeloTablaFarm;
    private List<Farmaceuta> farmaceutas;
    private ControlAdmin controlAdmin;

    public PanelFarmaceutas(List<Farmaceuta> farmaceutas, ControlAdmin controlAdmin) {
        this.farmaceutas = farmaceutas;
        this.controlAdmin = controlAdmin;
        init();
    }

    private void init() {
        setLayout(null);

        JLabel lblFarm = new JLabel("Farmacéuta");
        lblFarm.setFont(new Font("Arial", Font.BOLD, 12));
        lblFarm.setBounds(10, 10, 100, 20);
        add(lblFarm);

        JLabel lblId = new JLabel("Id");
        lblId.setBounds(10, 40, 50, 20);
        add(lblId);

        txtIdFarm = new JTextField();
        txtIdFarm.setBounds(100, 40, 150, 25);
        add(txtIdFarm);

        JLabel lblNombre = new JLabel("Nombre");
        lblNombre.setBounds(10, 70, 50, 20);
        add(lblNombre);

        txtNombreFarm = new JTextField();
        txtNombreFarm.setBounds(100, 70, 150, 25);
        add(txtNombreFarm);

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setBounds(320, 40, 100, 30);
        btnGuardar.setIcon(cargarIcono("/imagenes/Guardar logo.png", 20, 20));
        add(btnGuardar);

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBounds(430, 40, 100, 30);
        btnLimpiar.setIcon(cargarIcono("/imagenes/Limpiar logo.png", 20, 20));
        add(btnLimpiar);

        JButton btnBorrar = new JButton("Borrar");
        btnBorrar.setBounds(540, 40, 100, 30);
        btnBorrar.setIcon(cargarIcono("/imagenes/X logo.png", 20, 20));
        add(btnBorrar);

        JLabel lblBusqueda = new JLabel("Búsqueda");
        lblBusqueda.setFont(new Font("Arial", Font.BOLD, 12));
        lblBusqueda.setBounds(10, 150, 100, 20);
        add(lblBusqueda);

        JLabel lblBusquedaNombre = new JLabel("Nombre");
        lblBusquedaNombre.setBounds(10, 180, 60, 20);
        add(lblBusquedaNombre);

        txtBusquedaNombreFarm = new JTextField();
        txtBusquedaNombreFarm.setBounds(80, 180, 150, 25);
        add(txtBusquedaNombreFarm);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(240, 180, 100, 30);
        btnBuscar.setIcon(cargarIcono("/imagenes/Lupa de buscar logo.png", 20, 20));
        add(btnBuscar);

        JButton btnRestaurar = new JButton("⟳");
        btnRestaurar.setBounds(350, 180, 50, 30);
        add(btnRestaurar);

        JButton btnReporte = new JButton("Reporte");
        btnReporte.setBounds(410, 180, 100, 30);
        btnReporte.setIcon(cargarIcono("/imagenes/Reporte logo.png", 20, 20));
        add(btnReporte);

        JLabel lblListado = new JLabel("Listado");
        lblListado.setFont(new Font("Arial", Font.BOLD, 12));
        lblListado.setBounds(10, 230, 100, 20);
        add(lblListado);

        String[] columnas = {"Id", "Nombre"};
        modeloTablaFarm = new DefaultTableModel(columnas, 0);
        tablaFarm = new JTable(modeloTablaFarm);
        JScrollPane scrollPane = new JScrollPane(tablaFarm);
        scrollPane.setBounds(10, 260, 650, 300);
        add(scrollPane);

        // === Bordes estéticos ===
        txtIdFarm.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        txtNombreFarm.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        txtBusquedaNombreFarm.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        btnGuardar.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        btnLimpiar.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        btnBorrar.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        btnBuscar.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        btnRestaurar.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        btnReporte.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));

        // cargar lista inicial
        for (Farmaceuta f : farmaceutas) {
            modeloTablaFarm.addRow(new Object[]{f.getId(), f.getNombre()});
        }

        // === Listeners ===
        btnGuardar.addActionListener(e -> guardarFarmaceuta());
        btnBorrar.addActionListener(e -> borrarFarmaceuta());
        btnBuscar.addActionListener(e -> buscarFarmaceuta());
        btnRestaurar.addActionListener(e -> restaurarTabla());
        btnReporte.addActionListener(e -> mostrarReporte());
        btnLimpiar.addActionListener(e -> limpiarCampos());
    }

    private void guardarFarmaceuta() {
        String id = txtIdFarm.getText().trim();
        if (!controlAdmin.esIdUnico(id)) {
            JOptionPane.showMessageDialog(this, "El ID ya existe. Ingrese uno diferente.");
            return;
        }
        String nombre = txtNombreFarm.getText().trim();
        if (id.isEmpty() || nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe llenar todos los campos");
            return;
        }

        Farmaceuta f = new Farmaceuta(id, "123", nombre);
        controlAdmin.agregarFarmaceuta(f);
        modeloTablaFarm.addRow(new Object[]{f.getId(), f.getNombre()});
        controlAdmin.guardarFarmaceutas();

        JOptionPane.showMessageDialog(this, "✅ Farmacéuta guardado con éxito");
        limpiarCampos();
    }

    private void borrarFarmaceuta() {
        int fila = tablaFarm.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un farmacéuta en la tabla para borrar");
            return;
        }

        String id = (String) modeloTablaFarm.getValueAt(fila, 0);
        controlAdmin.eliminarFarmaceuta(id);
        modeloTablaFarm.removeRow(fila);
        controlAdmin.guardarFarmaceutas();

        JOptionPane.showMessageDialog(this, "✅ Farmacéuta eliminado");
    }

    private void buscarFarmaceuta() {
        String buscar = txtBusquedaNombreFarm.getText().trim().toLowerCase();
        modeloTablaFarm.setRowCount(0);
        for (Farmaceuta f : farmaceutas) {
            if (f.getNombre().toLowerCase().contains(buscar)) {
                modeloTablaFarm.addRow(new Object[]{f.getId(), f.getNombre()});
            }
        }
    }

    private void restaurarTabla() {
        modeloTablaFarm.setRowCount(0);
        for (Farmaceuta f : farmaceutas) {
            modeloTablaFarm.addRow(new Object[]{f.getId(), f.getNombre()});
        }
    }

    private void mostrarReporte() {
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
    }

    private void limpiarCampos() {
        txtIdFarm.setText("");
        txtNombreFarm.setText("");
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
}
