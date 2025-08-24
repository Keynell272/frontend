package proyecto.view;

import javax.swing.*;

public class VentanaMenuFarmaceuta extends JFrame {
    public VentanaMenuFarmaceuta() {
        setTitle("Menú Farmaceuta");
        setSize(300,200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton btnDespacho = new JButton("Despacho de Recetas");
        JButton btnDashboard = new JButton("Dashboard");
        JButton btnHistorico = new JButton("Histórico de Recetas");

        JPanel panel = new JPanel();
        panel.add(btnDespacho);
        panel.add(btnDashboard);
        panel.add(btnHistorico);

        add(panel);
    }
}
