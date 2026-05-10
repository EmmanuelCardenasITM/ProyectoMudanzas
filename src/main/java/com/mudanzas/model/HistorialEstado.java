package com.mudanzas.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Modelo de historial de cambios de estado de un servicio.
 */
public class HistorialEstado {

    private int    id;
    private int    servicioId;
    private String estadoAnterior;
    private String estadoNuevo;
    private int    usuarioId;
    private String usuarioNombre;
    private String usuarioRol;
    private String observacion;
    private String createdAt;

    public HistorialEstado() {}

    public int getId()                              { return id; }
    public void setId(int id)                       { this.id = id; }

    @JsonProperty("servicio_id")
    public int getServicioId()                      { return servicioId; }
    public void setServicioId(int s)                { this.servicioId = s; }

    @JsonProperty("estado_anterior")
    public String getEstadoAnterior()               { return estadoAnterior; }
    public void setEstadoAnterior(String e)         { this.estadoAnterior = e; }

    @JsonProperty("estado_nuevo")
    public String getEstadoNuevo()                  { return estadoNuevo; }
    public void setEstadoNuevo(String e)            { this.estadoNuevo = e; }

    @JsonProperty("usuario_id")
    public int getUsuarioId()                       { return usuarioId; }
    public void setUsuarioId(int u)                 { this.usuarioId = u; }

    @JsonProperty("usuario_nombre")
    public String getUsuarioNombre()                { return usuarioNombre; }
    public void setUsuarioNombre(String u)          { this.usuarioNombre = u; }

    @JsonProperty("usuario_rol")
    public String getUsuarioRol()                   { return usuarioRol; }
    public void setUsuarioRol(String u)             { this.usuarioRol = u; }

    public String getObservacion()                  { return observacion; }
    public void setObservacion(String o)            { this.observacion = o; }

    @JsonProperty("created_at")
    public String getCreatedAt()                    { return createdAt; }
    public void setCreatedAt(String c)              { this.createdAt = c; }
}
