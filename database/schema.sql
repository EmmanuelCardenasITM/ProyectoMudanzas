-- =============================================================
-- PASO 1: Crear la base de datos
-- =============================================================
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'sistema_mudanzas')
    CREATE DATABASE sistema_mudanzas;
GO

USE sistema_mudanzas;
GO

-- =============================================================
-- PASO 2: Crear las tablas
-- =============================================================

-- Usuarios del sistema (Administrador / Empleado)
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='usuarios' AND xtype='U')
CREATE TABLE usuarios (
    id            INT IDENTITY(1,1) PRIMARY KEY,
    nombre        VARCHAR(100)  NOT NULL,
    email         VARCHAR(150)  NOT NULL UNIQUE,
    password_hash VARCHAR(255)  NOT NULL,
    rol           VARCHAR(20)   NOT NULL CHECK (rol IN ('ADMINISTRADOR','EMPLEADO')),
    activo        BIT           NOT NULL DEFAULT 1,
    created_at    DATETIME      NOT NULL DEFAULT GETDATE()
);
GO

-- Clientes
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='clientes' AND xtype='U')
CREATE TABLE clientes (
    id         INT IDENTITY(1,1) PRIMARY KEY,
    nombre     VARCHAR(100) NOT NULL,
    apellido   VARCHAR(100) NOT NULL,
    email      VARCHAR(150) NOT NULL UNIQUE,
    telefono   VARCHAR(20),
    direccion  NVARCHAR(MAX),
    created_at DATETIME     NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME     NOT NULL DEFAULT GETDATE()
);
GO

-- Tarifas
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='tarifas' AND xtype='U')
CREATE TABLE tarifas (
    id                      INT IDENTITY(1,1) PRIMARY KEY,
    tarifa_por_km           DECIMAL(10,2) NOT NULL CHECK (tarifa_por_km > 0),
    tarifa_por_unidad_carga DECIMAL(10,2) NOT NULL CHECK (tarifa_por_unidad_carga > 0),
    updated_at              DATETIME      NOT NULL DEFAULT GETDATE()
);
GO

-- Servicios de mudanza
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='servicios' AND xtype='U')
CREATE TABLE servicios (
    id                INT IDENTITY(1,1) PRIMARY KEY,
    cliente_id        INT           NOT NULL,
    direccion_origen  NVARCHAR(MAX) NOT NULL,
    direccion_destino NVARCHAR(MAX) NOT NULL,
    fecha_servicio    DATE          NOT NULL,
    distancia_km      DECIMAL(10,2) NOT NULL CHECK (distancia_km > 0),
    carga             DECIMAL(10,2) NOT NULL CHECK (carga > 0),
    costo             DECIMAL(12,2) NOT NULL,
    estado            VARCHAR(20)   NOT NULL DEFAULT 'PENDIENTE'
                          CHECK (estado IN ('PENDIENTE','EN_PROCESO','FINALIZADO')),
    created_at        DATETIME      NOT NULL DEFAULT GETDATE(),
    updated_at        DATETIME      NOT NULL DEFAULT GETDATE(),
    FOREIGN KEY (cliente_id) REFERENCES clientes(id)
);
GO

-- Pagos
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='pagos' AND xtype='U')
CREATE TABLE pagos (
    id          INT IDENTITY(1,1) PRIMARY KEY,
    servicio_id INT           NOT NULL,
    monto       DECIMAL(12,2) NOT NULL CHECK (monto > 0),
    created_at  DATETIME      NOT NULL DEFAULT GETDATE(),
    FOREIGN KEY (servicio_id) REFERENCES servicios(id)
);
GO

-- Indices
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name='idx_servicios_cliente_id')
    CREATE INDEX idx_servicios_cliente_id ON servicios(cliente_id);
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name='idx_servicios_estado')
    CREATE INDEX idx_servicios_estado ON servicios(estado);
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name='idx_pagos_servicio_id')
    CREATE INDEX idx_pagos_servicio_id ON pagos(servicio_id);
GO

-- =============================================================
-- PASO 3: Insertar datos iniciales
-- =============================================================

-- Usuarios (password en texto plano, sin hash)
IF NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'admin@mudanzas.com')
    INSERT INTO usuarios (nombre, email, password_hash, rol)
    VALUES ('Administrador', 'admin@mudanzas.com', 'admin123', 'ADMINISTRADOR');

IF NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'empleado@mudanzas.com')
    INSERT INTO usuarios (nombre, email, password_hash, rol)
    VALUES ('Empleado', 'empleado@mudanzas.com', 'empleado123', 'EMPLEADO');

-- Tarifa inicial
IF NOT EXISTS (SELECT 1 FROM tarifas)
    INSERT INTO tarifas (tarifa_por_km, tarifa_por_unidad_carga)
    VALUES (5.00, 10.00);

-- Clientes de ejemplo
IF NOT EXISTS (SELECT 1 FROM clientes WHERE email = 'juan.perez@example.com')
    INSERT INTO clientes (nombre, apellido, email, telefono, direccion)
    VALUES ('Juan', 'Perez', 'juan.perez@example.com', '3001112233', 'Calle 10 # 20-30, Bogota');

IF NOT EXISTS (SELECT 1 FROM clientes WHERE email = 'maria.gonzalez@example.com')
    INSERT INTO clientes (nombre, apellido, email, telefono, direccion)
    VALUES ('Maria', 'Gonzalez', 'maria.gonzalez@example.com', '3104445566', 'Carrera 50 # 80-90, Medellin');
GO
