package proyecto.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

import proyecto.model.Medicamento; // suponiendo que tienes esta clase

public class VentanaMedicamentos extends JDialog {

    private JTable tablaMedicamentos;
    private DefaultTableModel modelo;

    public VentanaMedicamentos(Frame owner, List<Medicamento> medicamentos) {
        super(owner, "Medicamentos", true);
        setSize(600, 400);
        setLocationRelativeTo(owner);

        String[] columnas = {"Id", "Nombre", "Descripción", "Cantidad"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tablaMedicamentos = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tablaMedicamentos);
        add(scroll, BorderLayout.CENTER);

        cargarMedicamentos(medicamentos);
    }

    private void cargarMedicamentos(List<Medicamento> medicamentos) {
        modelo.setRowCount(0);
        for (Medicamento m : medicamentos) {
            modelo.addRow(new Object[]{
                m.getCodigo(),
                m.getNombre(),
                m.getPresentacion(),
                
            });
        }
    }
}

