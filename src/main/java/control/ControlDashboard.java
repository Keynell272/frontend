package control;

import model.*;
import java.util.*;
import java.util.stream.Collectors;

public class ControlDashboard {
    private List<Receta> recetas;
    private List<Medicamento> medicamentos;
    private ControlAdmin controlAdmin;
    private ControlReceta controlReceta;
    
    public ControlDashboard() {
        this.controlReceta = new ControlReceta();
        this.controlAdmin = new ControlAdmin();
        cargarDatos();
    }
    
    private void cargarDatos() {
        this.medicamentos = controlAdmin.getMedicamentos();
        this.recetas = controlReceta.getRecetas();
    }
    
    public void actualizarDatos() {
        controlAdmin.refrescarDatos();
        controlReceta.refrescarDatos();
        cargarDatos();
    }
    
    public List<Receta> getRecetas() {
        return recetas;
    }
    
    public List<Medicamento> getMedicamentos() {
        return medicamentos;
    }
    
    public void setRecetas(List<Receta> recetas) {
        this.recetas = recetas;
    }
    
    public void setMedicamentos(List<Medicamento> medicamentos) {
        this.medicamentos = medicamentos;
    }
    
    public Map<Integer, Integer> medicamentosPorMes(int year) {
        Map<Integer, Integer> conteo = new HashMap<>();
        for (Receta r : recetas) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(r.getFechaConfeccion());
            int mes = cal.get(Calendar.MONTH) + 1;
            int anio = cal.get(Calendar.YEAR);
            if (anio == year) {
                int total = r.getDetalles().stream().mapToInt(DetalleReceta::getCantidad).sum();
                conteo.put(mes, conteo.getOrDefault(mes, 0) + total);
            }
        }
        return conteo;
    }
    
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
                if (med != null && med.getNombre() != null && med.getNombre().equalsIgnoreCase(nombreMedicamento)) {
                    conteo.put(mes, conteo.get(mes) + d.getCantidad());
                }
            }
        }
        return conteo;
    }
    
    public Map<String, Integer> recetasPorEstado() {
        Map<String, Integer> res = new LinkedHashMap<>();
        for (Receta r : recetas) {
            String est = (r.getEstado() == null ? "desconocido" : r.getEstado());
            res.put(est, res.getOrDefault(est, 0) + 1);
        }
        return res;
    }
}