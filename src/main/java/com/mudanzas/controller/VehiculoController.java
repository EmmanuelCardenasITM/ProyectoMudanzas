package com.mudanzas.controller;

import com.mudanzas.model.Vehiculo;
import com.mudanzas.service.VehiculoService;
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
 * Controlador REST para la gestión de vehículos.
 * GET    /api/vehiculos              — listar todos
 * GET    /api/vehiculos/disponibles  — listar disponibles
 * GET    /api/vehiculos/{id}         — detalle
 * POST   /api/vehiculos              — crear (solo Admin)
 * PUT    /api/vehiculos/{id}         — actualizar (solo Admin)
 * DELETE /api/vehiculos/{id}         — eliminar (solo Admin)
 */
@RestController
@RequestMapping("/api/vehiculos")
@Tag(name = "Vehículos", description = "Gestión de la flota de vehículos")
@SecurityRequirement(name = "bearerAuth")
public class VehiculoController {

    private final VehiculoService vehiculoService = new VehiculoService();

    @GetMapping
    @Operation(summary = "Listar todos los vehículos")
    public ResponseEntity<?> listarVehiculos() {
        try {
            return ResponseEntity.ok(Map.of("success", true, "data", vehiculoService.listarVehiculos()));
        } catch (Exception e) {
            return error(500, "Error interno del servidor.");
        }
    }

    @GetMapping("/disponibles")
    @Operation(summary = "Listar vehículos disponibles")
    public ResponseEntity<?> listarDisponibles() {
        try {
            return ResponseEntity.ok(Map.of("success", true, "data", vehiculoService.listarDisponibles()));
        } catch (Exception e) {
            return error(500, "Error interno del servidor.");
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un vehículo por ID",
               responses = { @ApiResponse(responseCode = "200", description = "Datos del vehículo"),
                             @ApiResponse(responseCode = "404", description = "Vehículo no encontrado") })
    public ResponseEntity<?> obtenerVehiculo(@PathVariable int id) {
        try {
            return ResponseEntity.ok(Map.of("success", true, "data", vehiculoService.obtenerVehiculo(id)));
        } catch (IllegalArgumentException e) {
            return error(404, e.getMessage());
        } catch (Exception e) {
            return error(500, "Error interno del servidor.");
        }
    }

    @PostMapping
    @Operation(
        summary = "Registrar un nuevo vehículo",
        description = "Solo Administradores. Tipos: camioneta, camion_pequeno, camion_mediano, camion_grande.",
        responses = { @ApiResponse(responseCode = "201", description = "Vehículo registrado"),
                      @ApiResponse(responseCode = "409", description = "Placa ya registrada"),
                      @ApiResponse(responseCode = "403", description = "Acceso denegado") }
    )
    public ResponseEntity<?> crearVehiculo(@RequestBody Vehiculo vehiculo, Authentication auth) {
        if (!esAdmin(auth)) return error(403, "Acceso denegado.");
        try {
            return ResponseEntity.status(201).body(Map.of(
                "success", true,
                "message", "Vehículo registrado.",
                "data", vehiculoService.crearVehiculo(vehiculo)
            ));
        } catch (IllegalStateException e) {
            return error(409, e.getMessage());
        } catch (IllegalArgumentException e) {
            return error(400, e.getMessage());
        } catch (Exception e) {
            return error(500, "Error interno del servidor.");
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un vehículo",
               description = "Solo Administradores.",
               responses = { @ApiResponse(responseCode = "200", description = "Vehículo actualizado"),
                             @ApiResponse(responseCode = "404", description = "Vehículo no encontrado") })
    public ResponseEntity<?> actualizarVehiculo(@PathVariable int id,
                                                @RequestBody Vehiculo vehiculo,
                                                Authentication auth) {
        if (!esAdmin(auth)) return error(403, "Acceso denegado.");
        try {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Vehículo actualizado.",
                "data", vehiculoService.actualizarVehiculo(id, vehiculo)
            ));
        } catch (IllegalArgumentException e) {
            return error(e.getMessage().contains("no encontrado") ? 404 : 400, e.getMessage());
        } catch (Exception e) {
            return error(500, "Error interno del servidor.");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un vehículo",
               description = "Solo Administradores.",
               responses = { @ApiResponse(responseCode = "200", description = "Vehículo eliminado"),
                             @ApiResponse(responseCode = "404", description = "Vehículo no encontrado") })
    public ResponseEntity<?> eliminarVehiculo(@PathVariable int id, Authentication auth) {
        if (!esAdmin(auth)) return error(403, "Acceso denegado.");
        try {
            vehiculoService.eliminarVehiculo(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Vehículo eliminado correctamente."));
        } catch (IllegalArgumentException e) {
            return error(404, e.getMessage());
        } catch (Exception e) {
            return error(500, "Error interno del servidor.");
        }
    }

    private boolean esAdmin(Authentication auth) {
        if (auth == null) return false;
        String rol = ((Claims) auth.getPrincipal()).get("rol", String.class);
        return "administrador".equalsIgnoreCase(rol) || "ADMINISTRADOR".equals(rol);
    }

    private ResponseEntity<?> error(int status, String message) {
        return ResponseEntity.status(status).body(Map.of("success", false, "message", message));
    }
}
