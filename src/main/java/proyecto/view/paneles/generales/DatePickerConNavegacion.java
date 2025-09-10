package proyecto.view.paneles.generales;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatePickerConNavegacion {
    private JTextField txtFecha;
    private JButton btnCalendario;
    private JPopupMenu popup;
    private Calendar cal;

    public DatePickerConNavegacion(JTextField txtFecha, JButton btnCalendario) {
        this.txtFecha = txtFecha;
        this.btnCalendario = btnCalendario;
        this.cal = Calendar.getInstance();
        this.popup = new JPopupMenu();

        // Panel contenedor para el calendario
        JPanel[] panelHolder = new JPanel[1];
        panelHolder[0] = crearCalendario(cal, date -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            txtFecha.setText(sdf.format(date));
            popup.setVisible(false);
        }, panelHolder, popup);

        btnCalendario.addActionListener(e -> {
            popup.removeAll();
            popup.add(panelHolder[0]);
            popup.show(btnCalendario, 0, btnCalendario.getHeight());
        });
    }

    // ---- Métodos auxiliares del calendario ----
    private JPanel crearCalendario(Calendar cal, java.util.function.Consumer<Date> onDateSelected,
                                   JPanel[] panelHolder, JPopupMenu popup) {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel header = new JPanel(new FlowLayout());
        JButton btnPrevMes = new JButton("<");
        JButton btnNextMes = new JButton(">");
        JButton btnPrevAnio = new JButton("<<");
        JButton btnNextAnio = new JButton(">>");

        JLabel lblMesAnio = new JLabel(getMesAnio(cal), SwingConstants.CENTER);

        header.add(btnPrevAnio);
        header.add(btnPrevMes);
        header.add(lblMesAnio);
        header.add(btnNextMes);
        header.add(btnNextAnio);

        panel.add(header, BorderLayout.NORTH);

        JPanel diasPanel = new JPanel(new GridLayout(0, 7));
        actualizarDias(cal, diasPanel, onDateSelected);
        panel.add(diasPanel, BorderLayout.CENTER);

        btnPrevMes.addActionListener(e -> {
            cal.add(Calendar.MONTH, -1);
            lblMesAnio.setText(getMesAnio(cal));
            diasPanel.removeAll();
            actualizarDias(cal, diasPanel, onDateSelected);
            diasPanel.revalidate();
            diasPanel.repaint();
        });

        btnNextMes.addActionListener(e -> {
            cal.add(Calendar.MONTH, 1);
            lblMesAnio.setText(getMesAnio(cal));
            diasPanel.removeAll();
            actualizarDias(cal, diasPanel, onDateSelected);
            diasPanel.revalidate();
            diasPanel.repaint();
        });

        btnPrevAnio.addActionListener(e -> {
            cal.add(Calendar.YEAR, -1);
            lblMesAnio.setText(getMesAnio(cal));
            diasPanel.removeAll();
            actualizarDias(cal, diasPanel, onDateSelected);
            diasPanel.revalidate();
            diasPanel.repaint();
        });

        btnNextAnio.addActionListener(e -> {
            cal.add(Calendar.YEAR, 1);
            lblMesAnio.setText(getMesAnio(cal));
            diasPanel.removeAll();
            actualizarDias(cal, diasPanel, onDateSelected);
            diasPanel.revalidate();
            diasPanel.repaint();
        });

        panelHolder[0] = panel;
        return panel;
    }

    private void actualizarDias(Calendar cal, JPanel diasPanel, java.util.function.Consumer<Date> onDateSelected) {
        String[] dias = {"Do", "Lu", "Ma", "Mi", "Ju", "Vi", "Sa"};
        for (String d : dias) diasPanel.add(new JLabel(d, SwingConstants.CENTER));

        Calendar tempCal = (Calendar) cal.clone();
        tempCal.set(Calendar.DAY_OF_MONTH, 1);
        int primerDia = tempCal.get(Calendar.DAY_OF_WEEK) - 1;
        int diasEnMes = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 0; i < primerDia; i++) diasPanel.add(new JLabel(""));
        for (int d = 1; d <= diasEnMes; d++) {
            JButton btn = new JButton("" + d);
            int dia = d;
            btn.addActionListener(e -> {
                cal.set(Calendar.DAY_OF_MONTH, dia);
                onDateSelected.accept(cal.getTime());
            });
            diasPanel.add(btn);
        }
    }

    private String getMesAnio(Calendar cal) {
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                          "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        return meses[cal.get(Calendar.MONTH)] + " " + cal.get(Calendar.YEAR);
    }
}