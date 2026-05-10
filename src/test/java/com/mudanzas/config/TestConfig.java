package com.mudanzas.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Profile;

/**
 * Configuración específica para pruebas.
 * Configuración mínima para testing.
 */
@TestConfiguration
@Profile("test")
public class TestConfig {
    // Configuración básica para pruebas
    // Spring Boot maneja automáticamente la configuración de testing
}