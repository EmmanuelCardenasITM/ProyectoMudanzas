package com.mudanzas.controller;

import com.mudanzas.model.Cliente;
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
 * GET /api/clientes
 * GET /api/clientes/{id}
 * POST /api/clientes
 * PUT /api/clientes/{id}
 * DELETE /api/clientes/{id}
 */
@RestController
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "Gestión de clientes del sistema de mudanzas")
@SecurityRequirement(name = "bearerAuth")
public class ClienteController {

    private final ClienteService clienteService = new ClienteService();

    @GetMapping
    @Operation(summary = "Listar todos los clientes",
               responses = { @ApiResponse(responseCode = "200", description = "Lista de clientes"),
                             @ApiResponse(responseCode = "401", description = "Autenticación requerida") })
    public ResponseEntity<?> listarClientes() {
        try {
            return ResponseEntity.ok(clienteService.listarClientes());
        } catch (Exception e) {
            return error(500, "Error interno del servidor");
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cliente por ID",
               responses = { @ApiResponse(responseCode = "200", description = "Datos del cliente"),
                             @ApiResponse(responseCode = "404", description = "Cliente no encontrado") })
    public ResponseEntity<?> obtenerCliente(@PathVariable int id) {
        try {
            return ResponseEntity.ok(clienteService.obtenerCliente(id));
        } catch (IllegalArgumentException e) {
            return error(404, e.getMessage());
        } catch (Exception e) {
            return error(500, "Error interno del servidor");
        }
    }

    @PostMapping
    @Operation(summary = "Crear nuevo cliente",
               description = "Solo accesible para Administradores.",
               responses = { @ApiResponse(responseCode = "201", description = "Cliente creado"),
                             @ApiResponse(responseCode = "400", description = "Campos inválidos"),
                             @ApiResponse(responseCode = "403", description = "Acceso denegado") })
    public ResponseEntity<?> crearCliente(@RequestBody Cliente cliente, Authentication auth) {
        if (!esAdmin(auth)) return error(403, "Acceso denegado");
        try {
            return ResponseEntity.status(201).body(clienteService.crearCliente(cliente));
        } catch (IllegalArgumentException e) {
            return error(400, e.getMessage());
        } catch (Exception e) {
            return error(500, "Error interno del servidor");
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cliente",
               description = "Solo accesible para Administradores.",
               responses = { @ApiResponse(responseCode = "200", description = "Cliente actualizado"),
                             @ApiResponse(responseCode = "404", description = "Cliente no encontrado") })
    public ResponseEntity<?> actualizarCliente(@PathVariable int id,
                                               @RequestBody Cliente cliente,
                                               Authentication auth) {
        if (!esAdmin(auth)) return error(403, "Acceso denegado");
        try {
            return ResponseEntity.ok(clienteService.actualizarCliente(id, cliente));
        } catch (IllegalArgumentException e) {
            return error(e.getMessage().contains("no encontrado") ? 404 : 400, e.getMessage());
        } catch (Exception e) {
            return error(500, "Error interno del servidor");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar cliente",
               description = "Solo accesible para Administradores.",
               responses = { @ApiResponse(responseCode = "204", description = "Cliente eliminado"),
                             @ApiResponse(responseCode = "404", description = "Cliente no encontrado") })
    public ResponseEntity<?> eliminarCliente(@PathVariable int id, Authentication auth) {
        if (!esAdmin(auth)) return error(403, "Acceso denegado");
        try {
            clienteService.eliminarCliente(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return error(404, e.getMessage());
        } catch (Exception e) {
            return error(500, "Error interno del servidor");
        }
    }

    private boolean esAdmin(Authentication auth) {
        if (auth == null) return false;
        Claims claims = (Claims) auth.getPrincipal();
        return "ADMINISTRADOR".equals(claims.get("rol", String.class));
    }

    private ResponseEntity<?> error(int status, String message) {
        return ResponseEntity.status(status).body(Map.of("error", Map.of("message", message)));
    }
}
