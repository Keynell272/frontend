package proyecto.control;

import proyecto.model.Receta;
import proyecto.model.DetalleReceta;
import proyecto.model.Medicamento;
import java.util.*;
import java.util.stream.Collectors;

public class ControlDashboard {
    private List<Receta> recetas;

    public ControlDashboard(List<Receta> recetas) {
        this.recetas = recetas;
    }

    // Totales (todas las unidades) por mes
    public Map<Integer, Integer> medicamentosPorMes(int year) {
        Map<Integer, Integer> conteo = new HashMap<>();
        for (Receta r : recetas) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(r.getFechaConfeccion());
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

    // Serie por medicamento y rango de meses (inclusive)
    public Map<Integer, Integer> medicamentoPorMes(String nombreMedicamento, int year, int mesDesde, int mesHasta) {
        Map<Integer, Integer> conteo = new LinkedHashMap<>();
        for (int m = mesDesde; m <= mesHasta; m++) conteo.put(m, 0);

        for (Receta r : recetas) {
            Date f = r.getFechaConfeccion();
            if (f == null) continue;
            Calendar cal = Calendar.getInstance();
            cal.setTime(f);
            int mes = cal.get(Calendar.MONTH) + 1;
            int anio = cal.get(Calendar.YEAR);
            if (anio != year || mes < mesDesde || mes > mesHasta) continue;

            for (DetalleReceta d : r.getDetalles()) {
                Medicamento med = d.getMedicamento();
                if (med != null && med.getNombre() != null &&
                        med.getNombre().equalsIgnoreCase(nombreMedicamento)) {
                    conteo.put(mes, conteo.get(mes) + d.getCantidad());
                }
            }
        }
        return conteo;
    }

    // Recetas por estado (para el pastel)
    public Map<String, Integer> recetasPorEstado() {
        Map<String, Integer> res = new LinkedHashMap<>();
        for (Receta r : recetas) {
            String est = (r.getEstado() == null ? "desconocido" : r.getEstado());
            res.put(est, res.getOrDefault(est, 0) + 1);
        }
        return res;
    }
}