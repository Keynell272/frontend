package proyecto.view.paneles.admin;

import proyecto.model.Medico;
import proyecto.control.ControlAdmin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelMedicos extends JPanel {
    private JTextField txtId, txtNombre, txtEspecialidad, txtBusquedaNombre;
    private JButton btnRestaurar;
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    private List<Medico> medicos;
    private ControlAdmin controlAdmin;

    public PanelMedicos(List<Medico> medicos, ControlAdmin controlAdmin) {
        this.medicos = medicos;
        this.controlAdmin = controlAdmin;
        init();
    }

    private void init() {
        setLayout(null);

        JLabel lblMedico = new JLabel("Médico");
        lblMedico.setFont(new Font("Arial", Font.BOLD, 12));
        lblMedico.setBounds(10, 10, 100, 20);
        add(lblMedico);

        JLabel lblId = new JLabel("Id");
        lblId.setBounds(10, 40, 50, 20);
        add(lblId);

        txtId = new JTextField();
        txtId.setBounds(100, 40, 150, 25);
        add(txtId);

        JLabel lblNombre = new JLabel("Nombre");
        lblNombre.setBounds(10, 70, 50, 20);
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

        txtBusquedaNombre = new JTextField();
        txtBusquedaNombre.setBounds(80, 180, 150, 25);
        add(txtBusquedaNombre);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(240, 180, 100, 30);
        btnBuscar.setIcon(cargarIcono("/imagenes/Lupa de buscar logo.png", 20, 20));
        add(btnBuscar);

        btnRestaurar = new JButton("⟳");
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

        String[] columnas = {"Id", "Nombre", "Especialidad"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tabla = new JTable(modeloTabla);

        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBounds(10, 260, 650, 300);
        add(scrollPane);

        // BORDES
        txtId.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        txtNombre.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        txtEspecialidad.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        txtBusquedaNombre.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        btnGuardar.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        btnLimpiar.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        btnBorrar.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        btnBuscar.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        btnRestaurar.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        btnReporte.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));

        // cargar médicos iniciales
        for (Medico m : medicos) {
            modeloTabla.addRow(new Object[]{m.getId(), m.getNombre(), m.getEspecialidad()});
        }

        // === LISTENERS ===
        btnGuardar.addActionListener(e -> guardarMedico());
        btnBorrar.addActionListener(e -> borrarMedico());
        btnBuscar.addActionListener(e -> buscarMedico());
        btnRestaurar.addActionListener(e -> restaurarTabla());
        btnReporte.addActionListener(e -> mostrarReporte());
        btnLimpiar.addActionListener(e -> limpiarCampos());
    }

    private void guardarMedico() {
        String id = txtId.getText().trim();
        if (!controlAdmin.esIdUnico(id)) {
            JOptionPane.showMessageDialog(this, "El ID ya existe. Ingrese uno diferente.");
            return;
        }
        String nombre = txtNombre.getText().trim();
        String especialidad = txtEspecialidad.getText().trim();

        if (id.isEmpty() || nombre.isEmpty() || especialidad.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe llenar todos los campos");
            return;
        }

        Medico m = new Medico(id, "123", nombre, especialidad);
        controlAdmin.agregarMedico(m);
        modeloTabla.addRow(new Object[]{m.getId(), m.getNombre(), m.getEspecialidad()});
        controlAdmin.guardarMedicos();

        JOptionPane.showMessageDialog(this, "✅ Médico guardado con éxito");
        limpiarCampos();
    }

    private void borrarMedico() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un médico en la tabla para borrar");
            return;
        }

        String id = (String) modeloTabla.getValueAt(fila, 0);
        controlAdmin.eliminarMedico(id);
        modeloTabla.removeRow(fila);
        controlAdmin.guardarMedicos();

        JOptionPane.showMessageDialog(this, "✅ Médico eliminado");
    }

    private void buscarMedico() {
        String nombreBuscar = txtBusquedaNombre.getText().trim().toLowerCase();
        if (nombreBuscar.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un nombre para buscar");
            return;
        }

        modeloTabla.setRowCount(0);
        for (Medico m : medicos) {
            if (m.getNombre().toLowerCase().contains(nombreBuscar)) {
                modeloTabla.addRow(new Object[]{m.getId(), m.getNombre(), m.getEspecialidad()});
            }
        }
    }

    private void restaurarTabla() {
        modeloTabla.setRowCount(0);
        for (Medico m : medicos) {
            modeloTabla.addRow(new Object[]{m.getId(), m.getNombre(), m.getEspecialidad()});
        }
    }

    private void mostrarReporte() {
        if (medicos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay médicos para el reporte");
            return;
        }

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
        if (location == null) {
            System.err.println("No se encontró la imagen: " + ruta);
            return null;
        }
        ImageIcon icono = new ImageIcon(location);
        Image imagen = icono.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        return new ImageIcon(imagen);
    }
}
