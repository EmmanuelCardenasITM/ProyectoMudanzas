package com.mudanzas.model;

/**
 * Modelo de cliente del sistema de mudanzas.
 */
public class Cliente {

    private int    id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String direccion;
    private String createdAt;
    private String updatedAt;

    public Cliente() {}

    public int getId()                      { return id; }
    public void setId(int id)               { this.id = id; }

    public String getNombre()               { return nombre; }
    public void setNombre(String nombre)    { this.nombre = nombre; }

    public String getApellido()             { return apellido; }
    public void setApellido(String a)       { this.apellido = a; }

    public String getEmail()                { return email; }
    public void setEmail(String email)      { this.email = email; }

    public String getTelefono()             { return telefono; }
    public void setTelefono(String t)       { this.telefono = t; }

    public String getDireccion()            { return direccion; }
    public void setDireccion(String d)      { this.direccion = d; }

    public String getCreatedAt()            { return createdAt; }
    public void setCreatedAt(String c)      { this.createdAt = c; }

    public String getUpdatedAt()            { return updatedAt; }
    public void setUpdatedAt(String u)      { this.updatedAt = u; }
}
