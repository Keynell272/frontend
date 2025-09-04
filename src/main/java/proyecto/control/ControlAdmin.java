package proyecto.control;

import proyecto.model.*;
import proyecto.persistencia.XmlManager;

import java.util.*;

public class ControlAdmin {
    private List<Medico> medicos;
    private List<Farmaceuta> farmaceutas;
    private List<Paciente> pacientes;
    private List<Medicamento> medicamentos;

    public ControlAdmin() {
        this.medicos = XmlManager.cargarMedicos("medicos.xml");
        this.farmaceutas = XmlManager.cargarFarmaceutas("farmaceutas.xml");
        this.pacientes = XmlManager.cargarPacientes("pacientes.xml");
        this.medicamentos = XmlManager.cargarMedicamentos("medicamentos.xml");
    }

    // Getters
    public List<Medico> getMedicos() { return medicos; }
    public List<Farmaceuta> getFarmaceutas() { return farmaceutas; }
    public List<Paciente> getPacientes() { return pacientes; }
    public List<Medicamento> getMedicamentos() { return medicamentos; }


    // ------------------ CRUD Médicos ------------------
    public void agregarMedico(Medico m) { medicos.add(m); }
    public void eliminarMedico(String id) { medicos.removeIf(m -> m.getId().equals(id)); }
    public Medico buscarMedico(String id) {
        return medicos.stream().filter(m -> m.getId().equals(id)).findFirst().orElse(null);
    }
    public void guardarMedicos() {
        XmlManager.guardarMedicos(medicos, "medicos.xml");
    }
    // ------------------ CRUD Farmaceutas ------------------
    public void agregarFarmaceuta(Farmaceuta f) { farmaceutas.add(f); }
    public void eliminarFarmaceuta(String id) { farmaceutas.removeIf(f -> f.getId().equals(id)); }
    public void guardarFarmaceutas() {
        XmlManager.guardarFarmaceutas(farmaceutas, "farmaceutas.xml");
    }


    // ------------------ CRUD Pacientes ------------------
    public void agregarPaciente(Paciente p) { pacientes.add(p); }
    public void eliminarPaciente(String id) { pacientes.removeIf(p -> p.getId().equals(id)); }

    // ------------------ CRUD Medicamentos ------------------
    public void agregarMedicamento(Medicamento m) { medicamentos.add(m); }
    public void eliminarMedicamento(String codigo) { medicamentos.removeIf(m -> m.getCodigo().equals(codigo)); }
    public void guardarMedicamentos() {
        XmlManager.guardarMedicamentos(medicamentos, "medicamentos.xml");
    }
    
    

}
