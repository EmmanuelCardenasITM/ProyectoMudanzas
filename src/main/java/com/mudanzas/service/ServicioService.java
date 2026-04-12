package com.mudanzas.service;

import com.mudanzas.dao.ServicioDAO;
import com.mudanzas.dao.TarifaDAO;
import com.mudanzas.model.Servicio;
import com.mudanzas.model.Tarifa;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de lógica de negocio para servicios de mudanza.
 * Gestiona validaciones, cálculo de costo y máquina de estados.
 */
public class ServicioService {

    private final ServicioDAO servicioDAO = new ServicioDAO();
    private final TarifaDAO   tarifaDAO   = new TarifaDAO();

    public List<Servicio> listarServicios() throws SQLException {
        return servicioDAO.findAll();
    }

    public Servicio obtenerServicio(int id) throws SQLException {
        Servicio s = servicioDAO.findById(id);
        if (s == null) throw new IllegalArgumentException("Servicio con id " + id + " no encontrado");
        return s;
    }

    /**
     * Crea un nuevo servicio calculando el costo automáticamente.
     * Estado inicial siempre PENDIENTE.
     */
    public Servicio crearServicio(Servicio s) throws SQLException {
        validarCampos(s);
        Tarifa tarifa = tarifaDAO.findVigente();
        if (tarifa == null) throw new IllegalStateException("No hay tarifas configuradas");
        double costo = calcularCosto(s.getDistanciaKm(), s.getCarga(),
                                     tarifa.getTarifaPorKm(), tarifa.getTarifaPorUnidadCarga());
        s.setCosto(costo);
        return servicioDAO.create(s);
    }

    /**
     * Actualiza los datos de un servicio. No se puede modificar si está FINALIZADO.
     */
    public Servicio actualizarServicio(int id, Servicio s) throws SQLException {
        Servicio actual = obtenerServicio(id);
        if ("FINALIZADO".equals(actual.getEstado())) {
            throw new IllegalStateException("No se puede modificar un servicio finalizado");
        }
        validarCampos(s);
        Tarifa tarifa = tarifaDAO.findVigente();
        if (tarifa == null) throw new IllegalStateException("No hay tarifas configuradas");
        double costo = calcularCosto(s.getDistanciaKm(), s.getCarga(),
                                     tarifa.getTarifaPorKm(), tarifa.getTarifaPorUnidadCarga());
        s.setCosto(costo);
        return servicioDAO.update(id, s);
    }

    /**
     * Cambia el estado de un servicio validando la transición.
     * Transiciones válidas: PENDIENTE → EN_PROCESO, EN_PROCESO → FINALIZADO.
     */
    public Servicio cambiarEstado(int id, String nuevoEstado) throws SQLException {
        Servicio actual = obtenerServicio(id);
        String estadoActual = actual.getEstado();
        boolean valida = ("PENDIENTE".equals(estadoActual) && "EN_PROCESO".equals(nuevoEstado))
                      || ("EN_PROCESO".equals(estadoActual) && "FINALIZADO".equals(nuevoEstado));
        if (!valida) {
            throw new IllegalStateException(
                "Transición de estado inválida: " + estadoActual + " → " + nuevoEstado);
        }
        return servicioDAO.updateEstado(id, nuevoEstado);
    }

    /**
     * Elimina un servicio. Solo se puede eliminar si está en estado PENDIENTE.
     */
    public void eliminarServicio(int id) throws SQLException {
        Servicio actual = obtenerServicio(id);
        if (!"PENDIENTE".equals(actual.getEstado())) {
            throw new IllegalStateException(
                "No se puede eliminar un servicio en estado " + actual.getEstado());
        }
        servicioDAO.delete(id);
    }

    public List<Servicio> historialPorCliente(int clienteId, String estado) throws SQLException {
        return servicioDAO.findByClienteId(clienteId, estado);
    }

    // ── Fórmula de cálculo de costo ──────────────────────────────────────────
    public static double calcularCosto(double distanciaKm, double carga,
                                       double tarifaKm, double tarifaUnidad) {
        return (distanciaKm * tarifaKm) + (carga * tarifaUnidad);
    }

    private void validarCampos(Servicio s) {
        List<String> faltantes = new ArrayList<>();
        if (s.getClienteId() <= 0)                                    faltantes.add("cliente_id");
        if (s.getDireccionOrigen() == null || s.getDireccionOrigen().isBlank())  faltantes.add("direccion_origen");
        if (s.getDireccionDestino() == null || s.getDireccionDestino().isBlank()) faltantes.add("direccion_destino");
        if (s.getFechaServicio() == null || s.getFechaServicio().isBlank())       faltantes.add("fecha_servicio");
        if (!faltantes.isEmpty()) {
            throw new IllegalArgumentException("Campos obligatorios ausentes: " + String.join(", ", faltantes));
        }
        if (s.getDistanciaKm() <= 0) throw new IllegalArgumentException("distancia_km debe ser mayor a 0");
        if (s.getCarga() <= 0)       throw new IllegalArgumentException("carga debe ser mayor a 0");
    }
}
