package proyecto.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Receta {
    private String id;
    private Date fecha;
    private Date fechaRetiro;
    private String estado; // "confeccionada", "proceso", "lista", "entregada"
    private List<DetalleReceta> detalles;

    public Receta(String id, Date fecha, Date fechaRetiro, String estado) {
        this.id = id;
        this.fecha = fecha;
        this.fechaRetiro = fechaRetiro;
        this.estado = estado;
        this.detalles = new ArrayList<>();
    }

    public void agregarDetalle(DetalleReceta detalle) {
        detalles.add(detalle);
    }

    public void cambiarEstado(String nuevoEstado) {
        this.estado = nuevoEstado;
    }

    // Getters
    public String getId() {
        return id;
    }
    public Date getFecha() {
        return fecha;
    }
    public Date getFechaRetiro() {
        return fechaRetiro;
    }
    public String getEstado() {
        return estado;
    }
    public List<DetalleReceta> getDetalles() {
        return detalles;
    }
    
    // Setters
    public void setId(String id) {
        this.id = id;
    }   
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    public void setFechaRetiro(Date fechaRetiro) {
        this.fechaRetiro = fechaRetiro;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
    public void setDetalles(List<DetalleReceta> detalles) {
        this.detalles = detalles;
    }

}
