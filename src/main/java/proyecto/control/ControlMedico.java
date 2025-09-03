package proyecto.control;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import proyecto.model.Medico;
import proyecto.view.VentanaMenuMedico;

public class ControlMedico {
    private VentanaMenuMedico vista;
    private List<String[]> medicamentos; // Lista temporal de medicamentos

    public ControlMedico(VentanaMenuMedico vista, Medico medico) {
        this.vista = vista;
        
        this.medicamentos = new ArrayList<>();
    }

    public void buscarPaciente() {
        // Aquí se debería abrir un diálogo de búsqueda de pacientes
        vista.setPaciente("Kate Castillo");
        vista.mostrarMensaje("Paciente seleccionado: Kate Castillo");
    }

    public void agregarMedicamento() {
        // Ejemplo: agregar un medicamento de prueba
        String[] nuevo = {"Acetaminofen", "100 mg", "60", "Mañana y Tarde", "30"};
        medicamentos.add(nuevo);
        actualizarTabla();
    }

    public void guardarReceta() {
        if (medicamentos.isEmpty()) {
            vista.mostrarMensaje("Debe agregar al menos un medicamento");
            return;
        }
        // Aquí iría la lógica de persistencia con XmlManager
        vista.mostrarMensaje("Receta guardada correctamente para " + vista.getFechaRetiro());
    }

    public void limpiarReceta() {
        medicamentos.clear();
        actualizarTabla();
        vista.mostrarMensaje("Receta limpiada");
    }

    public void descartarMedicamento() {
        int fila = vista.getTablaMedicamentos().getSelectedRow();
        if (fila >= 0) {
            medicamentos.remove(fila);
            actualizarTabla();
            vista.mostrarMensaje("Medicamento descartado");
        } else {
            vista.mostrarMensaje("Debe seleccionar un medicamento de la tabla");
        }
    }

    public void verDetalles() {
        int fila = vista.getTablaMedicamentos().getSelectedRow();
        if (fila >= 0) {
            String[] med = medicamentos.get(fila);
            vista.mostrarMensaje("Detalles:\nMedicamento: " + med[0] +
                                 "\nPresentación: " + med[1] +
                                 "\nCantidad: " + med[2] +
                                 "\nIndicaciones: " + med[3] +
                                 "\nDuración: " + med[4]);
        } else {
            vista.mostrarMensaje("Seleccione un medicamento para ver detalles");
        }
    }

    private void actualizarTabla() {
        String[] columnas = {"Medicamento", "Presentación", "Cantidad", "Indicaciones", "Duración"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

        for (String[] med : medicamentos) {
            modelo.addRow(med);
        }

        vista.getTablaMedicamentos().setModel(modelo);
    }
}

