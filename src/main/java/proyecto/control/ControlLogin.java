package proyecto.control;
import java.util.List;

import proyecto.model.Farmaceuta;
import proyecto.model.Medico;
import proyecto.model.Usuario;
import proyecto.persistencia.XmlManager;
import proyecto.view.VentanaCambiarClave;
import proyecto.view.VentanaMenuAdmin;
import proyecto.view.VentanaMenuFarmaceuta;
import proyecto.view.VentanaMenuMedico;

public class ControlLogin {
    private List<Usuario> usuarios;
    private List<Medico> medicos;
    private List<Farmaceuta> farmaceutas;

    public ControlLogin() {
        usuarios = XmlManager.cargarUsuarios("usuarios.xml");
        medicos = XmlManager.cargarMedicos("medicos.xml");
        farmaceutas = XmlManager.cargarFarmaceutas("farmaceutas.xml");
    }

    public Usuario login(String id, String clave) {
        // Buscar en usuarios normales
        if (usuarios != null) {
            for (Usuario u : usuarios) {
                if (u.getId().equals(id) && u.getClave().equals(clave)) {
                   
                    return u;
                }
            }
        }

        // Buscar en medicos
        if (medicos != null) {
            for (Medico m : medicos) {
                if (m.getId().equals(id) && m.getClave().equals(clave)) {
                    
                    return m;
                }
            }
        }

        // Buscar en farmaceutas
        if (farmaceutas != null) {
            for (Farmaceuta f : farmaceutas) {
                if (f.getId().equals(id) && f.getClave().equals(clave)) {
                   
                    return f;
                }
            }
        }

        // No encontrado
        return null;
    }

    // Navegación: abrir la ventana correspondiente según el tipo de usuario
    public void postLogin(javax.swing.JFrame loginWindow, Usuario u) {
        if (u instanceof Medico) {
            new VentanaMenuMedico((Medico) u).setVisible(true);
        } else if (u instanceof Farmaceuta) {
            new VentanaMenuFarmaceuta((Farmaceuta) u).setVisible(true);
        } else {
            new VentanaMenuAdmin(u).setVisible(true);
        }
        if (loginWindow != null) loginWindow.dispose();
    }

    // Abrir ventana para cambiar clave
    public void openChangePassword(javax.swing.JFrame parent, Usuario u) {
        new VentanaCambiarClave(u, usuarios, parent).setVisible(true);
    }

}
