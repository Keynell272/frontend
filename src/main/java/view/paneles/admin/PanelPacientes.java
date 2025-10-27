package view.paneles.admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import control.ControlAdmin;
import model.Paciente;
import view.paneles.generales.DatePickerConNavegacion;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PanelPacientes extends JPanel {
    private JTextField txtIdPac, txtNombrePac, txtTelefonoPac, txtBusquedaNombrePac, txtFechaNac;
    private JTable tablaPac;
    private DefaultTableModel modeloTablaPac;
    private List<Paciente> pacientes;
    private ControlAdmin controlAdmin;
    private final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");

    public PanelPacientes(List<Paciente> pacientes, ControlAdmin controlAdmin) {
        this.pacientes = pacientes;
        this.controlAdmin = controlAdmin;
        init();
    }

    private void init() {
        setLayout(null);

        JLabel lblPac = new JLabel("Paciente");
        lblPac.setFont(new Font("Arial", Font.BOLD, 12));
        lblPac.setBounds(10, 10, 100, 20);
        add(lblPac);

        JLabel lblId = new JLabel("Id");
        lblId.setBounds(10, 40, 50, 20);
        add(lblId);

        txtIdPac = new JTextField();
        txtIdPac.setBounds(100, 40, 150, 25);
        add(txtIdPac);

        JLabel lblNombre = new JLabel("Nombre");
        lblNombre.setBounds(10, 70, 50, 20);
        add(lblNombre);

        txtNombrePac = new JTextField();
        txtNombrePac.setBounds(100, 70, 150, 25);
        add(txtNombrePac);

        JLabel lblFechaNac = new JLabel("Fecha Nac.");
        lblFechaNac.setBounds(10, 100, 100, 20);
        add(lblFechaNac);

        txtFechaNac = new JTextField();
        txtFechaNac.setBounds(100, 100, 150, 25);
        add(txtFechaNac);

        JButton btnCalendario = new JButton("📅");
        btnCalendario.setBounds(260, 100, 50, 25);
        add(btnCalendario);
        new DatePickerConNavegacion(txtFechaNac, btnCalendario);

        JLabel lblTel = new JLabel("Teléfono");
        lblTel.setBounds(10, 130, 100, 20);
        add(lblTel);

        txtTelefonoPac = new JTextField();
        txtTelefonoPac.setBounds(100, 130, 150, 25);
        add(txtTelefonoPac);

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
        lblBusqueda.setBounds(10, 170, 100, 20);
        add(lblBusqueda);

        JLabel lblBusquedaNombre = new JLabel("Nombre");
        lblBusquedaNombre.setBounds(10, 200, 60, 20);
        add(lblBusquedaNombre);

        txtBusquedaNombrePac = new JTextField();
        txtBusquedaNombrePac.setBounds(80, 200, 150, 25);
        add(txtBusquedaNombrePac);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(240, 200, 100, 30);
        btnBuscar.setIcon(cargarIcono("/imagenes/Lupa de buscar logo.png", 20, 20));
        add(btnBuscar);

        JButton btnRestaurar = new JButton("⟳");
        btnRestaurar.setBounds(350, 200, 50, 30);
        add(btnRestaurar);

        JButton btnReporte = new JButton("Reporte");
        btnReporte.setBounds(410, 200, 100, 30);
        btnReporte.setIcon(cargarIcono("/imagenes/Reporte logo.png", 20, 20));
        add(btnReporte);

        JLabel lblListado = new JLabel("Listado");
        lblListado.setFont(new Font("Arial", Font.BOLD, 12));
        lblListado.setBounds(10, 250, 100, 20);
        add(lblListado);

        String[] columnas = {"Id", "Nombre", "Fecha Nac.", "Teléfono"};
        modeloTablaPac = new DefaultTableModel(columnas, 0);
        tablaPac = new JTable(modeloTablaPac);
        JScrollPane scrollPane = new JScrollPane(tablaPac);
        scrollPane.setBounds(10, 280, 650, 280);
        add(scrollPane);

        txtIdPac.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        txtNombrePac.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        txtFechaNac.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        txtTelefonoPac.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        txtBusquedaNombrePac.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));

        cargarTabla();

        btnGuardar.addActionListener(e -> guardarPaciente());
        btnLimpiar.addActionListener(e -> limpiarCampos());
        btnBorrar.addActionListener(e -> borrarPaciente());
        btnBuscar.addActionListener(e -> buscarPaciente());
        btnRestaurar.addActionListener(e -> cargarTabla());
        btnReporte.addActionListener(e -> mostrarReporte());
    }

    private void cargarTabla() {
        modeloTablaPac.setRowCount(0);
        controlAdmin.refrescarDatos();
        pacientes = controlAdmin.getPacientes();
        for (Paciente p : pacientes) {
            modeloTablaPac.addRow(new Object[]{p.getId(), p.getNombre(), formatearFecha(p.getFechaNacimiento()), p.getTelefono()});
        }
    }

    private void guardarPaciente() {
        String id = txtIdPac.getText().trim();
        if (!controlAdmin.esIdUnico(id)) {
            JOptionPane.showMessageDialog(this, "El ID ya existe. Ingrese uno diferente.");
            return;
        }
        String nombre = txtNombrePac.getText().trim();
        String tel = txtTelefonoPac.getText().trim();
        Date fecha;

        try {
            if (!txtFechaNac.getText().isEmpty()) {
                fecha = SDF.parse(txtFechaNac.getText());
            } else {
                JOptionPane.showMessageDialog(this, "Debe seleccionar la fecha de nacimiento");
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido");
            return;
        }

        if (id.isEmpty() || nombre.isEmpty() || tel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe llenar todos los campos");
            return;
        }

        try {
            Paciente p = new Paciente(id, nombre, fecha, tel);
            controlAdmin.agregarPaciente(p);
            JOptionPane.showMessageDialog(this, "✅ Paciente guardado con éxito");
            cargarTabla();
            limpiarCampos();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage());
        }
    }

    private void borrarPaciente() {
        int fila = tablaPac.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un paciente");
            return;
        }
        
        String id = (String) modeloTablaPac.getValueAt(fila, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar el paciente " + id + "?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                controlAdmin.eliminarPaciente(id);
                JOptionPane.showMessageDialog(this, "✅ Paciente eliminado");
                cargarTabla();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage());
            }
        }
    }

    private void buscarPaciente() {
        String buscar = txtBusquedaNombrePac.getText().trim().toLowerCase();
        modeloTablaPac.setRowCount(0);
        for (Paciente p : pacientes) {
            if (p.getNombre().toLowerCase().contains(buscar)) {
                modeloTablaPac.addRow(new Object[]{p.getId(), p.getNombre(), formatearFecha(p.getFechaNacimiento()), p.getTelefono()});
            }
        }
    }

    private void mostrarReporte() {
        StringBuilder sb = new StringBuilder("=== Reporte de Pacientes ===\n\n");
        for (Paciente p : pacientes) {
            sb.append("ID: ").append(p.getId())
              .append(" | Nombre: ").append(p.getNombre())
              .append(" | Fecha Nac.: ").append(formatearFecha(p.getFechaNacimiento()))
              .append(" | Tel: ").append(p.getTelefono())
              .append("\n");
        }
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JScrollPane sp = new JScrollPane(area);
        sp.setPreferredSize(new Dimension(500, 300));
        JOptionPane.showMessageDialog(this, sp, "Reporte de Pacientes", JOptionPane.INFORMATION_MESSAGE);
    }

    private void limpiarCampos() {
        txtIdPac.setText("");
        txtNombrePac.setText("");
        txtTelefonoPac.setText("");
        txtFechaNac.setText("");
    }

    private String formatearFecha(Date fecha) {
        return fecha != null ? SDF.format(fecha) : "";
    }

    private ImageIcon cargarIcono(String ruta, int ancho, int alto) {
        java.net.URL location = getClass().getResource(ruta);
        if (location == null) return null;
        ImageIcon icono = new ImageIcon(location);
        Image img = icono.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
}