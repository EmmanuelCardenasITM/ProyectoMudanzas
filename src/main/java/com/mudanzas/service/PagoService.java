package com.mudanzas.service;

import com.mudanzas.dao.PagoDAO;
import com.mudanzas.dao.ServicioDAO;
import com.mudanzas.model.Pago;
import com.mudanzas.model.Servicio;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio de lógica de negocio para pagos.
 * Métodos de pago: efectivo, transferencia, tarjeta
 * Estados de pago: pendiente, pagado, reembolsado
 */
public class PagoService {

    private final PagoDAO     pagoDAO     = new PagoDAO();
    private final ServicioDAO servicioDAO = new ServicioDAO();

    public List<Pago> listarPagos(String estadoPago) throws SQLException {
        if (estadoPago != null && !estadoPago.isBlank()) {
            return pagoDAO.findByEstadoPago(estadoPago);
        }
        return pagoDAO.findAll();
    }

    public Pago obtenerPago(int id) throws SQLException {
        Pago p = pagoDAO.findById(id);
        if (p == null) throw new IllegalArgumentException("Pago con id " + id + " no encontrado.");
        return p;
    }

    /**
     * Retorna los pagos de un servicio junto con el resumen financiero.
     */
    public Map<String, Object> obtenerPagosPorServicio(int servicioId) throws SQLException {
        Servicio s = servicioDAO.findById(servicioId);
        if (s == null) throw new IllegalArgumentException("Servicio con id " + servicioId + " no encontrado.");

        List<Pago> pagos      = pagoDAO.findByServicioId(servicioId);
        double totalPagado    = pagoDAO.sumByServicioId(servicioId);
        double saldoPendiente = Math.max(0, s.getCostoTotal() - totalPagado);

        Map<String, Object> resumen = new HashMap<>();
        resumen.put("costo_total",      s.getCostoTotal());
        resumen.put("total_pagado",     totalPagado);
        resumen.put("saldo_pendiente",  saldoPendiente);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("pagos",   pagos);
        resultado.put("resumen", resumen);
        return resultado;
    }

    /**
     * Registra un nuevo pago.
     *
     * @throws IllegalArgumentException si el servicio no existe o el pago es inválido
     * @throws IllegalStateException    si el servicio está cancelado
     */
    public Pago registrarPago(Pago pago) throws SQLException {
        Servicio s = servicioDAO.findById(pago.getServicioId());
        if (s == null) {
            throw new IllegalArgumentException("Servicio con id " + pago.getServicioId() + " no encontrado.");
        }
        if ("cancelado".equals(s.getEstado())) {
            throw new IllegalStateException("No se puede registrar un pago para un servicio cancelado.");
        }
        if (pago.getMonto() <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a 0.");
        }
        if (pago.getMetodoPago() == null || pago.getMetodoPago().isBlank()) {
            throw new IllegalArgumentException("metodo_pago es obligatorio (efectivo, transferencia, tarjeta).");
        }
        return pagoDAO.create(pago);
    }

    /**
     * Actualiza un pago existente.
     *
     * @throws IllegalStateException si el pago ya está confirmado (pagado)
     */
    public Pago actualizarPago(int id, Pago datos) throws SQLException {
        Pago pago = obtenerPago(id);
        // Merge con valores actuales
        datos.setMonto(datos.getMonto() > 0 ? datos.getMonto() : pago.getMonto());
        if (datos.getMetodoPago() == null) datos.setMetodoPago(pago.getMetodoPago());
        if (datos.getEstadoPago() == null) datos.setEstadoPago(pago.getEstadoPago());
        if (datos.getFechaPago() == null)  datos.setFechaPago(pago.getFechaPago());
        if (datos.getReferencia() == null) datos.setReferencia(pago.getReferencia());
        if (datos.getNotas() == null)      datos.setNotas(pago.getNotas());
        return pagoDAO.update(id, datos);
    }

    /**
     * Elimina un pago. No se puede eliminar si ya está confirmado (pagado).
     */
    public void eliminarPago(int id) throws SQLException {
        Pago pago = obtenerPago(id);
        if ("pagado".equals(pago.getEstadoPago())) {
            throw new IllegalStateException("No se puede eliminar un pago ya confirmado.");
        }
        pagoDAO.delete(id);
    }
}
