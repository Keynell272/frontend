package proyecto.control;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import proyecto.model.Farmaceuta;
import proyecto.model.Medico;
import proyecto.model.Usuario;
import proyecto.model.Medicamento;
import proyecto.model.Receta;
import proyecto.persistencia.XmlManager;
import proyecto.view.VentanaCambiarClave;
import proyecto.view.VentanaMenuAdmin;
import proyecto.view.VentanaMenuFarmaceuta;
import proyecto.view.VentanaMenuMedico;

public class ControlLogin {
    private List<Usuario> usuarios;
    private List<Medico> medicos;
    private List<Farmaceuta> farmaceutas;
    private List<Receta> recetas;
    private List<Medicamento> medicamentos;

    public ControlLogin() {
        usuarios = XmlManager.cargarUsuarios("usuarios.xml");
        medicos = XmlManager.cargarMedicos("medicos.xml");
        farmaceutas = XmlManager.cargarFarmaceutas("farmaceutas.xml");
        medicamentos = XmlManager.cargarMedicamentos("medicamentos.xml");
        recetas = XmlManager.cargarRecetas("recetas.xml", medicamentos);
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
            new VentanaMenuMedico((Medico) u, new ControlReceta(recetas)).setVisible(true);
        } else if (u instanceof Farmaceuta) {
            new VentanaMenuFarmaceuta((Farmaceuta) u, new ControlReceta(recetas)).setVisible(true);
        } else {
            new VentanaMenuAdmin(u).setVisible(true);
        }
        if (loginWindow != null) loginWindow.dispose();
    }

    // Abrir ventana para cambiar clave
    public void openChangePassword(javax.swing.JFrame parent, Usuario u) {
        new VentanaCambiarClave(u, usuarios, parent, this).setVisible(true);
    }

    public void cambiarClave(Usuario usuario, List<Usuario> listaUsuarios,
                             String actual, String nueva, String confirmar,
                             JFrame ventana, JFrame ventanaLogin) {
        if (!usuario.getClave().equals(actual)) {
            JOptionPane.showMessageDialog(ventana, "❌ La clave actual no es correcta");
            return;
        }
        if (!nueva.equals(confirmar)) {
            JOptionPane.showMessageDialog(ventana, "❌ Las claves nuevas no coinciden");
            return;
        }
        if (nueva.isEmpty()) {
            JOptionPane.showMessageDialog(ventana, "❌ La nueva clave no puede estar vacía");
            return;
        }

        // Cambiar la clave a el usuario, medico o farmaceuta
        if (usuario instanceof Medico) {
            ((Medico) usuario).setClave(nueva);
            proyecto.persistencia.XmlManager.guardarMedicos(medicos, "medicos.xml");
        } else if (usuario instanceof Farmaceuta) {
            ((Farmaceuta) usuario).setClave(nueva);
            proyecto.persistencia.XmlManager.guardarFarmaceutas(farmaceutas, "farmaceutas.xml");
        }
        else
            usuario.setClave(nueva);
            proyecto.persistencia.XmlManager.guardarUsuarios(listaUsuarios, "usuarios.xml");
        

        JOptionPane.showMessageDialog(ventana, "✅ Clave cambiada correctamente");

        if (ventanaLogin != null) ventanaLogin.setVisible(true);
        ventana.dispose();
    }


}
