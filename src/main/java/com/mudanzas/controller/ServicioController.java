package com.mudanzas.controller;

import com.mudanzas.model.Servicio;
import com.mudanzas.service.ServicioService;
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
 * Controlador REST para servicios de mudanza.
 * GET, POST, PUT, DELETE /api/servicios
 * PUT /api/servicios/{id}/estado
 */
@RestController
@RequestMapping("/api/servicios")
@Tag(name = "Servicios", description = "Gestión de servicios de mudanza")
@SecurityRequirement(name = "bearerAuth")
public class ServicioController {

    private final ServicioService servicioService = new ServicioService();

    @GetMapping
    @Operation(summary = "Listar todos los servicios")
    public ResponseEntity<?> listarServicios() {
        try {
            return ResponseEntity.ok(servicioService.listarServicios());
        } catch (Exception e) {
            return error(500, "Error interno del servidor");
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener servicio por ID",
               responses = { @ApiResponse(responseCode = "200", description = "Datos del servicio"),
                             @ApiResponse(responseCode = "404", description = "Servicio no encontrado") })
    public ResponseEntity<?> obtenerServicio(@PathVariable int id) {
        try {
            return ResponseEntity.ok(servicioService.obtenerServicio(id));
        } catch (IllegalArgumentException e) {
            return error(404, e.getMessage());
        } catch (Exception e) {
            return error(500, "Error interno del servidor");
        }
    }

    @PostMapping
    @Operation(summary = "Crear nuevo servicio",
               description = "Solo Administradores. Estado inicial PENDIENTE, costo calculado automáticamente.",
               responses = { @ApiResponse(responseCode = "201", description = "Servicio creado"),
                             @ApiResponse(responseCode = "400", description = "Datos inválidos"),
                             @ApiResponse(responseCode = "403", description = "Acceso denegado") })
    public ResponseEntity<?> crearServicio(@RequestBody Servicio servicio, Authentication auth) {
        if (!esAdmin(auth)) return error(403, "Acceso denegado");
        try {
            return ResponseEntity.status(201).body(servicioService.crearServicio(servicio));
        } catch (IllegalArgumentException e) {
            return error(400, e.getMessage());
        } catch (IllegalStateException e) {
            return error(404, e.getMessage());
        } catch (Exception e) {
            return error(500, "Error interno del servidor");
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos del servicio",
               description = "Solo Administradores. No se puede modificar un servicio FINALIZADO.",
               responses = { @ApiResponse(responseCode = "200", description = "Servicio actualizado"),
                             @ApiResponse(responseCode = "409", description = "Servicio finalizado") })
    public ResponseEntity<?> actualizarServicio(@PathVariable int id,
                                                @RequestBody Servicio servicio,
                                                Authentication auth) {
        if (!esAdmin(auth)) return error(403, "Acceso denegado");
        try {
            return ResponseEntity.ok(servicioService.actualizarServicio(id, servicio));
        } catch (IllegalArgumentException e) {
            return error(e.getMessage().contains("no encontrado") ? 404 : 400, e.getMessage());
        } catch (IllegalStateException e) {
            return error(409, e.getMessage());
        } catch (Exception e) {
            return error(500, "Error interno del servidor");
        }
    }

    @PutMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado del servicio",
               description = "Transiciones válidas: PENDIENTE → EN_PROCESO, EN_PROCESO → FINALIZADO.",
               responses = { @ApiResponse(responseCode = "200", description = "Estado actualizado"),
                             @ApiResponse(responseCode = "409", description = "Transición inválida") })
    public ResponseEntity<?> cambiarEstado(@PathVariable int id,
                                           @RequestBody Map<String, String> body,
                                           Authentication auth) {
        if (!esAdmin(auth) && !esEmpleado(auth)) return error(403, "Acceso denegado");
        try {
            String nuevoEstado = body.get("estado");
            if (nuevoEstado == null || nuevoEstado.isBlank()) {
                return error(400, "El campo estado es obligatorio");
            }
            return ResponseEntity.ok(servicioService.cambiarEstado(id, nuevoEstado));
        } catch (IllegalArgumentException e) {
            return error(404, e.getMessage());
        } catch (IllegalStateException e) {
            return error(409, e.getMessage());
        } catch (Exception e) {
            return error(500, "Error interno del servidor");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar servicio",
               description = "Solo Administradores. Solo se puede eliminar si está en estado PENDIENTE.",
               responses = { @ApiResponse(responseCode = "204", description = "Servicio eliminado"),
                             @ApiResponse(responseCode = "409", description = "No se puede eliminar") })
    public ResponseEntity<?> eliminarServicio(@PathVariable int id, Authentication auth) {
        if (!esAdmin(auth)) return error(403, "Acceso denegado");
        try {
            servicioService.eliminarServicio(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return error(404, e.getMessage());
        } catch (IllegalStateException e) {
            return error(409, e.getMessage());
        } catch (Exception e) {
            return error(500, "Error interno del servidor");
        }
    }

    private boolean esAdmin(Authentication auth) {
        if (auth == null) return false;
        return "ADMINISTRADOR".equals(((Claims) auth.getPrincipal()).get("rol", String.class));
    }

    private boolean esEmpleado(Authentication auth) {
        if (auth == null) return false;
        return "EMPLEADO".equals(((Claims) auth.getPrincipal()).get("rol", String.class));
    }

    private ResponseEntity<?> error(int status, String message) {
        return ResponseEntity.status(status).body(Map.of("error", Map.of("message", message)));
    }
}
