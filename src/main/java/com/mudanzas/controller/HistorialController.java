package com.mudanzas.controller;

import com.mudanzas.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST para el historial de servicios por cliente.
 * GET /api/clientes/{clienteId}/historial
 *
 * Nota: Este endpoint también está disponible en ClienteController.
 * Se mantiene por compatibilidad con la ruta original.
 */
@RestController
@RequestMapping("/api/clientes/{clienteId}/historial")
@Tag(name = "Historial", description = "Historial de servicios por cliente")
@SecurityRequirement(name = "bearerAuth")
public class HistorialController {

    private final ClienteService clienteService = new ClienteService();

    @GetMapping
    @Operation(
        summary = "Listar historial de servicios de un cliente",
        description = "Retorna los servicios del cliente ordenados por fecha descendente.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Lista de servicios (puede ser vacía)"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
        }
    )
    public ResponseEntity<?> listarHistorial(
            @PathVariable int clienteId,
            @Parameter(description = "Filtrar por estado (ignorado, se retornan todos)")
            @RequestParam(required = false) String estado) {
        try {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", clienteService.obtenerHistorial(clienteId)
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404)
                .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("success", false, "message", "Error interno del servidor."));
        }
    }
}
