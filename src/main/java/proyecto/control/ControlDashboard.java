package proyecto.control;

import proyecto.model.Receta;
import proyecto.model.DetalleReceta;
import java.util.*;
import java.util.stream.Collectors;

public class ControlDashboard {
    private List<Receta> recetas;

    public ControlDashboard(List<Receta> recetas) {
        this.recetas = recetas;
    }

    // 🔹 Medicamentos prescritos por mes
    public Map<Integer, Integer> medicamentosPorMes(int year) {
        Map<Integer, Integer> conteo = new HashMap<>();
        for (Receta r : recetas) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(r.getFecha());
            int mes = cal.get(Calendar.MONTH) + 1;
            int anio = cal.get(Calendar.YEAR);
            if (anio == year) {
                int total = r.getDetalles().stream()
                        .mapToInt(DetalleReceta::getCantidad)
                        .sum();
                conteo.put(mes, conteo.getOrDefault(mes, 0) + total);
            }
        }
        return conteo;
    }

    // 🔹 Cantidad de recetas por estado
    public Map<String, Long> recetasPorEstado() {
        return recetas.stream()
                .collect(Collectors.groupingBy(Receta::getEstado, Collectors.counting()));
    }
}
