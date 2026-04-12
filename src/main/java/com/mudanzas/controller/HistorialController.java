package com.mudanzas.controller;

import com.mudanzas.service.ClienteService;
import com.mudanzas.service.ServicioService;
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
 * GET /api/clientes/{id}/historial?estado=
 */
@RestController
@RequestMapping("/api/clientes/{clienteId}/historial")
@Tag(name = "Historial", description = "Historial de servicios por cliente")
@SecurityRequirement(name = "bearerAuth")
public class HistorialController {

    private final ClienteService  clienteService  = new ClienteService();
    private final ServicioService servicioService = new ServicioService();

    @GetMapping
    @Operation(
        summary = "Listar historial de servicios de un cliente",
        description = "Retorna los servicios del cliente ordenados por fecha descendente. " +
                      "Filtro opcional por estado: PENDIENTE, EN_PROCESO, FINALIZADO.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Lista de servicios (puede ser vacía)"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
        }
    )
    public ResponseEntity<?> listarHistorial(
            @PathVariable int clienteId,
            @Parameter(description = "Filtrar por estado")
            @RequestParam(required = false) String estado) {
        try {
            clienteService.obtenerCliente(clienteId); // lanza 404 si no existe
            return ResponseEntity.ok(servicioService.historialPorCliente(clienteId, estado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404)
                .body(Map.of("error", Map.of("message", e.getMessage())));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", Map.of("message", "Error interno del servidor")));
        }
    }
}
