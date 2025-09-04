package proyecto.main;

import javax.swing.SwingUtilities;

import proyecto.control.ControlLogin;
import proyecto.view.VentanaLogin;


public class Main {
    public static void main(String[] args) {
        ControlLogin controlLogin = new ControlLogin();

        SwingUtilities.invokeLater(() -> {
            new VentanaLogin(controlLogin).setVisible(true);
        });
    }
}
