-- =============================================================
-- Datos iniciales -- Sistema de Mudanzas
-- Base de datos: Microsoft SQL Server
-- =============================================================
-- IMPORTANTE: Genera los hashes bcrypt reales antes de ejecutar.
-- Usa esta clase Java para generarlos:
--
--   import org.mindrot.jbcrypt.BCrypt;
--   public class GenHash {
--       public static void main(String[] args) {
--           System.out.println(BCrypt.hashpw("admin123", BCrypt.gensalt()));
--           System.out.println(BCrypt.hashpw("empleado123", BCrypt.gensalt()));
--       }
--   }
--
-- Reemplaza los valores de password_hash a continuacion.
-- =============================================================

USE sistema_mudanzas;
GO

-- Usuarios
INSERT INTO usuarios (nombre, email, password_hash, rol) VALUES
('Administrador', 'admin@mudanzas.com',    '$2a$10$REEMPLAZAR_HASH_ADMIN',    'ADMINISTRADOR');

INSERT INTO usuarios (nombre, email, password_hash, rol) VALUES
('Empleado',      'empleado@mudanzas.com', '$2a$10$REEMPLAZAR_HASH_EMPLEADO', 'EMPLEADO');
GO

-- Tarifa inicial
INSERT INTO tarifas (tarifa_por_km, tarifa_por_unidad_carga) VALUES (5.00, 10.00);
GO

-- Clientes de ejemplo
INSERT INTO clientes (nombre, apellido, email, telefono, direccion) VALUES
('Juan',  'Perez',    'juan.perez@example.com',     '+57 300 111 2233', 'Calle 10 # 20-30, Bogota');

INSERT INTO clientes (nombre, apellido, email, telefono, direccion) VALUES
('Maria', 'Gonzalez', 'maria.gonzalez@example.com', '+57 310 444 5566', 'Carrera 50 # 80-90, Medellin');
GO
