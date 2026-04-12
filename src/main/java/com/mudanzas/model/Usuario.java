package com.mudanzas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Modelo de usuario del sistema (Administrador / Empleado).
 */
public class Usuario {

    private int    id;
    private String nombre;
    private String email;

    @JsonIgnore
    private String passwordHash;

    private String  rol;
    private boolean activo;
    private String  createdAt;

    public Usuario() {}

    public int getId()                  { return id; }
    public void setId(int id)           { this.id = id; }

    public String getNombre()                   { return nombre; }
    public void setNombre(String nombre)        { this.nombre = nombre; }

    public String getEmail()                    { return email; }
    public void setEmail(String email)          { this.email = email; }

    @JsonIgnore
    public String getPasswordHash()             { return passwordHash; }

    @JsonProperty("password_hash")
    public void setPasswordHash(String h)       { this.passwordHash = h; }

    public String getRol()                      { return rol; }
    public void setRol(String rol)              { this.rol = rol; }

    public boolean isActivo()                   { return activo; }
    public void setActivo(boolean activo)       { this.activo = activo; }

    public String getCreatedAt()                { return createdAt; }
    public void setCreatedAt(String createdAt)  { this.createdAt = createdAt; }
}
