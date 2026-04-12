package com.mudanzas.filter;

import com.mudanzas.config.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filtro Spring que verifica el token JWT en cada solicitud.
 * Si el token es válido, registra al usuario en el SecurityContext.
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                Claims claims = JwtUtil.validarToken(token);
                String rol = claims.get("rol", String.class);

                UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                        claims,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + rol))
                    );
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                // Token inválido — Spring Security devolverá 401 automáticamente
                SecurityContextHolder.clearContext();
            }
        }

        chain.doFilter(request, response);
    }
}
