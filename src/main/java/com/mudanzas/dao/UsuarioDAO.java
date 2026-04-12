package com.mudanzas.dao;

import com.mudanzas.config.DatabaseConnection;
import com.mudanzas.model.Usuario;

import java.sql.*;

/**
 * DAO para la tabla `usuarios`.
 * Todas las operaciones usan SQL puro con JDBC y manejo manual de conexiones.
 */
public class UsuarioDAO {

    /**
     * Busca un usuario por su email.
     *
     * @param email Email del usuario
     * @return Usuario encontrado o null si no existe
     * @throws SQLException si ocurre un error de base de datos
     */
    public Usuario findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    /**
     * Crea un nuevo usuario en la base de datos.
     *
     * @param usuario Usuario a insertar (sin id)
     * @return Usuario creado con id generado
     * @throws SQLException si ocurre un error de base de datos
     */
    public Usuario create(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nombre, email, password_hash, rol) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getPasswordHash());
            ps.setString(4, usuario.getRol());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    usuario.setId(keys.getInt(1));
                }
            }
        }
        return usuario;
    }

    private Usuario mapRow(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setNombre(rs.getString("nombre"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setRol(rs.getString("rol"));
        u.setActivo(rs.getBoolean("activo"));
        u.setCreatedAt(rs.getString("created_at"));
        return u;
    }
}
