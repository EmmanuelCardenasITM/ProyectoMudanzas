package com.mudanzas.service;

import com.mudanzas.dao.ClienteDAO;
import com.mudanzas.model.Cliente;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de lógica de negocio para la gestión de clientes.
 */
public class ClienteService {

    private final ClienteDAO clienteDAO = new ClienteDAO();

    public List<Cliente> listarClientes() throws SQLException {
        return clienteDAO.findAll();
    }

    /**
     * @throws IllegalArgumentException si el cliente no existe (404)
     */
    public Cliente obtenerCliente(int id) throws SQLException {
        Cliente c = clienteDAO.findById(id);
        if (c == null) throw new IllegalArgumentException("Cliente con id " + id + " no encontrado");
        return c;
    }

    /**
     * @throws IllegalArgumentException si faltan campos obligatorios (400)
     */
    public Cliente crearCliente(Cliente cliente) throws SQLException {
        validarCampos(cliente);
        return clienteDAO.create(cliente);
    }

    /**
     * @throws IllegalArgumentException si el cliente no existe o faltan campos
     */
    public Cliente actualizarCliente(int id, Cliente cliente) throws SQLException {
        obtenerCliente(id); // lanza 404 si no existe
        validarCampos(cliente);
        return clienteDAO.update(id, cliente);
    }

    /**
     * @throws IllegalArgumentException si el cliente no existe (404)
     */
    public void eliminarCliente(int id) throws SQLException {
        obtenerCliente(id); // lanza 404 si no existe
        clienteDAO.delete(id);
    }

    private void validarCampos(Cliente c) {
        List<String> faltantes = new ArrayList<>();
        if (c.getNombre() == null || c.getNombre().isBlank())   faltantes.add("nombre");
        if (c.getApellido() == null || c.getApellido().isBlank()) faltantes.add("apellido");
        if (c.getEmail() == null || c.getEmail().isBlank())     faltantes.add("email");
        if (!faltantes.isEmpty()) {
            throw new IllegalArgumentException("Campos obligatorios ausentes: " + String.join(", ", faltantes));
        }
    }
}
