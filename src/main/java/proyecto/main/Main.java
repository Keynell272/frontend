package proyecto.main;

import proyecto.model.*;
import proyecto.control.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        // ----------------- Crear listas iniciales -----------------
        List<Usuario> usuarios = new ArrayList<>();
        List<Medico> medicos = new ArrayList<>();
        List<Farmaceuta> farmaceutas = new ArrayList<>();
        List<Paciente> pacientes = new ArrayList<>();
        List<Medicamento> medicamentos = new ArrayList<>();
        List<Receta> recetas = new ArrayList<>();

        // ----------------- Crear datos de prueba -----------------
        Medico m1 = new Medico("m1", "123", "Dr. Juan", "Cardiología");
        Farmaceuta f1 = new Farmaceuta("f1", "123", "Farmacéuta Ana");
        Administrador a1 = new Administrador("admin", "123", "Super Admin");
        Paciente p1 = new Paciente("p1", "Pedro Pérez", new Date(), "8888-8888");
        Medicamento med1 = new Medicamento("med1", "Acetaminofén", "500mg");

        medicos.add(m1);
        farmaceutas.add(f1);
        pacientes.add(p1);
        medicamentos.add(med1);
        usuarios.add(m1);
        usuarios.add(f1);
        usuarios.add(a1);

        // ----------------- Controladores -----------------
        ControlLogin controlLogin = new ControlLogin(usuarios);
        ControlAdmin controlAdmin = new ControlAdmin(medicos, farmaceutas, pacientes, medicamentos);
        ControlReceta controlReceta = new ControlReceta(recetas);
        ControlDespacho controlDespacho = new ControlDespacho(recetas);
        ControlDashboard controlDashboard = new ControlDashboard(recetas);

        // ----------------- Pruebas -----------------
        System.out.println("=== LOGIN ===");
        Usuario u = controlLogin.login("m1", "123");
        System.out.println(u != null ? "Login correcto: " + u.getNombre() : "Login fallido");

        System.out.println("\n=== CRUD Pacientes (Admin) ===");
        Paciente p2 = new Paciente("p2", "María López", new Date(), "7777-7777");
        controlAdmin.agregarPaciente(p2);
        System.out.println("Pacientes totales: " + pacientes.size());

        System.out.println("\n=== PRESCRIPCIÓN (Médico) ===");
        Receta r1 = controlReceta.crearReceta("r1", new Date(), new Date());
        controlReceta.agregarMedicamento(r1, med1, 10, "Tomar cada 8 horas", 5);
        System.out.println("Receta creada con estado: " + r1.getEstado() + " y detalles: " + r1.getDetalles().size());

        System.out.println("\n=== DESPACHO (Farmaceuta) ===");
        List<Receta> disponibles = controlDespacho.recetasDisponiblesParaDespacho(new Date());
        System.out.println("Recetas disponibles: " + disponibles.size());
        if (!disponibles.isEmpty()) {
            controlDespacho.cambiarEstado(disponibles.get(0), "proceso");
            System.out.println("Estado actualizado: " + disponibles.get(0).getEstado());
        }

        System.out.println("\n=== DASHBOARD ===");
        Map<Integer, Integer> medsPorMes = controlDashboard.medicamentosPorMes(Calendar.getInstance().get(Calendar.YEAR));
        Map<String, Long> recetasEstado = controlDashboard.recetasPorEstado();
        System.out.println("Medicamentos por mes: " + medsPorMes);
        System.out.println("Recetas por estado: " + recetasEstado);
    }
}
