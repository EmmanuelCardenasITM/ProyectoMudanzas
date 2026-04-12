package com.mudanzas.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Modelo de tarifa vigente del sistema.
 */
public class Tarifa {

    private int    id;
    private double tarifaPorKm;
    private double tarifaPorUnidadCarga;
    private String updatedAt;

    public Tarifa() {}

    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }

    @JsonProperty("tarifa_por_km")
    public double getTarifaPorKm()              { return tarifaPorKm; }
    public void setTarifaPorKm(double t)        { this.tarifaPorKm = t; }

    @JsonProperty("tarifa_por_unidad_carga")
    public double getTarifaPorUnidadCarga()     { return tarifaPorUnidadCarga; }
    public void setTarifaPorUnidadCarga(double t) { this.tarifaPorUnidadCarga = t; }

    @JsonProperty("updated_at")
    public String getUpdatedAt()                { return updatedAt; }
    public void setUpdatedAt(String u)          { this.updatedAt = u; }
}
