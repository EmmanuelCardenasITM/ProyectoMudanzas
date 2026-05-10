-- ============================================================
-- SISTEMA DE GESTIÓN DE SERVICIOS DE MUDANZA
-- Script de creación de base de datos - SQL Server
-- ============================================================

IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'sistema_mudanzas')
BEGIN
    CREATE DATABASE sistema_mudanzas;
END
GO

USE sistema_mudanzas;
GO

-- ------------------------------------------------------------
-- Tabla: usuarios (Administrador, Empleado, Cliente)
-- ------------------------------------------------------------
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='usuarios' AND xtype='U')
BEGIN
    CREATE TABLE usuarios (
        id          INT IDENTITY(1,1) PRIMARY KEY,
        nombre      NVARCHAR(100)  NOT NULL,
        apellido    NVARCHAR(100)  NOT NULL DEFAULT '',
        email       NVARCHAR(150)  NOT NULL UNIQUE,
        password    NVARCHAR(255)  NOT NULL,
        telefono    NVARCHAR(20)   NULL,
        rol         NVARCHAR(20)   NOT NULL DEFAULT 'cliente'
                        CHECK (rol IN ('administrador','empleado','cliente',
                                       'ADMINISTRADOR','EMPLEADO')),
        activo      BIT            NOT NULL DEFAULT 1,
        created_at  DATETIME       NOT NULL DEFAULT GETDATE(),
        updated_at  DATETIME       NOT NULL DEFAULT GETDATE()
    );
END
GO

-- ------------------------------------------------------------
-- Tabla: clientes
-- ------------------------------------------------------------
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='clientes' AND xtype='U')
BEGIN
    CREATE TABLE clientes (
        id          INT IDENTITY(1,1) PRIMARY KEY,
        usuario_id  INT            NOT NULL,
        direccion   NVARCHAR(255)  NULL,
        ciudad      NVARCHAR(100)  NULL,
        documento   NVARCHAR(30)   NULL UNIQUE,
        created_at  DATETIME       NOT NULL DEFAULT GETDATE(),
        updated_at  DATETIME       NOT NULL DEFAULT GETDATE(),
        CONSTRAINT fk_cliente_usuario FOREIGN KEY (usuario_id)
            REFERENCES usuarios(id) ON DELETE CASCADE
    );
END
GO

-- ------------------------------------------------------------
-- Tabla: vehiculos
-- ------------------------------------------------------------
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='vehiculos' AND xtype='U')
BEGIN
    CREATE TABLE vehiculos (
        id            INT IDENTITY(1,1) PRIMARY KEY,
        placa         NVARCHAR(20)   NOT NULL UNIQUE,
        tipo          NVARCHAR(20)   NOT NULL
                          CHECK (tipo IN ('camioneta','camion_pequeno','camion_mediano','camion_grande')),
        capacidad_kg  DECIMAL(10,2)  NOT NULL,
        disponible    BIT            NOT NULL DEFAULT 1,
        created_at    DATETIME       NOT NULL DEFAULT GETDATE(),
        updated_at    DATETIME       NOT NULL DEFAULT GETDATE()
    );
END
GO

-- ------------------------------------------------------------
-- Tabla: servicios_mudanza
-- ------------------------------------------------------------
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='servicios_mudanza' AND xtype='U')
BEGIN
    CREATE TABLE servicios_mudanza (
        id                  INT IDENTITY(1,1) PRIMARY KEY,
        cliente_id          INT            NOT NULL,
        vehiculo_id         INT            NULL,
        empleado_id         INT            NULL,
        fecha_servicio      DATE           NOT NULL,
        hora_servicio       NVARCHAR(10)   NOT NULL DEFAULT '08:00',
        direccion_origen    NVARCHAR(255)  NOT NULL,
        ciudad_origen       NVARCHAR(100)  NOT NULL DEFAULT '',
        direccion_destino   NVARCHAR(255)  NOT NULL,
        ciudad_destino      NVARCHAR(100)  NOT NULL DEFAULT '',
        distancia_km        DECIMAL(10,2)  NOT NULL DEFAULT 0,
        peso_carga_kg       DECIMAL(10,2)  NOT NULL DEFAULT 0,
        descripcion_carga   NVARCHAR(MAX)  NULL,
        costo_base          DECIMAL(10,2)  NOT NULL DEFAULT 0,
        costo_total         DECIMAL(10,2)  NOT NULL DEFAULT 0,
        estado              NVARCHAR(20)   NOT NULL DEFAULT 'pendiente'
                                CHECK (estado IN ('pendiente','confirmado','en_proceso','finalizado','cancelado',
                                                  'PENDIENTE','EN_PROCESO','FINALIZADO')),
        notas               NVARCHAR(MAX)  NULL,
        created_at          DATETIME       NOT NULL DEFAULT GETDATE(),
        updated_at          DATETIME       NOT NULL DEFAULT GETDATE(),
        CONSTRAINT fk_servicio_cliente  FOREIGN KEY (cliente_id)  REFERENCES clientes(id),
        CONSTRAINT fk_servicio_vehiculo FOREIGN KEY (vehiculo_id) REFERENCES vehiculos(id),
        CONSTRAINT fk_servicio_empleado FOREIGN KEY (empleado_id) REFERENCES usuarios(id)
    );
END
GO

-- ------------------------------------------------------------
-- Tabla: pagos
-- ------------------------------------------------------------
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='pagos' AND xtype='U')
BEGIN
    CREATE TABLE pagos (
        id              INT IDENTITY(1,1) PRIMARY KEY,
        servicio_id     INT            NOT NULL,
        monto           DECIMAL(10,2)  NOT NULL,
        metodo_pago     NVARCHAR(20)   NOT NULL
                            CHECK (metodo_pago IN ('efectivo','transferencia','tarjeta')),
        estado_pago     NVARCHAR(20)   NOT NULL DEFAULT 'pendiente'
                            CHECK (estado_pago IN ('pendiente','pagado','reembolsado')),
        fecha_pago      DATETIME       NULL,
        referencia      NVARCHAR(100)  NULL,
        notas           NVARCHAR(MAX)  NULL,
        created_at      DATETIME       NOT NULL DEFAULT GETDATE(),
        updated_at      DATETIME       NOT NULL DEFAULT GETDATE(),
        CONSTRAINT fk_pago_servicio FOREIGN KEY (servicio_id)
            REFERENCES servicios_mudanza(id) ON DELETE CASCADE
    );
END
GO

-- ------------------------------------------------------------
-- Tabla: historial_estados
-- ------------------------------------------------------------
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='historial_estados' AND xtype='U')
BEGIN
    CREATE TABLE historial_estados (
        id              INT IDENTITY(1,1) PRIMARY KEY,
        servicio_id     INT            NOT NULL,
        estado_anterior NVARCHAR(20)   NULL,
        estado_nuevo    NVARCHAR(20)   NOT NULL,
        usuario_id      INT            NOT NULL,
        observacion     NVARCHAR(MAX)  NULL,
        created_at      DATETIME       NOT NULL DEFAULT GETDATE(),
        CONSTRAINT fk_historial_servicio FOREIGN KEY (servicio_id)
            REFERENCES servicios_mudanza(id) ON DELETE CASCADE,
        CONSTRAINT fk_historial_usuario  FOREIGN KEY (usuario_id)
            REFERENCES usuarios(id)
    );
END
GO

-- ------------------------------------------------------------
-- Tabla: tarifas (mantenida para compatibilidad)
-- ------------------------------------------------------------
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='tarifas' AND xtype='U')
BEGIN
    CREATE TABLE tarifas (
        id                      INT IDENTITY(1,1) PRIMARY KEY,
        tarifa_por_km           DECIMAL(10,2) NOT NULL CHECK (tarifa_por_km > 0),
        tarifa_por_unidad_carga DECIMAL(10,2) NOT NULL CHECK (tarifa_por_unidad_carga > 0),
        updated_at              DATETIME      NOT NULL DEFAULT GETDATE()
    );
END
GO

-- ------------------------------------------------------------
-- Índices
-- ------------------------------------------------------------
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name='idx_servicios_cliente_id')
    CREATE INDEX idx_servicios_cliente_id ON servicios_mudanza(cliente_id);
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name='idx_servicios_estado')
    CREATE INDEX idx_servicios_estado ON servicios_mudanza(estado);
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name='idx_pagos_servicio_id')
    CREATE INDEX idx_pagos_servicio_id ON pagos(servicio_id);
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name='idx_historial_servicio_id')
    CREATE INDEX idx_historial_servicio_id ON historial_estados(servicio_id);
GO

-- ------------------------------------------------------------
-- Datos iniciales
-- ------------------------------------------------------------

-- Usuario administrador (password: admin123 — sin hash para compatibilidad con seed)
IF NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'admin@mudanzas.com')
    INSERT INTO usuarios (nombre, apellido, email, password, rol)
    VALUES ('Administrador', 'Sistema', 'admin@mudanzas.com', 'admin123', 'administrador');

-- Usuario empleado
IF NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'empleado@mudanzas.com')
    INSERT INTO usuarios (nombre, apellido, email, password, rol)
    VALUES ('Empleado', 'Demo', 'empleado@mudanzas.com', 'empleado123', 'empleado');

-- Tarifa inicial
IF NOT EXISTS (SELECT 1 FROM tarifas)
    INSERT INTO tarifas (tarifa_por_km, tarifa_por_unidad_carga)
    VALUES (1500.00, 50.00);

-- Vehículo de ejemplo
IF NOT EXISTS (SELECT 1 FROM vehiculos WHERE placa = 'ABC123')
    INSERT INTO vehiculos (placa, tipo, capacidad_kg)
    VALUES ('ABC123', 'camion_mediano', 3000.00);
GO
