package com.mudanzas.dao;

import com.mudanzas.config.DatabaseConnection;
import com.mudanzas.model.Pago;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla `pagos`.
 * Todas las operaciones usan SQL puro con JDBC y manejo manual de conexiones.
 */
public class PagoDAO {

    private static final String SELECT_BASE =
        "SELECT p.*, " +
        "s.fecha_servicio, s.ciudad_origen, s.ciudad_destino, " +
        "s.costo_total, s.estado AS estado_servicio, " +
        "CONCAT(uc.nombre, ' ', uc.apellido) AS cliente_nombre, " +
        "uc.email AS cliente_email " +
        "FROM pagos p " +
        "INNER JOIN servicios_mudanza s  ON p.servicio_id  = s.id " +
        "INNER JOIN clientes          cl ON s.cliente_id   = cl.id " +
        "INNER JOIN usuarios          uc ON cl.usuario_id  = uc.id ";

    public List<Pago> findAll() throws SQLException {
        String sql = SELECT_BASE + "ORDER BY p.created_at DESC";
        List<Pago> lista = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapRow(rs));
        }
        return lista;
    }

    public Pago findById(int id) throws SQLException {
        String sql = SELECT_BASE + "WHERE p.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public List<Pago> findByServicioId(int servicioId) throws SQLException {
        String sql = "SELECT id, servicio_id, monto, metodo_pago, estado_pago, " +
                     "fecha_pago, referencia, notas, created_at, updated_at " +
                     "FROM pagos WHERE servicio_id = ? ORDER BY created_at DESC";
        List<Pago> lista = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, servicioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRowSimple(rs));
            }
        }
        return lista;
    }

    public List<Pago> findByEstadoPago(String estadoPago) throws SQLException {
        String sql = SELECT_BASE + "WHERE p.estado_pago = ? ORDER BY p.created_at DESC";
        List<Pago> lista = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estadoPago);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    public double sumByServicioId(int servicioId) throws SQLException {
        String sql = "SELECT ISNULL(SUM(monto), 0) AS total_pagado " +
                     "FROM pagos WHERE servicio_id = ? AND estado_pago = 'pagado'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, servicioId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("total_pagado");
            }
        }
        return 0.0;
    }

    public Pago create(Pago pago) throws SQLException {
        String sql = "INSERT INTO pagos (servicio_id, monto, metodo_pago, estado_pago, fecha_pago, referencia, notas) " +
                     "OUTPUT INSERTED.id VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pago.getServicioId());
            ps.setDouble(2, pago.getMonto());
            ps.setString(3, pago.getMetodoPago());
            ps.setString(4, pago.getEstadoPago() != null ? pago.getEstadoPago() : "pendiente");
            if (pago.getFechaPago() != null) ps.setString(5, pago.getFechaPago());
            else ps.setNull(5, Types.TIMESTAMP);
            if (pago.getReferencia() != null) ps.setString(6, pago.getReferencia());
            else ps.setNull(6, Types.NVARCHAR);
            if (pago.getNotas() != null) ps.setString(7, pago.getNotas());
            else ps.setNull(7, Types.NVARCHAR);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) pago.setId(rs.getInt(1));
            }
        }
        return findById(pago.getId());
    }

    public Pago update(int id, Pago pago) throws SQLException {
        String sql = "UPDATE pagos SET monto=?, metodo_pago=?, estado_pago=?, " +
                     "fecha_pago=?, referencia=?, notas=?, updated_at=GETDATE() WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, pago.getMonto());
            ps.setString(2, pago.getMetodoPago());
            ps.setString(3, pago.getEstadoPago());
            if (pago.getFechaPago() != null) ps.setString(4, pago.getFechaPago());
            else ps.setNull(4, Types.TIMESTAMP);
            if (pago.getReferencia() != null) ps.setString(5, pago.getReferencia());
            else ps.setNull(5, Types.NVARCHAR);
            if (pago.getNotas() != null) ps.setString(6, pago.getNotas());
            else ps.setNull(6, Types.NVARCHAR);
            ps.setInt(7, id);
            ps.executeUpdate();
        }
        return findById(id);
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM pagos WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Pago mapRow(ResultSet rs) throws SQLException {
        Pago p = mapRowSimple(rs);
        try { p.setFechaServicio(rs.getString("fecha_servicio")); } catch (SQLException ignored) {}
        try { p.setCiudadOrigen(rs.getString("ciudad_origen")); } catch (SQLException ignored) {}
        try { p.setCiudadDestino(rs.getString("ciudad_destino")); } catch (SQLException ignored) {}
        try { p.setCostoTotal(rs.getDouble("costo_total")); } catch (SQLException ignored) {}
        try { p.setEstadoServicio(rs.getString("estado_servicio")); } catch (SQLException ignored) {}
        try { p.setClienteNombre(rs.getString("cliente_nombre")); } catch (SQLException ignored) {}
        try { p.setClienteEmail(rs.getString("cliente_email")); } catch (SQLException ignored) {}
        return p;
    }

    private Pago mapRowSimple(ResultSet rs) throws SQLException {
        Pago p = new Pago();
        p.setId(rs.getInt("id"));
        p.setServicioId(rs.getInt("servicio_id"));
        p.setMonto(rs.getDouble("monto"));
        p.setMetodoPago(rs.getString("metodo_pago"));
        p.setEstadoPago(rs.getString("estado_pago"));
        p.setFechaPago(rs.getString("fecha_pago"));
        p.setReferencia(rs.getString("referencia"));
        p.setNotas(rs.getString("notas"));
        p.setCreatedAt(rs.getString("created_at"));
        try { p.setUpdatedAt(rs.getString("updated_at")); } catch (SQLException ignored) {}
        return p;
    }
}
