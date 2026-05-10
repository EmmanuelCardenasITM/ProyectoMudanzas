package com.mudanzas.service;

import com.mudanzas.dao.VehiculoDAO;
import com.mudanzas.model.Vehiculo;

import java.sql.SQLException;
import java.util.List;

/**
 * Servicio de lógica de negocio para la gestión de vehículos.
 * Tipos válidos: camioneta, camion_pequeno, camion_mediano, camion_grande
 */
public class VehiculoService {

    private final VehiculoDAO vehiculoDAO = new VehiculoDAO();

    private static final List<String> TIPOS_VALIDOS = List.of(
        "camioneta", "camion_pequeno", "camion_mediano", "camion_grande"
    );

    public List<Vehiculo> listarVehiculos() throws SQLException {
        return vehiculoDAO.findAll();
    }

    public List<Vehiculo> listarDisponibles() throws SQLException {
        return vehiculoDAO.findDisponibles();
    }

    public Vehiculo obtenerVehiculo(int id) throws SQLException {
        Vehiculo v = vehiculoDAO.findById(id);
        if (v == null) throw new IllegalArgumentException("Vehículo con id " + id + " no encontrado.");
        return v;
    }

    public Vehiculo crearVehiculo(Vehiculo v) throws SQLException {
        validarCampos(v);

        // Verificar placa única
        if (vehiculoDAO.findByPlaca(v.getPlaca()) != null) {
            throw new IllegalStateException("Ya existe un vehículo con la placa \"" + v.getPlaca() + "\".");
        }

        return vehiculoDAO.create(v);
    }

    public Vehiculo actualizarVehiculo(int id, Vehiculo datos) throws SQLException {
        Vehiculo actual = obtenerVehiculo(id);

        // Merge con valores actuales
        if (datos.getPlaca() == null || datos.getPlaca().isBlank())
            datos.setPlaca(actual.getPlaca());
        if (datos.getTipo() == null || datos.getTipo().isBlank())
            datos.setTipo(actual.getTipo());
        if (datos.getCapacidadKg() <= 0)
            datos.setCapacidadKg(actual.getCapacidadKg());
        // disponible se toma del body; si no viene, mantiene el actual
        // (el controller debe setear explícitamente)

        validarCampos(datos);
        return vehiculoDAO.update(id, datos);
    }

    public void eliminarVehiculo(int id) throws SQLException {
        obtenerVehiculo(id); // lanza 404 si no existe
        vehiculoDAO.delete(id);
    }

    private void validarCampos(Vehiculo v) {
        if (v.getPlaca() == null || v.getPlaca().isBlank())
            throw new IllegalArgumentException("placa es obligatorio.");
        if (v.getTipo() == null || !TIPOS_VALIDOS.contains(v.getTipo()))
            throw new IllegalArgumentException(
                "tipo inválido. Valores permitidos: " + TIPOS_VALIDOS);
        if (v.getCapacidadKg() <= 0)
            throw new IllegalArgumentException("capacidad_kg debe ser mayor a 0.");
    }
}
