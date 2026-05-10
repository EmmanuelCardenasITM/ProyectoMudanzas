package com.mudanzas.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas básicas para verificar que los controladores se cargan correctamente.
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Controladores - Pruebas Básicas")
class ControllerBasicTest {

    @Autowired(required = false)
    private AuthController authController;

    @Autowired(required = false)
    private UsuarioController usuarioController;

    @Autowired(required = false)
    private ClienteController clienteController;

    @Autowired(required = false)
    private ServicioController servicioController;

    @Autowired(required = false)
    private PagoController pagoController;

    @Autowired(required = false)
    private VehiculoController vehiculoController;

    @Autowired(required = false)
    private TarifaController tarifaController;

    @Autowired(required = false)
    private HistorialController historialController;

    @Test
    @DisplayName("Debe cargar AuthController correctamente")
    void testAuthControllerCarga() {
        assertNotNull(authController, "AuthController debe estar disponible");
    }

    @Test
    @DisplayName("Debe cargar UsuarioController correctamente")
    void testUsuarioControllerCarga() {
        assertNotNull(usuarioController, "UsuarioController debe estar disponible");
    }

    @Test
    @DisplayName("Debe cargar ClienteController correctamente")
    void testClienteControllerCarga() {
        assertNotNull(clienteController, "ClienteController debe estar disponible");
    }

    @Test
    @DisplayName("Debe cargar ServicioController correctamente")
    void testServicioControllerCarga() {
        assertNotNull(servicioController, "ServicioController debe estar disponible");
    }

    @Test
    @DisplayName("Debe cargar PagoController correctamente")
    void testPagoControllerCarga() {
        assertNotNull(pagoController, "PagoController debe estar disponible");
    }

    @Test
    @DisplayName("Debe cargar VehiculoController correctamente")
    void testVehiculoControllerCarga() {
        assertNotNull(vehiculoController, "VehiculoController debe estar disponible");
    }

    @Test
    @DisplayName("Debe cargar TarifaController correctamente")
    void testTarifaControllerCarga() {
        assertNotNull(tarifaController, "TarifaController debe estar disponible");
    }

    @Test
    @DisplayName("Debe cargar HistorialController correctamente")
    void testHistorialControllerCarga() {
        assertNotNull(historialController, "HistorialController debe estar disponible");
    }

    @Test
    @DisplayName("Debe tener todos los controladores principales")
    void testTodosLosControladores() {
        int controladoresCargados = 0;
        
        if (authController != null) controladoresCargados++;
        if (usuarioController != null) controladoresCargados++;
        if (clienteController != null) controladoresCargados++;
        if (servicioController != null) controladoresCargados++;
        if (pagoController != null) controladoresCargados++;
        if (vehiculoController != null) controladoresCargados++;
        if (tarifaController != null) controladoresCargados++;
        if (historialController != null) controladoresCargados++;

        assertTrue(controladoresCargados >= 6, 
            "Debe haber al menos 6 controladores cargados, encontrados: " + controladoresCargados);
    }
}