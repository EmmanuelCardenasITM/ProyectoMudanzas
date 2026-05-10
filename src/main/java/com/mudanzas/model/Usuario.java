package com.mudanzas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Modelo de usuario del sistema.
 * Roles: administrador, empleado, cliente
 */
public class Usuario {

    private int    id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;

    @JsonIgnore
    private String password;

    private String  rol;
    private boolean activo;
    private String  createdAt;
    private String  updatedAt;

    public Usuario() {}

    public int getId()                      { return id; }
    public void setId(int id)               { this.id = id; }

    public String getNombre()               { return nombre; }
    public void setNombre(String n)         { this.nombre = n; }

    public String getApellido()             { return apellido; }
    public void setApellido(String a)       { this.apellido = a; }

    public String getEmail()                { return email; }
    public void setEmail(String e)          { this.email = e; }

    public String getTelefono()             { return telefono; }
    public void setTelefono(String t)       { this.telefono = t; }

    @JsonIgnore
    public String getPassword()             { return password; }

    /** Acepta tanto "password" como "password_hash" del JSON entrante */
    @JsonProperty("password")
    public void setPassword(String p)       { this.password = p; }

    @JsonProperty("password_hash")
    public void setPasswordHash(String p)   { this.password = p; }

    public String getRol()                  { return rol; }
    public void setRol(String rol)          { this.rol = rol; }

    public boolean isActivo()               { return activo; }
    public void setActivo(boolean activo)   { this.activo = activo; }

    @JsonProperty("created_at")
    public String getCreatedAt()            { return createdAt; }
    public void setCreatedAt(String c)      { this.createdAt = c; }

    @JsonProperty("updated_at")
    public String getUpdatedAt()            { return updatedAt; }
    public void setUpdatedAt(String u)      { this.updatedAt = u; }
}
