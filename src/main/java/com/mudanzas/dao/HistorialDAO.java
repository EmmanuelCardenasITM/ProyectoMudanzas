package com.mudanzas.dao;

import com.mudanzas.config.DatabaseConnection;
import com.mudanzas.model.HistorialEstado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla `historial_estados`.
 * Registra cada cambio de estado de un servicio de mudanza.
 */
public class HistorialDAO {

    /**
     * Retorna el historial de estados de un servicio, ordenado cronológicamente.
     */
    public List<HistorialEstado> findByServicioId(int servicioId) throws SQLException {
        String sql = "SELECT h.id, h.servicio_id, h.estado_anterior, h.estado_nuevo, " +
                     "h.observacion, h.created_at, h.usuario_id, " +
                     "CONCAT(u.nombre, ' ', u.apellido) AS usuario_nombre, u.rol AS usuario_rol " +
                     "FROM historial_estados h " +
                     "INNER JOIN usuarios u ON h.usuario_id = u.id " +
                     "WHERE h.servicio_id = ? ORDER BY h.created_at ASC";
        List<HistorialEstado> lista = new ArrayList<>();
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
     * Registra un nuevo cambio de estado.
     *
     * @param servicioId     ID del servicio
     * @param estadoAnterior Estado previo (null si es creación)
     * @param estadoNuevo    Nuevo estado
     * @param usuarioId      ID del usuario que realizó el cambio
     * @param observacion    Comentario opcional
     * @return ID del registro creado
     */
    public int create(int servicioId, String estadoAnterior, String estadoNuevo,
                      int usuarioId, String observacion) throws SQLException {
        String sql = "INSERT INTO historial_estados " +
                     "(servicio_id, estado_anterior, estado_nuevo, usuario_id, observacion) " +
                     "OUTPUT INSERTED.id VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, servicioId);
            if (estadoAnterior != null) ps.setString(2, estadoAnterior);
            else ps.setNull(2, Types.NVARCHAR);
            ps.setString(3, estadoNuevo);
            ps.setInt(4, usuarioId);
            if (observacion != null) ps.setString(5, observacion);
            else ps.setNull(5, Types.NVARCHAR);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    private HistorialEstado mapRow(ResultSet rs) throws SQLException {
        HistorialEstado h = new HistorialEstado();
        h.setId(rs.getInt("id"));
        h.setServicioId(rs.getInt("servicio_id"));
        h.setEstadoAnterior(rs.getString("estado_anterior"));
        h.setEstadoNuevo(rs.getString("estado_nuevo"));
        h.setUsuarioId(rs.getInt("usuario_id"));
        h.setUsuarioNombre(rs.getString("usuario_nombre"));
        h.setUsuarioRol(rs.getString("usuario_rol"));
        h.setObservacion(rs.getString("observacion"));
        h.setCreatedAt(rs.getString("created_at"));
        return h;
    }
}
