package com.mudanzas.controller;

import com.mudanzas.model.Servicio;
import com.mudanzas.service.CostoService;
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
 * GET    /api/servicios                  — listar (filtro ?estado=)
 * GET    /api/servicios/cotizar          — cotización estimada
 * GET    /api/servicios/cliente/{id}     — servicios de un cliente
 * GET    /api/servicios/{id}             — detalle
 * GET    /api/servicios/{id}/historial   — historial de estados
 * POST   /api/servicios                  — crear
 * PUT    /api/servicios/{id}             — actualizar
 * PATCH  /api/servicios/{id}/estado      — cambiar estado
 * DELETE /api/servicios/{id}             — eliminar
 */
@RestController
@RequestMapping("/api/servicios")
@Tag(name = "Servicios", description = "Programación y gestión de servicios de mudanza")
@SecurityRequirement(name = "bearerAuth")
public class ServicioController {

    private final ServicioService servicioService = new ServicioService();

    @GetMapping("/cotizar")
    @Operation(
        summary = "Calcular cotización estimada (sin crear servicio)",
        description = "Parámetros: distancia_km y peso_carga_kg",
        responses = { @ApiResponse(responseCode = "200", description = "Desglose del costo estimado"),
                      @ApiResponse(responseCode = "400", description = "Parámetros faltantes") }
    )
    public ResponseEntity<?> cotizar(@RequestParam(required = false) Double distancia_km,
                                     @RequestParam(required = false) Double peso_carga_kg) {
        if (distancia_km == null || peso_carga_kg == null) {
            return error(400, "Se requieren los parámetros distancia_km y peso_carga_kg.");
        }
        try {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", CostoService.calcularCostoDetallado(distancia_km, peso_carga_kg)
            ));
        } catch (IllegalArgumentException e) {
            return error(400, e.getMessage());
        }
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Obtener servicios de un cliente específico",
               responses = { @ApiResponse(responseCode = "200", description = "Lista de servicios"),
                             @ApiResponse(responseCode = "404", description = "Cliente no encontrado") })
    public ResponseEntity<?> obtenerPorCliente(@PathVariable int clienteId) {
        try {
            return ResponseEntity.ok(Map.of("success", true, "data",
                servicioService.obtenerPorCliente(clienteId)));
        } catch (IllegalArgumentException e) {
            return error(404, e.getMessage());
        } catch (Exception e) {
            return error(500, "Error interno del servidor.");
        }
    }

    @GetMapping
    @Operation(summary = "Listar todos los servicios de mudanza",
               description = "Filtro opcional: ?estado=pendiente|confirmado|en_proceso|finalizado|cancelado")
    public ResponseEntity<?> listarServicios(@RequestParam(required = false) String estado) {
        try {
            var servicios = (estado != null && !estado.isBlank())
                ? servicioService.listarPorEstado(estado)
                : servicioService.listarServicios();
            return ResponseEntity.ok(Map.of("success", true, "data", servicios));
        } catch (Exception e) {
            return error(500, "Error interno del servidor.");
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener servicio por ID",
               responses = { @ApiResponse(responseCode = "200", description = "Datos del servicio"),
                             @ApiResponse(responseCode = "404", description = "Servicio no encontrado") })
    public ResponseEntity<?> obtenerServicio(@PathVariable int id) {
        try {
            return ResponseEntity.ok(Map.of("success", true, "data",
                servicioService.obtenerServicio(id)));
        } catch (IllegalArgumentException e) {
            return error(404, e.getMessage());
        } catch (Exception e) {
            return error(500, "Error interno del servidor.");
        }
    }

    @GetMapping("/{id}/historial")
    @Operation(summary = "Obtener historial de cambios de estado de un servicio",
               responses = { @ApiResponse(responseCode = "200", description = "Historial de estados"),
                             @ApiResponse(responseCode = "404", description = "Servicio no encontrado") })
    public ResponseEntity<?> obtenerHistorial(@PathVariable int id) {
        try {
            return ResponseEntity.ok(Map.of("success", true, "data",
                servicioService.obtenerHistorial(id)));
        } catch (IllegalArgumentException e) {
            return error(404, e.getMessage());
        } catch (Exception e) {
            return error(500, "Error interno del servidor.");
        }
    }

    @PostMapping
    @Operation(
        summary = "Crear un nuevo servicio de mudanza",
        description = "Admin o Empleado. El costo se calcula automáticamente. Estado inicial: pendiente.",
        responses = { @ApiResponse(responseCode = "201", description = "Servicio creado"),
                      @ApiResponse(responseCode = "400", description = "Datos inválidos"),
                      @ApiResponse(responseCode = "404", description = "Cliente o vehículo no encontrado") }
    )
    public ResponseEntity<?> crearServicio(@RequestBody Servicio servicio, Authentication auth) {
        if (!esAdminOEmpleado(auth)) return error(403, "Acceso denegado.");
        try {
            int usuarioId = getUsuarioId(auth);
            return ResponseEntity.status(201).body(Map.of(
                "success", true,
                "message", "Servicio creado exitosamente.",
                "data", servicioService.crearServicio(servicio, usuarioId)
            ));
        } catch (IllegalArgumentException e) {
            return error(400, e.getMessage());
        } catch (IllegalStateException e) {
            return error(e.getMessage().contains("no encontrado") ? 404 : 409, e.getMessage());
        } catch (Exception e) {
            return error(500, "Error interno del servidor.");
        }
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar un servicio de mudanza",
        description = "Solo Administradores. No se puede modificar si está finalizado o cancelado.",
        responses = { @ApiResponse(responseCode = "200", description = "Servicio actualizado"),
                      @ApiResponse(responseCode = "409", description = "No se puede modificar") }
    )
    public ResponseEntity<?> actualizarServicio(@PathVariable int id,
                                                @RequestBody Servicio servicio,
                                                Authentication auth) {
        if (!esAdmin(auth)) return error(403, "Acceso denegado.");
        try {
            int usuarioId = getUsuarioId(auth);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Servicio actualizado.",
                "data", servicioService.actualizarServicio(id, servicio, usuarioId)
            ));
        } catch (IllegalArgumentException e) {
            return error(e.getMessage().contains("no encontrado") ? 404 : 400, e.getMessage());
        } catch (IllegalStateException e) {
            return error(409, e.getMessage());
        } catch (Exception e) {
            return error(500, "Error interno del servidor.");
        }
    }

    @PatchMapping("/{id}/estado")
    @Operation(
        summary = "Cambiar el estado de un servicio",
        description = "Admin o Empleado. Transiciones: pendiente→confirmado/cancelado, " +
                      "confirmado→en_proceso/cancelado, en_proceso→finalizado/cancelado.",
        responses = { @ApiResponse(responseCode = "200", description = "Estado actualizado"),
                      @ApiResponse(responseCode = "409", description = "Transición inválida") }
    )
    public ResponseEntity<?> cambiarEstado(@PathVariable int id,
                                           @RequestBody Map<String, String> body,
                                           Authentication auth) {
        if (!esAdminOEmpleado(auth)) return error(403, "Acceso denegado.");
        try {
            String estado = body.get("estado");
            if (estado == null || estado.isBlank()) return error(400, "El campo estado es obligatorio.");
            String observacion = body.get("observacion");
            int usuarioId = getUsuarioId(auth);
            Servicio actualizado = servicioService.cambiarEstado(id, estado, usuarioId, observacion);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Estado actualizado a \"" + estado + "\".",
                "data", actualizado
            ));
        } catch (IllegalArgumentException e) {
            return error(404, e.getMessage());
        } catch (IllegalStateException e) {
            return error(409, e.getMessage());
        } catch (Exception e) {
            return error(500, "Error interno del servidor.");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Eliminar un servicio",
        description = "Solo Administradores. Solo se pueden eliminar servicios pendientes o cancelados.",
        responses = { @ApiResponse(responseCode = "200", description = "Servicio eliminado"),
                      @ApiResponse(responseCode = "409", description = "No se puede eliminar") }
    )
    public ResponseEntity<?> eliminarServicio(@PathVariable int id, Authentication auth) {
        if (!esAdmin(auth)) return error(403, "Acceso denegado.");
        try {
            servicioService.eliminarServicio(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Servicio eliminado correctamente."));
        } catch (IllegalArgumentException e) {
            return error(404, e.getMessage());
        } catch (IllegalStateException e) {
            return error(409, e.getMessage());
        } catch (Exception e) {
            return error(500, "Error interno del servidor.");
        }
    }

    private boolean esAdmin(Authentication auth) {
        if (auth == null) return false;
        String rol = ((Claims) auth.getPrincipal()).get("rol", String.class);
        return "administrador".equalsIgnoreCase(rol) || "ADMINISTRADOR".equals(rol);
    }

    private boolean esAdminOEmpleado(Authentication auth) {
        if (auth == null) return false;
        String rol = ((Claims) auth.getPrincipal()).get("rol", String.class);
        return "administrador".equalsIgnoreCase(rol) || "ADMINISTRADOR".equals(rol)
            || "empleado".equalsIgnoreCase(rol) || "EMPLEADO".equals(rol);
    }

    private int getUsuarioId(Authentication auth) {
        Claims claims = (Claims) auth.getPrincipal();
        return Integer.parseInt(claims.getSubject());
    }

    private ResponseEntity<?> error(int status, String message) {
        return ResponseEntity.status(status).body(Map.of("success", false, "message", message));
    }
}
