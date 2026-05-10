package com.mudanzas.controller;

import com.mudanzas.model.Usuario;
import com.mudanzas.service.UsuarioService;
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
 * Controlador REST para la gestión de usuarios.
 * GET    /api/usuarios              — listar todos (solo Admin)
 * GET    /api/usuarios/empleados    — listar empleados activos (solo Admin)
 * GET    /api/usuarios/{id}         — detalle (solo Admin)
 * PUT    /api/usuarios/{id}         — actualizar (solo Admin)
 * PUT    /api/usuarios/{id}/password — cambiar contraseña (solo Admin)
 * DELETE /api/usuarios/{id}         — eliminar (solo Admin)
 */
@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Gestión de usuarios del sistema")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    private final UsuarioService usuarioService = new UsuarioService();

    @GetMapping
    @Operation(summary = "Listar todos los usuarios",
               description = "Solo Administradores.")
    public ResponseEntity<?> listarUsuarios(Authentication auth) {
        if (!esAdmin(auth)) return error(403, "Acceso denegado.");
        try {
            return ResponseEntity.ok(Map.of("success", true, "data", usuarioService.listarUsuarios()));
        } catch (Exception e) {
            return error(500, "Error interno del servidor.");
        }
    }

    @GetMapping("/empleados")
    @Operation(summary = "Listar empleados activos",
               description = "Solo Administradores.")
    public ResponseEntity<?> listarEmpleados(Authentication auth) {
        if (!esAdmin(auth)) return error(403, "Acceso denegado.");
        try {
            return ResponseEntity.ok(Map.of("success", true, "data", usuarioService.listarEmpleados()));
        } catch (Exception e) {
            return error(500, "Error interno del servidor.");
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un usuario por ID",
               description = "Solo Administradores.",
               responses = { @ApiResponse(responseCode = "200", description = "Datos del usuario"),
                             @ApiResponse(responseCode = "404", description = "Usuario no encontrado") })
    public ResponseEntity<?> obtenerUsuario(@PathVariable int id, Authentication auth) {
        if (!esAdmin(auth)) return error(403, "Acceso denegado.");
        try {
            return ResponseEntity.ok(Map.of("success", true, "data", usuarioService.obtenerUsuario(id)));
        } catch (IllegalArgumentException e) {
            return error(404, e.getMessage());
        } catch (Exception e) {
            return error(500, "Error interno del servidor.");
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un usuario",
               description = "Solo Administradores.",
               responses = { @ApiResponse(responseCode = "200", description = "Usuario actualizado"),
                             @ApiResponse(responseCode = "404", description = "Usuario no encontrado") })
    public ResponseEntity<?> actualizarUsuario(@PathVariable int id,
                                               @RequestBody Usuario usuario,
                                               Authentication auth) {
        if (!esAdmin(auth)) return error(403, "Acceso denegado.");
        try {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Usuario actualizado.",
                "data", usuarioService.actualizarUsuario(id, usuario)
            ));
        } catch (IllegalArgumentException e) {
            return error(404, e.getMessage());
        } catch (Exception e) {
            return error(500, "Error interno del servidor.");
        }
    }

    @PutMapping("/{id}/password")
    @Operation(summary = "Actualizar contraseña de un usuario",
               description = "Solo Administradores. Mínimo 6 caracteres.",
               responses = { @ApiResponse(responseCode = "200", description = "Contraseña actualizada"),
                             @ApiResponse(responseCode = "400", description = "Contraseña inválida") })
    public ResponseEntity<?> actualizarPassword(@PathVariable int id,
                                                @RequestBody Map<String, String> body,
                                                Authentication auth) {
        if (!esAdmin(auth)) return error(403, "Acceso denegado.");
        try {
            String password = body.get("password");
            usuarioService.actualizarPassword(id, password);
            return ResponseEntity.ok(Map.of("success", true, "message", "Contraseña actualizada correctamente."));
        } catch (IllegalArgumentException e) {
            return error(e.getMessage().contains("no encontrado") ? 404 : 400, e.getMessage());
        } catch (Exception e) {
            return error(500, "Error interno del servidor.");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un usuario",
               description = "Solo Administradores. No puedes eliminarte a ti mismo.",
               responses = { @ApiResponse(responseCode = "200", description = "Usuario eliminado"),
                             @ApiResponse(responseCode = "409", description = "No puedes eliminarte a ti mismo") })
    public ResponseEntity<?> eliminarUsuario(@PathVariable int id, Authentication auth) {
        if (!esAdmin(auth)) return error(403, "Acceso denegado.");
        try {
            int usuarioActualId = getUsuarioId(auth);
            usuarioService.eliminarUsuario(id, usuarioActualId);
            return ResponseEntity.ok(Map.of("success", true, "message", "Usuario eliminado correctamente."));
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

    private int getUsuarioId(Authentication auth) {
        Claims claims = (Claims) auth.getPrincipal();
        return Integer.parseInt(claims.getSubject());
    }

    private ResponseEntity<?> error(int status, String message) {
        return ResponseEntity.status(status).body(Map.of("success", false, "message", message));
    }
}
