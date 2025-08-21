package proyecto.control;

import proyecto.model.Receta;
import java.util.*;

public class ControlDespacho {
    private List<Receta> recetas;

    public ControlDespacho(List<Receta> recetas) {
        this.recetas = recetas;
    }

    public List<Receta> recetasDisponiblesParaDespacho(Date hoy) {
        List<Receta> disponibles = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(hoy);

        for (Receta r : recetas) {
            long diff = Math.abs(hoy.getTime() - r.getFechaRetiro().getTime());
            long dias = diff / (1000 * 60 * 60 * 24);
            if (dias <= 3 && r.getEstado().equals("confeccionada")) {
                disponibles.add(r);
            }
        }
        return disponibles;
    }

    public void cambiarEstado(Receta receta, String nuevoEstado) {
        receta.cambiarEstado(nuevoEstado);
    }
}
