package proyecto.model;

public class Medico extends Usuario {
    private String especialidad;

    public Medico(String id, String clave, String nombre, String especialidad) {
        super(id, clave, nombre);
        this.especialidad = especialidad;
    }

    public String getEspecialidad() { return especialidad; }
}
