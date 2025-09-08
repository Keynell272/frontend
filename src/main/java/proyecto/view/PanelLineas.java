package proyecto.view;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class PanelLineas extends JPanel {
    private Map<String, Map<Integer, Integer>> series = new LinkedHashMap<>();
    private java.util.List<Integer> mesesOrdenados = new ArrayList<>();
    private int anio = 2025;
    private String titulo = "Medicamentos";

    public PanelLineas() {
        setBackground(Color.WHITE);
    }

    public void setDatos(int anio, java.util.List<Integer> mesesOrdenados,
                         Map<String, Map<Integer, Integer>> series) {
        this.anio = anio;
        this.mesesOrdenados = new ArrayList<>(mesesOrdenados);
        this.series = new LinkedHashMap<>(series);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (mesesOrdenados == null || mesesOrdenados.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        int left = 60, right = 30, top = 40, bottom = 50;

        // Título
        g2.setColor(Color.BLACK);
        Font old = g2.getFont();
        g2.setFont(old.deriveFont(Font.BOLD, 16f));
        String t = titulo;
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(t, (w - fm.stringWidth(t)) / 2, 24);
        g2.setFont(old);

        // Área de dibujo
        int x0 = left, y0 = h - bottom, x1 = w - right, y1 = top;
        g2.setColor(new Color(230,230,230));
        g2.fillRect(x0, y1, x1 - x0, y0 - y1);
        g2.setColor(Color.GRAY);
        g2.drawRect(x0, y1, x1 - x0, y0 - y1);

        // Max Y
        int maxY = 0;
        for (Map<Integer, Integer> m : series.values()) {
            for (int mes : mesesOrdenados) maxY = Math.max(maxY, m.getOrDefault(mes, 0));
        }
        if (maxY == 0) maxY = 10;

        // Grid horizontal
        int lineas = 5;
        for (int i = 0; i <= lineas; i++) {
            int y = y0 - (i * (y0 - y1) / lineas);
            g2.setColor(new Color(210,210,210));
            g2.drawLine(x0, y, x1, y);
            g2.setColor(Color.DARK_GRAY);
            int val = (int) Math.round(i * (double) maxY / lineas);
            g2.drawString(Integer.toString(val), x0 - 35, y + 4);
        }

        // Eje X
        int puntos = mesesOrdenados.size();
        int pasoX = (x1 - x0) / Math.max(1, puntos - 1);
        for (int i = 0; i < puntos; i++) {
            int x = x0 + i * pasoX;
            g2.setColor(new Color(180,180,180));
            g2.drawLine(x, y0, x, y0 + 4);
            g2.setColor(Color.BLACK);
            String label = anio + "-" + mesesOrdenados.get(i);
            int sw = g2.getFontMetrics().stringWidth(label);
            g2.drawString(label, x - sw/2, y0 + 20);
        }
        g2.drawString("Mes", (x0 + x1)/2 - 10, h - 10);

        // Colores
        Color[] colors = new Color[]{
                new Color(220, 20, 60),  // rojo
                new Color(46, 104, 215), // azul
                new Color(0, 153, 0),    // verde
                new Color(255, 165, 0),  // naranja
                new Color(128, 0, 128)   // morado
        };

        // Series
        int idx = 0;
        for (Map.Entry<String, Map<Integer, Integer>> e : series.entrySet()) {
            String nombre = e.getKey();
            Map<Integer, Integer> serie = e.getValue();
            Color c = colors[idx % colors.length];
            idx++;

            int xPrev = -1, yPrev = -1;
            g2.setColor(c);
            for (int i = 0; i < puntos; i++) {
                int mes = mesesOrdenados.get(i);
                int valor = serie.getOrDefault(mes, 0);
                double ratio = valor / (double) maxY;
                int x = x0 + i * pasoX;
                int y = y0 - (int) Math.round(ratio * (y0 - y1));

                g2.fillOval(x - 4, y - 4, 8, 8);
                if (xPrev != -1) g2.drawLine(xPrev, yPrev, x, y);
                xPrev = x; yPrev = y;
            }
        }

        // Leyenda
        int lx = x0 + 10, ly = y1 + 10; idx = 0;
        for (String nombre : series.keySet()) {
            Color c = colors[idx % colors.length];
            g2.setColor(c);
            g2.fillRect(lx, ly - 10, 12, 12);
            g2.setColor(Color.BLACK);
            g2.drawRect(lx, ly - 10, 12, 12);
            g2.drawString(nombre, lx + 18, ly);
            ly += 18; idx++;
        }
        g2.dispose();
    }
}