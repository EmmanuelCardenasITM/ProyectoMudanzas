package com.mudanzas.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Modelo de servicio de mudanza.
 * Estados: pendiente, confirmado, en_proceso, finalizado, cancelado
 */
public class Servicio {

    private int    id;
    private int    clienteId;
    private Integer vehiculoId;
    private Integer empleadoId;
    private String fechaServicio;
    private String horaServicio;
    private String direccionOrigen;
    private String ciudadOrigen;
    private String direccionDestino;
    private String ciudadDestino;
    private double distanciaKm;
    private double pesoCargaKg;
    private String descripcionCarga;
    private double costoBase;
    private double costoTotal;
    private String estado;
    private String notas;
    private String createdAt;
    private String updatedAt;

    // Campos de JOIN (solo lectura)
    private String clienteNombre;
    private String clienteEmail;
    private String clienteTelefono;
    private String empleadoNombre;
    private String vehiculoPlaca;
    private String vehiculoTipo;

    public Servicio() {}

    public int getId()                              { return id; }
    public void setId(int id)                       { this.id = id; }

    @JsonProperty("cliente_id")
    public int getClienteId()                       { return clienteId; }
    public void setClienteId(int c)                 { this.clienteId = c; }

    @JsonProperty("vehiculo_id")
    public Integer getVehiculoId()                  { return vehiculoId; }
    public void setVehiculoId(Integer v)            { this.vehiculoId = v; }

    @JsonProperty("empleado_id")
    public Integer getEmpleadoId()                  { return empleadoId; }
    public void setEmpleadoId(Integer e)            { this.empleadoId = e; }

    @JsonProperty("fecha_servicio")
    public String getFechaServicio()                { return fechaServicio; }
    public void setFechaServicio(String f)          { this.fechaServicio = f; }

    @JsonProperty("hora_servicio")
    public String getHoraServicio()                 { return horaServicio; }
    public void setHoraServicio(String h)           { this.horaServicio = h; }

    @JsonProperty("direccion_origen")
    public String getDireccionOrigen()              { return direccionOrigen; }
    public void setDireccionOrigen(String d)        { this.direccionOrigen = d; }

    @JsonProperty("ciudad_origen")
    public String getCiudadOrigen()                 { return ciudadOrigen; }
    public void setCiudadOrigen(String c)           { this.ciudadOrigen = c; }

    @JsonProperty("direccion_destino")
    public String getDireccionDestino()             { return direccionDestino; }
    public void setDireccionDestino(String d)       { this.direccionDestino = d; }

    @JsonProperty("ciudad_destino")
    public String getCiudadDestino()                { return ciudadDestino; }
    public void setCiudadDestino(String c)          { this.ciudadDestino = c; }

    @JsonProperty("distancia_km")
    public double getDistanciaKm()                  { return distanciaKm; }
    public void setDistanciaKm(double d)            { this.distanciaKm = d; }

    @JsonProperty("peso_carga_kg")
    public double getPesoCargaKg()                  { return pesoCargaKg; }
    public void setPesoCargaKg(double p)            { this.pesoCargaKg = p; }

    @JsonProperty("descripcion_carga")
    public String getDescripcionCarga()             { return descripcionCarga; }
    public void setDescripcionCarga(String d)       { this.descripcionCarga = d; }

    @JsonProperty("costo_base")
    public double getCostoBase()                    { return costoBase; }
    public void setCostoBase(double c)              { this.costoBase = c; }

    @JsonProperty("costo_total")
    public double getCostoTotal()                   { return costoTotal; }
    public void setCostoTotal(double c)             { this.costoTotal = c; }

    public String getEstado()                       { return estado; }
    public void setEstado(String estado)            { this.estado = estado; }

    public String getNotas()                        { return notas; }
    public void setNotas(String n)                  { this.notas = n; }

    @JsonProperty("created_at")
    public String getCreatedAt()                    { return createdAt; }
    public void setCreatedAt(String c)              { this.createdAt = c; }

    @JsonProperty("updated_at")
    public String getUpdatedAt()                    { return updatedAt; }
    public void setUpdatedAt(String u)              { this.updatedAt = u; }

    @JsonProperty("cliente_nombre")
    public String getClienteNombre()                { return clienteNombre; }
    public void setClienteNombre(String c)          { this.clienteNombre = c; }

    @JsonProperty("cliente_email")
    public String getClienteEmail()                 { return clienteEmail; }
    public void setClienteEmail(String c)           { this.clienteEmail = c; }

    @JsonProperty("cliente_telefono")
    public String getClienteTelefono()              { return clienteTelefono; }
    public void setClienteTelefono(String c)        { this.clienteTelefono = c; }

    @JsonProperty("empleado_nombre")
    public String getEmpleadoNombre()               { return empleadoNombre; }
    public void setEmpleadoNombre(String e)         { this.empleadoNombre = e; }

    @JsonProperty("vehiculo_placa")
    public String getVehiculoPlaca()                { return vehiculoPlaca; }
    public void setVehiculoPlaca(String v)          { this.vehiculoPlaca = v; }

    @JsonProperty("vehiculo_tipo")
    public String getVehiculoTipo()                 { return vehiculoTipo; }
    public void setVehiculoTipo(String v)           { this.vehiculoTipo = v; }
}
