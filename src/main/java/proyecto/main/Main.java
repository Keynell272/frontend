package proyecto.main;

import proyecto.model.*;
import proyecto.control.ControlLogin;
import proyecto.persistencia.XmlManager;
import proyecto.view.VentanaLogin;

import javax.swing.*;
import java.io.File;
import java.util.*;


public class Main {
    public static void main(String[] args) {
        // Ruta del archivo XML de usuarios
        String rutaUsuarios = "usuarios.xml";

        // Lista de usuarios
        List<Usuario> usuarios;

        File archivo = new File(rutaUsuarios);
        if (archivo.exists()) {
            // 🔹 Cargar desde XML
            usuarios = XmlManager.cargarUsuarios(rutaUsuarios);
            System.out.println("Usuarios cargados desde " + rutaUsuarios);
        } else {
            // 🔹 Crear usuarios de prueba y guardar en XML
            usuarios = new ArrayList<>();
            usuarios.add(new Administrador("ADM-111", "123", "Administrador General"));
            usuarios.add(new Medico("MED-222", "abc", "Dr. Juan", "Cardiología"));
            usuarios.add(new Farmaceuta("FAR-333", "xyz", "Farmacéutica Ana"));

            XmlManager.guardarUsuarios(usuarios, rutaUsuarios);
            System.out.println("Archivo " + rutaUsuarios + " creado con usuarios de prueba.");
        }

        ControlLogin controlLogin = new ControlLogin();

        SwingUtilities.invokeLater(() -> {
            new VentanaLogin(controlLogin).setVisible(true);
        });
    }
}
