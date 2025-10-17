package proxy;

import model.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServiceProxy implements IService {
    
    // Singleton
    private static ServiceProxy instance;
    
    // Conexión con el Backend
    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter salida;
    private boolean conectado;
    
    // Thread para escuchar notificaciones asíncronas
    private Thread listenerThread;
    private NotificationListener notificationListener;
    
    // Cola para manejar respuestas síncronas
    private BlockingQueue<String> responseQueue;
    
    // Formato de fecha
    private SimpleDateFormat dateFormat;
    
    // Información del servidor
    private String serverHost;
    private int serverPort;
    
    /**
     * Constructor privado (Singleton)
     */
    private ServiceProxy() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        conectado = false;
        responseQueue = new LinkedBlockingQueue<>();
    }
    
    /**
     * Obtiene la instancia única del ServiceProxy
     */
    public static ServiceProxy getInstance() {
        if (instance == null) {
            synchronized (ServiceProxy.class) {
                if (instance == null) {
                    instance = new ServiceProxy();
                }
            }
        }
        return instance;
    }
    
    /**
     * Conecta con el Backend
     */
    public boolean conectar(String host, int port) {
        try {
            this.serverHost = host;
            this.serverPort = port;
            
            socket = new Socket(host, port);
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new PrintWriter(socket.getOutputStream(), true);
            conectado = true;
            
            // Iniciar thread para escuchar mensajes del servidor
            iniciarListenerMensajes();
            
            System.out.println("✓ Conectado al servidor: " + host + ":" + port);
            return true;
            
        } catch (Exception e) {
            System.err.println("Error al conectar con el servidor: " + e.getMessage());
            conectado = false;
            return false;
        }
    }
    
    /**
     * Desconecta del Backend
     */
    public void desconectar() {
        try {
            conectado = false;
            
            if (listenerThread != null && listenerThread.isAlive()) {
                listenerThread.interrupt();
            }
            
            if (entrada != null) entrada.close();
            if (salida != null) salida.close();
            if (socket != null && !socket.isClosed()) socket.close();
            
            System.out.println("✓ Desconectado del servidor");
        } catch (Exception e) {
            System.err.println("Error al desconectar: " + e.getMessage());
        }
    }
    
    /**
     * Verifica si está conectado
     */
    public boolean estaConectado() {
        return conectado && socket != null && !socket.isClosed();
    }
    
    /**
     * Registra un listener para notificaciones asíncronas
     */
    public void setNotificationListener(NotificationListener listener) {
        this.notificationListener = listener;
    }
    
    /**
     * Inicia el thread que escucha mensajes del servidor
     * Diferencia entre respuestas (RESPONSE) y notificaciones (NOTIFICATION)
     */
    private void iniciarListenerMensajes() {
        listenerThread = new Thread(() -> {
            try {
                String mensaje;
                while (conectado && (mensaje = entrada.readLine()) != null) {
                    procesarMensaje(mensaje);
                }
            } catch (Exception e) {
                if (conectado) {
                    System.err.println("Error en listener de mensajes: " + e.getMessage());
                }
            }
        });
        listenerThread.setDaemon(true);
        listenerThread.start();
    }
    
    /**
     * Procesa un mensaje recibido del servidor
     * Si es RESPONSE, lo pone en la cola de respuestas
     * Si es NOTIFICATION, lo procesa como notificación
     */
    private void procesarMensaje(String mensajeJson) {
        try {
            JSONObject mensaje = new JSONObject(mensajeJson);
            String tipo = mensaje.getString("type");
            
            if (tipo.equals(Protocol.TYPE_RESPONSE)) {
                // Es una respuesta síncrona, ponerla en la cola
                responseQueue.offer(mensajeJson);
            } else if (tipo.equals(Protocol.TYPE_NOTIFICATION)) {
                // Es una notificación asíncrona, procesarla
                String action = mensaje.getString("action");
                JSONObject data = mensaje.optJSONObject("data");
                
                if (notificationListener != null) {
                    notificationListener.onNotificacion(action, data);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al procesar mensaje: " + e.getMessage());
            // Si no es JSON válido, intentar ponerlo en la cola de respuestas
            responseQueue.offer(mensajeJson);
        }
    }
    
    /**
     * Envía una petición al servidor y espera la respuesta
     */
    private JSONObject enviarPeticion(String mensajeJson) throws Exception {
        if (!estaConectado()) {
            throw new Exception("No hay conexión con el servidor");
        }
        
        synchronized (salida) {
            // Limpiar la cola de respuestas antes de enviar
            responseQueue.clear();
            
            // Enviar petición
            salida.println(mensajeJson);
            
            // Esperar respuesta (con timeout de 10 segundos)
            String respuestaJson = responseQueue.poll(10, java.util.concurrent.TimeUnit.SECONDS);
            
            if (respuestaJson == null) {
                throw new Exception("Timeout esperando respuesta del servidor");
            }
            
            return new JSONObject(respuestaJson);
        }
    }
    
    // ==================== IMPLEMENTACIÓN DE IService ====================
    
    @Override
    public Usuario login(String usuarioId, String clave) throws Exception {
        JSONObject request = new JSONObject();
        request.put("type", Protocol.TYPE_REQUEST);
        request.put("action", Protocol.ACTION_LOGIN);
        
        JSONObject data = new JSONObject();
        data.put(Protocol.FIELD_USUARIO_ID, usuarioId);
        data.put(Protocol.FIELD_CLAVE, clave);
        request.put("data", data);
        
        JSONObject response = enviarPeticion(request.toString());
        
        if (response.getString("status").equals(Protocol.STATUS_SUCCESS)) {
            JSONObject userData = response.getJSONObject("data");
            String id = userData.getString(Protocol.FIELD_USUARIO_ID);
            String nombre = userData.getString(Protocol.FIELD_NOMBRE);
            String rol = userData.getString(Protocol.FIELD_ROL);
            
            Usuario usuario = null;
            switch (rol) {
                case "ADM":
                    usuario = new Administrador(id, clave, nombre);
                    break;
                case "MED":
                    String especialidad = userData.getString(Protocol.FIELD_ESPECIALIDAD);
                    usuario = new Medico(id, clave, nombre, especialidad);
                    break;
                case "FAR":
                    usuario = new Farmaceuta(id, clave, nombre);
                    break;
            }
            return usuario;
        } else {
            throw new Exception(response.getString("message"));
        }
    }
    
    @Override
    public void logout(String usuarioId) throws Exception {
        JSONObject request = new JSONObject();
        request.put("type", Protocol.TYPE_REQUEST);
        request.put("action", Protocol.ACTION_LOGOUT);
        
        JSONObject data = new JSONObject();
        data.put(Protocol.FIELD_USUARIO_ID, usuarioId);
        request.put("data", data);
        
        enviarPeticion(request.toString());
    }
    
    @Override
    public boolean cambiarClave(String usuarioId, String nuevaClave) throws Exception {
        JSONObject request = new JSONObject();
        request.put("type", Protocol.TYPE_REQUEST);
        request.put("action", Protocol.ACTION_CAMBIAR_CLAVE);
        
        JSONObject data = new JSONObject();
        data.put(Protocol.FIELD_USUARIO_ID, usuarioId);
        data.put(Protocol.FIELD_CLAVE, nuevaClave);
        request.put("data", data);
        
        JSONObject response = enviarPeticion(request.toString());
        return response.getString("status").equals(Protocol.STATUS_SUCCESS);
    }
    
    @Override
    public List<Medico> listarMedicos() throws Exception {
        JSONObject request = new JSONObject();
        request.put("type", Protocol.TYPE_REQUEST);
        request.put("action", Protocol.ACTION_LISTAR_MEDICOS);
        request.put("data", new JSONObject());
        
        JSONObject response = enviarPeticion(request.toString());
        
        if (response.getString("status").equals(Protocol.STATUS_SUCCESS)) {
            List<Medico> medicos = new ArrayList<>();
            JSONArray medicosArray = response.getJSONObject("data").getJSONArray("medicos");
            
            for (int i = 0; i < medicosArray.length(); i++) {
                JSONObject medicoJson = medicosArray.getJSONObject(i);
                String id = medicoJson.getString(Protocol.FIELD_USUARIO_ID);
                String nombre = medicoJson.getString(Protocol.FIELD_NOMBRE);
                String especialidad = medicoJson.getString(Protocol.FIELD_ESPECIALIDAD);
                
                medicos.add(new Medico(id, "", nombre, especialidad));
            }
            return medicos;
        } else {
            throw new Exception(response.getString("message"));
        }
    }
    
    @Override
    public List<Farmaceuta> listarFarmaceutas() throws Exception {
        JSONObject request = new JSONObject();
        request.put("type", Protocol.TYPE_REQUEST);
        request.put("action", Protocol.ACTION_LISTAR_FARMACEUTAS);
        request.put("data", new JSONObject());
        
        JSONObject response = enviarPeticion(request.toString());
        
        if (response.getString("status").equals(Protocol.STATUS_SUCCESS)) {
            List<Farmaceuta> farmaceutas = new ArrayList<>();
            JSONArray farmsArray = response.getJSONObject("data").getJSONArray("farmaceutas");
            
            for (int i = 0; i < farmsArray.length(); i++) {
                JSONObject farmJson = farmsArray.getJSONObject(i);
                String id = farmJson.getString(Protocol.FIELD_USUARIO_ID);
                String nombre = farmJson.getString(Protocol.FIELD_NOMBRE);
                
                farmaceutas.add(new Farmaceuta(id, "", nombre));
            }
            return farmaceutas;
        } else {
            throw new Exception(response.getString("message"));
        }
    }
    
    @Override
    public List<Paciente> listarPacientes() throws Exception {
        JSONObject request = new JSONObject();
        request.put("type", Protocol.TYPE_REQUEST);
        request.put("action", Protocol.ACTION_LISTAR_PACIENTES);
        request.put("data", new JSONObject());
        
        JSONObject response = enviarPeticion(request.toString());
        
        if (response.getString("status").equals(Protocol.STATUS_SUCCESS)) {
            List<Paciente> pacientes = new ArrayList<>();
            JSONArray pacsArray = response.getJSONObject("data").getJSONArray("pacientes");
            
            for (int i = 0; i < pacsArray.length(); i++) {
                JSONObject pacJson = pacsArray.getJSONObject(i);
                String id = pacJson.getString(Protocol.FIELD_PACIENTE_ID);
                String nombre = pacJson.getString(Protocol.FIELD_NOMBRE);
                String telefono = pacJson.getString(Protocol.FIELD_TELEFONO);
                Date fechaNac = dateFormat.parse(pacJson.getString(Protocol.FIELD_FECHA_NACIMIENTO));
                
                pacientes.add(new Paciente(id, nombre, fechaNac, telefono));
            }
            return pacientes;
        } else {
            throw new Exception(response.getString("message"));
        }
    }
    
    @Override
    public List<Medicamento> listarMedicamentos() throws Exception {
        JSONObject request = new JSONObject();
        request.put("type", Protocol.TYPE_REQUEST);
        request.put("action", Protocol.ACTION_LISTAR_MEDICAMENTOS);
        request.put("data", new JSONObject());
        
        JSONObject response = enviarPeticion(request.toString());
        
        if (response.getString("status").equals(Protocol.STATUS_SUCCESS)) {
            List<Medicamento> medicamentos = new ArrayList<>();
            JSONArray medsArray = response.getJSONObject("data").getJSONArray("medicamentos");
            
            for (int i = 0; i < medsArray.length(); i++) {
                JSONObject medJson = medsArray.getJSONObject(i);
                String codigo = medJson.getString(Protocol.FIELD_MEDICAMENTO_CODIGO);
                String nombre = medJson.getString(Protocol.FIELD_NOMBRE);
                String presentacion = medJson.getString(Protocol.FIELD_PRESENTACION);
                
                medicamentos.add(new Medicamento(codigo, nombre, presentacion));
            }
            return medicamentos;
        } else {
            throw new Exception(response.getString("message"));
        }
    }
    
    // ==================== CATÁLOGO DE MEDICAMENTOS ====================
    
    @Override
    public boolean agregarMedicamento(Medicamento medicamento) throws Exception {
        JSONObject request = new JSONObject();
        request.put("type", Protocol.TYPE_REQUEST);
        request.put("action", Protocol.ACTION_AGREGAR_MEDICAMENTO);
        
        JSONObject data = new JSONObject();
        data.put(Protocol.FIELD_MEDICAMENTO_CODIGO, medicamento.getCodigo());
        data.put(Protocol.FIELD_NOMBRE, medicamento.getNombre());
        data.put(Protocol.FIELD_PRESENTACION, medicamento.getPresentacion());
        request.put("data", data);
        
        JSONObject response = enviarPeticion(request.toString());
        return response.getString("status").equals(Protocol.STATUS_SUCCESS);
    }
    
    @Override
    public boolean actualizarMedicamento(Medicamento medicamento) throws Exception {
        JSONObject request = new JSONObject();
        request.put("type", Protocol.TYPE_REQUEST);
        request.put("action", Protocol.ACTION_ACTUALIZAR_MEDICAMENTO);
        
        JSONObject data = new JSONObject();
        data.put(Protocol.FIELD_MEDICAMENTO_CODIGO, medicamento.getCodigo());
        data.put(Protocol.FIELD_NOMBRE, medicamento.getNombre());
        data.put(Protocol.FIELD_PRESENTACION, medicamento.getPresentacion());
        request.put("data", data);
        
        JSONObject response = enviarPeticion(request.toString());
        return response.getString("status").equals(Protocol.STATUS_SUCCESS);
    }
    
    @Override
    public boolean eliminarMedicamento(String codigo) throws Exception {
        JSONObject request = new JSONObject();
        request.put("type", Protocol.TYPE_REQUEST);
        request.put("action", Protocol.ACTION_ELIMINAR_MEDICAMENTO);
        
        JSONObject data = new JSONObject();
        data.put(Protocol.FIELD_MEDICAMENTO_CODIGO, codigo);
        request.put("data", data);
        
        JSONObject response = enviarPeticion(request.toString());
        return response.getString("status").equals(Protocol.STATUS_SUCCESS);
    }
    
    @Override
    public Medicamento buscarMedicamento(String codigo) throws Exception {
        JSONObject request = new JSONObject();
        request.put("type", Protocol.TYPE_REQUEST);
        request.put("action", Protocol.ACTION_BUSCAR_MEDICAMENTO);
        
        JSONObject data = new JSONObject();
        data.put(Protocol.FIELD_MEDICAMENTO_CODIGO, codigo);
        request.put("data", data);
        
        JSONObject response = enviarPeticion(request.toString());
        
        if (response.getString("status").equals(Protocol.STATUS_SUCCESS)) {
            JSONObject medJson = response.getJSONObject("data");
            String cod = medJson.getString(Protocol.FIELD_MEDICAMENTO_CODIGO);
            String nombre = medJson.getString(Protocol.FIELD_NOMBRE);
            String presentacion = medJson.getString(Protocol.FIELD_PRESENTACION);
            
            return new Medicamento(cod, nombre, presentacion);
        } else {
            throw new Exception(response.getString("message"));
        }
    }
    
    // ==================== PACIENTES ====================
    
    @Override
    public boolean agregarPaciente(Paciente paciente) throws Exception {
        JSONObject request = new JSONObject();
        request.put("type", Protocol.TYPE_REQUEST);
        request.put("action", Protocol.ACTION_AGREGAR_PACIENTE);
        
        JSONObject data = new JSONObject();
        data.put(Protocol.FIELD_PACIENTE_ID, paciente.getId());
        data.put(Protocol.FIELD_NOMBRE, paciente.getNombre());
        data.put(Protocol.FIELD_FECHA_NACIMIENTO, dateFormat.format(paciente.getFechaNacimiento()));
        data.put(Protocol.FIELD_TELEFONO, paciente.getTelefono());
        request.put("data", data);
        
        JSONObject response = enviarPeticion(request.toString());
        return response.getString("status").equals(Protocol.STATUS_SUCCESS);
    }
    
    @Override
    public boolean actualizarPaciente(Paciente paciente) throws Exception {
        JSONObject request = new JSONObject();
        request.put("type", Protocol.TYPE_REQUEST);
        request.put("action", Protocol.ACTION_ACTUALIZAR_PACIENTE);
        
        JSONObject data = new JSONObject();
        data.put(Protocol.FIELD_PACIENTE_ID, paciente.getId());
        data.put(Protocol.FIELD_NOMBRE, paciente.getNombre());
        data.put(Protocol.FIELD_FECHA_NACIMIENTO, dateFormat.format(paciente.getFechaNacimiento()));
        data.put(Protocol.FIELD_TELEFONO, paciente.getTelefono());
        request.put("data", data);
        
        JSONObject response = enviarPeticion(request.toString());
        return response.getString("status").equals(Protocol.STATUS_SUCCESS);
    }
    
    @Override
    public Paciente buscarPaciente(String id) throws Exception {
        JSONObject request = new JSONObject();
        request.put("type", Protocol.TYPE_REQUEST);
        request.put("action", Protocol.ACTION_BUSCAR_PACIENTE);
        
        JSONObject data = new JSONObject();
        data.put(Protocol.FIELD_PACIENTE_ID, id);
        request.put("data", data);
        
        JSONObject response = enviarPeticion(request.toString());
        
        if (response.getString("status").equals(Protocol.STATUS_SUCCESS)) {
            JSONObject pacJson = response.getJSONObject("data");
            String pacId = pacJson.getString(Protocol.FIELD_PACIENTE_ID);
            String nombre = pacJson.getString(Protocol.FIELD_NOMBRE);
            String telefono = pacJson.getString(Protocol.FIELD_TELEFONO);
            Date fechaNac = dateFormat.parse(pacJson.getString(Protocol.FIELD_FECHA_NACIMIENTO));
            
            return new Paciente(pacId, nombre, fechaNac, telefono);
        } else {
            throw new Exception(response.getString("message"));
        }
    }
    
    // ==================== PRESCRIPCIÓN ====================
    
    @Override
    public boolean crearReceta(Receta receta) throws Exception {
        JSONObject request = new JSONObject();
        request.put("type", Protocol.TYPE_REQUEST);
        request.put("action", Protocol.ACTION_CREAR_RECETA);
        
        JSONObject data = new JSONObject();
        data.put(Protocol.FIELD_RECETA_ID, receta.getId());
        data.put(Protocol.FIELD_FECHA_CONFECCION, dateFormat.format(receta.getFechaConfeccion()));
        data.put(Protocol.FIELD_FECHA_RETIRO, dateFormat.format(receta.getFechaRetiro()));
        data.put(Protocol.FIELD_PACIENTE_ID, receta.getPaciente().getId());
        data.put(Protocol.FIELD_MEDICO_ID, receta.getMedicoId());
        
        // Agregar detalles
        JSONArray detallesArray = new JSONArray();
        for (DetalleReceta detalle : receta.getDetalles()) {
            JSONObject detalleJson = new JSONObject();
            detalleJson.put(Protocol.FIELD_MEDICAMENTO_CODIGO, detalle.getMedicamento().getCodigo());
            detalleJson.put(Protocol.FIELD_CANTIDAD, detalle.getCantidad());
            detalleJson.put(Protocol.FIELD_INDICACIONES, detalle.getIndicaciones());
            detalleJson.put(Protocol.FIELD_DURACION_DIAS, detalle.getDuracionDias());
            detallesArray.put(detalleJson);
        }
        data.put(Protocol.FIELD_DETALLES, detallesArray);
        
        request.put("data", data);
        
        JSONObject response = enviarPeticion(request.toString());
        return response.getString("status").equals(Protocol.STATUS_SUCCESS);
    }
    
    @Override
    public Receta buscarReceta(String id) throws Exception {
        JSONObject request = new JSONObject();
        request.put("type", Protocol.TYPE_REQUEST);
        request.put("action", Protocol.ACTION_BUSCAR_RECETA);
        
        JSONObject data = new JSONObject();
        data.put(Protocol.FIELD_RECETA_ID, id);
        request.put("data", data);
        
        JSONObject response = enviarPeticion(request.toString());
        
        if (response.getString("status").equals(Protocol.STATUS_SUCCESS)) {
            return jsonToReceta(response.getJSONObject("data"));
        } else {
            throw new Exception(response.getString("message"));
        }
    }
    
    @Override
    public List<Receta> listarRecetas() throws Exception {
        JSONObject request = new JSONObject();
        request.put("type", Protocol.TYPE_REQUEST);
        request.put("action", Protocol.ACTION_LISTAR_RECETAS);
        request.put("data", new JSONObject());
        
        JSONObject response = enviarPeticion(request.toString());
        
        if (response.getString("status").equals(Protocol.STATUS_SUCCESS)) {
            List<Receta> recetas = new ArrayList<>();
            JSONArray recetasArray = response.getJSONObject("data").getJSONArray("recetas");
            
            for (int i = 0; i < recetasArray.length(); i++) {
                JSONObject recetaJson = recetasArray.getJSONObject(i);
                recetas.add(jsonToReceta(recetaJson));
            }
            return recetas;
        } else {
            throw new Exception(response.getString("message"));
        }
    }
    
    @Override
    public List<Receta> listarRecetasPorEstado(String estado) throws Exception {
        JSONObject request = new JSONObject();
        request.put("type", Protocol.TYPE_REQUEST);
        request.put("action", Protocol.ACTION_LISTAR_RECETAS_ESTADO);
        
        JSONObject data = new JSONObject();
        data.put(Protocol.FIELD_ESTADO, estado);
        request.put("data", data);
        
        JSONObject response = enviarPeticion(request.toString());
        
        if (response.getString("status").equals(Protocol.STATUS_SUCCESS)) {
            List<Receta> recetas = new ArrayList<>();
            JSONArray recetasArray = response.getJSONObject("data").getJSONArray("recetas");
            
            for (int i = 0; i < recetasArray.length(); i++) {
                JSONObject recetaJson = recetasArray.getJSONObject(i);
                recetas.add(jsonToReceta(recetaJson));
            }
            return recetas;
        } else {
            throw new Exception(response.getString("message"));
        }
    }
    
    // ==================== DESPACHO ====================
    
    @Override
    public boolean iniciarDespacho(String recetaId) throws Exception {
        JSONObject request = new JSONObject();
        request.put("type", Protocol.TYPE_REQUEST);
        request.put("action", Protocol.ACTION_INICIAR_DESPACHO);
        
        JSONObject data = new JSONObject();
        data.put(Protocol.FIELD_RECETA_ID, recetaId);
        request.put("data", data);
        
        JSONObject response = enviarPeticion(request.toString());
        return response.getString("status").equals(Protocol.STATUS_SUCCESS);
    }
    
    @Override
    public boolean marcarRecetaLista(String recetaId) throws Exception {
        JSONObject request = new JSONObject();
        request.put("type", Protocol.TYPE_REQUEST);
        request.put("action", Protocol.ACTION_MARCAR_LISTA);
        
        JSONObject data = new JSONObject();
        data.put(Protocol.FIELD_RECETA_ID, recetaId);
        request.put("data", data);
        
        JSONObject response = enviarPeticion(request.toString());
        return response.getString("status").equals(Protocol.STATUS_SUCCESS);
    }
    
    @Override
    public boolean entregarReceta(String recetaId) throws Exception {
        JSONObject request = new JSONObject();
        request.put("type", Protocol.TYPE_REQUEST);
        request.put("action", Protocol.ACTION_ENTREGAR_RECETA);
        
        JSONObject data = new JSONObject();
        data.put(Protocol.FIELD_RECETA_ID, recetaId);
        request.put("data", data);
        
        JSONObject response = enviarPeticion(request.toString());
        return response.getString("status").equals(Protocol.STATUS_SUCCESS);
    }
    
    // ==================== USUARIOS ====================
    
    @Override
    public List<Usuario> listarUsuarios() throws Exception {
        JSONObject request = new JSONObject();
        request.put("type", Protocol.TYPE_REQUEST);
        request.put("action", Protocol.ACTION_LISTAR_USUARIOS);
        request.put("data", new JSONObject());
        
        JSONObject response = enviarPeticion(request.toString());
        
        if (response.getString("status").equals(Protocol.STATUS_SUCCESS)) {
            List<Usuario> usuarios = new ArrayList<>();
            JSONArray usuariosArray = response.getJSONObject("data").getJSONArray("usuarios");
            
            for (int i = 0; i < usuariosArray.length(); i++) {
                JSONObject userJson = usuariosArray.getJSONObject(i);
                String id = userJson.getString(Protocol.FIELD_USUARIO_ID);
                String nombre = userJson.getString(Protocol.FIELD_NOMBRE);
                String rol = userJson.getString(Protocol.FIELD_ROL);
                
                Usuario usuario = null;
                switch (rol) {
                    case "ADM":
                        usuario = new Administrador(id, "", nombre);
                        break;
                    case "MED":
                        String especialidad = userJson.getString(Protocol.FIELD_ESPECIALIDAD);
                        usuario = new Medico(id, "", nombre, especialidad);
                        break;
                    case "FAR":
                        usuario = new Farmaceuta(id, "", nombre);
                        break;
                }
                if (usuario != null) {
                    usuarios.add(usuario);
                }
            }
            return usuarios;
        } else {
            throw new Exception(response.getString("message"));
        }
    }
    
    @Override
    public boolean agregarUsuario(Usuario usuario) throws Exception {
        JSONObject request = new JSONObject();
        request.put("type", Protocol.TYPE_REQUEST);
        request.put("action", Protocol.ACTION_AGREGAR_USUARIO);
        
        JSONObject data = new JSONObject();
        data.put(Protocol.FIELD_USUARIO_ID, usuario.getId());
        data.put(Protocol.FIELD_CLAVE, usuario.getClave());
        data.put(Protocol.FIELD_NOMBRE, usuario.getNombre());
        data.put(Protocol.FIELD_ROL, usuario.getRol());
        
        if (usuario instanceof Medico) {
            data.put(Protocol.FIELD_ESPECIALIDAD, ((Medico) usuario).getEspecialidad());
        }
        
        request.put("data", data);
        
        JSONObject response = enviarPeticion(request.toString());
        return response.getString("status").equals(Protocol.STATUS_SUCCESS);
    }
    
    @Override
    public List<UsuarioActivo> listarUsuariosActivos() throws Exception {
        JSONObject request = new JSONObject();
        request.put("type", Protocol.TYPE_REQUEST);
        request.put("action", Protocol.ACTION_LISTAR_USUARIOS_ACTIVOS);
        request.put("data", new JSONObject());
        
        JSONObject response = enviarPeticion(request.toString());
        
        if (response.getString("status").equals(Protocol.STATUS_SUCCESS)) {
            List<UsuarioActivo> usuariosActivos = new ArrayList<>();
            JSONArray usuariosArray = response.getJSONObject("data").getJSONArray("usuariosActivos");
            
            for (int i = 0; i < usuariosArray.length(); i++) {
                JSONObject uaJson = usuariosArray.getJSONObject(i);
                String id = uaJson.getString(Protocol.FIELD_USUARIO_ID);
                String nombre = uaJson.getString(Protocol.FIELD_NOMBRE);
                String rol = uaJson.getString(Protocol.FIELD_ROL);
                
                UsuarioActivo ua = new UsuarioActivo(id, nombre, rol, new Date(), "");
                usuariosActivos.add(ua);
            }
            return usuariosActivos;
        } else {
            throw new Exception(response.getString("message"));
        }
    }
    
    // ==================== MENSAJERÍA ====================
    
    @Override
    public boolean enviarMensaje(Mensaje mensaje) throws Exception {
        JSONObject request = new JSONObject();
        request.put("type", Protocol.TYPE_REQUEST);
        request.put("action", Protocol.ACTION_ENVIAR_MENSAJE);
        
        JSONObject data = new JSONObject();
        data.put(Protocol.FIELD_REMITENTE_ID, mensaje.getRemitenteId());
        data.put(Protocol.FIELD_REMITENTE_NOMBRE, mensaje.getRemitenteNombre());
        data.put(Protocol.FIELD_DESTINATARIO_ID, mensaje.getDestinatarioId());
        data.put(Protocol.FIELD_DESTINATARIO_NOMBRE, mensaje.getDestinatarioNombre());
        data.put(Protocol.FIELD_TEXTO, mensaje.getTexto());
        request.put("data", data);
        
        JSONObject response = enviarPeticion(request.toString());
        return response.getString("status").equals(Protocol.STATUS_SUCCESS);
    }
    
    @Override
    public List<Mensaje> recibirMensajes(String usuarioId) throws Exception {
        JSONObject request = new JSONObject();
        request.put("type", Protocol.TYPE_REQUEST);
        request.put("action", Protocol.ACTION_RECIBIR_MENSAJES);
        
        JSONObject data = new JSONObject();
        data.put(Protocol.FIELD_USUARIO_ID, usuarioId);
        request.put("data", data);
        
        JSONObject response = enviarPeticion(request.toString());
        
        if (response.getString("status").equals(Protocol.STATUS_SUCCESS)) {
            List<Mensaje> mensajes = new ArrayList<>();
            JSONArray mensajesArray = response.getJSONObject("data").getJSONArray("mensajes");
            
            for (int i = 0; i < mensajesArray.length(); i++) {
                JSONObject msgJson = mensajesArray.getJSONObject(i);
                int id = msgJson.getInt(Protocol.FIELD_MENSAJE_ID);
                String remId = msgJson.getString(Protocol.FIELD_REMITENTE_ID);
                String remNombre = msgJson.getString(Protocol.FIELD_REMITENTE_NOMBRE);
                String texto = msgJson.getString(Protocol.FIELD_TEXTO);
                Date fechaEnvio = dateFormat.parse(msgJson.getString("fechaEnvio"));
                
                Mensaje msg = new Mensaje(remId, remNombre, usuarioId, "", texto);
                msg.setId(id);
                msg.setFechaEnvio(fechaEnvio);
                mensajes.add(msg);
            }
            return mensajes;
        } else {
            throw new Exception(response.getString("message"));
        }
    }
    
    @Override
    public boolean marcarMensajeLeido(int mensajeId) throws Exception {
        JSONObject request = new JSONObject();
        request.put("type", Protocol.TYPE_REQUEST);
        request.put("action", Protocol.ACTION_MARCAR_MENSAJE_LEIDO);
        
        JSONObject data = new JSONObject();
        data.put(Protocol.FIELD_MENSAJE_ID, mensajeId);
        request.put("data", data);
        
        JSONObject response = enviarPeticion(request.toString());
        return response.getString("status").equals(Protocol.STATUS_SUCCESS);
    }
    
    @Override
    public int contarMensajesNoLeidos(String usuarioId) throws Exception {
        JSONObject request = new JSONObject();
        request.put("type", Protocol.TYPE_REQUEST);
        request.put("action", Protocol.ACTION_CONTAR_MENSAJES_NO_LEIDOS);
        
        JSONObject data = new JSONObject();
        data.put(Protocol.FIELD_USUARIO_ID, usuarioId);
        request.put("data", data);
        
        JSONObject response = enviarPeticion(request.toString());
        
        if (response.getString("status").equals(Protocol.STATUS_SUCCESS)) {
            return response.getJSONObject("data").getInt("count");
        } else {
            throw new Exception(response.getString("message"));
        }
    }
    
    // ==================== MÉTODOS AUXILIARES ====================
    
    /**
     * Convierte un JSONObject en una Receta
     */
    private Receta jsonToReceta(JSONObject recetaJson) throws Exception {
        String id = recetaJson.getString(Protocol.FIELD_RECETA_ID);
        Date fechaConf = dateFormat.parse(recetaJson.getString(Protocol.FIELD_FECHA_CONFECCION));
        Date fechaRet = dateFormat.parse(recetaJson.getString(Protocol.FIELD_FECHA_RETIRO));
        String estado = recetaJson.getString(Protocol.FIELD_ESTADO);
        String medicoId = recetaJson.getString(Protocol.FIELD_MEDICO_ID);
        
        // Paciente
        JSONObject pacienteJson = recetaJson.getJSONObject("paciente");
        String pacId = pacienteJson.getString(Protocol.FIELD_PACIENTE_ID);
        String pacNombre = pacienteJson.getString(Protocol.FIELD_NOMBRE);
        String pacTelefono = pacienteJson.getString(Protocol.FIELD_TELEFONO);
        Date pacFechaNac = dateFormat.parse(pacienteJson.getString(Protocol.FIELD_FECHA_NACIMIENTO));
        Paciente paciente = new Paciente(pacId, pacNombre, pacFechaNac, pacTelefono);
        
        Receta receta = new Receta(id, fechaConf, fechaRet, paciente);
        receta.setEstado(estado);
        receta.setMedicoId(medicoId);
        
        // Fechas opcionales
        if (recetaJson.has("fechaProceso") && !recetaJson.isNull("fechaProceso")) {
            receta.setFechaProceso(dateFormat.parse(recetaJson.getString("fechaProceso")));
        }
        if (recetaJson.has("fechaLista") && !recetaJson.isNull("fechaLista")) {
            receta.setFechaLista(dateFormat.parse(recetaJson.getString("fechaLista")));
        }
        if (recetaJson.has("fechaEntrega") && !recetaJson.isNull("fechaEntrega")) {
            receta.setFechaEntrega(dateFormat.parse(recetaJson.getString("fechaEntrega")));
        }
        
        // Detalles
        JSONArray detallesArray = recetaJson.getJSONArray(Protocol.FIELD_DETALLES);
        for (int i = 0; i < detallesArray.length(); i++) {
            JSONObject detalleJson = detallesArray.getJSONObject(i);
            
            JSONObject medJson = detalleJson.getJSONObject("medicamento");
            String medCod = medJson.getString(Protocol.FIELD_MEDICAMENTO_CODIGO);
            String medNombre = medJson.getString(Protocol.FIELD_NOMBRE);
            String medPres = medJson.getString(Protocol.FIELD_PRESENTACION);
            Medicamento medicamento = new Medicamento(medCod, medNombre, medPres);
            
            int cantidad = detalleJson.getInt(Protocol.FIELD_CANTIDAD);
            String indicaciones = detalleJson.getString(Protocol.FIELD_INDICACIONES);
            int duracionDias = detalleJson.getInt(Protocol.FIELD_DURACION_DIAS);
            
            DetalleReceta detalle = new DetalleReceta(medicamento, cantidad, indicaciones, duracionDias);
            receta.agregarDetalle(detalle);
        }
        
        return receta;
    }
}