package com.mudanzas.service;

import com.mudanzas.dao.TarifaDAO;
import com.mudanzas.model.Tarifa;

import java.sql.SQLException;

/**
 * Servicio de lógica de negocio para tarifas.
 */
public class TarifaService {

    private final TarifaDAO tarifaDAO = new TarifaDAO();

    /**
     * @throws IllegalStateException si no hay tarifas configuradas (404)
     */
    public Tarifa obtenerTarifas() throws SQLException {
        Tarifa t = tarifaDAO.findVigente();
        if (t == null) throw new IllegalStateException("No hay tarifas configuradas");
        return t;
    }

    /**
     * @throws IllegalArgumentException si los valores no son positivos (400)
     */
    public Tarifa actualizarTarifas(double tarifaPorKm, double tarifaPorUnidadCarga) throws SQLException {
        if (tarifaPorKm <= 0)           throw new IllegalArgumentException("tarifa_por_km debe ser mayor a 0");
        if (tarifaPorUnidadCarga <= 0)  throw new IllegalArgumentException("tarifa_por_unidad_carga debe ser mayor a 0");
        return tarifaDAO.update(tarifaPorKm, tarifaPorUnidadCarga);
    }
}
