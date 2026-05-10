package com.mudanzas.service;

import com.mudanzas.dao.UsuarioDAO;
import com.mudanzas.model.Usuario;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.List;

/**
 * Servicio de lógica de negocio para la gestión de usuarios.
 */
public class UsuarioService {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public List<Usuario> listarUsuarios() throws SQLException {
        return usuarioDAO.findAll();
    }

    public List<Usuario> listarEmpleados() throws SQLException {
        return usuarioDAO.findEmpleados();
    }

    public Usuario obtenerUsuario(int id) throws SQLException {
        Usuario u = usuarioDAO.findById(id);
        if (u == null) throw new IllegalArgumentException("Usuario con id " + id + " no encontrado.");
        return u;
    }

    public Usuario actualizarUsuario(int id, Usuario datos) throws SQLException {
        obtenerUsuario(id); // lanza 404 si no existe
        return usuarioDAO.update(id, datos);
    }

    public void actualizarPassword(int id, String nuevaPassword) throws SQLException {
        obtenerUsuario(id); // lanza 404 si no existe
        if (nuevaPassword == null || nuevaPassword.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
        }
        String hashed = BCrypt.hashpw(nuevaPassword, BCrypt.gensalt(10));
        usuarioDAO.updatePassword(id, hashed);
    }

    public void eliminarUsuario(int id, int usuarioActualId) throws SQLException {
        obtenerUsuario(id); // lanza 404 si no existe
        if (id == usuarioActualId) {
            throw new IllegalStateException("No puedes eliminar tu propio usuario.");
        }
        usuarioDAO.delete(id);
    }
}
