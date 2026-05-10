package com.mudanzas.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Modelo de vehículo de la flota de mudanzas.
 * Tipos: camioneta, camion_pequeno, camion_mediano, camion_grande
 */
public class Vehiculo {

    private int     id;
    private String  placa;
    private String  tipo;
    private double  capacidadKg;
    private boolean disponible;
    private String  createdAt;
    private String  updatedAt;

    public Vehiculo() {}

    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }

    public String getPlaca()                    { return placa; }
    public void setPlaca(String placa)          { this.placa = placa; }

    public String getTipo()                     { return tipo; }
    public void setTipo(String tipo)            { this.tipo = tipo; }

    @JsonProperty("capacidad_kg")
    public double getCapacidadKg()              { return capacidadKg; }
    public void setCapacidadKg(double c)        { this.capacidadKg = c; }

    public boolean isDisponible()               { return disponible; }
    public void setDisponible(boolean d)        { this.disponible = d; }

    @JsonProperty("created_at")
    public String getCreatedAt()                { return createdAt; }
    public void setCreatedAt(String c)          { this.createdAt = c; }

    @JsonProperty("updated_at")
    public String getUpdatedAt()                { return updatedAt; }
    public void setUpdatedAt(String u)          { this.updatedAt = u; }
}
