package com.mudanzas.service;

import com.mudanzas.dao.ClienteDAO;
import com.mudanzas.dao.HistorialDAO;
import com.mudanzas.dao.ServicioDAO;
import com.mudanzas.dao.VehiculoDAO;
import com.mudanzas.model.Servicio;
import com.mudanzas.model.Vehiculo;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Servicio de lógica de negocio para servicios de mudanza.
 * Gestiona validaciones, cálculo de costo y máquina de estados.
 *
 * Transiciones de estado válidas:
 *   pendiente   → confirmado, cancelado
 *   confirmado  → en_proceso, cancelado
 *   en_proceso  → finalizado, cancelado
 *   finalizado  → (ninguna)
 *   cancelado   → (ninguna)
 */
public class ServicioService {

    private final ServicioDAO servicioDAO = new ServicioDAO();
    private final ClienteDAO  clienteDAO  = new ClienteDAO();
    private final VehiculoDAO vehiculoDAO = new VehiculoDAO();
    private final HistorialDAO historialDAO = new HistorialDAO();

    private static final Map<String, List<String>> TRANSICIONES_VALIDAS = Map.of(
        "pendiente",  List.of("confirmado", "cancelado"),
        "confirmado", List.of("en_proceso", "cancelado"),
        "en_proceso", List.of("finalizado", "cancelado"),
        "finalizado", List.of(),
        "cancelado",  List.of()
    );

    public List<Servicio> listarServicios() throws SQLException {
        return servicioDAO.findAll();
    }

    public List<Servicio> listarPorEstado(String estado) throws SQLException {
        return servicioDAO.findByEstado(estado);
    }

    public Servicio obtenerServicio(int id) throws SQLException {
        Servicio s = servicioDAO.findById(id);
        if (s == null) throw new IllegalArgumentException("Servicio con id " + id + " no encontrado.");
        return s;
    }

    public List<Servicio> obtenerPorCliente(int clienteId) throws SQLException {
        if (clienteDAO.findById(clienteId) == null) {
            throw new IllegalArgumentException("Cliente con id " + clienteId + " no encontrado.");
        }
        return servicioDAO.findByClienteId(clienteId);
    }

    /**
     * Crea un nuevo servicio con cálculo automático de costos.
     * Estado inicial: pendiente.
     *
     * @param s         Datos del servicio
     * @param usuarioId ID del usuario que crea el servicio (para historial)
     */
    public Servicio crearServicio(Servicio s, int usuarioId) throws SQLException {
        validarCampos(s);

        // Verificar que el cliente existe
        if (clienteDAO.findById(s.getClienteId()) == null) {
            throw new IllegalArgumentException("Cliente con id " + s.getClienteId() + " no encontrado.");
        }

        // Verificar vehículo si se especificó
        if (s.getVehiculoId() != null) {
            Vehiculo v = vehiculoDAO.findById(s.getVehiculoId());
            if (v == null) {
                throw new IllegalArgumentException("Vehículo con id " + s.getVehiculoId() + " no encontrado.");
            }
            if (!v.isDisponible()) {
                throw new IllegalStateException("El vehículo seleccionado no está disponible.");
            }
        }

        // Calcular costos automáticamente
        Map<String, Double> costos = CostoService.calcularCosto(s.getDistanciaKm(), s.getPesoCargaKg());
        s.setCostoBase(costos.get("costo_base"));
        s.setCostoTotal(costos.get("costo_total"));

        Servicio creado = servicioDAO.create(s);

        // Registrar en historial
        historialDAO.create(creado.getId(), null, "pendiente", usuarioId, "Servicio creado");

        return creado;
    }

    /**
     * Actualiza un servicio. Recalcula costos si cambian distancia o peso.
     * No se puede modificar si está finalizado o cancelado.
     */
    public Servicio actualizarServicio(int id, Servicio s, int usuarioId) throws SQLException {
        Servicio actual = obtenerServicio(id);

        if ("finalizado".equals(actual.getEstado()) || "cancelado".equals(actual.getEstado())) {
            throw new IllegalStateException(
                "No se puede modificar un servicio en estado \"" + actual.getEstado() + "\".");
        }

        // Recalcular costos
        double distancia = s.getDistanciaKm() > 0 ? s.getDistanciaKm() : actual.getDistanciaKm();
        double peso      = s.getPesoCargaKg() > 0 ? s.getPesoCargaKg() : actual.getPesoCargaKg();
        Map<String, Double> costos = CostoService.calcularCosto(distancia, peso);
        s.setCostoBase(costos.get("costo_base"));
        s.setCostoTotal(costos.get("costo_total"));

        return servicioDAO.update(id, s);
    }

    /**
     * Cambia el estado de un servicio con validación de transiciones.
     * Registra el cambio en el historial.
     */
    public Servicio cambiarEstado(int id, String nuevoEstado, int usuarioId, String observacion)
            throws SQLException {
        Servicio servicio = obtenerServicio(id);
        String estadoActual = servicio.getEstado();

        List<String> permitidas = TRANSICIONES_VALIDAS.getOrDefault(estadoActual, List.of());
        if (!permitidas.contains(nuevoEstado)) {
            throw new IllegalStateException(
                "Transición de estado inválida: \"" + estadoActual + "\" → \"" + nuevoEstado + "\". " +
                "Transiciones permitidas: " + permitidas);
        }

        servicioDAO.updateEstado(id, nuevoEstado);

        // Registrar en historial
        historialDAO.create(id, estadoActual, nuevoEstado, usuarioId, observacion);

        return servicioDAO.findById(id);
    }

    /**
     * Elimina un servicio. Solo se puede eliminar si está en estado pendiente o cancelado.
     */
    public void eliminarServicio(int id) throws SQLException {
        Servicio actual = obtenerServicio(id);
        if (!"pendiente".equals(actual.getEstado()) && !"cancelado".equals(actual.getEstado())) {
            throw new IllegalStateException(
                "Solo se pueden eliminar servicios en estado \"pendiente\" o \"cancelado\".");
        }
        servicioDAO.delete(id);
    }

    public List<com.mudanzas.model.HistorialEstado> obtenerHistorial(int id) throws SQLException {
        obtenerServicio(id); // lanza 404 si no existe
        return historialDAO.findByServicioId(id);
    }

    private void validarCampos(Servicio s) {
        if (s.getClienteId() <= 0)
            throw new IllegalArgumentException("cliente_id es obligatorio.");
        if (s.getDireccionOrigen() == null || s.getDireccionOrigen().isBlank())
            throw new IllegalArgumentException("direccion_origen es obligatorio.");
        if (s.getDireccionDestino() == null || s.getDireccionDestino().isBlank())
            throw new IllegalArgumentException("direccion_destino es obligatorio.");
        if (s.getFechaServicio() == null || s.getFechaServicio().isBlank())
            throw new IllegalArgumentException("fecha_servicio es obligatorio.");
        if (s.getDistanciaKm() <= 0)
            throw new IllegalArgumentException("distancia_km debe ser mayor a 0.");
        if (s.getPesoCargaKg() <= 0)
            throw new IllegalArgumentException("peso_carga_kg debe ser mayor a 0.");
    }
}
