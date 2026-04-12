package com.mudanzas.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * Utilidad para generar y validar tokens JWT.
 */
@Component
public class JwtUtil {

    private static Key secretKey;
    private static long expirationMs;

    @Value("${jwt.secret}")
    public void setSecret(String secret) {
        secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Value("${jwt.expiration.hours}")
    public void setExpirationHours(int hours) {
        expirationMs = (long) hours * 3600 * 1000;
    }

    /**
     * Genera un token JWT con los datos del usuario.
     */
    public static String generarToken(int id, String email, String rol) {
        return Jwts.builder()
                .setSubject(String.valueOf(id))
                .claim("email", email)
                .claim("rol", rol)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secretKey)
                .compact();
    }

    /**
     * Valida y parsea un token JWT.
     *
     * @throws JwtException si el token es inválido o expiró
     */
    public static Claims validarToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
