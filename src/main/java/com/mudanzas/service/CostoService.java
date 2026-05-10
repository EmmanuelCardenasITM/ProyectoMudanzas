package com.mudanzas.service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Servicio de cálculo automático de costos de mudanza.
 *
 * Fórmula:
 *   costo_base     = TARIFA_POR_KM * distancia_km
 *   costo_por_peso = TARIFA_POR_KG * peso_carga_kg
 *   costo_total    = max(costo_base + costo_por_peso, COSTO_MINIMO)
 *
 * Tarifas:
 *   - $1,500 COP por kilómetro
 *   - $50 COP por kilogramo
 *   - Costo mínimo: $80,000 COP
 */
public class CostoService {

    private static final double TARIFA_POR_KM = 1_500.0;  // COP por km
    private static final double TARIFA_POR_KG = 50.0;     // COP por kg
    private static final double COSTO_MINIMO  = 80_000.0; // COP mínimo

    /**
     * Calcula el costo base y total de un servicio.
     *
     * @param distanciaKm  Distancia en kilómetros (debe ser > 0)
     * @param pesoCargaKg  Peso de la carga en kilogramos (debe ser > 0)
     * @return Mapa con costo_base y costo_total
     * @throws IllegalArgumentException si los valores son negativos
     */
    public static Map<String, Double> calcularCosto(double distanciaKm, double pesoCargaKg) {
        if (distanciaKm < 0 || pesoCargaKg < 0) {
            throw new IllegalArgumentException("La distancia y el peso deben ser valores positivos.");
        }

        double costoPorDistancia = TARIFA_POR_KM * distanciaKm;
        double costoPorPeso      = TARIFA_POR_KG * pesoCargaKg;
        double costoCalculado    = costoPorDistancia + costoPorPeso;

        double costoBase  = Math.round(costoPorDistancia);
        double costoTotal = Math.max(Math.round(costoCalculado), COSTO_MINIMO);

        Map<String, Double> resultado = new LinkedHashMap<>();
        resultado.put("costo_base",  costoBase);
        resultado.put("costo_total", costoTotal);
        return resultado;
    }

    /**
     * Retorna el desglose detallado del cálculo de costos.
     */
    public static Map<String, Object> calcularCostoDetallado(double distanciaKm, double pesoCargaKg) {
        Map<String, Double> costos = calcularCosto(distanciaKm, pesoCargaKg);

        Map<String, Object> detalle = new LinkedHashMap<>();
        detalle.put("distancia_km",        distanciaKm);
        detalle.put("peso_carga_kg",        pesoCargaKg);
        detalle.put("tarifa_por_km",        TARIFA_POR_KM);
        detalle.put("tarifa_por_kg",        TARIFA_POR_KG);
        detalle.put("costo_por_distancia",  Math.round(TARIFA_POR_KM * distanciaKm));
        detalle.put("costo_por_peso",       Math.round(TARIFA_POR_KG * pesoCargaKg));
        detalle.put("costo_minimo",         COSTO_MINIMO);
        detalle.put("costo_base",           costos.get("costo_base"));
        detalle.put("costo_total",          costos.get("costo_total"));
        return detalle;
    }
}
