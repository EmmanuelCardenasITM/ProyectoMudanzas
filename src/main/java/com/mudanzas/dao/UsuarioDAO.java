package com.mudanzas.dao;

import com.mudanzas.config.DatabaseConnection;
import com.mudanzas.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla `usuarios`.
 * Todas las operaciones usan SQL puro con JDBC y manejo manual de conexiones.
 */
public class UsuarioDAO {

    public List<Usuario> findAll() throws SQLException {
        String sql = "SELECT id, nombre, apellido, email, telefono, rol, activo, created_at, updated_at " +
                     "FROM usuarios ORDER BY id ASC";
        List<Usuario> lista = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapRow(rs));
        }
        return lista;
    }

    public Usuario findById(int id) throws SQLException {
        String sql = "SELECT id, nombre, apellido, email, telefono, rol, activo, created_at, updated_at " +
                     "FROM usuarios WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public Usuario findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowConPassword(rs);
            }
        }
        return null;
    }

    public List<Usuario> findEmpleados() throws SQLException {
        String sql = "SELECT id, nombre, apellido, email, telefono, rol, activo " +
                     "FROM usuarios WHERE rol = 'empleado' AND activo = 1 ORDER BY nombre ASC";
        List<Usuario> lista = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapRow(rs));
        }
        return lista;
    }

    /**
     * Crea un nuevo usuario.
     *
     * @return Usuario creado con id generado
     */
    public Usuario create(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nombre, apellido, email, password, telefono, rol) " +
                     "OUTPUT INSERTED.id VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getApellido() != null ? usuario.getApellido() : "");
            ps.setString(3, usuario.getEmail());
            ps.setString(4, usuario.getPassword());
            if (usuario.getTelefono() != null) ps.setString(5, usuario.getTelefono());
            else ps.setNull(5, Types.NVARCHAR);
            ps.setString(6, usuario.getRol() != null ? usuario.getRol() : "cliente");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) usuario.setId(rs.getInt(1));
            }
        }
        return findById(usuario.getId());
    }

    public Usuario update(int id, Usuario usuario) throws SQLException {
        String sql = "UPDATE usuarios SET nombre=?, apellido=?, email=?, telefono=?, " +
                     "rol=?, activo=?, updated_at=GETDATE() WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getApellido());
            ps.setString(3, usuario.getEmail());
            if (usuario.getTelefono() != null) ps.setString(4, usuario.getTelefono());
            else ps.setNull(4, Types.NVARCHAR);
            ps.setString(5, usuario.getRol());
            ps.setBoolean(6, usuario.isActivo());
            ps.setInt(7, id);
            ps.executeUpdate();
        }
        return findById(id);
    }

    public boolean updatePassword(int id, String hashedPassword) throws SQLException {
        String sql = "UPDATE usuarios SET password=?, updated_at=GETDATE() WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hashedPassword);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /** Mapea sin incluir el password (para respuestas al cliente) */
    private Usuario mapRow(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setNombre(rs.getString("nombre"));
        try { u.setApellido(rs.getString("apellido")); } catch (SQLException ignored) {}
        u.setEmail(rs.getString("email"));
        try { u.setTelefono(rs.getString("telefono")); } catch (SQLException ignored) {}
        u.setRol(rs.getString("rol"));
        u.setActivo(rs.getBoolean("activo"));
        try { u.setCreatedAt(rs.getString("created_at")); } catch (SQLException ignored) {}
        try { u.setUpdatedAt(rs.getString("updated_at")); } catch (SQLException ignored) {}
        return u;
    }

    /** Mapea incluyendo el password (solo para autenticación) */
    private Usuario mapRowConPassword(ResultSet rs) throws SQLException {
        Usuario u = mapRow(rs);
        // Intenta leer "password" (nuevo schema) o "password_hash" (schema viejo)
        try { u.setPassword(rs.getString("password")); } catch (SQLException ignored) {}
        try { u.setPasswordHash(rs.getString("password_hash")); } catch (SQLException ignored) {}
        return u;
    }
}
