package proyecto.view;

import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import proyecto.control.ControlLogin;
import proyecto.model.Farmaceuta;
import proyecto.model.Medico;
import proyecto.model.Usuario;

public class VentanaLogin extends JFrame {
    private ControlLogin controlLogin;
    private JTextField txtId;
    private JPasswordField txtClave;
    private JButton btnEntrar, btnBorrar, btnCambiarClave;

    public VentanaLogin(ControlLogin controlLogin) {
        this.controlLogin = controlLogin;
        init();
    }

    // Método para escalar imágenes
    private ImageIcon escalarIcono(URL url, int ancho, int alto) {
        ImageIcon iconoOriginal = new ImageIcon(url);
        Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        return new ImageIcon(imagenEscalada);
    }

    private ImageIcon escalarIcono2(String ruta, int ancho, int alto) {
        ImageIcon iconoOriginal = new ImageIcon(getClass().getResource(ruta));
        Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        return new ImageIcon(imagenEscalada);
    }

    private void init() {
        setTitle("Recetas");
        setSize(350, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setResizable(false);

        // Panel central
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(230, 233, 237));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Icono superior
        JLabel lblIcono2 = new JLabel();
        lblIcono2.setHorizontalAlignment(SwingConstants.CENTER);
        lblIcono2.setIcon(escalarIcono2("/imagenes/Seguridad-logo.png", 48, 48));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblIcono2, gbc);

        // Campo ID
        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        panel.add(new JLabel("ID"), gbc);
        gbc.gridx = 1;
        txtId = new JTextField(15);
        panel.add(txtId, gbc);

        // Campo Clave
        gbc.gridy = 2; gbc.gridx = 0;
        panel.add(new JLabel("CLAVE"), gbc);
        gbc.gridx = 1;
        txtClave = new JPasswordField(15);
        panel.add(txtClave, gbc);

        // Botones
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

        panelBotones.add(btnEntrar);
        panelBotones.add(btnBorrar);
        panelBotones.add(btnCambiarClave);

        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        panel.add(panelBotones, gbc);

        add(panel, BorderLayout.CENTER);

        // Acción del botón Cambiar Clave
        btnCambiarClave.addActionListener(e -> {
            String id = txtId.getText().trim();
            String clave = new String(txtClave.getPassword());
            Usuario u = controlLogin.login(id, clave);

            if (u != null) {
                // Asumiendo que VentanaCambiarClave acepta Usuario y ControlLogin
                
            } else {
                JOptionPane.showMessageDialog(this, "Debe iniciar sesión para cambiar la clave");
            }
        });
    }

    // Acción de login con ventanas según tipo de usuario
    private void loginAction(ActionEvent e) {
        String id = txtId.getText().trim();
        String clave = new String(txtClave.getPassword());

        Usuario u = controlLogin.login(id, clave);

        if (u != null) {
            JOptionPane.showMessageDialog(this, "Bienvenido " + u.getId() + " (" + u.getRol() + ")");

            if (u instanceof Medico) {
                new VentanaMenuMedico((Medico) u).setVisible(true);
            } else if (u instanceof Farmaceuta) {
               
            } else {
                new VentanaMenuAdmin(u).setVisible(true);
            }

            dispose();

        } else {
            JOptionPane.showMessageDialog(this, "Usuario o clave incorrecta");
        }
    }

    private void limpiarCampos() {
        txtId.setText("");
        txtClave.setText("");
    }
}