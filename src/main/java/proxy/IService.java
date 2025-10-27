package proxy;

import model.*;
import java.util.List;

public interface IService {
    
    // ==================== AUTENTICACIÓN ====================
    
    Usuario login(String usuarioId, String clave) throws Exception;
    
    void logout(String usuarioId) throws Exception;
    
    boolean cambiarClave(String usuarioId, String nuevaClave) throws Exception;
    
    // ==================== LISTAS ====================
    
    List<Medico> listarMedicos() throws Exception;
    
    List<Farmaceuta> listarFarmaceutas() throws Exception;

    List<Paciente> listarPacientes() throws Exception;
    
    List<Medicamento> listarMedicamentos() throws Exception;
    
    // ==================== CATÁLOGO DE MEDICAMENTOS ====================
    
    boolean agregarMedicamento(Medicamento medicamento) throws Exception;
    
    boolean actualizarMedicamento(Medicamento medicamento) throws Exception;

    boolean eliminarMedicamento(String codigo) throws Exception;

    Medicamento buscarMedicamento(String codigo) throws Exception;
    
    // ==================== PACIENTES ====================
    
    boolean agregarPaciente(Paciente paciente) throws Exception;
    
    boolean actualizarPaciente(Paciente paciente) throws Exception;
    
    Paciente buscarPaciente(String id) throws Exception;
    
    boolean eliminarPaciente(String id) throws Exception;
    
    // ==================== PRESCRIPCIÓN ====================
    
    boolean crearReceta(Receta receta) throws Exception;
    
    Receta buscarReceta(String id) throws Exception;
    
    List<Receta> listarRecetas() throws Exception;
    
    List<Receta> listarRecetasPorEstado(String estado) throws Exception;
    
    // ==================== DESPACHO ====================
    
    boolean iniciarDespacho(String recetaId) throws Exception;
    
    boolean marcarRecetaLista(String recetaId) throws Exception;
    
    boolean entregarReceta(String recetaId) throws Exception;
    
    // ==================== USUARIOS ====================
    
    List<Usuario> listarUsuarios() throws Exception;
    
    boolean agregarUsuario(Usuario usuario) throws Exception;

    List<UsuarioActivo> listarUsuariosActivos() throws Exception;
    
    boolean eliminarUsuario(String id) throws Exception;

    // ==================== MENSAJERÍA ====================
    
    boolean enviarMensaje(Mensaje mensaje) throws Exception;
    
    List<Mensaje> recibirMensajes(String usuarioId) throws Exception;
    
    boolean marcarMensajeLeido(int mensajeId) throws Exception;
    
    int contarMensajesNoLeidos(String usuarioId) throws Exception;
}