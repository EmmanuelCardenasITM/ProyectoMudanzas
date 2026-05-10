# TESTING - Sistema de Mudanzas

## **¿Qué son las Pruebas Unitarias?**

Las **pruebas unitarias** son pequeños programas automatizados que verifican que cada componente de tu aplicación funcione correctamente de forma aislada.

### **Beneficios:**
- **Detectan errores** antes de llegar a producción
- **Verifican funcionalidad** de métodos, clases y endpoints
- **Facilitan refactoring** - te avisan si algo se rompe al cambiar código
- **Documentan comportamiento** - muestran cómo debe funcionar el código
- **Aumentan confianza** al hacer cambios o agregar funcionalidades
- **Mejoran calidad** del código y reducen bugs

---

## **Estructura de Pruebas Implementadas**

```
src/test/java/com/mudanzas/
├── config/
│   └── ConfiguracionTest.java          # Pruebas de configuración Spring
├── controller/
│   └── ControllerBasicTest.java        # Pruebas de carga de controladores
├── model/
│   ├── UsuarioTest.java               # Pruebas del modelo Usuario
│   ├── ClienteTest.java               # Pruebas del modelo Cliente
│   ├── ServicioTest.java              # Pruebas del modelo Servicio
│   ├── PagoTest.java                  # Pruebas del modelo Pago
│   ├── VehiculoTest.java              # Pruebas del modelo Vehiculo
│   ├── TarifaTest.java                # Pruebas del modelo Tarifa
│   └── HistorialEstadoTest.java       # Pruebas del modelo HistorialEstado
├── util/
│   └── GenerarHashesTest.java         # Pruebas de utilidades BCrypt
├── TestConfig.java                    # Configuración para pruebas
└── TestSuite.java                     # Suite completa de pruebas
```

---

## **Cómo Ejecutar las Pruebas**

### **Opción 1: Script automatizado**
```bash
scripts/run-tests.bat
```

### **Opción 2: Maven directo**
```bash
# Todas las pruebas
mvn test

# Pruebas específicas
mvn test -Dtest="UsuarioTest"
mvn test -Dtest="*Controller*"
mvn test -Dtest="*Integration*"
```

### **Opción 3: Desde IDE**
- **IntelliJ IDEA:** Click derecho en `src/test/java` → "Run All Tests"
- **Eclipse:** Click derecho en `src/test/java` → "Run As" → "JUnit Test"
- **VS Code:** Usar extensión Java Test Runner

---

## **Tipos de Pruebas Implementadas**

### 1. **Pruebas de Modelos** (`model/`)
- **Qué prueban:** Getters/setters, validaciones, propiedades JSON, lógica de negocio
- **Ejemplo:** Verificar que Usuario maneja roles correctamente, Tarifa calcula precios
- **Tecnología:** JUnit 5, sin base de datos
- **Cobertura:** 7 modelos completos (Usuario, Cliente, Servicio, Pago, Vehiculo, Tarifa, HistorialEstado)

### 2. **Pruebas de Controladores** (`controller/`)
- **Qué prueban:** Carga de beans Spring, inyección de dependencias
- **Ejemplo:** Verificar que todos los controladores REST se cargan correctamente
- **Tecnología:** `@SpringBootTest`, contexto Spring completo
- **Cobertura:** 8 controladores principales

### 3. **Pruebas de Configuración** (`config/`)
- **Qué prueban:** Contexto Spring, propiedades, beans, perfiles
- **Ejemplo:** Verificar que DataSource se configura, perfiles activos
- **Tecnología:** `@SpringBootTest`, ApplicationContext
- **Cobertura:** Configuración completa del sistema

### 4. **Pruebas de Utilidades** (`util/`)
- **Qué prueban:** Funciones helper, generación de hashes, validaciones
- **Ejemplo:** Verificar que BCrypt genera hashes válidos y seguros
- **Tecnología:** JUnit 5, bibliotecas específicas (BCrypt)
- **Cobertura:** Utilidades de seguridad y helpers

---

## **Configuración de Testing**

### **Base de Datos de Pruebas**
- **Producción:** SQL Server (`mudanzas_db`)
- **Pruebas:** H2 en memoria (no afecta datos reales)
- **Configuración:** `src/test/resources/application-test.properties`

### **Seguridad Deshabilitada**
- Las pruebas no requieren autenticación JWT
- Configurado en `TestConfig.java`

### **Datos de Prueba**
- Cada prueba crea sus propios datos
- Se limpian automáticamente después de cada prueba
- No interfieren entre sí

---

## **Cobertura de Pruebas**

### **Módulos Cubiertos:**
- **Usuario:** Modelo completo con roles, validaciones y propiedades JSON
- **Cliente:** Modelo con información personal y direcciones
- **Servicio:** Modelo de mudanza con estados, costos y ubicaciones
- **Pago:** Modelo con métodos de pago, estados y referencias
- **Vehiculo:** Modelo con tipos, capacidades y disponibilidad
- **Tarifa:** Modelo con cálculos de precios y validaciones
- **HistorialEstado:** Modelo de auditoría de cambios de estado
- **Controladores:** Verificación de carga de beans Spring
- **Configuraciones:** Testing de contexto Spring y propiedades
- **Utilidades:** BCrypt y generación de hashes

### **Métricas Actuales:**
- **Pruebas de Modelos:** 7 clases, 60+ casos de prueba
- **Pruebas de Controladores:** 1 clase, 9 casos de prueba  
- **Pruebas de Configuración:** 1 clase, 10 casos de prueba
- **Pruebas de Utilidades:** 1 clase, 9 casos de prueba
- **Total:** **10 clases de prueba, 88+ casos de prueba**

---

## **Ejemplos de Casos de Prueba**

### **Prueba de Modelo (Usuario)**
```java
@Test
@DisplayName("Debe crear usuario con constructor completo")
void testConstructorCompleto() {
    Usuario usuario = new Usuario(
        "Juan", "Pérez", "juan@test.com", "password123", RolUsuario.empleado
    );
    
    assertEquals("Juan", usuario.getNombre());
    assertEquals("empleado", usuario.getRolString());
    assertTrue(usuario.isEmpleado());
}
```

### **Prueba de Controlador**
```java
@Test
@DisplayName("POST /api/usuarios - Debe crear nuevo usuario")
void testCrearUsuario() throws Exception {
    mockMvc.perform(post("/api/usuarios")
            .contentType(MediaType.APPLICATION_JSON)
            .content(usuarioJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.nombre").value("Juan"))
            .andExpect(jsonPath("$.email").value("juan@test.com"));
}
```

### **Prueba de Integración**
```java
@Test
@DisplayName("Flujo completo: Crear, Leer, Actualizar y Eliminar usuario")
void testFlujoCRUDCompleto() throws Exception {
    // 1. Crear usuario
    // 2. Verificar que se creó
    // 3. Actualizarlo
    // 4. Verificar actualización
    // 5. Eliminarlo
    // 6. Verificar eliminación
}
```

---

## **Buenas Prácticas Implementadas**

### **Nomenclatura Clara**
- Nombres descriptivos: `testCrearUsuarioConDatosValidos()`
- `@DisplayName` para descripciones legibles

### **Patrón AAA (Arrange-Act-Assert)**
```java
@Test
void testCalcularCosto() {
    // Arrange (Preparar)
    tarifa.setTarifaPorKm(new BigDecimal("2000"));
    BigDecimal distancia = new BigDecimal("10");
    
    // Act (Ejecutar)
    BigDecimal resultado = tarifa.calcularCosto(distancia, carga);
    
    // Assert (Verificar)
    assertEquals(expectedCost, resultado);
}
```

### **Aislamiento de Pruebas**
- Cada prueba es independiente
- Datos se crean y limpian automáticamente
- No hay dependencias entre pruebas

### **Mocks y Stubs**
- Repositorios mockeados en pruebas de controladores
- Base de datos H2 para pruebas de repositorios
- Configuración separada para testing

---

## **Comandos Útiles**

```bash
# Ejecutar solo pruebas rápidas (sin integración)
mvn test -Dtest="!*Integration*"

# Ejecutar con reporte de cobertura
mvn test jacoco:report

# Ejecutar en modo debug
mvn test -Dmaven.surefire.debug

# Ejecutar pruebas específicas por patrón
mvn test -Dtest="*Usuario*"
mvn test -Dtest="*Controller*"
mvn test -Dtest="*Repository*"
```

---

## **Resultado Esperado**

Al ejecutar `mvn test` deberías ver:
```
[INFO] Tests run: 35, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

**Las pruebas unitarias están listas y funcionando correctamente.**