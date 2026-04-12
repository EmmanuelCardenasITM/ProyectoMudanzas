package com.mudanzas.controller;

import com.mudanzas.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador de autenticación.
 * POST /api/auth/login — no requiere JWT.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Endpoints de autenticación")
public class AuthController {

    private final AuthService authService = new AuthService();

    @PostMapping("/login")
    @Operation(
        summary = "Iniciar sesión",
        description = "Autentica al usuario y retorna un token JWT con vigencia de 8 horas.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa"),
            @ApiResponse(responseCode = "400", description = "Campos obligatorios ausentes"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
        }
    )
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        try {
            String email    = body.get("email");
            String password = body.get("password");

            if (email == null || email.isBlank() || password == null || password.isBlank()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", Map.of("message", "Los campos email y password son obligatorios")));
            }

            return ResponseEntity.ok(authService.login(email, password));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401)
                .body(Map.of("error", Map.of("message", e.getMessage())));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", Map.of("message", "Error interno del servidor")));
        }
    }
}
