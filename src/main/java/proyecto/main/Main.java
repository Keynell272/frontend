package proyecto.main;

import proyecto.model.*;
import proyecto.control.*;
import proyecto.view.*;

import javax.swing.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        // Datos de prueba (usuarios quemados en memoria)
        List<Usuario> usuarios = new ArrayList<>();
        usuarios.add(new Administrador("ADM-111", "123", "Administrador General"));
        usuarios.add(new Medico("MED-222", "abc", "Dr. Juan", "Cardiología"));
        usuarios.add(new Farmaceuta("FAR-333", "xyz", "Farmacéutica Ana"));

        ControlLogin controlLogin = new ControlLogin(usuarios);

        // Arrancar la GUI en el hilo de Swing
        SwingUtilities.invokeLater(() -> {
            new VentanaLogin(controlLogin).setVisible(true);
        });
    }
}
