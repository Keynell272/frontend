package proyecto.view.paneles.generales;
import javax.swing.*;
import java.awt.*;
import java.util.Map;

/** Gráfico de pastel dibujado a mano (sin librerías). */
public class PanelPastel extends JPanel {
    private Map<String, Integer> datos;
    private String titulo = "Recetas";

    public PanelPastel() { setBackground(Color.WHITE); }

    public void setDatos(Map<String, Integer> datos) {
        this.datos = datos;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (datos == null || datos.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();

        // Título
        g2.setColor(Color.BLACK);
        Font old = g2.getFont();
        g2.setFont(old.deriveFont(Font.BOLD, 16f));
        String t = titulo;
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(t, (w - fm.stringWidth(t)) / 2, 24);
        g2.setFont(old);

        int size = Math.min(w, h) - 80;
        int x = (w - size) / 2 - 40; // espacio para leyenda a la derecha
        int y = (h - size) / 2 + 10;

        Color[] colors = new Color[]{
                new Color(220, 20, 60), new Color(46, 104, 215),
                new Color(0, 153, 0), new Color(255, 165, 0),
                new Color(128, 0, 128)
        };

        int total = 0;
        for (int v : datos.values()) total += v;
        if (total == 0) total = 1;

        int start = 0, i = 0;
        for (Map.Entry<String, Integer> e : datos.entrySet()) {
            int valor = e.getValue();
            int angle = (int) Math.round(360.0 * valor / total);
            g2.setColor(colors[i % colors.length]);
            g2.fillArc(x, y, size, size, start, angle);
            start += angle; i++;
        }
        g2.setColor(Color.GRAY);
        g2.drawOval(x, y, size, size);

        // Leyenda
        int lx = x + size + 20, ly = y + 10; i = 0;
        for (Map.Entry<String, Integer> e : datos.entrySet()) {
            String nombre = e.getKey();
            int valor = e.getValue();
            double pct = 100.0 * valor / total;

            g2.setColor(colors[i % colors.length]);
            g2.fillRect(lx, ly - 10, 12, 12);
            g2.setColor(Color.BLACK);
            g2.drawRect(lx, ly - 10, 12, 12);
            String label = nombre.toUpperCase() + " - " + valor + " (" + String.format("%.0f", pct) + "%)";
            g2.drawString(label, lx + 18, ly);
            ly += 20; i++;
        }
        g2.dispose();
    }
}