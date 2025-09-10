package proyecto.view.paneles.generales;

import javax.swing.*;
import java.awt.*;

public class PanelAcercaDe extends JPanel {

    public PanelAcercaDe() {
        setLayout(new BorderLayout());
        setBackground(new Color(230, 233, 237));

        // --- Título ---
        JLabel lblTitulo = new JLabel("Prescripción y Despacho de Recetas", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(Color.BLUE);
        add(lblTitulo, BorderLayout.NORTH);

        // --- Imagen central ---
        JLabel lblImagen = new JLabel();
        lblImagen.setHorizontalAlignment(SwingConstants.CENTER);
        lblImagen.setIcon(cargarIcono("/imagenes/hospital imagen principal.jpg", 700, 300));
        add(lblImagen, BorderLayout.CENTER);

        // --- Subtítulo ---
        JPanel subPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        subPanel.setBackground(new Color(230, 233, 237));
        JLabel lblInfo = new JLabel("Total Soft Inc.   @totalsoft   Tel. 67197691");
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 14));
        subPanel.add(lblInfo);
        add(subPanel, BorderLayout.SOUTH);
    }

    // Método auxiliar para cargar imágenes
    private ImageIcon cargarIcono(String ruta, int ancho, int alto) {
        java.net.URL location = getClass().getResource(ruta);
        if (location == null) {
            System.err.println("No se encontró la imagen: " + ruta);
            return null;
        }
        ImageIcon icono = new ImageIcon(location);
        Image imagen = icono.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        return new ImageIcon(imagen);
    }
}
