package proyecto.view.paneles.admin;

import proyecto.model.Medicamento;
import proyecto.control.ControlAdmin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelMedicamentos extends JPanel {
    private JTextField txtCodigo, txtNombre, txtPresentacion, txtBusquedaNombre;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private List<Medicamento> medicamentos;
    private ControlAdmin controlAdmin;

    public PanelMedicamentos(List<Medicamento> medicamentos, ControlAdmin controlAdmin) {
        this.medicamentos = medicamentos;
        this.controlAdmin = controlAdmin;
        init();
    }

    private void init() {
        setLayout(null);

        JLabel lblTitulo = new JLabel("Medicamento");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 12));
        lblTitulo.setBounds(10, 10, 100, 20);
        add(lblTitulo);

        JLabel lblCodigo = new JLabel("Código");
        lblCodigo.setBounds(10, 40, 50, 20);
        add(lblCodigo);

        txtCodigo = new JTextField();
        txtCodigo.setBounds(100, 40, 150, 25);
        add(txtCodigo);

        JLabel lblNombre = new JLabel("Nombre");
        lblNombre.setBounds(10, 70, 50, 20);
        add(lblNombre);

        txtNombre = new JTextField();
        txtNombre.setBounds(100, 70, 150, 25);
        add(txtNombre);

        JLabel lblPresentacion = new JLabel("Presentación");
        lblPresentacion.setBounds(10, 100, 100, 20);
        add(lblPresentacion);

        txtPresentacion = new JTextField();
        txtPresentacion.setBounds(100, 100, 150, 25);
        add(txtPresentacion);

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setIcon(cargarIcono("/imagenes/Guardar logo.png", 20, 20));
        btnGuardar.setBounds(320, 40, 100, 30);
        add(btnGuardar);

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setIcon(cargarIcono("/imagenes/Limpiar logo.png", 20, 20));
        btnLimpiar.setBounds(430, 40, 100, 30);
        add(btnLimpiar);

        JButton btnBorrar = new JButton("Borrar");
        btnBorrar.setIcon(cargarIcono("/imagenes/X logo.png", 20, 20));
        btnBorrar.setBounds(540, 40, 100, 30);
        add(btnBorrar);

        JLabel lblBusqueda = new JLabel("Búsqueda");
        lblBusqueda.setFont(new Font("Arial", Font.BOLD, 12));
        lblBusqueda.setBounds(10, 150, 100, 20);
        add(lblBusqueda);

        JLabel lblBusquedaNombre = new JLabel("Nombre");
        lblBusquedaNombre.setBounds(10, 180, 60, 20);
        add(lblBusquedaNombre);

        txtBusquedaNombre = new JTextField();
        txtBusquedaNombre.setBounds(80, 180, 150, 25);
        add(txtBusquedaNombre);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setIcon(cargarIcono("/imagenes/Lupa de buscar logo.png", 20, 20));
        btnBuscar.setBounds(240, 180, 100, 30);
        add(btnBuscar);

        JButton btnRestaurar = new JButton("⟳");
        btnRestaurar.setBounds(350, 180, 50, 30);
        add(btnRestaurar);

        JButton btnReporte = new JButton("Reporte");
        btnReporte.setIcon(cargarIcono("/imagenes/Reporte logo.png", 20, 20));
        btnReporte.setBounds(410, 180, 100, 30);
        add(btnReporte);

        JLabel lblListado = new JLabel("Listado");
        lblListado.setFont(new Font("Arial", Font.BOLD, 12));
        lblListado.setBounds(10, 230, 100, 20);
        add(lblListado);

        String[] columnas = {"Código", "Nombre", "Presentación"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tabla = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBounds(10, 260, 650, 300);
        add(scrollPane);

        // === Bordes ===
        txtCodigo.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        txtNombre.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        txtPresentacion.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        txtBusquedaNombre.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));

        // === Cargar medicamentos existentes ===
        for (Medicamento med : medicamentos) {
            modeloTabla.addRow(new Object[]{med.getCodigo(), med.getNombre(), med.getPresentacion()});
        }

        // === Listeners ===
        btnGuardar.addActionListener(e -> guardarMedicamento());
        btnLimpiar.addActionListener(e -> limpiarCampos());
        btnBorrar.addActionListener(e -> borrarMedicamento());
        btnBuscar.addActionListener(e -> buscarMedicamento());
        btnRestaurar.addActionListener(e -> restaurarTabla());
        btnReporte.addActionListener(e -> mostrarReporte());
    }

    private void guardarMedicamento() {
        String codigo = txtCodigo.getText().trim();
        if (controlAdmin.existeMedicamento(codigo)) {
            JOptionPane.showMessageDialog(this, "El código ya existe. Ingrese uno diferente.");
            return;
        }
        String nombre = txtNombre.getText().trim();
        String presentacion = txtPresentacion.getText().trim();

        if (codigo.isEmpty() || nombre.isEmpty() || presentacion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe llenar todos los campos");
            return;
        }

        Medicamento med = new Medicamento(codigo, nombre, presentacion);
        controlAdmin.agregarMedicamento(med);
        modeloTabla.addRow(new Object[]{med.getCodigo(), med.getNombre(), med.getPresentacion()});
        controlAdmin.guardarMedicamentos();

        JOptionPane.showMessageDialog(this, "✅ Medicamento guardado con éxito");
        limpiarCampos();
    }

    private void borrarMedicamento() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un medicamento en la tabla para borrar");
            return;
        }
        String codigo = (String) modeloTabla.getValueAt(fila, 0);
        controlAdmin.eliminarMedicamento(codigo);
        modeloTabla.removeRow(fila);
        controlAdmin.guardarMedicamentos();
        JOptionPane.showMessageDialog(this, "✅ Medicamento eliminado");
    }

    private void buscarMedicamento() {
        String nombreBuscar = txtBusquedaNombre.getText().trim().toLowerCase();
        modeloTabla.setRowCount(0);
        for (Medicamento m : medicamentos) {
            if (m.getNombre().toLowerCase().contains(nombreBuscar)) {
                modeloTabla.addRow(new Object[]{m.getCodigo(), m.getNombre(), m.getPresentacion()});
            }
        }
    }

    private void restaurarTabla() {
        modeloTabla.setRowCount(0);
        for (Medicamento m : medicamentos) {
            modeloTabla.addRow(new Object[]{m.getCodigo(), m.getNombre(), m.getPresentacion()});
        }
    }

    private void mostrarReporte() {
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
    }

    private void limpiarCampos() {
        txtCodigo.setText("");
        txtNombre.setText("");
        txtPresentacion.setText("");
    }

    private ImageIcon cargarIcono(String ruta, int ancho, int alto) {
        java.net.URL location = getClass().getResource(ruta);
        if (location == null) return null;
        ImageIcon icono = new ImageIcon(location);
        Image img = icono.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
}
