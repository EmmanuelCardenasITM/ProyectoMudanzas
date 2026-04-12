package com.mudanzas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicación Spring Boot.
 * Ejecutar con: mvn spring-boot:run
 * Swagger UI: http://localhost:8080/swagger-ui.html
 */
@SpringBootApplication
public class SistemaMudanzasApplication {
    public static void main(String[] args) {
        SpringApplication.run(SistemaMudanzasApplication.class, args);
    }
}
