package proyecto.control;

import proyecto.model.*;
import java.util.*;

public class ControlReceta {
    private List<Receta> recetas;

    public ControlReceta(List<Receta> recetas) {
        this.recetas = recetas;
    }

    public Receta crearReceta(String id, Date fechaConfeccion, Date fechaRetiro, Paciente paciente) {
        Receta receta = new Receta(id, fechaConfeccion, fechaRetiro, paciente);
        receta.setEstado("confeccionada");
        recetas.add(receta);
        return receta;
    }

    public void agregarMedicamento(Receta receta, Medicamento med, int cantidad, String indicaciones, int dias) {
        DetalleReceta detalle = new DetalleReceta(med, cantidad, indicaciones, dias);
        receta.agregarDetalle(detalle);
    }

    public List<Receta> buscarPorPaciente(String pacienteId) {
        List<Receta> result = new ArrayList<>();
        for (Receta r : recetas) {
            if (r.getPaciente() != null && r.getPaciente().getId().equals(pacienteId)) {
                result.add(r);
            }
        }
        return result;
    }

    public List<Receta> getRecetas() {
        return recetas;
    }
}
