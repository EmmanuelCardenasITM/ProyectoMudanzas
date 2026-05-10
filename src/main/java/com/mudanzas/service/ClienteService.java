package com.mudanzas.service;

import com.mudanzas.dao.ClienteDAO;
import com.mudanzas.dao.UsuarioDAO;
import com.mudanzas.model.Cliente;
import com.mudanzas.model.Servicio;
import com.mudanzas.model.Usuario;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Servicio de lógica de negocio para la gestión de clientes.
 * Un cliente es un usuario con rol "cliente" + perfil en la tabla clientes.
 */
public class ClienteService {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public List<Cliente> listarClientes() throws SQLException {
        return clienteDAO.findAll();
    }

    /**
     * @throws IllegalArgumentException si el cliente no existe (404)
     */
    public Cliente obtenerCliente(int id) throws SQLException {
        Cliente c = clienteDAO.findById(id);
        if (c == null) throw new IllegalArgumentException("Cliente con id " + id + " no encontrado.");
        return c;
    }

    /**
     * Crea un usuario con rol "cliente" y su perfil de cliente.
     * Requiere: nombre, apellido, email. Password opcional (default: Mudanza123!).
     */
    public Cliente crearCliente(Map<String, String> datos) throws SQLException {
        String nombre    = datos.get("nombre");
        String apellido  = datos.get("apellido");
        String email     = datos.get("email");
        String password  = datos.getOrDefault("password", "Mudanza123!");
        String telefono  = datos.get("telefono");
        String direccion = datos.get("direccion");
        String ciudad    = datos.get("ciudad");
        String documento = datos.get("documento");

        if (nombre == null || nombre.isBlank())   throw new IllegalArgumentException("nombre es obligatorio.");
        if (apellido == null || apellido.isBlank()) throw new IllegalArgumentException("apellido es obligatorio.");
        if (email == null || email.isBlank())     throw new IllegalArgumentException("email es obligatorio.");

        // Verificar email único
        if (usuarioDAO.findByEmail(email) != null) {
            throw new IllegalStateException("El email ya está registrado.");
        }

        // Crear usuario
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt(10));
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setEmail(email);
        usuario.setPassword(hashed);
        usuario.setTelefono(telefono);
        usuario.setRol("cliente");
        Usuario creado = usuarioDAO.create(usuario);

        // Crear perfil de cliente
        int clienteId = clienteDAO.create(creado.getId(), direccion, ciudad, documento);
        return clienteDAO.findById(clienteId);
    }

    /**
     * Actualiza datos del usuario y del perfil de cliente.
     */
    public Cliente actualizarCliente(int id, Map<String, String> datos) throws SQLException {
        Cliente cliente = obtenerCliente(id);

        String nombre    = datos.getOrDefault("nombre",    cliente.getNombre());
        String apellido  = datos.getOrDefault("apellido",  cliente.getApellido());
        String telefono  = datos.getOrDefault("telefono",  cliente.getTelefono());
        String direccion = datos.getOrDefault("direccion", cliente.getDireccion());
        String ciudad    = datos.getOrDefault("ciudad",    cliente.getCiudad());
        String documento = datos.getOrDefault("documento", cliente.getDocumento());

        // Actualizar usuario asociado
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setApellido(apellido);
        u.setEmail(cliente.getEmail());
        u.setTelefono(telefono);
        u.setRol("cliente");
        u.setActivo(cliente.isActivo());
        usuarioDAO.update(cliente.getUsuarioId(), u);

        // Actualizar perfil de cliente
        clienteDAO.update(id, direccion, ciudad, documento);

        return clienteDAO.findById(id);
    }

    /**
     * Elimina el usuario (cascade elimina el cliente).
     */
    public void eliminarCliente(int id) throws SQLException {
        Cliente cliente = obtenerCliente(id);
        usuarioDAO.delete(cliente.getUsuarioId());
    }

    public List<Servicio> obtenerHistorial(int id) throws SQLException {
        obtenerCliente(id); // lanza 404 si no existe
        return clienteDAO.findHistorialServicios(id);
    }
}
