package proyecto.view;

import proyecto.control.ControlLogin;
import proyecto.model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;


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

    // En tu constructor o método de inicialización

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
        gbc.insets = new Insets(5,5,5,5);

        // Icono superior (usuario)
        JLabel lblIcono = new JLabel();
        lblIcono.setHorizontalAlignment(SwingConstants.CENTER);
        setIconImage(new ImageIcon(getClass().getResource("/imagenes/Recetas logo.png")).getImage()); // <-- coloca aquí tu imagen
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblIcono, gbc);

        JLabel lblIcono2 = new JLabel();
        lblIcono2.setHorizontalAlignment(SwingConstants.CENTER);

        // Escalar la imagen a 48x48
        lblIcono2.setIcon(escalarIcono2("/imagenes/Seguridad-logo.png", 48, 48));

        // Posicionamiento con GridBagLayout
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
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

        // En tu constructor o método de inicialización
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnEntrar = new JButton(escalarIcono(getClass().getResource("/imagenes/Entrar logo.png"), 32, 32));
        btnBorrar = new JButton(escalarIcono(getClass().getResource("/imagenes/X logo.png"), 32, 32));
        btnCambiarClave = new JButton(escalarIcono(getClass().getResource("/imagenes/clave de seguridad logo.png"), 32, 32));


        // Quitar bordes
        btnEntrar.setBorderPainted(true);
        btnEntrar.setContentAreaFilled(true);
        btnBorrar.setBorderPainted(true);
        btnBorrar.setContentAreaFilled(true);
        btnCambiarClave.setBorderPainted(true);
        btnCambiarClave.setContentAreaFilled(true);

        // Funcionalidad botones
        btnEntrar.addActionListener(this::loginAction);
        btnBorrar.addActionListener(e -> limpiarCampos());
        btnCambiarClave.addActionListener(e -> new VentanaCambiarClave().setVisible(true));

        panelBotones.add(btnEntrar);
        panelBotones.add(btnBorrar);
        panelBotones.add(btnCambiarClave);

        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        panel.add(panelBotones, gbc);

        add(panel, BorderLayout.CENTER);
    }

    private void loginAction(ActionEvent e) {
        String id = txtId.getText().trim();
        String clave = new String(txtClave.getPassword());

        Usuario u = controlLogin.login(id, clave);

        if (u != null) {
            JOptionPane.showMessageDialog(this, "Bienvenido " + u.getNombre() +
                    " (" + u.getClass().getSimpleName() + ")");
            dispose();
            // Aquí abres el menú correspondiente (igual que antes)
        } else {
            JOptionPane.showMessageDialog(this, "Usuario o clave incorrecta");
        }
    }

    private void limpiarCampos() {
        txtId.setText("");
        txtClave.setText("");
    }
}
