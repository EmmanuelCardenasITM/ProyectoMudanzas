package com.mudanzas.dao;

import com.mudanzas.config.DatabaseConnection;
import com.mudanzas.model.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla `clientes`.
 * Todas las operaciones usan SQL puro con JDBC y manejo manual de conexiones.
 */
public class ClienteDAO {

    /**
     * Retorna todos los clientes ordenados por id.
     */
    public List<Cliente> findAll() throws SQLException {
        String sql = "SELECT * FROM clientes ORDER BY id";
        List<Cliente> lista = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    /**
     * Busca un cliente por su id.
     *
     * @return Cliente encontrado o null si no existe
     */
    public Cliente findById(int id) throws SQLException {
        String sql = "SELECT * FROM clientes WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    /**
     * Inserta un nuevo cliente.
     *
     * @return Cliente creado con id generado
     */
    public Cliente create(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO clientes (nombre, apellido, email, telefono, direccion) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getApellido());
            ps.setString(3, cliente.getEmail());
            ps.setString(4, cliente.getTelefono());
            ps.setString(5, cliente.getDireccion());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) cliente.setId(keys.getInt(1));
            }
        }
        return findById(cliente.getId());
    }

    /**
     * Actualiza los datos de un cliente existente.
     *
     * @return Cliente actualizado
     */
    public Cliente update(int id, Cliente cliente) throws SQLException {
        String sql = "UPDATE clientes SET nombre=?, apellido=?, email=?, telefono=?, direccion=?, updated_at=GETDATE() WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getApellido());
            ps.setString(3, cliente.getEmail());
            ps.setString(4, cliente.getTelefono());
            ps.setString(5, cliente.getDireccion());
            ps.setInt(6, id);
            ps.executeUpdate();
        }
        return findById(id);
    }

    /**
     * Elimina un cliente por su id.
     *
     * @return true si se eliminó, false si no existía
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM clientes WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Cliente mapRow(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setId(rs.getInt("id"));
        c.setNombre(rs.getString("nombre"));
        c.setApellido(rs.getString("apellido"));
        c.setEmail(rs.getString("email"));
        c.setTelefono(rs.getString("telefono"));
        c.setDireccion(rs.getString("direccion"));
        c.setCreatedAt(rs.getString("created_at"));
        c.setUpdatedAt(rs.getString("updated_at"));
        return c;
    }
}
