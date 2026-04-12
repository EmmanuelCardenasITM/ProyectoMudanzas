package com.mudanzas.dao;

import com.mudanzas.config.DatabaseConnection;
import com.mudanzas.model.Tarifa;

import java.sql.*;

/**
 * DAO para la tabla `tarifas`.
 * Todas las operaciones usan SQL puro con JDBC y manejo manual de conexiones.
 */
public class TarifaDAO {

    /**
     * Retorna la tarifa vigente (la de mayor id).
     *
     * @return Tarifa vigente o null si no hay ninguna configurada
     */
    public Tarifa findVigente() throws SQLException {
        String sql = "SELECT TOP 1 * FROM tarifas ORDER BY id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    /**
     * Actualiza la tarifa vigente.
     *
     * @return Tarifa actualizada
     */
    public Tarifa update(double tarifaPorKm, double tarifaPorUnidadCarga) throws SQLException {
        String sql = "UPDATE tarifas SET tarifa_por_km=?, tarifa_por_unidad_carga=?, updated_at=GETDATE() " +
                     "WHERE id=(SELECT TOP 1 id FROM tarifas ORDER BY id DESC)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, tarifaPorKm);
            ps.setDouble(2, tarifaPorUnidadCarga);
            ps.executeUpdate();
        }
        return findVigente();
    }

    /**
     * Inserta una nueva tarifa (para seed inicial).
     */
    public Tarifa create(double tarifaPorKm, double tarifaPorUnidadCarga) throws SQLException {
        String sql = "INSERT INTO tarifas (tarifa_por_km, tarifa_por_unidad_carga) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, tarifaPorKm);
            ps.setDouble(2, tarifaPorUnidadCarga);
            ps.executeUpdate();
        }
        return findVigente();
    }

    private Tarifa mapRow(ResultSet rs) throws SQLException {
        Tarifa t = new Tarifa();
        t.setId(rs.getInt("id"));
        t.setTarifaPorKm(rs.getDouble("tarifa_por_km"));
        t.setTarifaPorUnidadCarga(rs.getDouble("tarifa_por_unidad_carga"));
        t.setUpdatedAt(rs.getString("updated_at"));
        return t;
    }
}
