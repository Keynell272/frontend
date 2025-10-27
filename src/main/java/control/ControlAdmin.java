package control;

import model.*;
import proxy.ServiceProxy;
import java.util.*;

public class ControlAdmin {
    private ServiceProxy proxy;
    private List<Medico> medicos;
    private List<Farmaceuta> farmaceutas;
    private List<Paciente> pacientes;
    private List<Medicamento> medicamentos;
    private List<Usuario> usuarios;
    
    public ControlAdmin() {
        this.proxy = ServiceProxy.getInstance();
        cargarDatos();
    }
    
    private void cargarDatos() {
        try {
            this.medicos = proxy.listarMedicos();
            this.farmaceutas = proxy.listarFarmaceutas();
            this.pacientes = proxy.listarPacientes();
            this.medicamentos = proxy.listarMedicamentos();
            this.usuarios = proxy.listarUsuarios();
        } catch (Exception e) {
            System.err.println("Error al cargar datos: " + e.getMessage());
            this.medicos = new ArrayList<>();
            this.farmaceutas = new ArrayList<>();
            this.pacientes = new ArrayList<>();
            this.medicamentos = new ArrayList<>();
            this.usuarios = new ArrayList<>();
        }
    }
    
    public void refrescarDatos() {
        cargarDatos();
    }
    
    public List<Medico> getMedicos() { 
        return medicos; 
    }
    
    public List<Farmaceuta> getFarmaceutas() { 
        return farmaceutas; 
    }
    
    public List<Paciente> getPacientes() { 
        return pacientes; 
    }
    
    public List<Medicamento> getMedicamentos() { 
        return medicamentos; 
    }
    
    public void agregarMedico(Medico m) throws Exception {
        boolean exito = proxy.agregarUsuario(m);
        if (exito) {
            medicos.add(m);
        } else {
            throw new Exception("No se pudo agregar el médico");
        }
    }
    
    public void eliminarMedico(String id) {
        try {
            boolean exito = proxy.eliminarUsuario(id);
            if (exito) {
                medicos.removeIf(m -> m.getId().equals(id));
                refrescarDatos();
            } else {
                throw new Exception("No se pudo eliminar el médico en el servidor");
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error al eliminar: " + ex.getMessage());
        }
    }
    
    public Medico buscarMedico(String id) {
        return medicos.stream()
            .filter(m -> m.getId().equals(id))
            .findFirst()
            .orElse(null);
    }
    
    public void guardarMedicos() {
        refrescarDatos();
    }
    
    public boolean existeMedico(String id) {
        return medicos.stream().anyMatch(m -> m.getId().equals(id));
    }
    
    public void agregarFarmaceuta(Farmaceuta f) throws Exception {
        boolean exito = proxy.agregarUsuario(f);
        if (exito) {
            farmaceutas.add(f);
        } else {
            throw new Exception("No se pudo agregar el farmaceuta");
        }
    }
    
    public void eliminarFarmaceuta(String id) {
        try {
            boolean exito = proxy.eliminarUsuario(id);
            if (exito) {
                farmaceutas.removeIf(f -> f.getId().equals(id));
                refrescarDatos();
            } else {
                throw new Exception("No se pudo eliminar el farmacéuta en el servidor");
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error al eliminar: " + ex.getMessage());
        }
    }
    
    public void guardarFarmaceutas() {
        refrescarDatos();
    }
    
    public boolean existeFarmaceuta(String id) {
        return farmaceutas.stream().anyMatch(f -> f.getId().equals(id));
    }
    
    public void agregarPaciente(Paciente p) throws Exception {
        boolean exito = proxy.agregarPaciente(p);
        if (exito) {
            pacientes.add(p);
        } else {
            throw new Exception("No se pudo agregar el paciente");
        }
    }
    
    public void eliminarPaciente(String id) {
        try {
            boolean exito = proxy.eliminarPaciente(id);
            if (exito) {
                pacientes.removeIf(p -> p.getId().equals(id));
                refrescarDatos();
            } else {
                throw new Exception("No se pudo eliminar el paciente en el servidor");
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error al eliminar: " + ex.getMessage());
        }
    }
    
    public void guardarPacientes() {
        refrescarDatos();
    }
    
    public boolean existePaciente(String id) {
        return pacientes.stream().anyMatch(p -> p.getId().equals(id));
    }
    
    public void agregarMedicamento(Medicamento m) throws Exception {
        boolean exito = proxy.agregarMedicamento(m);
        if (exito) {
            medicamentos.add(m);
        } else {
            throw new Exception("No se pudo agregar el medicamento");
        }
    }
    
    public void eliminarMedicamento(String codigo) {
        try {
            boolean exito = proxy.eliminarMedicamento(codigo);
            if (exito) {
                medicamentos.removeIf(m -> m.getCodigo().equals(codigo));
                refrescarDatos();
            } else {
                throw new Exception("No se pudo eliminar el medicamento en el servidor");
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error al eliminar: " + ex.getMessage());
        }
    }
    
    public void guardarMedicamentos() {
        refrescarDatos();
    }
    
    public boolean existeMedicamento(String codigo) {
        return medicamentos.stream().anyMatch(m -> m.getCodigo().equals(codigo));
    }
    
    public boolean existeUsuario(String id) {
        return usuarios.stream().anyMatch(u -> u.getId().equals(id));
    }
    
    public boolean esIdUnico(String id) {
        return !existeMedico(id) && 
               !existeFarmaceuta(id) && 
               !existePaciente(id) && 
               !existeUsuario(id);
    }
}