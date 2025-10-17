package view.paneles.admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import control.ControlAdmin;
import model.Medico;
import java.awt.*;
import java.util.List;

public class PanelMedicos extends JPanel {
    private JTextField txtId, txtNombre, txtEspecialidad, txtBusquedaNombre;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private ControlAdmin controlAdmin;
    private List<Medico> medicos;

    public PanelMedicos(List<Medico> medicos, ControlAdmin controlAdmin) {
        this.controlAdmin = controlAdmin;
        this.medicos = medicos;
        init();
    }

    private void init() {
        setLayout(null);

        JLabel lblTitulo = new JLabel("Gestión de Médicos");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitulo.setBounds(10, 10, 200, 20);
        add(lblTitulo);

        JLabel lblId = new JLabel("ID");
        lblId.setBounds(10, 40, 50, 20);
        add(lblId);

        txtId = new JTextField();
        txtId.setBounds(100, 40, 150, 25);
        add(txtId);

        JLabel lblNombre = new JLabel("Nombre");
        lblNombre.setBounds(10, 70, 80, 20);
        add(lblNombre);

        txtNombre = new JTextField();
        txtNombre.setBounds(100, 70, 150, 25);
        add(txtNombre);

        JLabel lblEspecialidad = new JLabel("Especialidad");
        lblEspecialidad.setBounds(10, 100, 100, 20);
        add(lblEspecialidad);

        txtEspecialidad = new JTextField();
        txtEspecialidad.setBounds(100, 100, 150, 25);
        add(txtEspecialidad);

        JButton btnGuardar = new JButton("Guardar", cargarIcono("/imagenes/Guardar logo.png", 20, 20));
        btnGuardar.setBounds(320, 40, 100, 30);
        add(btnGuardar);

        JButton btnBorrar = new JButton("Borrar", cargarIcono("/imagenes/X logo.png", 20, 20));
        btnBorrar.setBounds(430, 40, 100, 30);
        add(btnBorrar);

        JButton btnLimpiar = new JButton("Limpiar", cargarIcono("/imagenes/Limpiar logo.png", 20, 20));
        btnLimpiar.setBounds(540, 40, 100, 30);
        add(btnLimpiar);

        JLabel lblBusqueda = new JLabel("Buscar por nombre");
        lblBusqueda.setFont(new Font("Arial", Font.BOLD, 12));
        lblBusqueda.setBounds(10, 150, 150, 20);
        add(lblBusqueda);

        txtBusquedaNombre = new JTextField();
        txtBusquedaNombre.setBounds(10, 180, 150, 25);
        add(txtBusquedaNombre);

        JButton btnBuscar = new JButton("Buscar", cargarIcono("/imagenes/Lupa de buscar logo.png", 20, 20));
        btnBuscar.setBounds(170, 180, 100, 30);
        add(btnBuscar);

        JButton btnRestaurar = new JButton("⟳");
        btnRestaurar.setBounds(280, 180, 50, 30);
        add(btnRestaurar);

        JButton btnReporte = new JButton("Reporte", cargarIcono("/imagenes/Reporte logo.png", 20, 20));
        btnReporte.setBounds(340, 180, 100, 30);
        add(btnReporte);

        JLabel lblListado = new JLabel("Listado de Médicos");
        lblListado.setFont(new Font("Arial", Font.BOLD, 12));
        lblListado.setBounds(10, 230, 200, 20);
        add(lblListado);

        String[] columnas = {"ID", "Nombre", "Especialidad"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tabla = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(10, 260, 650, 300);
        add(scroll);

        txtId.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        txtNombre.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        txtEspecialidad.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        txtBusquedaNombre.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));

        cargarTabla();

        btnGuardar.addActionListener(e -> guardarMedico());
        btnBorrar.addActionListener(e -> borrarMedico());
        btnBuscar.addActionListener(e -> buscarMedico());
        btnRestaurar.addActionListener(e -> cargarTabla());
        btnReporte.addActionListener(e -> mostrarReporte());
        btnLimpiar.addActionListener(e -> limpiarCampos());
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        controlAdmin.refrescarDatos();
        medicos = controlAdmin.getMedicos();
        if (medicos != null) {
            for (Medico m : medicos) {
                modeloTabla.addRow(new Object[]{m.getId(), m.getNombre(), m.getEspecialidad()});
            }
        }
    }

    private void guardarMedico() {
        String id = txtId.getText().trim();
        String nombre = txtNombre.getText().trim();
        String especialidad = txtEspecialidad.getText().trim();

        if (id.isEmpty() || nombre.isEmpty() || especialidad.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe llenar todos los campos.");
            return;
        }

        if (controlAdmin.existeMedico(id)) {
            JOptionPane.showMessageDialog(this, "❌ Ya existe un médico con ese ID");
            return;
        }

        try {
            Medico nuevo = new Medico(id, "123", nombre, especialidad);
            controlAdmin.agregarMedico(nuevo);
            JOptionPane.showMessageDialog(this, "✅ Médico guardado con éxito");
            cargarTabla();
            limpiarCampos();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage());
        }
    }

    private void borrarMedico() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un médico para eliminar");
            return;
        }

        String id = (String) modeloTabla.getValueAt(fila, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar el médico " + id + "?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            controlAdmin.eliminarMedico(id);
            controlAdmin.guardarMedicos();
            JOptionPane.showMessageDialog(this, "✅ Médico eliminado correctamente");
            cargarTabla();
        }
    }

    private void buscarMedico() {
        String nombreBuscar = txtBusquedaNombre.getText().trim().toLowerCase();
        modeloTabla.setRowCount(0);

        for (Medico m : medicos) {
            if (m.getNombre().toLowerCase().contains(nombreBuscar)) {
                modeloTabla.addRow(new Object[]{m.getId(), m.getNombre(), m.getEspecialidad()});
            }
        }
    }

    private void mostrarReporte() {
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
    }

    private void limpiarCampos() {
        txtId.setText("");
        txtNombre.setText("");
        txtEspecialidad.setText("");
    }

    private ImageIcon cargarIcono(String ruta, int ancho, int alto) {
        java.net.URL location = getClass().getResource(ruta);
        if (location == null) return null;
        ImageIcon icono = new ImageIcon(location);
        Image img = icono.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
}