package com.mudanzas.dao;

import com.mudanzas.config.DatabaseConnection;
import com.mudanzas.model.Servicio;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla `servicios_mudanza`.
 * Todas las operaciones usan SQL puro con JDBC y manejo manual de conexiones.
 */
public class ServicioDAO {

    private static final String SELECT_BASE =
        "SELECT s.*, " +
        "CONCAT(uc.nombre, ' ', uc.apellido) AS cliente_nombre, " +
        "uc.email AS cliente_email, " +
        "CONCAT(ue.nombre, ' ', ue.apellido) AS empleado_nombre, " +
        "v.placa AS vehiculo_placa, v.tipo AS vehiculo_tipo " +
        "FROM servicios_mudanza s " +
        "INNER JOIN clientes cl ON s.cliente_id = cl.id " +
        "INNER JOIN usuarios uc ON cl.usuario_id = uc.id " +
        "LEFT  JOIN usuarios ue ON s.empleado_id = ue.id " +
        "LEFT  JOIN vehiculos v ON s.vehiculo_id = v.id ";

    public List<Servicio> findAll() throws SQLException {
        String sql = SELECT_BASE + "ORDER BY s.fecha_servicio DESC, s.hora_servicio DESC";
        List<Servicio> lista = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapRow(rs));
        }
        return lista;
    }

    public Servicio findById(int id) throws SQLException {
        String sql = SELECT_BASE + "WHERE s.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public List<Servicio> findByEstado(String estado) throws SQLException {
        String sql = SELECT_BASE + "WHERE s.estado = ? ORDER BY s.fecha_servicio ASC";
        List<Servicio> lista = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    public List<Servicio> findByClienteId(int clienteId) throws SQLException {
        String sql = SELECT_BASE + "WHERE s.cliente_id = ? ORDER BY s.fecha_servicio DESC";
        List<Servicio> lista = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clienteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    public Servicio create(Servicio s) throws SQLException {
        String sql = "INSERT INTO servicios_mudanza " +
                     "(cliente_id, vehiculo_id, empleado_id, fecha_servicio, hora_servicio, " +
                     "direccion_origen, ciudad_origen, direccion_destino, ciudad_destino, " +
                     "distancia_km, peso_carga_kg, descripcion_carga, costo_base, costo_total, notas) " +
                     "OUTPUT INSERTED.id " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, s.getClienteId());
            setNullableInt(ps, 2, s.getVehiculoId());
            setNullableInt(ps, 3, s.getEmpleadoId());
            ps.setString(4, s.getFechaServicio());
            ps.setString(5, s.getHoraServicio() != null ? s.getHoraServicio() : "08:00");
            ps.setString(6, s.getDireccionOrigen());
            ps.setString(7, s.getCiudadOrigen() != null ? s.getCiudadOrigen() : "");
            ps.setString(8, s.getDireccionDestino());
            ps.setString(9, s.getCiudadDestino() != null ? s.getCiudadDestino() : "");
            ps.setDouble(10, s.getDistanciaKm());
            ps.setDouble(11, s.getPesoCargaKg());
            if (s.getDescripcionCarga() != null) ps.setString(12, s.getDescripcionCarga());
            else ps.setNull(12, Types.NVARCHAR);
            ps.setDouble(13, s.getCostoBase());
            ps.setDouble(14, s.getCostoTotal());
            if (s.getNotas() != null) ps.setString(15, s.getNotas());
            else ps.setNull(15, Types.NVARCHAR);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) s.setId(rs.getInt(1));
            }
        }
        return findById(s.getId());
    }

    public Servicio update(int id, Servicio s) throws SQLException {
        String sql = "UPDATE servicios_mudanza SET " +
                     "vehiculo_id=?, empleado_id=?, fecha_servicio=?, hora_servicio=?, " +
                     "direccion_origen=?, ciudad_origen=?, direccion_destino=?, ciudad_destino=?, " +
                     "distancia_km=?, peso_carga_kg=?, descripcion_carga=?, " +
                     "costo_base=?, costo_total=?, notas=?, updated_at=GETDATE() WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setNullableInt(ps, 1, s.getVehiculoId());
            setNullableInt(ps, 2, s.getEmpleadoId());
            ps.setString(3, s.getFechaServicio());
            ps.setString(4, s.getHoraServicio() != null ? s.getHoraServicio() : "08:00");
            ps.setString(5, s.getDireccionOrigen());
            ps.setString(6, s.getCiudadOrigen() != null ? s.getCiudadOrigen() : "");
            ps.setString(7, s.getDireccionDestino());
            ps.setString(8, s.getCiudadDestino() != null ? s.getCiudadDestino() : "");
            ps.setDouble(9, s.getDistanciaKm());
            ps.setDouble(10, s.getPesoCargaKg());
            if (s.getDescripcionCarga() != null) ps.setString(11, s.getDescripcionCarga());
            else ps.setNull(11, Types.NVARCHAR);
            ps.setDouble(12, s.getCostoBase());
            ps.setDouble(13, s.getCostoTotal());
            if (s.getNotas() != null) ps.setString(14, s.getNotas());
            else ps.setNull(14, Types.NVARCHAR);
            ps.setInt(15, id);
            ps.executeUpdate();
        }
        return findById(id);
    }

    public Servicio updateEstado(int id, String estado) throws SQLException {
        String sql = "UPDATE servicios_mudanza SET estado=?, updated_at=GETDATE() WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
        return findById(id);
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM servicios_mudanza WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private void setNullableInt(PreparedStatement ps, int index, Integer value) throws SQLException {
        if (value != null) ps.setInt(index, value);
        else ps.setNull(index, Types.INTEGER);
    }

    private Servicio mapRow(ResultSet rs) throws SQLException {
        Servicio s = new Servicio();
        s.setId(rs.getInt("id"));
        s.setClienteId(rs.getInt("cliente_id"));
        int vid = rs.getInt("vehiculo_id"); if (!rs.wasNull()) s.setVehiculoId(vid);
        int eid = rs.getInt("empleado_id"); if (!rs.wasNull()) s.setEmpleadoId(eid);
        s.setFechaServicio(rs.getString("fecha_servicio"));
        s.setHoraServicio(rs.getString("hora_servicio"));
        s.setDireccionOrigen(rs.getString("direccion_origen"));
        s.setCiudadOrigen(rs.getString("ciudad_origen"));
        s.setDireccionDestino(rs.getString("direccion_destino"));
        s.setCiudadDestino(rs.getString("ciudad_destino"));
        s.setDistanciaKm(rs.getDouble("distancia_km"));
        s.setPesoCargaKg(rs.getDouble("peso_carga_kg"));
        s.setDescripcionCarga(rs.getString("descripcion_carga"));
        s.setCostoBase(rs.getDouble("costo_base"));
        s.setCostoTotal(rs.getDouble("costo_total"));
        s.setEstado(rs.getString("estado"));
        s.setNotas(rs.getString("notas"));
        s.setCreatedAt(rs.getString("created_at"));
        s.setUpdatedAt(rs.getString("updated_at"));
        // Campos de JOIN
        try { s.setClienteNombre(rs.getString("cliente_nombre")); } catch (SQLException ignored) {}
        try { s.setClienteEmail(rs.getString("cliente_email")); } catch (SQLException ignored) {}
        try { s.setEmpleadoNombre(rs.getString("empleado_nombre")); } catch (SQLException ignored) {}
        try { s.setVehiculoPlaca(rs.getString("vehiculo_placa")); } catch (SQLException ignored) {}
        try { s.setVehiculoTipo(rs.getString("vehiculo_tipo")); } catch (SQLException ignored) {}
        return s;
    }
}
