package proyecto.model;

public class DetalleReceta {
    private Medicamento medicamento;
    private int cantidad;
    private String indicaciones;
    private int duracionDias;

    public DetalleReceta(Medicamento medicamento, int cantidad, String indicaciones, int duracionDias) {
        this.medicamento = medicamento;
        this.cantidad = cantidad;
        this.indicaciones = indicaciones;
        this.duracionDias = duracionDias;
    }

    // Getters
    public Medicamento getMedicamento() {
        return medicamento;
    }
    public int getCantidad() {
        return cantidad;
    }
    public String getIndicaciones() {
        return indicaciones;
    }
    public int getDuracionDias() {
        return duracionDias;
    }
    // Setters
    public void setMedicamento(Medicamento medicamento) {
        this.medicamento = medicamento;
    }
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    public void setIndicaciones(String indicaciones) {
        this.indicaciones = indicaciones;
    }
    public void setDuracionDias(int duracionDias) {
        this.duracionDias = duracionDias;
    }
    
}
