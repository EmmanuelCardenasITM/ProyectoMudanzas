package com.mudanzas.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Modelo de pago asociado a un servicio de mudanza.
 */
public class Pago {

    private int    id;
    private int    servicioId;
    private double monto;
    private String createdAt;

    public Pago() {}

    public int getId()                      { return id; }
    public void setId(int id)               { this.id = id; }

    @JsonProperty("servicio_id")
    public int getServicioId()              { return servicioId; }
    public void setServicioId(int s)        { this.servicioId = s; }

    public double getMonto()                { return monto; }
    public void setMonto(double monto)      { this.monto = monto; }

    @JsonProperty("created_at")
    public String getCreatedAt()            { return createdAt; }
    public void setCreatedAt(String c)      { this.createdAt = c; }
}
