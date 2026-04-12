package com.mudanzas.service;

import com.mudanzas.config.JwtUtil;
import com.mudanzas.dao.UsuarioDAO;
import com.mudanzas.model.Usuario;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Servicio de autenticación.
 * Verifica credenciales y genera tokens JWT.
 */
public class AuthService {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    /**
     * Autentica al usuario y retorna un token JWT.
     *
     * @param email    Email del usuario
     * @param password Contraseña en texto plano
     * @return Mapa con token y datos del usuario
     * @throws IllegalArgumentException si las credenciales son inválidas
     * @throws SQLException             si ocurre un error de base de datos
     */
    public Map<String, Object> login(String email, String password) throws SQLException {
        Usuario usuario = usuarioDAO.findByEmail(email);
        if (usuario == null || !password.equals(usuario.getPasswordHash())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        String token = JwtUtil.generarToken(usuario.getId(), usuario.getEmail(), usuario.getRol());

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("token", token);

        Map<String, Object> usuarioData = new HashMap<>();
        usuarioData.put("id",     usuario.getId());
        usuarioData.put("nombre", usuario.getNombre());
        usuarioData.put("email",  usuario.getEmail());
        usuarioData.put("rol",    usuario.getRol());
        resultado.put("usuario", usuarioData);

        return resultado;
    }
}
