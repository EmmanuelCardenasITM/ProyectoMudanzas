package com.mudanzas.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Modelo de cliente del sistema de mudanzas.
 * El cliente tiene un usuario asociado (usuario_id).
 */
public class Cliente {

    private int    id;
    private int    usuarioId;
    // Campos del usuario asociado (JOIN)
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private boolean activo;
    // Campos propios del cliente
    private String direccion;
    private String ciudad;
    private String documento;
    private String createdAt;
    private String updatedAt;

    public Cliente() {}

    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }

    @JsonProperty("usuario_id")
    public int getUsuarioId()                   { return usuarioId; }
    public void setUsuarioId(int u)             { this.usuarioId = u; }

    public String getNombre()                   { return nombre; }
    public void setNombre(String n)             { this.nombre = n; }

    public String getApellido()                 { return apellido; }
    public void setApellido(String a)           { this.apellido = a; }

    public String getEmail()                    { return email; }
    public void setEmail(String e)              { this.email = e; }

    public String getTelefono()                 { return telefono; }
    public void setTelefono(String t)           { this.telefono = t; }

    public boolean isActivo()                   { return activo; }
    public void setActivo(boolean a)            { this.activo = a; }

    public String getDireccion()                { return direccion; }
    public void setDireccion(String d)          { this.direccion = d; }

    public String getCiudad()                   { return ciudad; }
    public void setCiudad(String c)             { this.ciudad = c; }

    public String getDocumento()                { return documento; }
    public void setDocumento(String d)          { this.documento = d; }

    @JsonProperty("created_at")
    public String getCreatedAt()                { return createdAt; }
    public void setCreatedAt(String c)          { this.createdAt = c; }

    @JsonProperty("updated_at")
    public String getUpdatedAt()                { return updatedAt; }
    public void setUpdatedAt(String u)          { this.updatedAt = u; }
}
