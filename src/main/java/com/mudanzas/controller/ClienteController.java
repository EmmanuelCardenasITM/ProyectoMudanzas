package com.mudanzas.controller;

import com.mudanzas.service.ClienteService;
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
 * Controlador REST para la gestión de clientes.
 * GET    /api/clientes
 * GET    /api/clientes/{id}
 * GET    /api/clientes/{id}/historial
 * POST   /api/clientes
 * PUT    /api/clientes/{id}
 * DELETE /api/clientes/{id}
 */
@RestController
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "Registro y gestión de clientes")
@SecurityRequirement(name = "bearerAuth")
public class ClienteController {

    private final ClienteService clienteService = new ClienteService();

    @GetMapping
    @Operation(summary = "Listar todos los clientes",
               responses = { @ApiResponse(responseCode = "200", description = "Lista de clientes") })
    public ResponseEntity<?> listarClientes() {
        try {
            return ResponseEntity.ok(Map.of("success", true, "data", clienteService.listarClientes()));
        } catch (Exception e) {
            return error(500, "Error interno del servidor.");
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cliente por ID",
               responses = { @ApiResponse(responseCode = "200", description = "Datos del cliente"),
                             @ApiResponse(responseCode = "404", description = "Cliente no encontrado") })
    public ResponseEntity<?> obtenerCliente(@PathVariable int id) {
        try {
            return ResponseEntity.ok(Map.of("success", true, "data", clienteService.obtenerCliente(id)));
        } catch (IllegalArgumentException e) {
            return error(404, e.getMessage());
        } catch (Exception e) {
            return error(500, "Error interno del servidor.");
        }
    }

    @GetMapping("/{id}/historial")
    @Operation(summary = "Obtener historial de servicios de un cliente",
               responses = { @ApiResponse(responseCode = "200", description = "Historial de servicios"),
                             @ApiResponse(responseCode = "404", description = "Cliente no encontrado") })
    public ResponseEntity<?> obtenerHistorial(@PathVariable int id) {
        try {
            return ResponseEntity.ok(Map.of("success", true, "data", clienteService.obtenerHistorial(id)));
        } catch (IllegalArgumentException e) {
            return error(404, e.getMessage());
        } catch (Exception e) {
            return error(500, "Error interno del servidor.");
        }
    }

    @PostMapping
    @Operation(summary = "Registrar un nuevo cliente",
               description = "Solo Administradores. Crea usuario + perfil de cliente.",
               responses = { @ApiResponse(responseCode = "201", description = "Cliente registrado"),
                             @ApiResponse(responseCode = "409", description = "Email ya registrado"),
                             @ApiResponse(responseCode = "403", description = "Acceso denegado") })
    public ResponseEntity<?> crearCliente(@RequestBody Map<String, String> body, Authentication auth) {
        if (!esAdminOEmpleado(auth)) return error(403, "Acceso denegado.");
        try {
            return ResponseEntity.status(201).body(Map.of(
                "success", true,
                "message", "Cliente registrado exitosamente.",
                "data", clienteService.crearCliente(body)
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
    @Operation(summary = "Actualizar datos de un cliente",
               description = "Solo Administradores.",
               responses = { @ApiResponse(responseCode = "200", description = "Cliente actualizado"),
                             @ApiResponse(responseCode = "404", description = "Cliente no encontrado") })
    public ResponseEntity<?> actualizarCliente(@PathVariable int id,
                                               @RequestBody Map<String, String> body,
                                               Authentication auth) {
        if (!esAdmin(auth)) return error(403, "Acceso denegado.");
        try {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Cliente actualizado.",
                "data", clienteService.actualizarCliente(id, body)
            ));
        } catch (IllegalArgumentException e) {
            return error(404, e.getMessage());
        } catch (Exception e) {
            return error(500, "Error interno del servidor.");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un cliente",
               description = "Solo Administradores.",
               responses = { @ApiResponse(responseCode = "200", description = "Cliente eliminado"),
                             @ApiResponse(responseCode = "404", description = "Cliente no encontrado") })
    public ResponseEntity<?> eliminarCliente(@PathVariable int id, Authentication auth) {
        if (!esAdmin(auth)) return error(403, "Acceso denegado.");
        try {
            clienteService.eliminarCliente(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Cliente eliminado correctamente."));
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
