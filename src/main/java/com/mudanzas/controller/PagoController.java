package com.mudanzas.controller;

import com.mudanzas.service.PagoService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST para pagos de servicios.
 * GET  /api/servicios/{id}/pagos
 * POST /api/servicios/{id}/pagos
 */
@RestController
@RequestMapping("/api/servicios/{servicioId}/pagos")
@Tag(name = "Pagos", description = "Registro y consulta de pagos por servicio")
@SecurityRequirement(name = "bearerAuth")
public class PagoController {

    private final PagoService pagoService = new PagoService();

    @GetMapping
    @Operation(summary = "Listar pagos de un servicio",
               responses = { @ApiResponse(responseCode = "200", description = "Lista de pagos y total acumulado"),
                             @ApiResponse(responseCode = "404", description = "Servicio no encontrado") })
    public ResponseEntity<?> listarPagos(@PathVariable int servicioId) {
        try {
            return ResponseEntity.ok(pagoService.listarPagos(servicioId));
        } catch (IllegalArgumentException e) {
            return error(404, e.getMessage());
        } catch (Exception e) {
            return error(500, "Error interno del servidor");
        }
    }

    @PostMapping
    @Operation(summary = "Registrar pago para un servicio",
               description = "Solo Administradores.",
               responses = { @ApiResponse(responseCode = "201", description = "Pago registrado"),
                             @ApiResponse(responseCode = "400", description = "Monto inválido o excede el costo"),
                             @ApiResponse(responseCode = "403", description = "Acceso denegado"),
                             @ApiResponse(responseCode = "404", description = "Servicio no encontrado") })
    public ResponseEntity<?> registrarPago(@PathVariable int servicioId,
                                           @RequestBody Map<String, Object> body,
                                           Authentication auth) {
        if (!esAdmin(auth)) return error(403, "Acceso denegado");
        try {
            Object montoObj = body.get("monto");
            if (montoObj == null) return error(400, "El campo monto es obligatorio");
            double monto = Double.parseDouble(montoObj.toString());
            return ResponseEntity.status(201).body(pagoService.registrarPago(servicioId, monto));
        } catch (IllegalArgumentException e) {
            return error(e.getMessage().contains("no encontrado") ? 404 : 400, e.getMessage());
        } catch (Exception e) {
            return error(500, "Error interno del servidor");
        }
    }

    private boolean esAdmin(Authentication auth) {
        if (auth == null) return false;
        return "ADMINISTRADOR".equals(((Claims) auth.getPrincipal()).get("rol", String.class));
    }

    private ResponseEntity<?> error(int status, String message) {
        return ResponseEntity.status(status).body(Map.of("error", Map.of("message", message)));
    }
}
