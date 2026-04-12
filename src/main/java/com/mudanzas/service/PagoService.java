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
 */
public class PagoService {

    private final PagoDAO     pagoDAO     = new PagoDAO();
    private final ServicioDAO servicioDAO = new ServicioDAO();

    /**
     * Retorna los pagos de un servicio junto con el total acumulado.
     */
    public Map<String, Object> listarPagos(int servicioId) throws SQLException {
        Servicio s = servicioDAO.findById(servicioId);
        if (s == null) throw new IllegalArgumentException("Servicio con id " + servicioId + " no encontrado");

        List<Pago> pagos = pagoDAO.findByServicioId(servicioId);
        double total = pagoDAO.sumByServicioId(servicioId);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("pagos", pagos);
        resultado.put("total_acumulado", total);
        return resultado;
    }

    /**
     * Registra un nuevo pago para un servicio.
     *
     * @throws IllegalArgumentException si el servicio no existe, monto <= 0 o excede el costo
     */
    public Pago registrarPago(int servicioId, double monto) throws SQLException {
        Servicio s = servicioDAO.findById(servicioId);
        if (s == null) throw new IllegalArgumentException("Servicio con id " + servicioId + " no encontrado");
        if (monto <= 0) throw new IllegalArgumentException("El monto debe ser mayor a 0");

        double totalActual = pagoDAO.sumByServicioId(servicioId);
        if (totalActual + monto > s.getCosto()) {
            throw new IllegalArgumentException("El monto excede el costo del servicio");
        }
        return pagoDAO.create(servicioId, monto);
    }
}
