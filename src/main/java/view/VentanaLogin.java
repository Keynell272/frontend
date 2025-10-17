package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import control.ControlLogin;
import model.Usuario;
import proxy.ServiceProxy;

public class VentanaLogin extends JFrame {
    private ControlLogin controlLogin;
    private JTextField txtId;
    private JPasswordField txtClave;
    private JButton btnEntrar, btnBorrar, btnCambiarClave;

    public VentanaLogin() {
        this.controlLogin = new ControlLogin();
        init();
    }

    private ImageIcon escalarIcono(URL url, int ancho, int alto) {
        if (url == null) return null;
        ImageIcon iconoOriginal = new ImageIcon(url);
        Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        return new ImageIcon(imagenEscalada);
    }

    private ImageIcon escalarIcono2(String path, int ancho, int alto) {
        ImageIcon iconoOriginal = new ImageIcon(getClass().getResource(path));
        Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        return new ImageIcon(imagenEscalada);
    }

    private void init() {
        setTitle("Login - Sistema de Recetas");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setIconImage(escalarIcono(getClass().getResource("/imagenes/Recetas logo.png"), 32, 32).getImage());

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);

        JLabel lblIcono2 = new JLabel();
        lblIcono2.setHorizontalAlignment(SwingConstants.CENTER);
        lblIcono2.setIcon(escalarIcono2("/imagenes/Seguridad-logo.png", 48, 48));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblIcono2, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        panel.add(new JLabel("ID"), gbc);
        gbc.gridx = 1;
        txtId = new JTextField(15);
        panel.add(txtId, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        panel.add(new JLabel("CLAVE"), gbc);
        gbc.gridx = 1;
        txtClave = new JPasswordField(15);
        panel.add(txtClave, gbc);

        JPanel panelBotones = new JPanel(new FlowLayout());
        btnEntrar = new JButton(escalarIcono(getClass().getResource("/imagenes/Entrar logo.png"), 32, 32));
        btnBorrar = new JButton(escalarIcono(getClass().getResource("/imagenes/X logo.png"), 32, 32));
        btnCambiarClave = new JButton(escalarIcono(getClass().getResource("/imagenes/clave de seguridad logo.png"), 32, 32));

        btnEntrar.setBorderPainted(true);
        btnEntrar.setContentAreaFilled(true);
        btnBorrar.setBorderPainted(true);
        btnBorrar.setContentAreaFilled(true);
        btnCambiarClave.setBorderPainted(true);
        btnCambiarClave.setContentAreaFilled(true);

        btnEntrar.addActionListener(this::loginAction);
        btnBorrar.addActionListener(e -> limpiarCampos());
        btnCambiarClave.addActionListener(this::cambiarClaveAction);

        panelBotones.add(btnEntrar);
        panelBotones.add(btnBorrar);
        panelBotones.add(btnCambiarClave);

        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        panel.add(panelBotones, gbc);

        add(panel, BorderLayout.CENTER);
    }

    private void cambiarClaveAction(ActionEvent e) {
        String id = txtId.getText().trim();
        String clave = new String(txtClave.getPassword());
        Usuario u = controlLogin.login(id, clave);

        if (u != null) {
            controlLogin.openChangePassword(this, u);
        } else {
            JOptionPane.showMessageDialog(this, "Debe iniciar sesión para cambiar la clave");
        }
    }

    private void loginAction(ActionEvent e) {
        String id = txtId.getText().trim();
        String clave = new String(txtClave.getPassword());

        if (id.isEmpty() || clave.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar ID y clave");
            return;
        }

        btnEntrar.setEnabled(false);
        btnEntrar.setText("Conectando...");

        Usuario u = controlLogin.login(id, clave);

        if (u != null) {
            JOptionPane.showMessageDialog(this, "Bienvenido " + u.getNombre() + " (" + u.getRol() + ")");
            controlLogin.postLogin(this, u);
        } else {
            JOptionPane.showMessageDialog(this, "Usuario o clave incorrecta");
            btnEntrar.setEnabled(true);
            btnEntrar.setText("");
        }
    }

    private void limpiarCampos() {
        txtId.setText("");
        txtClave.setText("");
    }
}