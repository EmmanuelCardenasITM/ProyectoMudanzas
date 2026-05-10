package com.mudanzas.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Modelo de pago asociado a un servicio de mudanza.
 * Métodos de pago: efectivo, transferencia, tarjeta
 * Estados de pago: pendiente, pagado, reembolsado
 */
public class Pago {

    private int    id;
    private int    servicioId;
    private double monto;
    private String metodoPago;
    private String estadoPago;
    private String fechaPago;
    private String referencia;
    private String notas;
    private String createdAt;
    private String updatedAt;

    // Campos de JOIN (solo lectura)
    private String fechaServicio;
    private String ciudadOrigen;
    private String ciudadDestino;
    private double costoTotal;
    private String estadoServicio;
    private String clienteNombre;
    private String clienteEmail;

    public Pago() {}

    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }

    @JsonProperty("servicio_id")
    public int getServicioId()                  { return servicioId; }
    public void setServicioId(int s)            { this.servicioId = s; }

    public double getMonto()                    { return monto; }
    public void setMonto(double monto)          { this.monto = monto; }

    @JsonProperty("metodo_pago")
    public String getMetodoPago()               { return metodoPago; }
    public void setMetodoPago(String m)         { this.metodoPago = m; }

    @JsonProperty("estado_pago")
    public String getEstadoPago()               { return estadoPago; }
    public void setEstadoPago(String e)         { this.estadoPago = e; }

    @JsonProperty("fecha_pago")
    public String getFechaPago()                { return fechaPago; }
    public void setFechaPago(String f)          { this.fechaPago = f; }

    public String getReferencia()               { return referencia; }
    public void setReferencia(String r)         { this.referencia = r; }

    public String getNotas()                    { return notas; }
    public void setNotas(String n)              { this.notas = n; }

    @JsonProperty("created_at")
    public String getCreatedAt()                { return createdAt; }
    public void setCreatedAt(String c)          { this.createdAt = c; }

    @JsonProperty("updated_at")
    public String getUpdatedAt()                { return updatedAt; }
    public void setUpdatedAt(String u)          { this.updatedAt = u; }

    @JsonProperty("fecha_servicio")
    public String getFechaServicio()            { return fechaServicio; }
    public void setFechaServicio(String f)      { this.fechaServicio = f; }

    @JsonProperty("ciudad_origen")
    public String getCiudadOrigen()             { return ciudadOrigen; }
    public void setCiudadOrigen(String c)       { this.ciudadOrigen = c; }

    @JsonProperty("ciudad_destino")
    public String getCiudadDestino()            { return ciudadDestino; }
    public void setCiudadDestino(String c)      { this.ciudadDestino = c; }

    @JsonProperty("costo_total")
    public double getCostoTotal()               { return costoTotal; }
    public void setCostoTotal(double c)         { this.costoTotal = c; }

    @JsonProperty("estado_servicio")
    public String getEstadoServicio()           { return estadoServicio; }
    public void setEstadoServicio(String e)     { this.estadoServicio = e; }

    @JsonProperty("cliente_nombre")
    public String getClienteNombre()            { return clienteNombre; }
    public void setClienteNombre(String c)      { this.clienteNombre = c; }

    @JsonProperty("cliente_email")
    public String getClienteEmail()             { return clienteEmail; }
    public void setClienteEmail(String c)       { this.clienteEmail = c; }
}
