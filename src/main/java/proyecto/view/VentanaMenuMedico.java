package proyecto.view;

import javax.swing.*;

public class VentanaMenuMedico extends JFrame {
    public VentanaMenuMedico() {
        setTitle("Menú Médico");
        setSize(300,200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton btnPrescripcion = new JButton("Prescribir Recetas");
        JButton btnDashboard = new JButton("Dashboard");
        JButton btnHistorico = new JButton("Histórico de Recetas");

        JPanel panel = new JPanel();
        panel.add(btnPrescripcion);
        panel.add(btnDashboard);
        panel.add(btnHistorico);

        add(panel);
    }
}
