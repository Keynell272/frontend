package proyecto.view;

import proyecto.model.Usuario;
import proyecto.persistencia.XmlManager;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.List;

public class VentanaCambiarClave extends JFrame {
    private JPasswordField txtActual, txtNueva, txtConfirmar;
    private Usuario usuario;
    private List<Usuario> listaUsuarios;
    private JFrame ventanaLogin; // referencia al login

    public VentanaCambiarClave(Usuario usuario, List<Usuario> listaUsuarios, JFrame ventanaLogin) {
        this.usuario = usuario;
        this.listaUsuarios = listaUsuarios;
        this.ventanaLogin = ventanaLogin;
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
        } else { 
            System.err.println("❌ No se encontró el ícono de la ventana."); 
        }

        // Ocultar login mientras está abierta esta ventana
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

        // Botones con íconos
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

        btnOk.addActionListener(e -> cambiarClave());
        btnCancel.addActionListener(e -> {
            if (ventanaLogin != null) ventanaLogin.setVisible(true); // volver a mostrar login
            dispose();
        });
    }

    private void cambiarClave() {
        String actual = new String(txtActual.getPassword());
        String nueva = new String(txtNueva.getPassword());
        String confirmar = new String(txtConfirmar.getPassword());

        if (!usuario.getClave().equals(actual)) {
            JOptionPane.showMessageDialog(this, "❌ La clave actual no es correcta");
            return;
        }
        if (!nueva.equals(confirmar)) {
            JOptionPane.showMessageDialog(this, "❌ Las claves nuevas no coinciden");
            return;
        }
        if (nueva.isEmpty()) {
            JOptionPane.showMessageDialog(this, "❌ La nueva clave no puede estar vacía");
            return;
        }

        usuario.setClave(nueva);
        XmlManager.guardarUsuarios(listaUsuarios, "usuarios.xml");

        JOptionPane.showMessageDialog(this, "✅ Clave cambiada correctamente");

        if (ventanaLogin != null) ventanaLogin.setVisible(true); // volver al login
        dispose();
    }
}
