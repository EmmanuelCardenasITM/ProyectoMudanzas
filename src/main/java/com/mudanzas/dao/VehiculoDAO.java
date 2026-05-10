package com.mudanzas.dao;

import com.mudanzas.config.DatabaseConnection;
import com.mudanzas.model.Vehiculo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla `vehiculos`.
 * Todas las operaciones usan SQL puro con JDBC y manejo manual de conexiones.
 */
public class VehiculoDAO {

    public List<Vehiculo> findAll() throws SQLException {
        String sql = "SELECT id, placa, tipo, capacidad_kg, disponible, created_at, updated_at " +
                     "FROM vehiculos ORDER BY id ASC";
        List<Vehiculo> lista = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapRow(rs));
        }
        return lista;
    }

    public Vehiculo findById(int id) throws SQLException {
        String sql = "SELECT id, placa, tipo, capacidad_kg, disponible, created_at, updated_at " +
                     "FROM vehiculos WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public Vehiculo findByPlaca(String placa) throws SQLException {
        String sql = "SELECT * FROM vehiculos WHERE placa = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, placa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public List<Vehiculo> findDisponibles() throws SQLException {
        String sql = "SELECT id, placa, tipo, capacidad_kg " +
                     "FROM vehiculos WHERE disponible = 1 ORDER BY capacidad_kg ASC";
        List<Vehiculo> lista = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Vehiculo v = new Vehiculo();
                v.setId(rs.getInt("id"));
                v.setPlaca(rs.getString("placa"));
                v.setTipo(rs.getString("tipo"));
                v.setCapacidadKg(rs.getDouble("capacidad_kg"));
                v.setDisponible(true);
                lista.add(v);
            }
        }
        return lista;
    }

    public Vehiculo create(Vehiculo v) throws SQLException {
        String sql = "INSERT INTO vehiculos (placa, tipo, capacidad_kg) " +
                     "OUTPUT INSERTED.id VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, v.getPlaca());
            ps.setString(2, v.getTipo());
            ps.setDouble(3, v.getCapacidadKg());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) v.setId(rs.getInt(1));
            }
        }
        return findById(v.getId());
    }

    public Vehiculo update(int id, Vehiculo v) throws SQLException {
        String sql = "UPDATE vehiculos SET placa=?, tipo=?, capacidad_kg=?, disponible=?, " +
                     "updated_at=GETDATE() WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, v.getPlaca());
            ps.setString(2, v.getTipo());
            ps.setDouble(3, v.getCapacidadKg());
            ps.setBoolean(4, v.isDisponible());
            ps.setInt(5, id);
            ps.executeUpdate();
        }
        return findById(id);
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM vehiculos WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Vehiculo mapRow(ResultSet rs) throws SQLException {
        Vehiculo v = new Vehiculo();
        v.setId(rs.getInt("id"));
        v.setPlaca(rs.getString("placa"));
        v.setTipo(rs.getString("tipo"));
        v.setCapacidadKg(rs.getDouble("capacidad_kg"));
        v.setDisponible(rs.getBoolean("disponible"));
        v.setCreatedAt(rs.getString("created_at"));
        v.setUpdatedAt(rs.getString("updated_at"));
        return v;
    }
}
