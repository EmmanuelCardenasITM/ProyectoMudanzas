package com.mudanzas.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la entidad Cliente.
 * Verifica getters, setters y propiedades JSON.
 */
@DisplayName("Cliente - Pruebas Unitarias")
class ClienteTest {

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
    }

    @Test
    @DisplayName("Debe crear cliente con constructor vacío")
    void testConstructorVacio() {
        assertNotNull(cliente);
        assertEquals(0, cliente.getId());
        assertEquals(0, cliente.getUsuarioId());
        assertNull(cliente.getNombre());
        assertNull(cliente.getEmail());
        assertFalse(cliente.isActivo());
    }

    @Test
    @DisplayName("Debe establecer y obtener propiedades básicas correctamente")
    void testPropiedadesBasicas() {
        cliente.setId(1);
        cliente.setUsuarioId(100);
        cliente.setNombre("Juan Carlos");
        cliente.setApellido("Pérez López");
        cliente.setEmail("juan.carlos@email.com");
        cliente.setTelefono("3001234567");
        cliente.setActivo(true);

        assertEquals(1, cliente.getId());
        assertEquals(100, cliente.getUsuarioId());
        assertEquals("Juan Carlos", cliente.getNombre());
        assertEquals("Pérez López", cliente.getApellido());
        assertEquals("juan.carlos@email.com", cliente.getEmail());
        assertEquals("3001234567", cliente.getTelefono());
        assertTrue(cliente.isActivo());
    }

    @Test
    @DisplayName("Debe manejar propiedades específicas de cliente")
    void testPropiedadesCliente() {
        cliente.setDireccion("Calle 123 #45-67");
        cliente.setCiudad("Bogotá");
        cliente.setDocumento("12345678");

        assertEquals("Calle 123 #45-67", cliente.getDireccion());
        assertEquals("Bogotá", cliente.getCiudad());
        assertEquals("12345678", cliente.getDocumento());
    }

    @Test
    @DisplayName("Debe manejar fechas con propiedades JSON")
    void testFechasJSON() {
        cliente.setCreatedAt("2024-05-09 10:00:00");
        cliente.setUpdatedAt("2024-05-09 11:30:00");

        assertEquals("2024-05-09 10:00:00", cliente.getCreatedAt());
        assertEquals("2024-05-09 11:30:00", cliente.getUpdatedAt());
    }

    @Test
    @DisplayName("Debe validar cliente completo")
    void testClienteCompleto() {
        cliente.setId(50);
        cliente.setUsuarioId(200);
        cliente.setNombre("María Elena");
        cliente.setApellido("González Rodríguez");
        cliente.setEmail("maria.elena@empresa.com");
        cliente.setTelefono("3009876543");
        cliente.setActivo(true);
        cliente.setDireccion("Carrera 15 #20-30 Apto 501");
        cliente.setCiudad("Medellín");
        cliente.setDocumento("87654321");
        cliente.setCreatedAt("2024-01-15 08:30:00");
        cliente.setUpdatedAt("2024-05-09 14:45:00");

        // Verificar todos los campos
        assertEquals(50, cliente.getId());
        assertEquals(200, cliente.getUsuarioId());
        assertEquals("María Elena", cliente.getNombre());
        assertEquals("González Rodríguez", cliente.getApellido());
        assertEquals("maria.elena@empresa.com", cliente.getEmail());
        assertEquals("3009876543", cliente.getTelefono());
        assertTrue(cliente.isActivo());
        assertEquals("Carrera 15 #20-30 Apto 501", cliente.getDireccion());
        assertEquals("Medellín", cliente.getCiudad());
        assertEquals("87654321", cliente.getDocumento());
        assertEquals("2024-01-15 08:30:00", cliente.getCreatedAt());
        assertEquals("2024-05-09 14:45:00", cliente.getUpdatedAt());
    }

    @Test
    @DisplayName("Debe manejar valores nulos correctamente")
    void testValoresNulos() {
        Cliente nuevoCliente = new Cliente();
        
        assertNull(nuevoCliente.getNombre());
        assertNull(nuevoCliente.getApellido());
        assertNull(nuevoCliente.getEmail());
        assertNull(nuevoCliente.getTelefono());
        assertNull(nuevoCliente.getDireccion());
        assertNull(nuevoCliente.getCiudad());
        assertNull(nuevoCliente.getDocumento());
        assertNull(nuevoCliente.getCreatedAt());
        assertNull(nuevoCliente.getUpdatedAt());
    }

    @Test
    @DisplayName("Debe permitir modificar estado activo")
    void testEstadoActivo() {
        // Por defecto inactivo
        assertFalse(cliente.isActivo());

        // Activar cliente
        cliente.setActivo(true);
        assertTrue(cliente.isActivo());

        // Desactivar cliente
        cliente.setActivo(false);
        assertFalse(cliente.isActivo());
    }

    @Test
    @DisplayName("Debe manejar diferentes tipos de documentos")
    void testTiposDocumento() {
        // Cédula
        cliente.setDocumento("1234567890");
        assertEquals("1234567890", cliente.getDocumento());

        // Pasaporte
        cliente.setDocumento("AB123456");
        assertEquals("AB123456", cliente.getDocumento());

        // Documento extranjero
        cliente.setDocumento("EXT-789456123");
        assertEquals("EXT-789456123", cliente.getDocumento());
    }

    @Test
    @DisplayName("Debe manejar diferentes ciudades")
    void testCiudades() {
        String[] ciudades = {"Bogotá", "Medellín", "Cali", "Barranquilla", "Cartagena"};
        
        for (String ciudad : ciudades) {
            cliente.setCiudad(ciudad);
            assertEquals(ciudad, cliente.getCiudad());
        }
    }
}