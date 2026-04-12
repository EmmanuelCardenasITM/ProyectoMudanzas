package com.mudanzas.controller;

import com.mudanzas.service.TarifaService;
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
 * Controlador REST para tarifas.
 * GET /api/tarifas — Admin y Empleado
 * PUT /api/tarifas — solo Admin
 */
@RestController
@RequestMapping("/api/tarifas")
@Tag(name = "Tarifas", description = "Gestión de tarifas del sistema")
@SecurityRequirement(name = "bearerAuth")
public class TarifaController {

    private final TarifaService tarifaService = new TarifaService();

    @GetMapping
    @Operation(summary = "Obtener tarifas vigentes",
               responses = { @ApiResponse(responseCode = "200", description = "Tarifas vigentes"),
                             @ApiResponse(responseCode = "404", description = "Sin tarifas configuradas") })
    public ResponseEntity<?> obtenerTarifas() {
        try {
            return ResponseEntity.ok(tarifaService.obtenerTarifas());
        } catch (IllegalStateException e) {
            return error(404, e.getMessage());
        } catch (Exception e) {
            return error(500, "Error interno del servidor");
        }
    }

    @PutMapping
    @Operation(summary = "Actualizar tarifas vigentes",
               description = "Solo Administradores.",
               responses = { @ApiResponse(responseCode = "200", description = "Tarifas actualizadas"),
                             @ApiResponse(responseCode = "400", description = "Valores inválidos"),
                             @ApiResponse(responseCode = "403", description = "Acceso denegado") })
    public ResponseEntity<?> actualizarTarifas(@RequestBody Map<String, Object> body,
                                               Authentication auth) {
        if (!esAdmin(auth)) return error(403, "Acceso denegado");
        try {
            double km    = Double.parseDouble(body.get("tarifa_por_km").toString());
            double carga = Double.parseDouble(body.get("tarifa_por_unidad_carga").toString());
            return ResponseEntity.ok(tarifaService.actualizarTarifas(km, carga));
        } catch (IllegalArgumentException e) {
            return error(400, e.getMessage());
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
