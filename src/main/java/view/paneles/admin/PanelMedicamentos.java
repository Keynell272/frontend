package view.paneles.admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import control.ControlAdmin;
import model.Medicamento;
import java.awt.*;
import java.util.List;

public class PanelMedicamentos extends JPanel {
    private JTextField txtCodigo, txtNombre, txtPresentacion, txtBusquedaNombre;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private ControlAdmin controlAdmin;
    private List<Medicamento> medicamentos;

    public PanelMedicamentos(List<Medicamento> medicamentos, ControlAdmin controlAdmin) {
        this.controlAdmin = controlAdmin;
        this.medicamentos = medicamentos;
        init();
    }

    private void init() {
        setLayout(null);

        JLabel lblTitulo = new JLabel("Medicamentos");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitulo.setBounds(10, 10, 200, 20);
        add(lblTitulo);

        JLabel lblCodigo = new JLabel("Código");
        lblCodigo.setBounds(10, 40, 80, 20);
        add(lblCodigo);

        txtCodigo = new JTextField();
        txtCodigo.setBounds(100, 40, 150, 25);
        add(txtCodigo);

        JLabel lblNombre = new JLabel("Nombre");
        lblNombre.setBounds(10, 70, 80, 20);
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

        JButton btnGuardar = new JButton("Guardar", cargarIcono("/imagenes/Guardar logo.png", 20, 20));
        btnGuardar.setBounds(320, 40, 100, 30);
        add(btnGuardar);

        JButton btnBorrar = new JButton("Borrar", cargarIcono("/imagenes/X logo.png", 20, 20));
        btnBorrar.setBounds(430, 40, 100, 30);
        add(btnBorrar);

        JButton btnLimpiar = new JButton("Limpiar", cargarIcono("/imagenes/Limpiar logo.png", 20, 20));
        btnLimpiar.setBounds(540, 40, 100, 30);
        add(btnLimpiar);

        JLabel lblBusqueda = new JLabel("Búsqueda por nombre");
        lblBusqueda.setFont(new Font("Arial", Font.BOLD, 12));
        lblBusqueda.setBounds(10, 150, 200, 20);
        add(lblBusqueda);

        txtBusquedaNombre = new JTextField();
        txtBusquedaNombre.setBounds(10, 180, 200, 25);
        add(txtBusquedaNombre);

        JButton btnBuscar = new JButton("Buscar", cargarIcono("/imagenes/Lupa de buscar logo.png", 20, 20));
        btnBuscar.setBounds(220, 180, 100, 30);
        add(btnBuscar);

        JButton btnRestaurar = new JButton("⟳");
        btnRestaurar.setBounds(330, 180, 50, 30);
        add(btnRestaurar);

        JButton btnReporte = new JButton("Reporte", cargarIcono("/imagenes/Reporte logo.png", 20, 20));
        btnReporte.setBounds(390, 180, 100, 30);
        add(btnReporte);

        String[] columnas = {"Código", "Nombre", "Presentación"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tabla = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(10, 230, 650, 300);
        add(scroll);

        txtCodigo.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        txtNombre.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        txtPresentacion.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        txtBusquedaNombre.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));

        cargarTabla();

        btnGuardar.addActionListener(e -> guardarMedicamento());
        btnBorrar.addActionListener(e -> borrarMedicamento());
        btnBuscar.addActionListener(e -> buscarMedicamento());
        btnRestaurar.addActionListener(e -> cargarTabla());
        btnReporte.addActionListener(e -> mostrarReporte());
        btnLimpiar.addActionListener(e -> limpiarCampos());
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        controlAdmin.refrescarDatos();
        medicamentos = controlAdmin.getMedicamentos();
        if (medicamentos != null) {
            for (Medicamento m : medicamentos) {
                modeloTabla.addRow(new Object[]{m.getCodigo(), m.getNombre(), m.getPresentacion()});
            }
        }
    }

    private void guardarMedicamento() {
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String presentacion = txtPresentacion.getText().trim();

        if (codigo.isEmpty() || nombre.isEmpty() || presentacion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe llenar todos los campos.");
            return;
        }

        if (controlAdmin.existeMedicamento(codigo)) {
            JOptionPane.showMessageDialog(this, "❌ Ya existe un medicamento con ese código");
            return;
        }

        try {
            Medicamento nuevo = new Medicamento(codigo, nombre, presentacion);
            controlAdmin.agregarMedicamento(nuevo);
            JOptionPane.showMessageDialog(this, "✅ Medicamento guardado con éxito");
            cargarTabla();
            limpiarCampos();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage());
        }
    }

    private void borrarMedicamento() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un medicamento de la tabla para borrar");
            return;
        }

        String codigo = (String) modeloTabla.getValueAt(fila, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar el medicamento " + codigo + "?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                controlAdmin.eliminarMedicamento(codigo);
                JOptionPane.showMessageDialog(this, "✅ Medicamento eliminado");
                cargarTabla();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage());
            }
        }
    }

    private void buscarMedicamento() {
        String nombreBuscar = txtBusquedaNombre.getText().trim().toLowerCase();
        modeloTabla.setRowCount(0);

        if (nombreBuscar.isEmpty()) {
            cargarTabla();
            return;
        }

        for (Medicamento m : medicamentos) {
            if (m.getNombre().toLowerCase().contains(nombreBuscar)) {
                modeloTabla.addRow(new Object[]{m.getCodigo(), m.getNombre(), m.getPresentacion()});
            }
        }
    }

    private void mostrarReporte() {
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