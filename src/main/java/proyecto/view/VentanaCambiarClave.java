package proyecto.view;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class VentanaCambiarClave extends JFrame {
    private JPasswordField txtActual, txtNueva, txtConfirmar;

    // Método para escalar íconos
    private ImageIcon escalarIcono(URL url, int ancho, int alto) {
        if (url == null) {
            System.err.println("❌ Imagen no encontrada (URL es null)");
            return null;
        }
        ImageIcon iconoOriginal = new ImageIcon(url);
        Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        return new ImageIcon(imagenEscalada);
    }

    public VentanaCambiarClave() {
        setTitle("Cambiar Clave");

        // Cargar icono de la ventana
        URL iconoVentana = getClass().getResource("/imagenes/clave de seguridad logo.png");
        if (iconoVentana != null) {
            setIconImage(new ImageIcon(iconoVentana).getImage());
        } else {
            System.err.println("❌ No se encontró el ícono de la ventana.");
        }

        setSize(350, 200);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2, 5, 5));
        setResizable(false);
        

        add(new JLabel("Clave Actual:"));
        txtActual = new JPasswordField();
        add(txtActual);

        add(new JLabel("Clave Nueva:"));
        txtNueva = new JPasswordField();
        add(txtNueva);

        add(new JLabel("Confirmar Clave Nueva:"));
        txtConfirmar = new JPasswordField();
        add(txtConfirmar);

        // Cargar imágenes de botones
        URL iconoOk = getClass().getResource("/imagenes/Check logo.png");
        URL iconoCancel = getClass().getResource("/imagenes/X logo.png");

        JButton btnOk, btnCancel;

        if (iconoOk != null && iconoCancel != null) {
            btnOk = new JButton(escalarIcono(iconoOk, 32, 32));
            btnCancel = new JButton(escalarIcono(iconoCancel, 32, 32));

            // Opcional: Estilo plano (sin bordes ni fondo)
            btnOk.setBorderPainted(false);
            btnCancel.setBorderPainted(false);
            btnOk.setContentAreaFilled(false);
            btnCancel.setContentAreaFilled(false);
        } else {
            System.err.println("❌ No se encontraron uno o más íconos de botones. Usando botones de texto.");
            btnOk = new JButton("Aceptar");
            btnCancel = new JButton("Cancelar");
        }

        add(btnOk);
        add(btnCancel);

        // Acción del botón OK
        btnOk.addActionListener(e -> {
            if (new String(txtNueva.getPassword())
                    .equals(new String(txtConfirmar.getPassword()))) {
                JOptionPane.showMessageDialog(this, "Clave cambiada correctamente (demo)");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Las claves nuevas no coinciden");
            }
        });

        // Acción del botón Cancelar
        btnCancel.addActionListener(e -> dispose());
    }
}