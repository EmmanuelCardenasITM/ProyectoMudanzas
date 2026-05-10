package com.mudanzas.controller;

import com.mudanzas.model.Pago;
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
 * Controlador REST para pagos.
 * GET    /api/pagos                          — listar (filtro ?estado_pago=)
 * GET    /api/pagos/{id}                     — detalle
 * GET    /api/pagos/servicio/{servicioId}    — pagos de un servicio con resumen
 * POST   /api/pagos                          — registrar pago
 * PUT    /api/pagos/{id}                     — actualizar pago
 * DELETE /api/pagos/{id}                     — eliminar pago
 */
@RestController
@RequestMapping("/api/pagos")
@Tag(name = "Pagos", description = "Registro y gestión de pagos de servicios")
@SecurityRequirement(name = "bearerAuth")
public class PagoController {

    private final PagoService pagoService = new PagoService();

    @GetMapping
    @Operation(summary = "Listar todos los pagos",
               description = "Solo Administradores. Filtro opcional: ?estado_pago=pendiente|pagado|reembolsado")
    public ResponseEntity<?> listarPagos(@RequestParam(required = false) String estado_pago,
                                         Authentication auth) {
        if (!esAdmin(auth)) return error(403, "Acceso denegado.");
        try {
            return ResponseEntity.ok(Map.of("success", true,
                "data", pagoService.listarPagos(estado_pago)));
        } catch (Exception e) {
            return error(500, "Error interno del servidor.");
        }
    }

    @GetMapping("/servicio/{servicioId}")
    @Operation(summary = "Obtener pagos de un servicio con resumen financiero",
               responses = { @ApiResponse(responseCode = "200", description = "Pagos y resumen"),
                             @ApiResponse(responseCode = "404", description = "Servicio no encontrado") })
    public ResponseEntity<?> obtenerPagosPorServicio(@PathVariable int servicioId) {
        try {
            return ResponseEntity.ok(Map.of("success", true,
                "data", pagoService.obtenerPagosPorServicio(servicioId)));
        } catch (IllegalArgumentException e) {
            return error(404, e.getMessage());
        } catch (Exception e) {
            return error(500, "Error interno del servidor.");
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un pago por ID",
               responses = { @ApiResponse(responseCode = "200", description = "Datos del pago"),
                             @ApiResponse(responseCode = "404", description = "Pago no encontrado") })
    public ResponseEntity<?> obtenerPago(@PathVariable int id) {
        try {
            return ResponseEntity.ok(Map.of("success", true, "data", pagoService.obtenerPago(id)));
        } catch (IllegalArgumentException e) {
            return error(404, e.getMessage());
        } catch (Exception e) {
            return error(500, "Error interno del servidor.");
        }
    }

    @PostMapping
    @Operation(
        summary = "Registrar un nuevo pago",
        description = "Admin o Empleado. Campos requeridos: servicio_id, monto, metodo_pago.",
        responses = { @ApiResponse(responseCode = "201", description = "Pago registrado"),
                      @ApiResponse(responseCode = "404", description = "Servicio no encontrado"),
                      @ApiResponse(responseCode = "409", description = "Servicio cancelado") }
    )
    public ResponseEntity<?> registrarPago(@RequestBody Pago pago, Authentication auth) {
        if (!esAdminOEmpleado(auth)) return error(403, "Acceso denegado.");
        try {
            Pago creado = pagoService.registrarPago(pago);
            return ResponseEntity.status(201).body(Map.of(
                "success", true,
                "message", "Pago registrado exitosamente.",
                "data", creado
            ));
        } catch (IllegalArgumentException e) {
            return error(e.getMessage().contains("no encontrado") ? 404 : 400, e.getMessage());
        } catch (IllegalStateException e) {
            return error(409, e.getMessage());
        } catch (Exception e) {
            return error(500, "Error interno del servidor.");
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un pago",
               description = "Solo Administradores.",
               responses = { @ApiResponse(responseCode = "200", description = "Pago actualizado"),
                             @ApiResponse(responseCode = "404", description = "Pago no encontrado") })
    public ResponseEntity<?> actualizarPago(@PathVariable int id,
                                            @RequestBody Pago pago,
                                            Authentication auth) {
        if (!esAdmin(auth)) return error(403, "Acceso denegado.");
        try {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Pago actualizado.",
                "data", pagoService.actualizarPago(id, pago)
            ));
        } catch (IllegalArgumentException e) {
            return error(404, e.getMessage());
        } catch (Exception e) {
            return error(500, "Error interno del servidor.");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un pago",
               description = "Solo Administradores. No se puede eliminar un pago ya confirmado.",
               responses = { @ApiResponse(responseCode = "200", description = "Pago eliminado"),
                             @ApiResponse(responseCode = "409", description = "Pago ya confirmado") })
    public ResponseEntity<?> eliminarPago(@PathVariable int id, Authentication auth) {
        if (!esAdmin(auth)) return error(403, "Acceso denegado.");
        try {
            pagoService.eliminarPago(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Pago eliminado correctamente."));
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

    private ResponseEntity<?> error(int status, String message) {
        return ResponseEntity.status(status).body(Map.of("success", false, "message", message));
    }
}
