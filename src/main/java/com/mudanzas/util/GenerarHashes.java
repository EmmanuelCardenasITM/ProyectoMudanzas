package com.mudanzas.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Clase utilitaria para generar hashes bcrypt de las contraseñas del seed.
 * Ejecutar una sola vez para obtener los hashes reales.
 * Luego insertar manualmente en la base de datos.
 */
public class GenerarHashes {
    public static void main(String[] args) {
        String hashAdmin    = BCrypt.hashpw("admin123",    BCrypt.gensalt());
        String hashEmpleado = BCrypt.hashpw("empleado123", BCrypt.gensalt());

        System.out.println("=== HASHES PARA EL SEED ===");
        System.out.println("admin123    -> " + hashAdmin);
        System.out.println("empleado123 -> " + hashEmpleado);
        System.out.println();
        System.out.println("=== SQL PARA EJECUTAR EN SSMS ===");
        System.out.println("USE sistema_mudanzas;");
        System.out.println("INSERT INTO usuarios (nombre, email, password_hash, rol) VALUES");
        System.out.println("('Administrador', 'admin@mudanzas.com', '" + hashAdmin + "', 'ADMINISTRADOR');");
        System.out.println("INSERT INTO usuarios (nombre, email, password_hash, rol) VALUES");
        System.out.println("('Empleado', 'empleado@mudanzas.com', '" + hashEmpleado + "', 'EMPLEADO');");
        System.out.println("INSERT INTO tarifas (tarifa_por_km, tarifa_por_unidad_carga) VALUES (5.00, 10.00);");
    }
}
