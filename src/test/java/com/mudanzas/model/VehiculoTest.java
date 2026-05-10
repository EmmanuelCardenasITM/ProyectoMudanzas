package com.mudanzas.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la entidad Vehiculo.
 * Verifica tipos de vehículos, capacidades y disponibilidad.
 */
@DisplayName("Vehiculo - Pruebas Unitarias")
class VehiculoTest {

    private Vehiculo vehiculo;

    @BeforeEach
    void setUp() {
        vehiculo = new Vehiculo();
    }

    @Test
    @DisplayName("Debe crear vehículo con constructor vacío")
    void testConstructorVacio() {
        assertNotNull(vehiculo);
        assertEquals(0, vehiculo.getId());
        assertNull(vehiculo.getPlaca());
        assertNull(vehiculo.getTipo());
        assertEquals(0.0, vehiculo.getCapacidadKg());
        assertFalse(vehiculo.isDisponible());
    }

    @Test
    @DisplayName("Debe establecer y obtener propiedades básicas")
    void testPropiedadesBasicas() {
        vehiculo.setId(1);
        vehiculo.setPlaca("ABC123");
        vehiculo.setTipo("camioneta");
        vehiculo.setCapacidadKg(1500.0);
        vehiculo.setDisponible(true);

        assertEquals(1, vehiculo.getId());
        assertEquals("ABC123", vehiculo.getPlaca());
        assertEquals("camioneta", vehiculo.getTipo());
        assertEquals(1500.0, vehiculo.getCapacidadKg(), 0.01);
        assertTrue(vehiculo.isDisponible());
    }

    @Test
    @DisplayName("Debe validar tipos de vehículos permitidos")
    void testTiposVehiculos() {
        String[] tiposValidos = {
            "camioneta", 
            "camion_pequeno", 
            "camion_mediano", 
            "camion_grande"
        };

        for (String tipo : tiposValidos) {
            vehiculo.setTipo(tipo);
            assertEquals(tipo, vehiculo.getTipo());
        }
    }

    @Test
    @DisplayName("Debe manejar diferentes capacidades de carga")
    void testCapacidadesCarga() {
        // Camioneta pequeña
        vehiculo.setTipo("camioneta");
        vehiculo.setCapacidadKg(1000.0);
        assertEquals(1000.0, vehiculo.getCapacidadKg(), 0.01);

        // Camión pequeño
        vehiculo.setTipo("camion_pequeno");
        vehiculo.setCapacidadKg(3000.0);
        assertEquals(3000.0, vehiculo.getCapacidadKg(), 0.01);

        // Camión mediano
        vehiculo.setTipo("camion_mediano");
        vehiculo.setCapacidadKg(5000.0);
        assertEquals(5000.0, vehiculo.getCapacidadKg(), 0.01);

        // Camión grande
        vehiculo.setTipo("camion_grande");
        vehiculo.setCapacidadKg(8000.0);
        assertEquals(8000.0, vehiculo.getCapacidadKg(), 0.01);
    }

    @Test
    @DisplayName("Debe manejar placas con diferentes formatos")
    void testFormatosPlaca() {
        String[] placas = {
            "ABC123",
            "DEF456", 
            "GHI789",
            "JKL012",
            "MNO345"
        };

        for (String placa : placas) {
            vehiculo.setPlaca(placa);
            assertEquals(placa, vehiculo.getPlaca());
        }
    }

    @Test
    @DisplayName("Debe manejar disponibilidad del vehículo")
    void testDisponibilidad() {
        // Por defecto no disponible
        assertFalse(vehiculo.isDisponible());

        // Marcar como disponible
        vehiculo.setDisponible(true);
        assertTrue(vehiculo.isDisponible());

        // Marcar como no disponible (en servicio)
        vehiculo.setDisponible(false);
        assertFalse(vehiculo.isDisponible());
    }

    @Test
    @DisplayName("Debe manejar fechas con propiedades JSON")
    void testFechasJSON() {
        vehiculo.setCreatedAt("2024-01-01 10:00:00");
        vehiculo.setUpdatedAt("2024-05-09 15:30:00");

        assertEquals("2024-01-01 10:00:00", vehiculo.getCreatedAt());
        assertEquals("2024-05-09 15:30:00", vehiculo.getUpdatedAt());
    }

    @Test
    @DisplayName("Debe validar vehículo completo")
    void testVehiculoCompleto() {
        vehiculo.setId(25);
        vehiculo.setPlaca("XYZ789");
        vehiculo.setTipo("camion_mediano");
        vehiculo.setCapacidadKg(4500.75);
        vehiculo.setDisponible(true);
        vehiculo.setCreatedAt("2024-03-15 09:00:00");
        vehiculo.setUpdatedAt("2024-05-09 16:45:00");

        // Verificar todos los campos
        assertEquals(25, vehiculo.getId());
        assertEquals("XYZ789", vehiculo.getPlaca());
        assertEquals("camion_mediano", vehiculo.getTipo());
        assertEquals(4500.75, vehiculo.getCapacidadKg(), 0.01);
        assertTrue(vehiculo.isDisponible());
        assertEquals("2024-03-15 09:00:00", vehiculo.getCreatedAt());
        assertEquals("2024-05-09 16:45:00", vehiculo.getUpdatedAt());
    }

    @Test
    @DisplayName("Debe manejar capacidades decimales")
    void testCapacidadesDecimales() {
        vehiculo.setCapacidadKg(1500.50);
        assertEquals(1500.50, vehiculo.getCapacidadKg(), 0.001);

        vehiculo.setCapacidadKg(2750.25);
        assertEquals(2750.25, vehiculo.getCapacidadKg(), 0.001);

        vehiculo.setCapacidadKg(5000.99);
        assertEquals(5000.99, vehiculo.getCapacidadKg(), 0.001);
    }

    @Test
    @DisplayName("Debe manejar valores por defecto")
    void testValoresPorDefecto() {
        Vehiculo nuevoVehiculo = new Vehiculo();
        
        assertEquals(0, nuevoVehiculo.getId());
        assertNull(nuevoVehiculo.getPlaca());
        assertNull(nuevoVehiculo.getTipo());
        assertEquals(0.0, nuevoVehiculo.getCapacidadKg());
        assertFalse(nuevoVehiculo.isDisponible());
        assertNull(nuevoVehiculo.getCreatedAt());
        assertNull(nuevoVehiculo.getUpdatedAt());
    }

    @Test
    @DisplayName("Debe permitir modificaciones múltiples")
    void testModificacionesMultiples() {
        // Primera configuración
        vehiculo.setPlaca("AAA111");
        vehiculo.setTipo("camioneta");
        vehiculo.setCapacidadKg(1000.0);
        vehiculo.setDisponible(true);

        assertEquals("AAA111", vehiculo.getPlaca());
        assertEquals("camioneta", vehiculo.getTipo());
        assertEquals(1000.0, vehiculo.getCapacidadKg());
        assertTrue(vehiculo.isDisponible());

        // Segunda configuración
        vehiculo.setPlaca("BBB222");
        vehiculo.setTipo("camion_grande");
        vehiculo.setCapacidadKg(8000.0);
        vehiculo.setDisponible(false);

        assertEquals("BBB222", vehiculo.getPlaca());
        assertEquals("camion_grande", vehiculo.getTipo());
        assertEquals(8000.0, vehiculo.getCapacidadKg());
        assertFalse(vehiculo.isDisponible());
    }
}