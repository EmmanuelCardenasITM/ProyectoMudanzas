package com.mudanzas.service;

import com.mudanzas.config.JwtUtil;
import com.mudanzas.dao.ClienteDAO;
import com.mudanzas.dao.UsuarioDAO;
import com.mudanzas.model.Usuario;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Servicio de autenticación y registro de usuarios.
 */
public class AuthService {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();

    /**
     * Registra un nuevo usuario. Si el rol es "cliente", crea también el perfil de cliente.
     *
     * @param nombre    Nombre del usuario
     * @param apellido  Apellido del usuario
     * @param email     Email único
     * @param password  Contraseña en texto plano (se hashea con BCrypt)
     * @param telefono  Teléfono (opcional)
     * @param rol       Rol: administrador, empleado, cliente (default: cliente)
     * @param direccion Dirección (solo para clientes)
     * @param ciudad    Ciudad (solo para clientes)
     * @param documento Documento de identidad (solo para clientes)
     * @return Datos del usuario creado
     */
    public Map<String, Object> register(String nombre, String apellido, String email,
                                        String password, String telefono, String rol,
                                        String direccion, String ciudad, String documento)
            throws SQLException {

        // Verificar email único
        if (usuarioDAO.findByEmail(email) != null) {
            throw new IllegalStateException("El email ya está registrado.");
        }

        // Hashear contraseña
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(10));

        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido != null ? apellido : "");
        usuario.setEmail(email);
        usuario.setPassword(hashedPassword);
        usuario.setTelefono(telefono);
        usuario.setRol(rol != null ? rol : "cliente");

        Usuario creado = usuarioDAO.create(usuario);

        // Si es cliente, crear perfil de cliente
        if ("cliente".equals(creado.getRol())) {
            clienteDAO.create(creado.getId(), direccion, ciudad, documento);
        }

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("id",       creado.getId());
        resultado.put("nombre",   nombre);
        resultado.put("apellido", apellido);
        resultado.put("email",    email);
        resultado.put("rol",      creado.getRol());
        return resultado;
    }

    /**
     * Autentica un usuario y retorna un JWT.
     *
     * @param email    Email del usuario
     * @param password Contraseña en texto plano
     * @return Mapa con token y datos del usuario
     * @throws IllegalArgumentException si las credenciales son inválidas
     */
    public Map<String, Object> login(String email, String password) throws SQLException {
        Usuario usuario = usuarioDAO.findByEmail(email);

        if (usuario == null) {
            throw new IllegalArgumentException("Credenciales inválidas.");
        }

        if (!usuario.isActivo()) {
            throw new IllegalStateException("Usuario inactivo. Contacte al administrador.");
        }

        // Verificar contraseña — soporta tanto BCrypt (nuevo) como texto plano (seed viejo)
        boolean passwordValido;
        String storedPassword = usuario.getPassword();
        if (storedPassword != null && storedPassword.startsWith("$2")) {
            passwordValido = BCrypt.checkpw(password, storedPassword);
        } else {
            // Compatibilidad con datos de seed sin hash
            passwordValido = password.equals(storedPassword);
        }

        if (!passwordValido) {
            throw new IllegalArgumentException("Credenciales inválidas.");
        }

        String token = JwtUtil.generarToken(usuario.getId(), usuario.getEmail(), usuario.getRol());

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("token", token);

        Map<String, Object> usuarioData = new HashMap<>();
        usuarioData.put("id",       usuario.getId());
        usuarioData.put("nombre",   usuario.getNombre());
        usuarioData.put("apellido", usuario.getApellido());
        usuarioData.put("email",    usuario.getEmail());
        usuarioData.put("rol",      usuario.getRol());
        resultado.put("usuario", usuarioData);

        return resultado;
    }
}
