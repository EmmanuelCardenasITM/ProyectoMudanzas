package com.mudanzas.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la entidad Usuario.
 * Verifica la funcionalidad básica del modelo.
 */
@DisplayName("Usuario - Pruebas Unitarias")
class UsuarioTest {

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
    }

    @Test
    @DisplayName("Debe crear usuario con constructor vacío")
    void testConstructorVacio() {
        assertNotNull(usuario);
        assertEquals(0, usuario.getId());
        assertNull(usuario.getNombre());
        assertNull(usuario.getEmail());
        assertNull(usuario.getRol());
        assertFalse(usuario.isActivo());
    }

    @Test
    @DisplayName("Debe establecer y obtener propiedades correctamente")
    void testGettersYSetters() {
        usuario.setId(1);
        usuario.setNombre("María");
        usuario.setApellido("González");
        usuario.setEmail("maria@test.com");
        usuario.setPassword("secreto123");
        usuario.setTelefono("3001234567");
        usuario.setRol("administrador");
        usuario.setActivo(true);

        assertEquals(1, usuario.getId());
        assertEquals("María", usuario.getNombre());
        assertEquals("González", usuario.getApellido());
        assertEquals("maria@test.com", usuario.getEmail());
        assertEquals("secreto123", usuario.getPassword());
        assertEquals("3001234567", usuario.getTelefono());
        assertEquals("administrador", usuario.getRol());
        assertTrue(usuario.isActivo());
    }

    @Test
    @DisplayName("Debe manejar diferentes roles correctamente")
    void testRoles() {
        usuario.setRol("administrador");
        assertEquals("administrador", usuario.getRol());

        usuario.setRol("empleado");
        assertEquals("empleado", usuario.getRol());

        usuario.setRol("cliente");
        assertEquals("cliente", usuario.getRol());
    }

    @Test
    @DisplayName("Debe manejar password con diferentes propiedades JSON")
    void testPasswordProperties() {
        // Usando @JsonProperty("password")
        usuario.setPassword("password123");
        assertEquals("password123", usuario.getPassword());

        // Usando @JsonProperty("password_hash")
        usuario.setPasswordHash("hash456");
        assertEquals("hash456", usuario.getPassword());
    }

    @Test
    @DisplayName("Debe manejar fechas con propiedades JSON")
    void testFechasConPropiedadesJSON() {
        usuario.setCreatedAt("2024-01-01 10:00:00");
        usuario.setUpdatedAt("2024-01-02 11:00:00");

        assertEquals("2024-01-01 10:00:00", usuario.getCreatedAt());
        assertEquals("2024-01-02 11:00:00", usuario.getUpdatedAt());
    }

    @Test
    @DisplayName("Debe manejar valores por defecto correctamente")
    void testValoresPorDefecto() {
        Usuario nuevoUsuario = new Usuario();
        
        assertEquals(0, nuevoUsuario.getId());
        assertFalse(nuevoUsuario.isActivo());
        assertNull(nuevoUsuario.getRol());
        assertNull(nuevoUsuario.getEmail());
    }

    @Test
    @DisplayName("Debe validar datos de usuario completos")
    void testUsuarioCompleto() {
        usuario.setId(100);
        usuario.setNombre("Juan Carlos");
        usuario.setApellido("Pérez López");
        usuario.setEmail("juan.carlos@empresa.com");
        usuario.setTelefono("3001234567");
        usuario.setPassword("miPassword123");
        usuario.setRol("empleado");
        usuario.setActivo(true);
        usuario.setCreatedAt("2024-05-09 20:00:00");
        usuario.setUpdatedAt("2024-05-09 21:00:00");

        // Verificar todos los campos
        assertEquals(100, usuario.getId());
        assertEquals("Juan Carlos", usuario.getNombre());
        assertEquals("Pérez López", usuario.getApellido());
        assertEquals("juan.carlos@empresa.com", usuario.getEmail());
        assertEquals("3001234567", usuario.getTelefono());
        assertEquals("miPassword123", usuario.getPassword());
        assertEquals("empleado", usuario.getRol());
        assertTrue(usuario.isActivo());
        assertEquals("2024-05-09 20:00:00", usuario.getCreatedAt());
        assertEquals("2024-05-09 21:00:00", usuario.getUpdatedAt());
    }
}