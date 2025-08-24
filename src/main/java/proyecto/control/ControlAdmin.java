package proyecto.control;

import proyecto.model.*;
import java.util.*;

public class ControlAdmin {
    private List<Medico> medicos;
    private List<Farmaceuta> farmaceutas;
    private List<Paciente> pacientes;
    private List<Medicamento> medicamentos;

    public ControlAdmin(List<Medico> medicos, List<Farmaceuta> farmaceutas,
                        List<Paciente> pacientes, List<Medicamento> medicamentos) {
        this.medicos = medicos;
        this.farmaceutas = farmaceutas;
        this.pacientes = pacientes;
        this.medicamentos = medicamentos;
    }

    // ------------------ CRUD Médicos ------------------
    public void agregarMedico(Medico m) { medicos.add(m); }
    public void eliminarMedico(String id) { medicos.removeIf(m -> m.getId().equals(id)); }
    public Medico buscarMedico(String id) {
        return medicos.stream().filter(m -> m.getId().equals(id)).findFirst().orElse(null);
    }
    

    // ------------------ CRUD Farmaceutas ------------------
    public void agregarFarmaceuta(Farmaceuta f) { farmaceutas.add(f); }
    public void eliminarFarmaceuta(String id) { farmaceutas.removeIf(f -> f.getId().equals(id)); }

    // ------------------ CRUD Pacientes ------------------
    public void agregarPaciente(Paciente p) { pacientes.add(p); }
    public void eliminarPaciente(String id) { pacientes.removeIf(p -> p.getId().equals(id)); }

    // ------------------ CRUD Medicamentos ------------------
    public void agregarMedicamento(Medicamento m) { medicamentos.add(m); }
    public void eliminarMedicamento(String codigo) { medicamentos.removeIf(m -> m.getCodigo().equals(codigo)); }
    
    public List<Medico> getMedicos() {
        return medicos;
    }
}
