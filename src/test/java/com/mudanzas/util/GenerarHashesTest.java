package com.mudanzas.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mindrot.jbcrypt.BCrypt;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la clase utilitaria GenerarHashes.
 * Verifica la generación de hashes BCrypt.
 */
@DisplayName("GenerarHashes - Pruebas Unitarias")
class GenerarHashesTest {

    @Test
    @DisplayName("Debe generar hashes BCrypt válidos")
    void testGenerarHashesBCrypt() {
        String password = "testPassword123";
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());

        // Verificar que el hash no es nulo ni vacío
        assertNotNull(hash);
        assertFalse(hash.isEmpty());

        // Verificar que el hash tiene el formato BCrypt correcto
        assertTrue(hash.startsWith("$2a$") || hash.startsWith("$2b$") || hash.startsWith("$2y$"));

        // Verificar que el hash puede verificar la contraseña original
        assertTrue(BCrypt.checkpw(password, hash));

        // Verificar que el hash no verifica una contraseña incorrecta
        assertFalse(BCrypt.checkpw("wrongPassword", hash));
    }

    @Test
    @DisplayName("Debe generar hashes diferentes para la misma contraseña")
    void testHashesDiferentes() {
        String password = "samePassword";
        String hash1 = BCrypt.hashpw(password, BCrypt.gensalt());
        String hash2 = BCrypt.hashpw(password, BCrypt.gensalt());

        // Los hashes deben ser diferentes debido al salt aleatorio
        assertNotEquals(hash1, hash2);

        // Pero ambos deben verificar la misma contraseña
        assertTrue(BCrypt.checkpw(password, hash1));
        assertTrue(BCrypt.checkpw(password, hash2));
    }

    @Test
    @DisplayName("Debe manejar contraseñas con caracteres especiales")
    void testContraseniasEspeciales() {
        String[] passwords = {
            "admin123!",
            "empleado@2024",
            "password#$%",
            "contraseña_ñ",
            "123456789",
            "MiContraseña123!"
        };

        for (String password : passwords) {
            String hash = BCrypt.hashpw(password, BCrypt.gensalt());
            
            assertNotNull(hash);
            assertFalse(hash.isEmpty());
            assertTrue(BCrypt.checkpw(password, hash));
        }
    }

    @Test
    @DisplayName("Debe generar output correcto en main")
    void testMainOutput() {
        // Capturar la salida del método main
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            // Ejecutar el método main
            GenerarHashes.main(new String[]{});

            String output = outputStream.toString();

            // Verificar que contiene las secciones esperadas
            assertTrue(output.contains("=== HASHES PARA EL SEED ==="));
            assertTrue(output.contains("=== SQL PARA EJECUTAR EN SSMS ==="));
            assertTrue(output.contains("admin123"));
            assertTrue(output.contains("empleado123"));
            assertTrue(output.contains("INSERT INTO usuarios"));
            assertTrue(output.contains("INSERT INTO tarifas"));

        } finally {
            // Restaurar la salida original
            System.setOut(originalOut);
        }
    }

    @Test
    @DisplayName("Debe verificar hashes de contraseñas específicas del sistema")
    void testContraseniasDelSistema() {
        // Contraseñas que se usan en el sistema
        String adminPassword = "admin123";
        String empleadoPassword = "empleado123";

        // Generar hashes
        String adminHash = BCrypt.hashpw(adminPassword, BCrypt.gensalt());
        String empleadoHash = BCrypt.hashpw(empleadoPassword, BCrypt.gensalt());

        // Verificar que los hashes funcionan correctamente
        assertTrue(BCrypt.checkpw(adminPassword, adminHash));
        assertTrue(BCrypt.checkpw(empleadoPassword, empleadoHash));

        // Verificar que no verifican contraseñas incorrectas
        assertFalse(BCrypt.checkpw("wrongAdmin", adminHash));
        assertFalse(BCrypt.checkpw("wrongEmpleado", empleadoHash));
    }

    @Test
    @DisplayName("Debe manejar contraseñas vacías correctamente")
    void testContraseniasVaciasYNulas() {
        // Contraseña vacía - BCrypt la acepta
        String emptyPassword = "";
        String emptyHash = BCrypt.hashpw(emptyPassword, BCrypt.gensalt());
        assertTrue(BCrypt.checkpw(emptyPassword, emptyHash));

        // Para null, BCrypt maneja el caso sin lanzar excepción
        // Simplemente verificamos que no cause un crash
        assertDoesNotThrow(() -> {
            String nullHash = BCrypt.hashpw(null, BCrypt.gensalt());
            // Si BCrypt acepta null, el hash debería ser válido
            if (nullHash != null) {
                assertNotNull(nullHash);
            }
        });
    }

    @Test
    @DisplayName("Debe generar hashes con diferentes niveles de complejidad")
    void testNivelesComplejidad() {
        String password = "testPassword";
        
        // Diferentes niveles de salt (rounds)
        int[] rounds = {4, 6, 8, 10, 12};
        
        for (int round : rounds) {
            String salt = BCrypt.gensalt(round);
            String hash = BCrypt.hashpw(password, salt);
            
            assertNotNull(hash);
            assertTrue(BCrypt.checkpw(password, hash));
            
            // Verificar que el hash contiene el número de rounds correcto
            assertTrue(hash.contains("$" + String.format("%02d", round) + "$"));
        }
    }

    @Test
    @DisplayName("Debe ser consistente con verificación de hashes")
    void testConsistenciaVerificacion() {
        String password = "consistencyTest";
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());

        // Verificar múltiples veces que el resultado es consistente
        for (int i = 0; i < 10; i++) {
            assertTrue(BCrypt.checkpw(password, hash));
            assertFalse(BCrypt.checkpw("wrongPassword", hash));
        }
    }

    @Test
    @DisplayName("Debe manejar contraseñas largas")
    void testContraseniasLargas() {
        // Contraseña muy larga
        StringBuilder longPassword = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longPassword.append("a");
        }
        
        String password = longPassword.toString();
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        
        assertNotNull(hash);
        assertTrue(BCrypt.checkpw(password, hash));
    }
}