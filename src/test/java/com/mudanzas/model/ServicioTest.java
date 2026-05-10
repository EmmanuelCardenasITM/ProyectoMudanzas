package com.mudanzas.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la entidad Servicio.
 * Verifica estados, costos y información del servicio de mudanza.
 */
@DisplayName("Servicio - Pruebas Unitarias")
class ServicioTest {

    private Servicio servicio;

    @BeforeEach
    void setUp() {
        servicio = new Servicio();
    }

    @Test
    @DisplayName("Debe crear servicio con constructor vacío")
    void testConstructorVacio() {
        assertNotNull(servicio);
        assertEquals(0, servicio.getId());
        assertEquals(0, servicio.getClienteId());
        assertNull(servicio.getVehiculoId());
        assertNull(servicio.getEmpleadoId());
        assertEquals(0.0, servicio.getDistanciaKm());
        assertEquals(0.0, servicio.getPesoCargaKg());
    }

    @Test
    @DisplayName("Debe establecer y obtener propiedades básicas")
    void testPropiedadesBasicas() {
        servicio.setId(1);
        servicio.setClienteId(100);
        servicio.setVehiculoId(10);
        servicio.setEmpleadoId(5);
        servicio.setFechaServicio("2024-05-15");
        servicio.setHoraServicio("09:00");

        assertEquals(1, servicio.getId());
        assertEquals(100, servicio.getClienteId());
        assertEquals(10, servicio.getVehiculoId());
        assertEquals(5, servicio.getEmpleadoId());
        assertEquals("2024-05-15", servicio.getFechaServicio());
        assertEquals("09:00", servicio.getHoraServicio());
    }

    @Test
    @DisplayName("Debe manejar direcciones de origen y destino")
    void testDirecciones() {
        servicio.setDireccionOrigen("Calle 123 #45-67");
        servicio.setCiudadOrigen("Bogotá");
        servicio.setDireccionDestino("Carrera 15 #20-30");
        servicio.setCiudadDestino("Medellín");

        assertEquals("Calle 123 #45-67", servicio.getDireccionOrigen());
        assertEquals("Bogotá", servicio.getCiudadOrigen());
        assertEquals("Carrera 15 #20-30", servicio.getDireccionDestino());
        assertEquals("Medellín", servicio.getCiudadDestino());
    }

    @Test
    @DisplayName("Debe manejar información de carga")
    void testInformacionCarga() {
        servicio.setDistanciaKm(350.5);
        servicio.setPesoCargaKg(1500.75);
        servicio.setDescripcionCarga("Muebles de apartamento: sofá, cama, mesa, electrodomésticos");

        assertEquals(350.5, servicio.getDistanciaKm(), 0.01);
        assertEquals(1500.75, servicio.getPesoCargaKg(), 0.01);
        assertEquals("Muebles de apartamento: sofá, cama, mesa, electrodomésticos", 
                    servicio.getDescripcionCarga());
    }

    @Test
    @DisplayName("Debe manejar costos del servicio")
    void testCostos() {
        servicio.setCostoBase(200000.0);
        servicio.setCostoTotal(350000.0);

        assertEquals(200000.0, servicio.getCostoBase(), 0.01);
        assertEquals(350000.0, servicio.getCostoTotal(), 0.01);
    }

    @Test
    @DisplayName("Debe validar estados de servicio permitidos")
    void testEstadosServicio() {
        String[] estadosValidos = {
            "pendiente", 
            "confirmado", 
            "en_proceso", 
            "finalizado", 
            "cancelado"
        };

        for (String estado : estadosValidos) {
            servicio.setEstado(estado);
            assertEquals(estado, servicio.getEstado());
        }
    }

    @Test
    @DisplayName("Debe manejar notas adicionales")
    void testNotas() {
        servicio.setNotas("Cliente requiere embalaje especial para objetos frágiles");
        assertEquals("Cliente requiere embalaje especial para objetos frágiles", 
                    servicio.getNotas());
    }

    @Test
    @DisplayName("Debe manejar fechas con propiedades JSON")
    void testFechasJSON() {
        servicio.setCreatedAt("2024-05-09 10:00:00");
        servicio.setUpdatedAt("2024-05-09 14:30:00");

        assertEquals("2024-05-09 10:00:00", servicio.getCreatedAt());
        assertEquals("2024-05-09 14:30:00", servicio.getUpdatedAt());
    }

    @Test
    @DisplayName("Debe manejar campos de JOIN del cliente")
    void testCamposJoinCliente() {
        servicio.setClienteNombre("Juan Carlos Pérez");
        servicio.setClienteEmail("juan.carlos@email.com");
        servicio.setClienteTelefono("3001234567");

        assertEquals("Juan Carlos Pérez", servicio.getClienteNombre());
        assertEquals("juan.carlos@email.com", servicio.getClienteEmail());
        assertEquals("3001234567", servicio.getClienteTelefono());
    }

    @Test
    @DisplayName("Debe manejar campos de JOIN del empleado")
    void testCamposJoinEmpleado() {
        servicio.setEmpleadoNombre("María González");
        assertEquals("María González", servicio.getEmpleadoNombre());
    }

    @Test
    @DisplayName("Debe manejar campos de JOIN del vehículo")
    void testCamposJoinVehiculo() {
        servicio.setVehiculoPlaca("ABC123");
        servicio.setVehiculoTipo("camion_mediano");

        assertEquals("ABC123", servicio.getVehiculoPlaca());
        assertEquals("camion_mediano", servicio.getVehiculoTipo());
    }

    @Test
    @DisplayName("Debe validar servicio completo")
    void testServicioCompleto() {
        servicio.setId(75);
        servicio.setClienteId(150);
        servicio.setVehiculoId(25);
        servicio.setEmpleadoId(8);
        servicio.setFechaServicio("2024-05-20");
        servicio.setHoraServicio("14:30");
        servicio.setDireccionOrigen("Avenida 68 #45-123");
        servicio.setCiudadOrigen("Bogotá");
        servicio.setDireccionDestino("Calle 50 #30-15");
        servicio.setCiudadDestino("Cali");
        servicio.setDistanciaKm(460.5);
        servicio.setPesoCargaKg(2500.25);
        servicio.setDescripcionCarga("Mudanza completa de casa: muebles, electrodomésticos, cajas");
        servicio.setCostoBase(300000.0);
        servicio.setCostoTotal(520000.0);
        servicio.setEstado("confirmado");
        servicio.setNotas("Mudanza programada para fin de semana");
        servicio.setCreatedAt("2024-05-09 09:00:00");
        servicio.setUpdatedAt("2024-05-09 16:30:00");

        // Campos de JOIN
        servicio.setClienteNombre("Ana María López");
        servicio.setClienteEmail("ana.lopez@email.com");
        servicio.setClienteTelefono("3009876543");
        servicio.setEmpleadoNombre("Carlos Rodríguez");
        servicio.setVehiculoPlaca("XYZ789");
        servicio.setVehiculoTipo("camion_grande");

        // Verificar todos los campos
        assertEquals(75, servicio.getId());
        assertEquals(150, servicio.getClienteId());
        assertEquals(25, servicio.getVehiculoId());
        assertEquals(8, servicio.getEmpleadoId());
        assertEquals("2024-05-20", servicio.getFechaServicio());
        assertEquals("14:30", servicio.getHoraServicio());
        assertEquals("Avenida 68 #45-123", servicio.getDireccionOrigen());
        assertEquals("Bogotá", servicio.getCiudadOrigen());
        assertEquals("Calle 50 #30-15", servicio.getDireccionDestino());
        assertEquals("Cali", servicio.getCiudadDestino());
        assertEquals(460.5, servicio.getDistanciaKm(), 0.01);
        assertEquals(2500.25, servicio.getPesoCargaKg(), 0.01);
        assertEquals("Mudanza completa de casa: muebles, electrodomésticos, cajas", 
                    servicio.getDescripcionCarga());
        assertEquals(300000.0, servicio.getCostoBase(), 0.01);
        assertEquals(520000.0, servicio.getCostoTotal(), 0.01);
        assertEquals("confirmado", servicio.getEstado());
        assertEquals("Mudanza programada para fin de semana", servicio.getNotas());
        assertEquals("2024-05-09 09:00:00", servicio.getCreatedAt());
        assertEquals("2024-05-09 16:30:00", servicio.getUpdatedAt());

        // Campos de JOIN
        assertEquals("Ana María López", servicio.getClienteNombre());
        assertEquals("ana.lopez@email.com", servicio.getClienteEmail());
        assertEquals("3009876543", servicio.getClienteTelefono());
        assertEquals("Carlos Rodríguez", servicio.getEmpleadoNombre());
        assertEquals("XYZ789", servicio.getVehiculoPlaca());
        assertEquals("camion_grande", servicio.getVehiculoTipo());
    }

    @Test
    @DisplayName("Debe manejar IDs opcionales como null")
    void testIDsOpcionales() {
        // VehiculoId y EmpleadoId pueden ser null
        servicio.setVehiculoId(null);
        servicio.setEmpleadoId(null);

        assertNull(servicio.getVehiculoId());
        assertNull(servicio.getEmpleadoId());

        // Luego asignar valores
        servicio.setVehiculoId(15);
        servicio.setEmpleadoId(3);

        assertEquals(15, servicio.getVehiculoId());
        assertEquals(3, servicio.getEmpleadoId());
    }

    @Test
    @DisplayName("Debe manejar diferentes horarios")
    void testHorarios() {
        String[] horarios = {"08:00", "09:30", "14:15", "16:45", "18:00"};

        for (String horario : horarios) {
            servicio.setHoraServicio(horario);
            assertEquals(horario, servicio.getHoraServicio());
        }
    }

    @Test
    @DisplayName("Debe permitir cambios de estado del servicio")
    void testCambiosEstado() {
        // Estado inicial
        servicio.setEstado("pendiente");
        assertEquals("pendiente", servicio.getEstado());

        // Confirmación
        servicio.setEstado("confirmado");
        assertEquals("confirmado", servicio.getEstado());

        // En proceso
        servicio.setEstado("en_proceso");
        assertEquals("en_proceso", servicio.getEstado());

        // Finalizado
        servicio.setEstado("finalizado");
        assertEquals("finalizado", servicio.getEstado());
    }
}