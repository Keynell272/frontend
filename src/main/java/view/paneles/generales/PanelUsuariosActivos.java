package view.paneles.generales;

import model.Mensaje;
import model.Usuario;
import model.UsuarioActivo;
import proxy.NotificationListener;
import proxy.Protocol;
import proxy.ServiceProxy;

import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel que muestra la lista de usuarios activos (conectados)
 * y permite enviar/recibir mensajes entre ellos.
 */
public class PanelUsuariosActivos extends JPanel implements NotificationListener {
    
    private Usuario usuarioActual;
    private ServiceProxy proxy;
    
    // Componentes UI
    private DefaultListModel<UsuarioActivo> modeloLista;
    private JList<UsuarioActivo> listaUsuarios;
    private JLabel lblTitulo;
    private JLabel lblCantidad;
    private JButton btnEnviarMensaje;
    private JButton btnRecibirMensajes;
    private JLabel lblMensajesNuevos;
    
    // Lista de usuarios activos
    private List<UsuarioActivo> usuariosActivos;
    
    /**
     * Constructor
     * @param usuarioActual Usuario que está usando la aplicación
     */
    public PanelUsuariosActivos(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
        this.proxy = ServiceProxy.getInstance();
        this.usuariosActivos = new ArrayList<>();
        
        // Registrar este panel como listener de notificaciones
        proxy.setNotificationListener(this);
        
        inicializarComponentes();
        cargarUsuariosActivos();
        iniciarActualizacionMensajes();
    }
    
    /**
     * Inicializa los componentes de la interfaz
     */
    private void inicializarComponentes() {
        setLayout(new BorderLayout(5, 5));
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setPreferredSize(new Dimension(250, 0));
        
        // Panel superior con título y contador
        JPanel panelSuperior = new JPanel(new BorderLayout(5, 5));
        panelSuperior.setBackground(new Color(52, 73, 94));
        panelSuperior.setBorder(new EmptyBorder(8, 10, 8, 10));
        
        lblTitulo = new JLabel("👥 Usuarios Activos");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitulo.setForeground(Color.WHITE);
        
        lblCantidad = new JLabel("0");
        lblCantidad.setFont(new Font("Arial", Font.BOLD, 12));
        lblCantidad.setForeground(new Color(46, 204, 113));
        lblCantidad.setHorizontalAlignment(SwingConstants.CENTER);
        lblCantidad.setPreferredSize(new Dimension(30, 20));
        
        panelSuperior.add(lblTitulo, BorderLayout.CENTER);
        panelSuperior.add(lblCantidad, BorderLayout.EAST);
        
        // Lista de usuarios
        modeloLista = new DefaultListModel<>();
        listaUsuarios = new JList<>(modeloLista);
        listaUsuarios.setCellRenderer(new UsuarioActivoCellRenderer());
        listaUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Doble clic para enviar mensaje
        listaUsuarios.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    enviarMensaje();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(listaUsuarios);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new GridLayout(3, 1, 5, 5));
        panelBotones.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        btnEnviarMensaje = new JButton("✉️ Enviar");
        btnEnviarMensaje.setFont(new Font("Arial", Font.BOLD, 11));
        btnEnviarMensaje.setBackground(new Color(52, 152, 219));
        btnEnviarMensaje.setForeground(Color.BLACK); // Cambiado a negro
        btnEnviarMensaje.setFocusPainted(false);
        btnEnviarMensaje.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(41, 128, 185), 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        btnEnviarMensaje.setOpaque(true); // Asegurar que el fondo sea visible
        btnEnviarMensaje.addActionListener(e -> enviarMensaje());
        
        btnRecibirMensajes = new JButton("📬 Recibir");
        btnRecibirMensajes.setFont(new Font("Arial", Font.BOLD, 11));
        btnRecibirMensajes.setBackground(new Color(46, 204, 113));
        btnRecibirMensajes.setForeground(Color.BLACK); // Cambiado a negro
        btnRecibirMensajes.setFocusPainted(false);
        btnRecibirMensajes.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(39, 174, 96), 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        btnRecibirMensajes.setOpaque(true); // Asegurar que el fondo sea visible
        btnRecibirMensajes.addActionListener(e -> recibirMensajes());
        
        lblMensajesNuevos = new JLabel("0 mensajes nuevos", SwingConstants.CENTER);
        lblMensajesNuevos.setFont(new Font("Arial", Font.ITALIC, 10));
        lblMensajesNuevos.setForeground(new Color(231, 76, 60));
        
        panelBotones.add(btnEnviarMensaje);
        panelBotones.add(btnRecibirMensajes);
        panelBotones.add(lblMensajesNuevos);
        
        // Agregar componentes al panel
        add(panelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    /**
     * Carga la lista inicial de usuarios activos desde el backend
     */
    private void cargarUsuariosActivos() {
        try {
            usuariosActivos = proxy.listarUsuariosActivos();
            actualizarLista();
        } catch (Exception e) {
            System.err.println("Error al cargar usuarios activos: " + e.getMessage());
        }
    }
    
    /**
     * Actualiza la lista visual de usuarios
     */
    private void actualizarLista() {
        SwingUtilities.invokeLater(() -> {
            modeloLista.clear();
            
            // Filtrar usuarios (no mostrar el usuario actual)
            for (UsuarioActivo ua : usuariosActivos) {
                if (!ua.getUsuarioId().equals(usuarioActual.getId())) {
                    modeloLista.addElement(ua);
                }
            }
            
            // Actualizar contador
            lblCantidad.setText(String.valueOf(modeloLista.getSize()));
        });
    }
    
    /**
     * Envía un mensaje al usuario seleccionado
     */
    private void enviarMensaje() {
        UsuarioActivo destinatario = listaUsuarios.getSelectedValue();
        
        if (destinatario == null) {
            JOptionPane.showMessageDialog(this,
                "Por favor, seleccione un usuario de la lista",
                "Seleccione un usuario",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Ventana para ingresar el mensaje
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            "Enviar mensaje a " + destinatario.getNombre(), true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        
        JPanel panelContenido = new JPanel(new BorderLayout(5, 5));
        panelContenido.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel lblInstruccion = new JLabel("Escriba su mensaje:");
        JTextArea txtMensaje = new JTextArea(5, 30);
        txtMensaje.setLineWrap(true);
        txtMensaje.setWrapStyleWord(true);
        JScrollPane scrollMensaje = new JScrollPane(txtMensaje);
        
        panelContenido.add(lblInstruccion, BorderLayout.NORTH);
        panelContenido.add(scrollMensaje, BorderLayout.CENTER);
        
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnEnviar = new JButton("Enviar");
        btnEnviar.setBackground(new Color(46, 204, 113));
        btnEnviar.setForeground(Color.WHITE);
        btnEnviar.setFocusPainted(false);
        
        JButton btnCancelar = new JButton("Cancelar");
        
        btnEnviar.addActionListener(e -> {
            String texto = txtMensaje.getText().trim();
            if (texto.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "El mensaje no puede estar vacío",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                Mensaje mensaje = new Mensaje(
                    usuarioActual.getId(),
                    usuarioActual.getNombre(),
                    destinatario.getUsuarioId(),
                    destinatario.getNombre(),
                    texto
                );
                
                boolean enviado = proxy.enviarMensaje(mensaje);
                
                if (enviado) {
                    JOptionPane.showMessageDialog(dialog,
                        "✅ Mensaje enviado correctamente a " + destinatario.getNombre(),
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                        "❌ No se pudo enviar el mensaje",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Error al enviar mensaje: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        panelBotones.add(btnEnviar);
        panelBotones.add(btnCancelar);
        
        dialog.add(panelContenido, BorderLayout.CENTER);
        dialog.add(panelBotones, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    /**
     * Muestra los mensajes recibidos
     */
    private void recibirMensajes() {
        try {
            List<Mensaje> mensajes = proxy.recibirMensajes(usuarioActual.getId());
            
            if (mensajes.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "No tiene mensajes nuevos",
                    "Sin mensajes",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Crear ventana para mostrar mensajes
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Mensajes recibidos (" + mensajes.size() + ")", true);
            dialog.setSize(600, 400);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());
            
            // Modelo de la tabla
            String[] columnas = {"De", "Mensaje", "Fecha"};
            DefaultListModel<Mensaje> modeloMensajes = new DefaultListModel<>();
            for (Mensaje m : mensajes) {
                modeloMensajes.addElement(m);
            }
            
            JList<Mensaje> listaMensajes = new JList<>(modeloMensajes);
            listaMensajes.setCellRenderer(new MensajeCellRenderer());
            listaMensajes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            
            JScrollPane scroll = new JScrollPane(listaMensajes);
            
            // Panel de botones
            JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER));
            
            JButton btnMarcarLeido = new JButton("✓ Marcar como leído");
            btnMarcarLeido.setBackground(new Color(52, 152, 219));
            btnMarcarLeido.setForeground(Color.WHITE);
            btnMarcarLeido.setFocusPainted(false);
            
            JButton btnCerrar = new JButton("Cerrar");
            
            btnMarcarLeido.addActionListener(e -> {
                Mensaje seleccionado = listaMensajes.getSelectedValue();
                if (seleccionado != null) {
                    try {
                        proxy.marcarMensajeLeido(seleccionado.getId());
                        modeloMensajes.removeElement(seleccionado);
                        actualizarContadorMensajes();
                        
                        if (modeloMensajes.isEmpty()) {
                            JOptionPane.showMessageDialog(dialog,
                                "No hay más mensajes",
                                "Info",
                                JOptionPane.INFORMATION_MESSAGE);
                            dialog.dispose();
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog,
                            "Error al marcar mensaje: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(dialog,
                        "Seleccione un mensaje",
                        "Advertencia",
                        JOptionPane.WARNING_MESSAGE);
                }
            });
            
            btnCerrar.addActionListener(e -> dialog.dispose());
            
            panelBotones.add(btnMarcarLeido);
            panelBotones.add(btnCerrar);
            
            dialog.add(scroll, BorderLayout.CENTER);
            dialog.add(panelBotones, BorderLayout.SOUTH);
            
            dialog.setVisible(true);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al recibir mensajes: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Inicia un timer para actualizar el contador de mensajes no leídos
     */
    private void iniciarActualizacionMensajes() {
        Timer timer = new Timer(5000, e -> actualizarContadorMensajes());
        timer.start();
    }
    
    /**
     * Actualiza el contador de mensajes no leídos
     */
    private void actualizarContadorMensajes() {
        try {
            int count = proxy.contarMensajesNoLeidos(usuarioActual.getId());
            SwingUtilities.invokeLater(() -> {
                lblMensajesNuevos.setText(count + " mensaje" + (count != 1 ? "s" : "") + " nuevo" + (count != 1 ? "s" : ""));
                if (count > 0) {
                    lblMensajesNuevos.setForeground(new Color(231, 76, 60));
                    lblMensajesNuevos.setFont(new Font("Arial", Font.BOLD, 10));
                } else {
                    lblMensajesNuevos.setForeground(new Color(149, 165, 166));
                    lblMensajesNuevos.setFont(new Font("Arial", Font.ITALIC, 10));
                }
            });
        } catch (Exception e) {
            System.err.println("Error al actualizar contador de mensajes: " + e.getMessage());
        }
    }
    
    // ==================== IMPLEMENTACIÓN NotificationListener ====================
    
    @Override
    public void onNotificacion(String action, JSONObject data) {
        switch (action) {
            case Protocol.NOTIFICATION_USER_LOGIN:
                agregarUsuarioActivo(data);
                break;
                
            case Protocol.NOTIFICATION_USER_LOGOUT:
                removerUsuarioActivo(data);
                break;
                
            case Protocol.NOTIFICATION_NEW_MESSAGE:
                notificarNuevoMensaje(data);
                break;
        }
    }
    
    /**
     * Agrega un usuario que acaba de hacer login
     */
    private void agregarUsuarioActivo(JSONObject data) {
        try {
            String usuarioId = data.getString(Protocol.FIELD_USUARIO_ID);
            String nombre = data.getString(Protocol.FIELD_NOMBRE);
            String rol = data.getString(Protocol.FIELD_ROL);
            
            // Evitar agregar duplicados
            boolean existe = usuariosActivos.stream()
                .anyMatch(ua -> ua.getUsuarioId().equals(usuarioId));
            
            if (!existe) {
                UsuarioActivo nuevoUsuario = new UsuarioActivo(
                    usuarioId, nombre, rol, new java.util.Date(), ""
                );
                usuariosActivos.add(nuevoUsuario);
                actualizarLista();
                
                // Mostrar notificación
                System.out.println("✅ " + nombre + " se ha conectado");
            }
            
        } catch (Exception e) {
            System.err.println("Error al agregar usuario activo: " + e.getMessage());
        }
    }
    
    /**
     * Remueve un usuario que acaba de hacer logout
     */
    private void removerUsuarioActivo(JSONObject data) {
        try {
            String usuarioId = data.getString(Protocol.FIELD_USUARIO_ID);
            String nombre = data.optString(Protocol.FIELD_NOMBRE, "Usuario");
            
            usuariosActivos.removeIf(ua -> ua.getUsuarioId().equals(usuarioId));
            actualizarLista();
            
            // Mostrar notificación
            System.out.println("❌ " + nombre + " se ha desconectado");
            
        } catch (Exception e) {
            System.err.println("Error al remover usuario activo: " + e.getMessage());
        }
    }
    
    /**
     * Notifica que hay un nuevo mensaje
     */
    private void notificarNuevoMensaje(JSONObject data) {
        try {
            String remitenteNombre = data.getString(Protocol.FIELD_REMITENTE_NOMBRE);
            
            SwingUtilities.invokeLater(() -> {
                actualizarContadorMensajes();
                
                // Opcional: mostrar un toast/notificación visual
                JOptionPane.showMessageDialog(this,
                    "📬 Nuevo mensaje de: " + remitenteNombre,
                    "Mensaje recibido",
                    JOptionPane.INFORMATION_MESSAGE);
            });
            
        } catch (Exception e) {
            System.err.println("Error al notificar nuevo mensaje: " + e.getMessage());
        }
    }
    
    // ==================== CELL RENDERERS ====================
    
    /**
     * Renderer personalizado para mostrar usuarios activos
     */
    private class UsuarioActivoCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                     int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof UsuarioActivo) {
                UsuarioActivo ua = (UsuarioActivo) value;
                
                String icono = getIconoPorRol(ua.getRol());
                setText(icono + " " + ua.getNombre());
                
                if (isSelected) {
                    setBackground(new Color(52, 152, 219));
                    setForeground(Color.WHITE);
                } else {
                    setBackground(Color.WHITE);
                    setForeground(Color.BLACK);
                }
                
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(236, 240, 241)),
                    new EmptyBorder(5, 10, 5, 10)
                ));
            }
            
            return this;
        }
        
        private String getIconoPorRol(String rol) {
            switch (rol) {
                case "ADM": return "👤";
                case "MED": return "👨‍⚕️";
                case "FAR": return "💊";
                default: return "•";
            }
        }
    }
    
    /**
     * Renderer personalizado para mostrar mensajes
     */
    private class MensajeCellRenderer extends JPanel implements ListCellRenderer<Mensaje> {
        
        private JLabel lblRemitente;
        private JTextArea txtMensaje;
        private JLabel lblFecha;
        
        public MensajeCellRenderer() {
            setLayout(new BorderLayout(5, 5));
            setBorder(new EmptyBorder(5, 10, 5, 10));
            
            lblRemitente = new JLabel();
            lblRemitente.setFont(new Font("Arial", Font.BOLD, 12));
            
            txtMensaje = new JTextArea();
            txtMensaje.setLineWrap(true);
            txtMensaje.setWrapStyleWord(true);
            txtMensaje.setEditable(false);
            txtMensaje.setOpaque(false);
            txtMensaje.setFont(new Font("Arial", Font.PLAIN, 11));
            
            lblFecha = new JLabel();
            lblFecha.setFont(new Font("Arial", Font.ITALIC, 10));
            lblFecha.setForeground(Color.GRAY);
            
            JPanel panelSuperior = new JPanel(new BorderLayout());
            panelSuperior.setOpaque(false);
            panelSuperior.add(lblRemitente, BorderLayout.WEST);
            panelSuperior.add(lblFecha, BorderLayout.EAST);
            
            add(panelSuperior, BorderLayout.NORTH);
            add(txtMensaje, BorderLayout.CENTER);
        }
        
        @Override
        public Component getListCellRendererComponent(JList<? extends Mensaje> list,
                                                     Mensaje mensaje, int index,
                                                     boolean isSelected, boolean cellHasFocus) {
            lblRemitente.setText("✉️ " + mensaje.getRemitenteNombre());
            txtMensaje.setText(mensaje.getTexto());
            
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
            lblFecha.setText(sdf.format(mensaje.getFechaEnvio()));
            
            if (isSelected) {
                setBackground(new Color(230, 240, 255));
            } else {
                setBackground(Color.WHITE);
            }
            
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(236, 240, 241)),
                new EmptyBorder(8, 10, 8, 10)
            ));
            
            return this;
        }
    }
}
