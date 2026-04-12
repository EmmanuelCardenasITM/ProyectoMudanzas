package com.mudanzas.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Gestión manual de conexiones a la base de datos.
 * Usa el DataSource configurado en application.properties
 * pero entrega conexiones individuales que el llamador debe cerrar.
 *
 * Patrón: cada método DAO abre su conexión con getConnection()
 * y la cierra con try-with-resources.
 */
@Component
public class DatabaseConnection {

    private static DataSource dataSource;

    @Autowired
    public DatabaseConnection(DataSource dataSource) {
        DatabaseConnection.dataSource = dataSource;
    }

    /**
     * Abre y retorna una nueva conexión a la base de datos.
     * El llamador es responsable de cerrarla (try-with-resources).
     *
     * @return Connection activa
     * @throws SQLException si no se puede establecer la conexión
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
