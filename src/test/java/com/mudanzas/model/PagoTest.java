package com.mudanzas.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la entidad Pago.
 * Verifica métodos de pago, estados y montos.
 */
@DisplayName("Pago - Pruebas Unitarias")
class PagoTest {

    private Pago pago;

    @BeforeEach
    void setUp() {
        pago = new Pago();
    }

    @Test
    @DisplayName("Debe crear pago con constructor vacío")
    void testConstructorVacio() {
        assertNotNull(pago);
        assertEquals(0, pago.getId());
        assertEquals(0, pago.getServicioId());
        assertEquals(0.0, pago.getMonto());
        assertNull(pago.getMetodoPago());
        assertNull(pago.getEstadoPago());
    }

    @Test
    @DisplayName("Debe establecer y obtener propiedades básicas")
    void testPropiedadesBasicas() {
        pago.setId(1);
        pago.setServicioId(100);
        pago.setMonto(250000.0);
        pago.setMetodoPago("transferencia");
        pago.setEstadoPago("pagado");

        assertEquals(1, pago.getId());
        assertEquals(100, pago.getServicioId());
        assertEquals(250000.0, pago.getMonto(), 0.01);
        assertEquals("transferencia", pago.getMetodoPago());
        assertEquals("pagado", pago.getEstadoPago());
    }

    @Test
    @DisplayName("Debe validar métodos de pago permitidos")
    void testMetodosPago() {
        String[] metodosValidos = {"efectivo", "transferencia", "tarjeta"};

        for (String metodo : metodosValidos) {
            pago.setMetodoPago(metodo);
            assertEquals(metodo, pago.getMetodoPago());
        }
    }

    @Test
    @DisplayName("Debe validar estados de pago permitidos")
    void testEstadosPago() {
        String[] estadosValidos = {"pendiente", "pagado", "reembolsado"};

        for (String estado : estadosValidos) {
            pago.setEstadoPago(estado);
            assertEquals(estado, pago.getEstadoPago());
        }
    }

    @Test
    @DisplayName("Debe manejar información adicional del pago")
    void testInformacionAdicional() {
        pago.setFechaPago("2024-05-09 14:30:00");
        pago.setReferencia("REF123456789");
        pago.setNotas("Pago realizado por transferencia bancaria");

        assertEquals("2024-05-09 14:30:00", pago.getFechaPago());
        assertEquals("REF123456789", pago.getReferencia());
        assertEquals("Pago realizado por transferencia bancaria", pago.getNotas());
    }

    @Test
    @DisplayName("Debe manejar fechas con propiedades JSON")
    void testFechasJSON() {
        pago.setCreatedAt("2024-05-09 10:00:00");
        pago.setUpdatedAt("2024-05-09 14:30:00");

        assertEquals("2024-05-09 10:00:00", pago.getCreatedAt());
        assertEquals("2024-05-09 14:30:00", pago.getUpdatedAt());
    }

    @Test
    @DisplayName("Debe manejar campos de JOIN del servicio")
    void testCamposJoinServicio() {
        pago.setFechaServicio("2024-05-10");
        pago.setCiudadOrigen("Bogotá");
        pago.setCiudadDestino("Medellín");
        pago.setCostoTotal(300000.0);
        pago.setEstadoServicio("finalizado");

        assertEquals("2024-05-10", pago.getFechaServicio());
        assertEquals("Bogotá", pago.getCiudadOrigen());
        assertEquals("Medellín", pago.getCiudadDestino());
        assertEquals(300000.0, pago.getCostoTotal(), 0.01);
        assertEquals("finalizado", pago.getEstadoServicio());
    }

    @Test
    @DisplayName("Debe manejar campos de JOIN del cliente")
    void testCamposJoinCliente() {
        pago.setClienteNombre("Juan Pérez");
        pago.setClienteEmail("juan.perez@email.com");

        assertEquals("Juan Pérez", pago.getClienteNombre());
        assertEquals("juan.perez@email.com", pago.getClienteEmail());
    }

    @Test
    @DisplayName("Debe validar pago completo")
    void testPagoCompleto() {
        pago.setId(50);
        pago.setServicioId(200);
        pago.setMonto(450000.75);
        pago.setMetodoPago("tarjeta");
        pago.setEstadoPago("pagado");
        pago.setFechaPago("2024-05-09 16:45:00");
        pago.setReferencia("TXN987654321");
        pago.setNotas("Pago con tarjeta de crédito Visa");
        pago.setCreatedAt("2024-05-09 10:00:00");
        pago.setUpdatedAt("2024-05-09 16:45:00");

        // Campos de JOIN
        pago.setFechaServicio("2024-05-10");
        pago.setCiudadOrigen("Cali");
        pago.setCiudadDestino("Barranquilla");
        pago.setCostoTotal(450000.75);
        pago.setEstadoServicio("confirmado");
        pago.setClienteNombre("María González");
        pago.setClienteEmail("maria.gonzalez@email.com");

        // Verificar todos los campos
        assertEquals(50, pago.getId());
        assertEquals(200, pago.getServicioId());
        assertEquals(450000.75, pago.getMonto(), 0.01);
        assertEquals("tarjeta", pago.getMetodoPago());
        assertEquals("pagado", pago.getEstadoPago());
        assertEquals("2024-05-09 16:45:00", pago.getFechaPago());
        assertEquals("TXN987654321", pago.getReferencia());
        assertEquals("Pago con tarjeta de crédito Visa", pago.getNotas());
        assertEquals("2024-05-09 10:00:00", pago.getCreatedAt());
        assertEquals("2024-05-09 16:45:00", pago.getUpdatedAt());

        // Campos de JOIN
        assertEquals("2024-05-10", pago.getFechaServicio());
        assertEquals("Cali", pago.getCiudadOrigen());
        assertEquals("Barranquilla", pago.getCiudadDestino());
        assertEquals(450000.75, pago.getCostoTotal(), 0.01);
        assertEquals("confirmado", pago.getEstadoServicio());
        assertEquals("María González", pago.getClienteNombre());
        assertEquals("maria.gonzalez@email.com", pago.getClienteEmail());
    }

    @Test
    @DisplayName("Debe manejar montos decimales")
    void testMontosDecimales() {
        pago.setMonto(125000.50);
        assertEquals(125000.50, pago.getMonto(), 0.001);

        pago.setMonto(999999.99);
        assertEquals(999999.99, pago.getMonto(), 0.001);

        pago.setMonto(0.01);
        assertEquals(0.01, pago.getMonto(), 0.001);
    }

    @Test
    @DisplayName("Debe manejar diferentes tipos de referencias")
    void testTiposReferencias() {
        // Referencia de transferencia
        pago.setReferencia("TRANS-2024050912345");
        assertEquals("TRANS-2024050912345", pago.getReferencia());

        // Referencia de tarjeta
        pago.setReferencia("CARD-****1234-AUTH789");
        assertEquals("CARD-****1234-AUTH789", pago.getReferencia());

        // Referencia de efectivo
        pago.setReferencia("CASH-RECEIPT-001");
        assertEquals("CASH-RECEIPT-001", pago.getReferencia());
    }

    @Test
    @DisplayName("Debe manejar valores por defecto")
    void testValoresPorDefecto() {
        Pago nuevoPago = new Pago();
        
        assertEquals(0, nuevoPago.getId());
        assertEquals(0, nuevoPago.getServicioId());
        assertEquals(0.0, nuevoPago.getMonto());
        assertNull(nuevoPago.getMetodoPago());
        assertNull(nuevoPago.getEstadoPago());
        assertNull(nuevoPago.getFechaPago());
        assertNull(nuevoPago.getReferencia());
        assertNull(nuevoPago.getNotas());
    }

    @Test
    @DisplayName("Debe permitir cambios de estado")
    void testCambiosEstado() {
        // Estado inicial
        pago.setEstadoPago("pendiente");
        assertEquals("pendiente", pago.getEstadoPago());

        // Cambio a pagado
        pago.setEstadoPago("pagado");
        pago.setFechaPago("2024-05-09 15:00:00");
        assertEquals("pagado", pago.getEstadoPago());
        assertEquals("2024-05-09 15:00:00", pago.getFechaPago());

        // Cambio a reembolsado
        pago.setEstadoPago("reembolsado");
        assertEquals("reembolsado", pago.getEstadoPago());
    }
}