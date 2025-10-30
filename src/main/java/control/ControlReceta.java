package control;

import model.*;
import proxy.ServiceProxy;
import javax.swing.JOptionPane;
import java.text.SimpleDateFormat;
import java.util.*;

public class ControlReceta {
    private ServiceProxy proxy;
    private List<Receta> recetas;
    private List<Medicamento> medicamentos;
    private List<Paciente> pacientes;
    private static final SimpleDateFormat SDF_DMY = new SimpleDateFormat("dd/MM/yyyy");
    
    public ControlReceta() {
        this.proxy = ServiceProxy.getInstance();
        cargarDatos();
    }
    
    private void cargarDatos() {
        try {
            this.recetas = proxy.listarRecetas();
            this.medicamentos = proxy.listarMedicamentos();
            this.pacientes = proxy.listarPacientes();
        } catch (Exception e) {
            System.err.println("Error al cargar datos: " + e.getMessage());
            this.recetas = new ArrayList<>();
            this.medicamentos = new ArrayList<>();
            this.pacientes = new ArrayList<>();
        }
    }
    
    public void refrescarDatos() {
        cargarDatos();
    }
    
    public List<Receta> getRecetas() {
        try {
            return proxy.listarRecetas();
        } catch (Exception e) {
            System.err.println("Error al obtener recetas: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public List<Medicamento> getMedicamentos() {
        return medicamentos;
    }
    
    public List<Paciente> getPacientes() {
        return pacientes;
    }
    
    private Date soloFecha(Date d) throws Exception {
        return SDF_DMY.parse(SDF_DMY.format(d));
    }
    
    private String generarSiguienteIdReceta() {
        int maxId = 0;
        for (Receta r : recetas) {
            try {
                int id = Integer.parseInt(r.getId().substring(1));
                if (id > maxId) maxId = id;
            } catch (Exception e) {}
        }
        return "R" + String.format("%03d", maxId + 1);
    }
    
    private Date parseFechaFlexible(String s) {
        if (s == null) return null;
        String t = s.trim();
        try { 
            return SDF_DMY.parse(t); 
        } catch (Exception ignore) {}
        return null;
    }
    
    public void guardarReceta(Paciente pacienteSeleccionado, 
                             javax.swing.JTable tablaMedicamentos, 
                             javax.swing.JTextField txtFechaRetiro,
                             String medicoId) {
        
        if (pacienteSeleccionado == null) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar un paciente.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (tablaMedicamentos.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Debe agregar al menos un medicamento.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        for (int i = 0; i < tablaMedicamentos.getRowCount(); i++) {
            for (int j = 0; j < tablaMedicamentos.getColumnCount(); j++) {
                Object v = tablaMedicamentos.getValueAt(i, j);
                if (v == null || v.toString().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null,
                        "Debe llenar todos los campos de los medicamentos (fila " + (i + 1) + ").",
                        "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
        }
        
        Date fechaRetiro = parseFechaFlexible(txtFechaRetiro.getText());
        if (fechaRetiro == null) {
            JOptionPane.showMessageDialog(null, "Formato de fecha inválido (use dd/MM/yyyy).",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            Date fechaConfeccion = soloFecha(new Date());
            fechaRetiro = soloFecha(fechaRetiro);
            
            if (fechaRetiro.before(fechaConfeccion)) {
                JOptionPane.showMessageDialog(null,
                    "La fecha de retiro no puede ser anterior a la fecha de confección (" +
                    SDF_DMY.format(fechaConfeccion) + ").",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String nuevoId = generarSiguienteIdReceta();
            Receta receta = new Receta(nuevoId, fechaConfeccion, fechaRetiro, pacienteSeleccionado);
            receta.setMedicoId(medicoId);
            receta.setEstado("confeccionada");
            
            for (int i = 0; i < tablaMedicamentos.getRowCount(); i++) {
                String nombreMed = tablaMedicamentos.getValueAt(i, 0).toString();
                String presentacion = tablaMedicamentos.getValueAt(i, 1).toString();
                int cantidad = Integer.parseInt(tablaMedicamentos.getValueAt(i, 2).toString());
                String indicaciones = tablaMedicamentos.getValueAt(i, 3).toString();
                int duracion = Integer.parseInt(tablaMedicamentos.getValueAt(i, 4).toString());
                
                Medicamento med = null;
                for (Medicamento m : medicamentos) {
                    if (m.getNombre().equals(nombreMed) && m.getPresentacion().equals(presentacion)) {
                        med = m;
                        break;
                    }
                }
                
                if (med != null) {
                    DetalleReceta detalle = new DetalleReceta(med, cantidad, indicaciones, duracion);
                    receta.agregarDetalle(detalle);
                }
            }
            
            boolean exito = proxy.crearReceta(receta);
            
            if (exito) {
                recetas.add(receta);
                JOptionPane.showMessageDialog(null, "Receta guardada exitosamente. ID: " + nuevoId,
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "No se pudo guardar la receta",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al guardar receta: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public Receta buscarPorId(String id) {
        try {
            return proxy.buscarReceta(id);
        } catch (Exception e) {
            System.err.println("Error al buscar receta: " + e.getMessage());
            return null;
        }
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
    
    public boolean iniciarDespacho(String recetaId) {
        try {
            return proxy.iniciarDespacho(recetaId);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al iniciar despacho: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public boolean marcarRecetaLista(String recetaId) {
        try {
            return proxy.marcarRecetaLista(recetaId);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al marcar receta como lista: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public boolean entregarReceta(String recetaId) {
        try {
            return proxy.entregarReceta(recetaId);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al entregar receta: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public String validarFechaRetiro(Receta receta) {
        Date hoy = new Date();
        Date fechaRetiro = receta.getFechaRetiro();
        
        if (hoy.before(fechaRetiro)) return "anticipado"; 
        if (hoy.after(fechaRetiro)) return "atrasado";   
        if (hoy.equals(fechaRetiro)) return "normal";
        return "error"; 
    }
    
    public void agregarMedicamento(Receta receta, Medicamento med, int cantidad, String indicaciones, int dias) {
        DetalleReceta detalle = new DetalleReceta(med, cantidad, indicaciones, dias);
        receta.agregarDetalle(detalle);
    }
    
    public void guardarRecetas(String ruta) {
        refrescarDatos();
    }
    
    public void guardarCambios(String ruta) {
        refrescarDatos();
    }

    /**
     * Obtiene las recetas que aún están pendientes (no entregadas)
     */
    public List<Receta> getRecetasPendientes() {
        try {
            List<Receta> todas = proxy.listarRecetas();
            List<Receta> pendientes = new ArrayList<>();
            
            for (Receta r : todas) {
                // Excluir recetas entregadas
                if (!r.getEstado().equals("entregada")) {
                    pendientes.add(r);
                }
            }
            
            return pendientes;
        } catch (Exception e) {
            System.err.println("Error al obtener recetas pendientes: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}