package proyecto.view;

import javax.swing.*;

public class VentanaMenuAdmin extends JFrame {
    public VentanaMenuAdmin() {
        setTitle("Menú Administrador");
        setSize(400,200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton btnMedicos = new JButton("Gestionar Médicos");
        JButton btnFarmaceutas = new JButton("Gestionar Farmaceutas");
        JButton btnPacientes = new JButton("Gestionar Pacientes");
        JButton btnMedicamentos = new JButton("Gestionar Medicamentos");
        JButton btnDashboard = new JButton("Dashboard");
        JButton btnHistorico = new JButton("Histórico de Recetas");

        JPanel panel = new JPanel();
        panel.add(btnMedicos);
        panel.add(btnFarmaceutas);
        panel.add(btnPacientes);
        panel.add(btnMedicamentos);
        panel.add(btnDashboard);
        panel.add(btnHistorico);

        add(panel);
    }
}
