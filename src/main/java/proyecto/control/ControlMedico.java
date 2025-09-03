package proyecto.control;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import proyecto.model.Medicamento;
import proyecto.model.Paciente;
import proyecto.view.VentanaMenuMedico;

public class ControlMedico {

    private VentanaMenuMedico vista;
    private List<String[]> medicamentos; 
    private List<Paciente> pacientes;

    public ControlMedico(VentanaMenuMedico vista) {
        this.vista = vista;
        this.medicamentos = new ArrayList<>();
        this.pacientes = cargarPacientesDesdeXML("pacientes.xml");
    }

    // Método para cargar pacientes desde XML (moved here)
    private List<Paciente> cargarPacientesDesdeXML(String rutaArchivo) {
        List<Paciente> lista = new ArrayList<>();
        try {
            File archivo = new File(rutaArchivo);
            if (!archivo.exists()) {
                JOptionPane.showMessageDialog(vista, "No se encontró el archivo pacientes.xml", "Archivo no encontrado", JOptionPane.ERROR_MESSAGE);
                return lista;
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(archivo);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("paciente");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            for (int i = 0; i < nList.getLength(); i++) {
                Element elem = (Element) nList.item(i);

                String id = elem.getAttribute("id");
                String nombre = elem.getAttribute("nombre");
                String telefono = elem.getAttribute("telefono");
                String fechaNacStr = elem.getAttribute("fechaNacimiento");

                Date fechaNacimiento = sdf.parse(fechaNacStr);
                lista.add(new Paciente(id, nombre, fechaNacimiento, telefono));
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(vista, "Error al cargar pacientes.xml", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return lista;
    }

    // Nuevo método para mostrar ventana de búsqueda y seleccionar paciente
    public void buscarPaciente() {
        // Construir y mostrar ventana modal para seleccionar paciente
        JDialog dialog = new JDialog(vista, "Pacientes", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(vista);
        dialog.setLayout(new java.awt.BorderLayout());

        // Panel de filtro
        JPanel panelFiltro = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        JComboBox<String> comboFiltro = new JComboBox<>(new String[]{"nombre", "id"});
        JTextField txtFiltro = new JTextField(20);
        panelFiltro.add(new JLabel("Filtrar por:"));
        panelFiltro.add(comboFiltro);
        panelFiltro.add(txtFiltro);
        dialog.add(panelFiltro, java.awt.BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"Id", "Nombre", "Telefono", "Fec. Nac."};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable tabla = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabla);
        dialog.add(scroll, java.awt.BorderLayout.CENTER);

        // Botones
        JPanel panelBotones = new JPanel();
        JButton btnOK = new JButton("OK");
        JButton btnCancel = new JButton("Cancel");
        panelBotones.add(btnOK);
        panelBotones.add(btnCancel);
        dialog.add(panelBotones, java.awt.BorderLayout.SOUTH);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Runnable cargarTabla = () -> {
            modelo.setRowCount(0);
            String criterio = comboFiltro.getSelectedItem().toString();
            String filtroTexto = txtFiltro.getText().trim().toLowerCase();

            for (Paciente p : pacientes) {
                String valor = criterio.equals("nombre") ? p.getNombre() : p.getId();
                if (valor.toLowerCase().contains(filtroTexto)) {
                    modelo.addRow(new String[]{
                        p.getId(),
                        p.getNombre(),
                        p.getTelefono(),
                        sdf.format(p.getFechaNacimiento())
                    });
                }
            }
        };

        cargarTabla.run();

        txtFiltro.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyReleased(java.awt.event.KeyEvent e) {
                cargarTabla.run();
            }
        });

        comboFiltro.addActionListener(e -> cargarTabla.run());

        btnOK.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila != -1) {
                String idSeleccionado = (String) modelo.getValueAt(fila, 0);
                Paciente seleccionado = pacientes.stream()
                        .filter(p -> p.getId().equals(idSeleccionado))
                        .findFirst()
                        .orElse(null);

                if (seleccionado != null) {
                    vista.setPacienteSeleccionado(seleccionado);
                    dialog.dispose();
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Seleccione un paciente.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    public List<Medicamento> cargarMedicamentosDesdeXML(String rutaArchivo) {
    List<Medicamento> lista = new ArrayList<>();
    try {
        File archivo = new File(rutaArchivo);
        if (!archivo.exists()) {
            JOptionPane.showMessageDialog(null, "No se encontró el archivo medicamentos.xml", "Archivo no encontrado", JOptionPane.ERROR_MESSAGE);
            return lista;
        }

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(archivo);
        doc.getDocumentElement().normalize();

        NodeList nList = doc.getElementsByTagName("medicamento");

        for (int i = 0; i < nList.getLength(); i++) {
            Element elem = (Element) nList.item(i);

            String id = elem.getAttribute("codigo");
            String nombre = elem.getAttribute("nombre");
            String presentacion = elem.getAttribute("presentacion");
            

            Medicamento medicamento = new Medicamento(id, nombre, presentacion);
            lista.add(medicamento);
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al cargar medicamentos.xml", "Error", JOptionPane.ERROR_MESSAGE);
    }
    return lista;
}
      public void limpiarFormulario() {
        medicamentos.clear();
        vista.limpiarCampos();
    }

    public void descartarMedicamento() {
    int filaSeleccionada = vista.getTablaMedicamentos().getSelectedRow();
    if (filaSeleccionada == -1) {
        JOptionPane.showMessageDialog(vista, "Seleccione un medicamento para descartar.", "Aviso", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Eliminar de la lista interna
    medicamentos.remove(filaSeleccionada);

    // Eliminar fila de la tabla (sin recargar todo)
    DefaultTableModel modelo = (DefaultTableModel) vista.getTablaMedicamentos().getModel();
    modelo.removeRow(filaSeleccionada);
    }
    
    


}


