package com.mudanzas.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la entidad HistorialEstado.
 * Verifica el registro de cambios de estado de servicios.
 */
@DisplayName("HistorialEstado - Pruebas Unitarias")
class HistorialEstadoTest {

    private HistorialEstado historial;

    @BeforeEach
    void setUp() {
        historial = new HistorialEstado();
    }

    @Test
    @DisplayName("Debe crear historial con constructor vacío")
    void testConstructorVacio() {
        assertNotNull(historial);
        assertEquals(0, historial.getId());
        assertEquals(0, historial.getServicioId());
        assertEquals(0, historial.getUsuarioId());
        assertNull(historial.getEstadoAnterior());
        assertNull(historial.getEstadoNuevo());
    }

    @Test
    @DisplayName("Debe establecer y obtener propiedades básicas")
    void testPropiedadesBasicas() {
        historial.setId(1);
        historial.setServicioId(100);
        historial.setUsuarioId(5);
        historial.setEstadoAnterior("pendiente");
        historial.setEstadoNuevo("confirmado");

        assertEquals(1, historial.getId());
        assertEquals(100, historial.getServicioId());
        assertEquals(5, historial.getUsuarioId());
        assertEquals("pendiente", historial.getEstadoAnterior());
        assertEquals("confirmado", historial.getEstadoNuevo());
    }

    @Test
    @DisplayName("Debe manejar información del usuario")
    void testInformacionUsuario() {
        historial.setUsuarioNombre("María González");
        historial.setUsuarioRol("empleado");

        assertEquals("María González", historial.getUsuarioNombre());
        assertEquals("empleado", historial.getUsuarioRol());
    }

    @Test
    @DisplayName("Debe manejar observaciones")
    void testObservaciones() {
        historial.setObservacion("Cliente confirmó la fecha y hora del servicio");
        assertEquals("Cliente confirmó la fecha y hora del servicio", 
                    historial.getObservacion());
    }

    @Test
    @DisplayName("Debe manejar fecha de creación con propiedad JSON")
    void testFechaCreacion() {
        historial.setCreatedAt("2024-05-09 14:30:00");
        assertEquals("2024-05-09 14:30:00", historial.getCreatedAt());
    }

    @Test
    @DisplayName("Debe validar diferentes cambios de estado")
    void testCambiosEstado() {
        // Cambio de pendiente a confirmado
        historial.setEstadoAnterior("pendiente");
        historial.setEstadoNuevo("confirmado");
        assertEquals("pendiente", historial.getEstadoAnterior());
        assertEquals("confirmado", historial.getEstadoNuevo());

        // Cambio de confirmado a en_proceso
        historial.setEstadoAnterior("confirmado");
        historial.setEstadoNuevo("en_proceso");
        assertEquals("confirmado", historial.getEstadoAnterior());
        assertEquals("en_proceso", historial.getEstadoNuevo());

        // Cambio de en_proceso a finalizado
        historial.setEstadoAnterior("en_proceso");
        historial.setEstadoNuevo("finalizado");
        assertEquals("en_proceso", historial.getEstadoAnterior());
        assertEquals("finalizado", historial.getEstadoNuevo());
    }

    @Test
    @DisplayName("Debe manejar estado anterior nulo para creación inicial")
    void testEstadoAnteriorNulo() {
        // Cuando se crea un servicio por primera vez, no hay estado anterior
        historial.setEstadoAnterior(null);
        historial.setEstadoNuevo("pendiente");

        assertNull(historial.getEstadoAnterior());
        assertEquals("pendiente", historial.getEstadoNuevo());
    }

    @Test
    @DisplayName("Debe validar diferentes roles de usuario")
    void testRolesUsuario() {
        String[] roles = {"administrador", "empleado", "cliente"};

        for (String rol : roles) {
            historial.setUsuarioRol(rol);
            assertEquals(rol, historial.getUsuarioRol());
        }
    }

    @Test
    @DisplayName("Debe validar historial completo")
    void testHistorialCompleto() {
        historial.setId(25);
        historial.setServicioId(150);
        historial.setEstadoAnterior("confirmado");
        historial.setEstadoNuevo("en_proceso");
        historial.setUsuarioId(8);
        historial.setUsuarioNombre("Carlos Rodríguez");
        historial.setUsuarioRol("empleado");
        historial.setObservacion("Iniciando proceso de mudanza. Vehículo asignado y en camino.");
        historial.setCreatedAt("2024-05-09 08:30:00");

        // Verificar todos los campos
        assertEquals(25, historial.getId());
        assertEquals(150, historial.getServicioId());
        assertEquals("confirmado", historial.getEstadoAnterior());
        assertEquals("en_proceso", historial.getEstadoNuevo());
        assertEquals(8, historial.getUsuarioId());
        assertEquals("Carlos Rodríguez", historial.getUsuarioNombre());
        assertEquals("empleado", historial.getUsuarioRol());
        assertEquals("Iniciando proceso de mudanza. Vehículo asignado y en camino.", 
                    historial.getObservacion());
        assertEquals("2024-05-09 08:30:00", historial.getCreatedAt());
    }

    @Test
    @DisplayName("Debe manejar observaciones largas")
    void testObservacionesLargas() {
        String observacionLarga = "El cliente solicitó cambio de fecha debido a inconvenientes " +
                                 "personales. Se reprogramó para la siguiente semana. " +
                                 "Se confirmó disponibilidad de vehículo y empleado. " +
                                 "Cliente muy satisfecho con la flexibilidad del servicio.";
        
        historial.setObservacion(observacionLarga);
        assertEquals(observacionLarga, historial.getObservacion());
    }

    @Test
    @DisplayName("Debe manejar cancelaciones con observación")
    void testCancelaciones() {
        historial.setEstadoAnterior("confirmado");
        historial.setEstadoNuevo("cancelado");
        historial.setObservacion("Cliente canceló el servicio por motivos personales. " +
                                "Se procesará reembolso según políticas de la empresa.");

        assertEquals("confirmado", historial.getEstadoAnterior());
        assertEquals("cancelado", historial.getEstadoNuevo());
        assertTrue(historial.getObservacion().contains("canceló"));
        assertTrue(historial.getObservacion().contains("reembolso"));
    }

    @Test
    @DisplayName("Debe manejar valores por defecto")
    void testValoresPorDefecto() {
        HistorialEstado nuevoHistorial = new HistorialEstado();
        
        assertEquals(0, nuevoHistorial.getId());
        assertEquals(0, nuevoHistorial.getServicioId());
        assertEquals(0, nuevoHistorial.getUsuarioId());
        assertNull(nuevoHistorial.getEstadoAnterior());
        assertNull(nuevoHistorial.getEstadoNuevo());
        assertNull(nuevoHistorial.getUsuarioNombre());
        assertNull(nuevoHistorial.getUsuarioRol());
        assertNull(nuevoHistorial.getObservacion());
        assertNull(nuevoHistorial.getCreatedAt());
    }

    @Test
    @DisplayName("Debe permitir múltiples cambios en secuencia")
    void testCambiosSecuenciales() {
        // Primer cambio
        historial.setEstadoAnterior(null);
        historial.setEstadoNuevo("pendiente");
        historial.setObservacion("Servicio creado");
        assertEquals("pendiente", historial.getEstadoNuevo());

        // Segundo cambio
        historial.setEstadoAnterior("pendiente");
        historial.setEstadoNuevo("confirmado");
        historial.setObservacion("Cliente confirmó el servicio");
        assertEquals("confirmado", historial.getEstadoNuevo());

        // Tercer cambio
        historial.setEstadoAnterior("confirmado");
        historial.setEstadoNuevo("finalizado");
        historial.setObservacion("Servicio completado exitosamente");
        assertEquals("finalizado", historial.getEstadoNuevo());
    }

    @Test
    @DisplayName("Debe manejar diferentes tipos de usuarios que hacen cambios")
    void testTiposUsuarios() {
        // Cambio hecho por administrador
        historial.setUsuarioNombre("Admin Sistema");
        historial.setUsuarioRol("administrador");
        historial.setObservacion("Cambio administrativo del estado");
        assertEquals("administrador", historial.getUsuarioRol());

        // Cambio hecho por empleado
        historial.setUsuarioNombre("Juan Empleado");
        historial.setUsuarioRol("empleado");
        historial.setObservacion("Actualización por parte del empleado asignado");
        assertEquals("empleado", historial.getUsuarioRol());

        // Cambio hecho por cliente
        historial.setUsuarioNombre("María Cliente");
        historial.setUsuarioRol("cliente");
        historial.setObservacion("Cliente solicitó modificación");
        assertEquals("cliente", historial.getUsuarioRol());
    }
}