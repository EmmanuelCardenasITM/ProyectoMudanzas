package com.mudanzas.dao;

import com.mudanzas.config.DatabaseConnection;
import com.mudanzas.model.Servicio;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla `servicios`.
 * Todas las operaciones usan SQL puro con JDBC y manejo manual de conexiones.
 */
public class ServicioDAO {

    public List<Servicio> findAll() throws SQLException {
        String sql = "SELECT * FROM servicios ORDER BY id";
        List<Servicio> lista = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapRow(rs));
        }
        return lista;
    }

    public Servicio findById(int id) throws SQLException {
        String sql = "SELECT * FROM servicios WHERE id = ?";
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
     * Inserta un nuevo servicio con estado PENDIENTE.
     */
    public Servicio create(Servicio s) throws SQLException {
        String sql = "INSERT INTO servicios " +
                     "(cliente_id, direccion_origen, direccion_destino, fecha_servicio, distancia_km, carga, costo, estado) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, 'PENDIENTE')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, s.getClienteId());
            ps.setString(2, s.getDireccionOrigen());
            ps.setString(3, s.getDireccionDestino());
            ps.setString(4, s.getFechaServicio());
            ps.setDouble(5, s.getDistanciaKm());
            ps.setDouble(6, s.getCarga());
            ps.setDouble(7, s.getCosto());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) s.setId(keys.getInt(1));
            }
        }
        return findById(s.getId());
    }

    /**
     * Actualiza los datos de un servicio (sin cambiar el estado).
     */
    public Servicio update(int id, Servicio s) throws SQLException {
        String sql = "UPDATE servicios SET cliente_id=?, direccion_origen=?, direccion_destino=?, " +
                     "fecha_servicio=?, distancia_km=?, carga=?, costo=?, updated_at=GETDATE() WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, s.getClienteId());
            ps.setString(2, s.getDireccionOrigen());
            ps.setString(3, s.getDireccionDestino());
            ps.setString(4, s.getFechaServicio());
            ps.setDouble(5, s.getDistanciaKm());
            ps.setDouble(6, s.getCarga());
            ps.setDouble(7, s.getCosto());
            ps.setInt(8, id);
            ps.executeUpdate();
        }
        return findById(id);
    }

    /**
     * Actualiza únicamente el estado de un servicio.
     */
    public Servicio updateEstado(int id, String estado) throws SQLException {
        String sql = "UPDATE servicios SET estado=?, updated_at=GETDATE() WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
        return findById(id);
    }

    /**
     * Elimina un servicio por su id.
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM servicios WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Retorna los servicios de un cliente, opcionalmente filtrados por estado.
     * Ordenados por fecha_servicio DESC.
     */
    public List<Servicio> findByClienteId(int clienteId, String estado) throws SQLException {
        String sql;
        List<Servicio> lista = new ArrayList<>();
        if (estado != null && !estado.isEmpty()) {
            sql = "SELECT * FROM servicios WHERE cliente_id=? AND estado=? ORDER BY fecha_servicio DESC";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, clienteId);
                ps.setString(2, estado);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) lista.add(mapRow(rs));
                }
            }
        } else {
            sql = "SELECT * FROM servicios WHERE cliente_id=? ORDER BY fecha_servicio DESC";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, clienteId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) lista.add(mapRow(rs));
                }
            }
        }
        return lista;
    }

    private Servicio mapRow(ResultSet rs) throws SQLException {
        Servicio s = new Servicio();
        s.setId(rs.getInt("id"));
        s.setClienteId(rs.getInt("cliente_id"));
        s.setDireccionOrigen(rs.getString("direccion_origen"));
        s.setDireccionDestino(rs.getString("direccion_destino"));
        s.setFechaServicio(rs.getString("fecha_servicio"));
        s.setDistanciaKm(rs.getDouble("distancia_km"));
        s.setCarga(rs.getDouble("carga"));
        s.setCosto(rs.getDouble("costo"));
        s.setEstado(rs.getString("estado"));
        s.setCreatedAt(rs.getString("created_at"));
        s.setUpdatedAt(rs.getString("updated_at"));
        return s;
    }
}
