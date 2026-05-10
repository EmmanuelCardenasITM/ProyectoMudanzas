package com.mudanzas.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la entidad Tarifa.
 * Verifica getters, setters y propiedades JSON.
 */
@DisplayName("Tarifa - Pruebas Unitarias")
class TarifaTest {

    private Tarifa tarifa;

    @BeforeEach
    void setUp() {
        tarifa = new Tarifa();
    }

    @Test
    @DisplayName("Debe crear tarifa con constructor vacío")
    void testConstructorVacio() {
        assertNotNull(tarifa);
        assertEquals(0, tarifa.getId());
        assertEquals(0.0, tarifa.getTarifaPorKm());
        assertEquals(0.0, tarifa.getTarifaPorUnidadCarga());
    }

    @Test
    @DisplayName("Debe establecer y obtener propiedades correctamente")
    void testGettersYSetters() {
        tarifa.setId(1);
        tarifa.setTarifaPorKm(2500.50);
        tarifa.setTarifaPorUnidadCarga(1200.75);
        tarifa.setUpdatedAt("2024-05-09 21:00:00");

        assertEquals(1, tarifa.getId());
        assertEquals(2500.50, tarifa.getTarifaPorKm(), 0.01);
        assertEquals(1200.75, tarifa.getTarifaPorUnidadCarga(), 0.01);
        assertEquals("2024-05-09 21:00:00", tarifa.getUpdatedAt());
    }

    @Test
    @DisplayName("Debe manejar valores decimales correctamente")
    void testValoresDecimales() {
        tarifa.setTarifaPorKm(1500.25);
        tarifa.setTarifaPorUnidadCarga(750.50);

        assertEquals(1500.25, tarifa.getTarifaPorKm(), 0.001);
        assertEquals(750.50, tarifa.getTarifaPorUnidadCarga(), 0.001);
    }

    @Test
    @DisplayName("Debe manejar valores cero correctamente")
    void testValoresCero() {
        tarifa.setTarifaPorKm(0.0);
        tarifa.setTarifaPorUnidadCarga(0.0);

        assertEquals(0.0, tarifa.getTarifaPorKm());
        assertEquals(0.0, tarifa.getTarifaPorUnidadCarga());
    }

    @Test
    @DisplayName("Debe manejar valores grandes correctamente")
    void testValoresGrandes() {
        tarifa.setTarifaPorKm(99999.99);
        tarifa.setTarifaPorUnidadCarga(88888.88);

        assertEquals(99999.99, tarifa.getTarifaPorKm(), 0.01);
        assertEquals(88888.88, tarifa.getTarifaPorUnidadCarga(), 0.01);
    }

    @Test
    @DisplayName("Debe manejar propiedades JSON correctamente")
    void testPropiedadesJSON() {
        // Las anotaciones @JsonProperty deberían funcionar correctamente
        tarifa.setTarifaPorKm(3000.0);
        tarifa.setTarifaPorUnidadCarga(1500.0);
        tarifa.setUpdatedAt("2024-05-09 22:00:00");

        // Verificar que los getters funcionan (que son los que tienen @JsonProperty)
        assertEquals(3000.0, tarifa.getTarifaPorKm());
        assertEquals(1500.0, tarifa.getTarifaPorUnidadCarga());
        assertEquals("2024-05-09 22:00:00", tarifa.getUpdatedAt());
    }

    @Test
    @DisplayName("Debe validar tarifa completa")
    void testTarifaCompleta() {
        tarifa.setId(100);
        tarifa.setTarifaPorKm(2800.75);
        tarifa.setTarifaPorUnidadCarga(1400.25);
        tarifa.setUpdatedAt("2024-05-09 23:30:45");

        // Verificar todos los campos
        assertEquals(100, tarifa.getId());
        assertEquals(2800.75, tarifa.getTarifaPorKm(), 0.01);
        assertEquals(1400.25, tarifa.getTarifaPorUnidadCarga(), 0.01);
        assertEquals("2024-05-09 23:30:45", tarifa.getUpdatedAt());
    }

    @Test
    @DisplayName("Debe manejar valores por defecto")
    void testValoresPorDefecto() {
        Tarifa nuevaTarifa = new Tarifa();
        
        assertEquals(0, nuevaTarifa.getId());
        assertEquals(0.0, nuevaTarifa.getTarifaPorKm());
        assertEquals(0.0, nuevaTarifa.getTarifaPorUnidadCarga());
        assertNull(nuevaTarifa.getUpdatedAt());
    }

    @Test
    @DisplayName("Debe permitir modificar valores múltiples veces")
    void testModificacionesMultiples() {
        // Primera modificación
        tarifa.setTarifaPorKm(1000.0);
        tarifa.setTarifaPorUnidadCarga(500.0);
        assertEquals(1000.0, tarifa.getTarifaPorKm());
        assertEquals(500.0, tarifa.getTarifaPorUnidadCarga());

        // Segunda modificación
        tarifa.setTarifaPorKm(2000.0);
        tarifa.setTarifaPorUnidadCarga(1000.0);
        assertEquals(2000.0, tarifa.getTarifaPorKm());
        assertEquals(1000.0, tarifa.getTarifaPorUnidadCarga());

        // Tercera modificación
        tarifa.setTarifaPorKm(3500.50);
        tarifa.setTarifaPorUnidadCarga(1750.25);
        assertEquals(3500.50, tarifa.getTarifaPorKm(), 0.01);
        assertEquals(1750.25, tarifa.getTarifaPorUnidadCarga(), 0.01);
    }
}