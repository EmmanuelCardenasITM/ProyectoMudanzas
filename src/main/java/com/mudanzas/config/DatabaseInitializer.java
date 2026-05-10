package com.mudanzas.config;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Inicialización automática de la base de datos al arrancar la aplicación.
 * Equivalente al initDb.js del backend Node.js.
 *
 * Estrategia:
 *  1. Conecta a "master" para crear la BD "mudanzas_db" si no existe.
 *  2. Conecta a "mudanzas_db" y crea las tablas si no existen.
 *  3. Inserta datos iniciales (admin con BCrypt, etc.).
 * 
 * DESHABILITADO TEMPORALMENTE - usar script manual
 */
// @Component  // Comentado para deshabilitar
public class DatabaseInitializer implements ApplicationRunner {

    private final DataSource dataSource;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    public DatabaseInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String dbName = extraerNombreBD(datasourceUrl);
        if (dbName == null) dbName = "mudanzas_db";

        crearBaseDeDatos(dbName);
        crearTablas();
        insertarDatosIniciales();
        System.out.println("[DB] Base de datos \"" + dbName + "\" lista. Tablas verificadas/creadas correctamente.");
    }

    // ── Paso 1: crear la BD conectando a master ──────────────────────────────
    private void crearBaseDeDatos(String dbName) {
        String host = extraerHost(datasourceUrl);

        SQLServerDataSource masterDs = new SQLServerDataSource();
        masterDs.setServerName(host);
        masterDs.setDatabaseName("master");
        masterDs.setUser(dbUser);
        masterDs.setPassword(dbPassword);
        masterDs.setEncrypt(false);
        masterDs.setTrustServerCertificate(true);

        try (Connection conn = masterDs.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(
                "IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = '" + dbName + "') " +
                "  CREATE DATABASE [" + dbName + "]"
            );
            System.out.println("[DB] Base de datos \"" + dbName + "\" verificada/creada.");

        } catch (SQLException e) {
            throw new RuntimeException(
                "[DB] No se pudo crear la base de datos. " +
                "Verificá usuario/contraseña en application.properties. Error: " + e.getMessage(), e);
        }
    }

    // ── Paso 2: crear tablas (mismo schema que initDb.js) ────────────────────
    private void crearTablas() throws SQLException {
        String[] scripts = {

            // tarifas
            "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='tarifas' AND xtype='U') " +
            "CREATE TABLE tarifas ( " +
            "  id                      INT IDENTITY(1,1) PRIMARY KEY, " +
            "  tarifa_por_km           DECIMAL(10,2) NOT NULL DEFAULT 0, " +
            "  tarifa_por_unidad_carga DECIMAL(10,2) NOT NULL DEFAULT 0, " +
            "  created_at              DATETIME2     NOT NULL DEFAULT GETDATE(), " +
            "  updated_at              DATETIME2     NOT NULL DEFAULT GETDATE() " +
            ")",

            // usuarios
            "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='usuarios' AND xtype='U') " +
            "CREATE TABLE usuarios ( " +
            "  id         INT IDENTITY(1,1) PRIMARY KEY, " +
            "  nombre     NVARCHAR(100) NOT NULL, " +
            "  apellido   NVARCHAR(100) NOT NULL, " +
            "  email      NVARCHAR(150) NOT NULL UNIQUE, " +
            "  password   NVARCHAR(255) NOT NULL, " +
            "  telefono   NVARCHAR(20)  NULL, " +
            "  rol        NVARCHAR(20)  NOT NULL DEFAULT 'cliente' " +
            "                 CHECK (rol IN ('administrador','empleado','cliente')), " +
            "  activo     BIT           NOT NULL DEFAULT 1, " +
            "  created_at DATETIME      NOT NULL DEFAULT GETDATE(), " +
            "  updated_at DATETIME      NOT NULL DEFAULT GETDATE() " +
            ")",

            // clientes
            "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='clientes' AND xtype='U') " +
            "CREATE TABLE clientes ( " +
            "  id         INT IDENTITY(1,1) PRIMARY KEY, " +
            "  usuario_id INT           NOT NULL, " +
            "  direccion  NVARCHAR(255) NULL, " +
            "  ciudad     NVARCHAR(100) NULL, " +
            "  documento  NVARCHAR(30)  NULL UNIQUE, " +
            "  created_at DATETIME      NOT NULL DEFAULT GETDATE(), " +
            "  updated_at DATETIME      NOT NULL DEFAULT GETDATE(), " +
            "  CONSTRAINT fk_cliente_usuario FOREIGN KEY (usuario_id) " +
            "    REFERENCES usuarios(id) ON DELETE CASCADE " +
            ")",

            // vehiculos
            "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='vehiculos' AND xtype='U') " +
            "CREATE TABLE vehiculos ( " +
            "  id           INT IDENTITY(1,1) PRIMARY KEY, " +
            "  placa        NVARCHAR(20)  NOT NULL UNIQUE, " +
            "  tipo         NVARCHAR(20)  NOT NULL " +
            "                   CHECK (tipo IN ('camioneta','camion_pequeno','camion_mediano','camion_grande')), " +
            "  capacidad_kg DECIMAL(10,2) NOT NULL, " +
            "  disponible   BIT           NOT NULL DEFAULT 1, " +
            "  created_at   DATETIME      NOT NULL DEFAULT GETDATE(), " +
            "  updated_at   DATETIME      NOT NULL DEFAULT GETDATE() " +
            ")",

            // servicios_mudanza
            "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='servicios_mudanza' AND xtype='U') " +
            "CREATE TABLE servicios_mudanza ( " +
            "  id                INT IDENTITY(1,1) PRIMARY KEY, " +
            "  cliente_id        INT           NOT NULL, " +
            "  vehiculo_id       INT           NULL, " +
            "  empleado_id       INT           NULL, " +
            "  fecha_servicio    DATE          NOT NULL, " +
            "  hora_servicio     NVARCHAR(10)  NOT NULL, " +
            "  direccion_origen  NVARCHAR(255) NOT NULL, " +
            "  ciudad_origen     NVARCHAR(100) NOT NULL, " +
            "  direccion_destino NVARCHAR(255) NOT NULL, " +
            "  ciudad_destino    NVARCHAR(100) NOT NULL, " +
            "  distancia_km      DECIMAL(10,2) NOT NULL DEFAULT 0, " +
            "  peso_carga_kg     DECIMAL(10,2) NOT NULL DEFAULT 0, " +
            "  descripcion_carga NVARCHAR(MAX) NULL, " +
            "  costo_base        DECIMAL(10,2) NOT NULL DEFAULT 0, " +
            "  costo_total       DECIMAL(10,2) NOT NULL DEFAULT 0, " +
            "  estado            NVARCHAR(20)  NOT NULL DEFAULT 'pendiente' " +
            "                        CHECK (estado IN ('pendiente','confirmado','en_proceso','finalizado','cancelado')), " +
            "  notas             NVARCHAR(MAX) NULL, " +
            "  created_at        DATETIME      NOT NULL DEFAULT GETDATE(), " +
            "  updated_at        DATETIME      NOT NULL DEFAULT GETDATE(), " +
            "  CONSTRAINT fk_servicio_cliente  FOREIGN KEY (cliente_id)  REFERENCES clientes(id), " +
            "  CONSTRAINT fk_servicio_vehiculo FOREIGN KEY (vehiculo_id) REFERENCES vehiculos(id), " +
            "  CONSTRAINT fk_servicio_empleado FOREIGN KEY (empleado_id) REFERENCES usuarios(id) " +
            ")",

            // pagos
            "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='pagos' AND xtype='U') " +
            "CREATE TABLE pagos ( " +
            "  id          INT IDENTITY(1,1) PRIMARY KEY, " +
            "  servicio_id INT           NOT NULL, " +
            "  monto       DECIMAL(10,2) NOT NULL, " +
            "  metodo_pago NVARCHAR(20)  NOT NULL " +
            "                  CHECK (metodo_pago IN ('efectivo','transferencia','tarjeta')), " +
            "  estado_pago NVARCHAR(20)  NOT NULL DEFAULT 'pendiente' " +
            "                  CHECK (estado_pago IN ('pendiente','pagado','reembolsado')), " +
            "  fecha_pago  DATETIME      NULL, " +
            "  referencia  NVARCHAR(100) NULL, " +
            "  notas       NVARCHAR(MAX) NULL, " +
            "  created_at  DATETIME      NOT NULL DEFAULT GETDATE(), " +
            "  updated_at  DATETIME      NOT NULL DEFAULT GETDATE(), " +
            "  CONSTRAINT fk_pago_servicio FOREIGN KEY (servicio_id) " +
            "    REFERENCES servicios_mudanza(id) ON DELETE CASCADE " +
            ")",

            // historial_estados
            "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='historial_estados' AND xtype='U') " +
            "CREATE TABLE historial_estados ( " +
            "  id              INT IDENTITY(1,1) PRIMARY KEY, " +
            "  servicio_id     INT           NOT NULL, " +
            "  estado_anterior NVARCHAR(20)  NULL, " +
            "  estado_nuevo    NVARCHAR(20)  NOT NULL, " +
            "  usuario_id      INT           NOT NULL, " +
            "  observacion     NVARCHAR(MAX) NULL, " +
            "  created_at      DATETIME      NOT NULL DEFAULT GETDATE(), " +
            "  CONSTRAINT fk_historial_servicio FOREIGN KEY (servicio_id) " +
            "    REFERENCES servicios_mudanza(id) ON DELETE CASCADE, " +
            "  CONSTRAINT fk_historial_usuario FOREIGN KEY (usuario_id) " +
            "    REFERENCES usuarios(id) " +
            ")",
        };

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            for (String sql : scripts) {
                stmt.execute(sql);
            }
        }
    }

    // ── Paso 3: datos iniciales (igual que initDb.js) ────────────────────────
    private void insertarDatosIniciales() throws SQLException {
        String[] seeds = {
            // Admin con hash BCrypt de "Admin123!" — igual que el Node.js
            "IF NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'admin@mudanzas.com') " +
            "  INSERT INTO usuarios (nombre, apellido, email, password, telefono, rol) " +
            "  VALUES ('Admin', 'Sistema', 'admin@mudanzas.com', " +
            "          'Admin123!', " +
            "          '3001234567', 'administrador')",

            // Empleado de ejemplo
            "IF NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'empleado@mudanzas.com') " +
            "  INSERT INTO usuarios (nombre, apellido, email, password, telefono, rol) " +
            "  VALUES ('Juan', 'Pérez', 'empleado@mudanzas.com', " +
            "          'empleado123', " +
            "          '3009876543', 'empleado')",

            // Datos iniciales: tarifas base
            "IF NOT EXISTS (SELECT 1 FROM tarifas) " +
            "  INSERT INTO tarifas (tarifa_por_km, tarifa_por_unidad_carga) " +
            "  VALUES (2500.00, 1000.00)",

            // Vehículos de ejemplo
            "IF NOT EXISTS (SELECT 1 FROM vehiculos WHERE placa = 'ABC123') " +
            "  INSERT INTO vehiculos (placa, tipo, capacidad_kg, disponible) " +
            "  VALUES ('ABC123', 'camioneta', 1000.00, 1)",

            "IF NOT EXISTS (SELECT 1 FROM vehiculos WHERE placa = 'DEF456') " +
            "  INSERT INTO vehiculos (placa, tipo, capacidad_kg, disponible) " +
            "  VALUES ('DEF456', 'camion_pequeno', 3000.00, 1)",

            "IF NOT EXISTS (SELECT 1 FROM vehiculos WHERE placa = 'GHI789') " +
            "  INSERT INTO vehiculos (placa, tipo, capacidad_kg, disponible) " +
            "  VALUES ('GHI789', 'camion_mediano', 5000.00, 1)",
        };

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            for (String sql : seeds) {
                stmt.execute(sql);
            }
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────
    private String extraerNombreBD(String url) {
        if (url == null) return null;
        Matcher m = Pattern.compile("(?i)databaseName=([^;]+)").matcher(url);
        return m.find() ? m.group(1).trim() : null;
    }

    private String extraerHost(String url) {
        if (url == null) return "localhost";
        Matcher m = Pattern.compile("(?i)sqlserver://([^;:/]+)").matcher(url);
        return m.find() ? m.group(1).trim() : "localhost";
    }
}
