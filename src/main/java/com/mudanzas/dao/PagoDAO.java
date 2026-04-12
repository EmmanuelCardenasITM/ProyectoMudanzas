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

    /**
     * Retorna todos los pagos de un servicio.
     */
    public List<Pago> findByServicioId(int servicioId) throws SQLException {
        String sql = "SELECT * FROM pagos WHERE servicio_id = ? ORDER BY created_at";
        List<Pago> lista = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, servicioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    /**
     * Retorna la suma total de pagos de un servicio.
     */
    public double sumByServicioId(int servicioId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(monto), 0) AS total FROM pagos WHERE servicio_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, servicioId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("total");
            }
        }
        return 0.0;
    }

    /**
     * Registra un nuevo pago.
     */
    public Pago create(int servicioId, double monto) throws SQLException {
        String sql = "INSERT INTO pagos (servicio_id, monto) VALUES (?, ?)";
        int generatedId;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, servicioId);
            ps.setDouble(2, monto);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                generatedId = keys.getInt(1);
            }
        }
        // Recuperar el pago recién creado
        String sqlGet = "SELECT * FROM pagos WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlGet)) {
            ps.setInt(1, generatedId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    private Pago mapRow(ResultSet rs) throws SQLException {
        Pago p = new Pago();
        p.setId(rs.getInt("id"));
        p.setServicioId(rs.getInt("servicio_id"));
        p.setMonto(rs.getDouble("monto"));
        p.setCreatedAt(rs.getString("created_at"));
        return p;
    }
}
