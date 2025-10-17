package view;

import javax.swing.*;
import control.ControlLogin;
import model.Usuario;
import java.awt.*;
import java.net.URL;

public class VentanaCambiarClave extends JFrame {
    private JPasswordField txtActual, txtNueva, txtConfirmar;
    private Usuario usuario;
    private JFrame ventanaLogin;
    private ControlLogin controlLogin;

    public VentanaCambiarClave(Usuario usuario, JFrame ventanaLogin, ControlLogin controlLogin) {
        this.usuario = usuario;
        this.ventanaLogin = ventanaLogin;
        this.controlLogin = controlLogin;
        init();
    }

    private ImageIcon escalarIcono(URL url, int ancho, int alto) {
        if (url == null) return null;
        ImageIcon iconoOriginal = new ImageIcon(url);
        Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        return new ImageIcon(imagenEscalada);
    }

    private void init() {
        setTitle("Cambiar Clave");
        setSize(350, 200);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2, 5, 5));
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        URL iconoVentana = getClass().getResource("/imagenes/clave de seguridad logo.png");
        if (iconoVentana != null) {
            setIconImage(new ImageIcon(iconoVentana).getImage());
        }

        if (ventanaLogin != null) ventanaLogin.setVisible(false);

        add(new JLabel("Clave Actual:"));
        txtActual = new JPasswordField();
        add(txtActual);

        add(new JLabel("Clave Nueva:"));
        txtNueva = new JPasswordField();
        add(txtNueva);

        add(new JLabel("Confirmar Clave Nueva:"));
        txtConfirmar = new JPasswordField();
        add(txtConfirmar);

        URL iconoOk = getClass().getResource("/imagenes/Check logo.png");
        URL iconoCancel = getClass().getResource("/imagenes/X logo.png");

        JButton btnOk = (iconoOk != null) ? new JButton(escalarIcono(iconoOk, 32, 32)) : new JButton("Aceptar");
        JButton btnCancel = (iconoCancel != null) ? new JButton(escalarIcono(iconoCancel, 32, 32)) : new JButton("Cancelar");

        btnOk.setBorderPainted(false);
        btnOk.setContentAreaFilled(false);
        btnCancel.setBorderPainted(false);
        btnCancel.setContentAreaFilled(false);

        add(btnOk);
        add(btnCancel);

        btnOk.addActionListener(e -> {
            String actual = new String(txtActual.getPassword());
            String nueva = new String(txtNueva.getPassword());
            String confirmar = new String(txtConfirmar.getPassword());
            controlLogin.cambiarClave(usuario, actual, nueva, confirmar, this, ventanaLogin);
        });

        btnCancel.addActionListener(e -> {
            if (ventanaLogin != null) ventanaLogin.setVisible(true);
            dispose();
        });
    }
}