package com.mudanzas.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Modelo de servicio de mudanza.
 */
public class Servicio {

    private int    id;
    private int    clienteId;
    private String direccionOrigen;
    private String direccionDestino;
    private String fechaServicio;
    private double distanciaKm;
    private double carga;
    private double costo;
    private String estado;   // PENDIENTE | EN_PROCESO | FINALIZADO
    private String createdAt;
    private String updatedAt;

    public Servicio() {}

    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }

    @JsonProperty("cliente_id")
    public int getClienteId()                   { return clienteId; }
    public void setClienteId(int clienteId)     { this.clienteId = clienteId; }

    @JsonProperty("direccion_origen")
    public String getDireccionOrigen()          { return direccionOrigen; }
    public void setDireccionOrigen(String d)    { this.direccionOrigen = d; }

    @JsonProperty("direccion_destino")
    public String getDireccionDestino()         { return direccionDestino; }
    public void setDireccionDestino(String d)   { this.direccionDestino = d; }

    @JsonProperty("fecha_servicio")
    public String getFechaServicio()            { return fechaServicio; }
    public void setFechaServicio(String f)      { this.fechaServicio = f; }

    @JsonProperty("distancia_km")
    public double getDistanciaKm()              { return distanciaKm; }
    public void setDistanciaKm(double d)        { this.distanciaKm = d; }

    public double getCarga()                    { return carga; }
    public void setCarga(double carga)          { this.carga = carga; }

    public double getCosto()                    { return costo; }
    public void setCosto(double costo)          { this.costo = costo; }

    public String getEstado()                   { return estado; }
    public void setEstado(String estado)        { this.estado = estado; }

    @JsonProperty("created_at")
    public String getCreatedAt()                { return createdAt; }
    public void setCreatedAt(String c)          { this.createdAt = c; }

    @JsonProperty("updated_at")
    public String getUpdatedAt()                { return updatedAt; }
    public void setUpdatedAt(String u)          { this.updatedAt = u; }
}
