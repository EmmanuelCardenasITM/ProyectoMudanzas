package com.mudanzas.dao;

import com.mudanzas.config.DatabaseConnection;
import com.mudanzas.model.Cliente;
import com.mudanzas.model.Servicio;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla `clientes`.
 * Los clientes están vinculados a un usuario (usuario_id).
 */
public class ClienteDAO {

    public List<Cliente> findAll() throws SQLException {
        String sql = "SELECT c.id, c.usuario_id, c.direccion, c.ciudad, c.documento, " +
                     "c.created_at, c.updated_at, " +
                     "u.nombre, u.apellido, u.email, u.telefono, u.activo " +
                     "FROM clientes c INNER JOIN usuarios u ON c.usuario_id = u.id " +
                     "ORDER BY c.id ASC";
        List<Cliente> lista = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapRow(rs));
        }
        return lista;
    }

    public Cliente findById(int id) throws SQLException {
        String sql = "SELECT c.id, c.usuario_id, c.direccion, c.ciudad, c.documento, " +
                     "c.created_at, c.updated_at, " +
                     "u.nombre, u.apellido, u.email, u.telefono, u.activo " +
                     "FROM clientes c INNER JOIN usuarios u ON c.usuario_id = u.id " +
                     "WHERE c.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public Cliente findByUsuarioId(int usuarioId) throws SQLException {
        String sql = "SELECT c.id, c.usuario_id, c.direccion, c.ciudad, c.documento, " +
                     "c.created_at, c.updated_at, " +
                     "u.nombre, u.apellido, u.email, u.telefono, u.activo " +
                     "FROM clientes c INNER JOIN usuarios u ON c.usuario_id = u.id " +
                     "WHERE c.usuario_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    /**
     * Crea un perfil de cliente vinculado a un usuario existente.
     *
     * @return ID del cliente creado
     */
    public int create(int usuarioId, String direccion, String ciudad, String documento) throws SQLException {
        String sql = "INSERT INTO clientes (usuario_id, direccion, ciudad, documento) " +
                     "OUTPUT INSERTED.id VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            if (direccion != null) ps.setString(2, direccion); else ps.setNull(2, Types.NVARCHAR);
            if (ciudad != null)    ps.setString(3, ciudad);    else ps.setNull(3, Types.NVARCHAR);
            if (documento != null) ps.setString(4, documento); else ps.setNull(4, Types.NVARCHAR);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    public Cliente update(int id, String direccion, String ciudad, String documento) throws SQLException {
        String sql = "UPDATE clientes SET direccion=?, ciudad=?, documento=?, updated_at=GETDATE() WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (direccion != null) ps.setString(1, direccion); else ps.setNull(1, Types.NVARCHAR);
            if (ciudad != null)    ps.setString(2, ciudad);    else ps.setNull(2, Types.NVARCHAR);
            if (documento != null) ps.setString(3, documento); else ps.setNull(3, Types.NVARCHAR);
            ps.setInt(4, id);
            ps.executeUpdate();
        }
        return findById(id);
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM clientes WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Retorna el historial de servicios de un cliente con datos de empleado y vehículo.
     */
    public List<Servicio> findHistorialServicios(int clienteId) throws SQLException {
        String sql = "SELECT s.id, s.fecha_servicio, s.hora_servicio, " +
                     "s.direccion_origen, s.ciudad_origen, s.direccion_destino, s.ciudad_destino, " +
                     "s.distancia_km, s.peso_carga_kg, s.costo_total, s.estado, s.created_at, " +
                     "CONCAT(ue.nombre, ' ', ue.apellido) AS empleado_nombre, " +
                     "v.placa AS vehiculo_placa, v.tipo AS vehiculo_tipo " +
                     "FROM servicios_mudanza s " +
                     "LEFT JOIN usuarios  ue ON s.empleado_id = ue.id " +
                     "LEFT JOIN vehiculos v  ON s.vehiculo_id = v.id " +
                     "WHERE s.cliente_id = ? ORDER BY s.fecha_servicio DESC";
        List<Servicio> lista = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clienteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Servicio s = new Servicio();
                    s.setId(rs.getInt("id"));
                    s.setFechaServicio(rs.getString("fecha_servicio"));
                    s.setHoraServicio(rs.getString("hora_servicio"));
                    s.setDireccionOrigen(rs.getString("direccion_origen"));
                    s.setCiudadOrigen(rs.getString("ciudad_origen"));
                    s.setDireccionDestino(rs.getString("direccion_destino"));
                    s.setCiudadDestino(rs.getString("ciudad_destino"));
                    s.setDistanciaKm(rs.getDouble("distancia_km"));
                    s.setPesoCargaKg(rs.getDouble("peso_carga_kg"));
                    s.setCostoTotal(rs.getDouble("costo_total"));
                    s.setEstado(rs.getString("estado"));
                    s.setCreatedAt(rs.getString("created_at"));
                    s.setEmpleadoNombre(rs.getString("empleado_nombre"));
                    s.setVehiculoPlaca(rs.getString("vehiculo_placa"));
                    s.setVehiculoTipo(rs.getString("vehiculo_tipo"));
                    lista.add(s);
                }
            }
        }
        return lista;
    }

    private Cliente mapRow(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setId(rs.getInt("id"));
        c.setUsuarioId(rs.getInt("usuario_id"));
        c.setNombre(rs.getString("nombre"));
        c.setApellido(rs.getString("apellido"));
        c.setEmail(rs.getString("email"));
        c.setTelefono(rs.getString("telefono"));
        c.setActivo(rs.getBoolean("activo"));
        c.setDireccion(rs.getString("direccion"));
        c.setCiudad(rs.getString("ciudad"));
        c.setDocumento(rs.getString("documento"));
        c.setCreatedAt(rs.getString("created_at"));
        c.setUpdatedAt(rs.getString("updated_at"));
        return c;
    }
}
