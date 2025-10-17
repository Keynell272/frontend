package control;

import model.*;
import proxy.ServiceProxy;
import view.VentanaCambiarClave;
import view.VentanaMenuAdmin;
import view.VentanaMenuFarmaceuta;
import view.VentanaMenuMedico;

import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ControlLogin {
    private ServiceProxy proxy;

    public ControlLogin() {
        this.proxy = ServiceProxy.getInstance();
    }

    /**
     * Realiza el login del usuario
     * @return Usuario si las credenciales son correctas, null si no
     */
    public Usuario login(String id, String clave) {
        try {
            Usuario usuario = proxy.login(id, clave);
            return usuario;
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Error al iniciar sesión: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    /**
     * Navegación: abrir la ventana correspondiente según el tipo de usuario
     */
    public void postLogin(JFrame loginWindow, Usuario u) {
        if (u instanceof Medico) {
            new VentanaMenuMedico((Medico) u, new ControlReceta()).setVisible(true);
        } else if (u instanceof Farmaceuta) {
            new VentanaMenuFarmaceuta((Farmaceuta) u, new ControlReceta()).setVisible(true);
        } else {
            new VentanaMenuAdmin(u).setVisible(true);
        }
        
        if (loginWindow != null) {
            loginWindow.dispose();
        }
    }

    public void openChangePassword(JFrame parent, Usuario u) {
        new VentanaCambiarClave(u, parent, this).setVisible(true);
    }

    public void cambiarClave(Usuario usuario, String actual, String nueva, 
                            String confirmar, JFrame ventana, JFrame ventanaLogin) {
        // Validaciones
        if (!usuario.getClave().equals(actual)) {
            JOptionPane.showMessageDialog(ventana, "❌ La clave actual no es correcta");
            return;
        }
        if (!nueva.equals(confirmar)) {
            JOptionPane.showMessageDialog(ventana, "❌ Las claves nuevas no coinciden");
            return;
        }
        if (nueva.isEmpty()) {
            JOptionPane.showMessageDialog(ventana, "❌ La nueva clave no puede estar vacía");
            return;
        }

        try {
            // Llamar al Backend para cambiar la clave
            boolean exito = proxy.cambiarClave(usuario.getId(), nueva);
            
            if (exito) {
                // Actualizar la clave localmente también
                usuario.setClave(nueva);
                
                JOptionPane.showMessageDialog(ventana, "✅ Clave cambiada correctamente");
                
                if (ventanaLogin != null) {
                    ventanaLogin.setVisible(true);
                }
                ventana.dispose();
            } else {
                JOptionPane.showMessageDialog(ventana, 
                    "❌ No se pudo cambiar la clave",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(ventana, 
                "Error al cambiar clave: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void logout(String usuarioId) {
        try {
            proxy.logout(usuarioId);
        } catch (Exception e) {
            System.err.println("Error al hacer logout: " + e.getMessage());
        }
    }
}