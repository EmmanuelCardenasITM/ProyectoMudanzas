package com.mudanzas.controller;

import com.mudanzas.service.AuthService;
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
 * Controlador de autenticación.
 * POST /api/auth/register — registro de nuevos usuarios
 * POST /api/auth/login    — inicio de sesión
 * GET  /api/auth/me       — datos del usuario autenticado
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Registro e inicio de sesión de usuarios")
public class AuthController {

    private final AuthService authService = new AuthService();

    @PostMapping("/register")
    @Operation(
        summary = "Registrar un nuevo usuario",
        description = "Crea un usuario. Si el rol es 'cliente', también crea el perfil de cliente.",
        responses = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Campos obligatorios ausentes"),
            @ApiResponse(responseCode = "409", description = "El email ya está registrado")
        }
    )
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        try {
            String nombre   = body.get("nombre");
            String apellido = body.get("apellido");
            String email    = body.get("email");
            String password = body.get("password");

            if (nombre == null || nombre.isBlank() || email == null || email.isBlank()
                    || password == null || password.isBlank()) {
                return error(400, "Los campos nombre, email y password son obligatorios.");
            }

            Map<String, Object> usuario = authService.register(
                nombre, apellido, email, password,
                body.get("telefono"), body.get("rol"),
                body.get("direccion"), body.get("ciudad"), body.get("documento")
            );

            return ResponseEntity.status(201).body(Map.of(
                "success", true,
                "message", "Usuario registrado exitosamente.",
                "data", usuario
            ));
        } catch (IllegalStateException e) {
            return error(409, e.getMessage());
        } catch (IllegalArgumentException e) {
            return error(400, e.getMessage());
        } catch (Exception e) {
            return error(500, "Error interno del servidor.");
        }
    }

    @PostMapping("/login")
    @Operation(
        summary = "Iniciar sesión",
        description = "Autentica al usuario y retorna un token JWT.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa"),
            @ApiResponse(responseCode = "400", description = "Campos obligatorios ausentes"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
            @ApiResponse(responseCode = "403", description = "Usuario inactivo")
        }
    )
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        try {
            String email    = body.get("email");
            String password = body.get("password");

            if (email == null || email.isBlank() || password == null || password.isBlank()) {
                return error(400, "Los campos email y password son obligatorios.");
            }

            Map<String, Object> resultado = authService.login(email, password);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Inicio de sesión exitoso.",
                "data", resultado
            ));
        } catch (IllegalArgumentException e) {
            return error(401, e.getMessage());
        } catch (IllegalStateException e) {
            return error(403, e.getMessage());
        } catch (Exception e) {
            return error(500, "Error interno del servidor.");
        }
    }

    @GetMapping("/me")
    @Operation(
        summary = "Obtener datos del usuario autenticado",
        responses = {
            @ApiResponse(responseCode = "200", description = "Datos del usuario"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
        }
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> me(Authentication auth) {
        if (auth == null) return error(401, "No autenticado.");
        Claims claims = (Claims) auth.getPrincipal();
        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", Map.of(
                "id",    Integer.parseInt(claims.getSubject()),
                "email", claims.get("email", String.class),
                "rol",   claims.get("rol", String.class)
            )
        ));
    }

    private ResponseEntity<?> error(int status, String message) {
        return ResponseEntity.status(status).body(Map.of(
            "success", false,
            "message", message
        ));
    }
}
