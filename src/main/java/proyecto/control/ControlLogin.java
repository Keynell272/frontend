package proyecto.control;
import proyecto.model.Usuario;
import java.util.List;

public class ControlLogin {
    private List<Usuario> usuarios;

    public ControlLogin(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public Usuario login(String id, String clave) {
        for (Usuario u : usuarios) {
            if (u.getId().equals(id) && u.getClave().equals(clave)) {
                return u;
            }
        }
        return null; // login fallido
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }
}

