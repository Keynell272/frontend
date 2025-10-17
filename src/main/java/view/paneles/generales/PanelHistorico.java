package view.paneles.generales;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import control.ControlReceta;
import model.Receta;

import java.awt.*;
import java.text.SimpleDateFormat;

public class PanelHistorico extends JPanel {
    private final ControlReceta controlReceta;
    private final DefaultTableModel modeloHistorico;
    private final JTable tablaHistorico;
    private final JTextField txtBuscar;

    private final SimpleDateFormat SDF_DMY = new SimpleDateFormat("dd/MM/yyyy");

    public PanelHistorico(ControlReceta controlReceta) {
        this.controlReceta = controlReceta;
        setLayout(new BorderLayout());

        String[] columnas = {
            "ID", "Paciente", "Estado",
            "Fecha Confección", "Fecha Retiro", "Fecha Proceso", "Fecha Lista", "Fecha Entregada"
        };

        modeloHistorico = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaHistorico = new JTable(modeloHistorico);
        JScrollPane scroll = new JScrollPane(tablaHistorico);

        // --- Panel búsqueda ---
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblBuscar = new JLabel("Buscar por ID:");
        txtBuscar = new JTextField(10);
        JButton btnBuscar = new JButton("Buscar");
        JButton btnReset = new JButton("⟳");

        panelBusqueda.add(lblBuscar);
        panelBusqueda.add(txtBuscar);
        panelBusqueda.add(btnBuscar);
        panelBusqueda.add(btnReset);

        // --- Acciones ---
        btnBuscar.addActionListener(e -> buscarPorId());
        btnReset.addActionListener(e -> {
            txtBuscar.setText("");
            cargarHistorico();
        });

        add(panelBusqueda, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        cargarHistorico();
    }

    private void buscarPorId() {
        String texto = txtBuscar.getText().trim();
        modeloHistorico.setRowCount(0); // limpiar
        if (texto.isEmpty()) {
            cargarHistorico();
            return;
        }

        Receta r = controlReceta.buscarPorId(texto);
        if (r != null) {
            modeloHistorico.addRow(new Object[]{
                r.getId(),
                (r.getPaciente() != null ? r.getPaciente().getNombre() : ""),
                r.getEstado(),
                formatearFecha(r.getFechaConfeccion()),
                formatearFecha(r.getFechaRetiro()),
                formatearFecha(r.getFechaProceso()),
                formatearFecha(r.getFechaLista()),
                formatearFecha(r.getFechaEntrega())
            });
        } else {
            JOptionPane.showMessageDialog(this, "No se encontró una receta con ID: " + texto);
        }
    }

    private void cargarHistorico() {
        modeloHistorico.setRowCount(0);
        for (Receta r : controlReceta.getRecetas()) {
            modeloHistorico.addRow(new Object[]{
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

    private String formatearFecha(java.util.Date d) {
        return (d != null ? SDF_DMY.format(d) : "");
    }
}
