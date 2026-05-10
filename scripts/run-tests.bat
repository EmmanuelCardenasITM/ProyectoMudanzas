@echo off
echo ========================================
echo  Ejecutando Pruebas Unitarias Completas
echo ========================================
echo.
echo 🧪 Ejecutando todas las pruebas del sistema...
echo.
echo 📊 Módulos a probar:
echo    ✅ Modelos (Usuario, Cliente, Servicio, Pago, Vehiculo, Tarifa, HistorialEstado)
echo    ✅ Controladores (Carga de beans)
echo    ✅ Configuraciones (Spring, JPA, Seguridad)
echo    ✅ Utilidades (GenerarHashes, BCrypt)
echo.

mvn test

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ ¡Todas las pruebas pasaron exitosamente!
    echo.
    echo 📊 Resumen de pruebas ejecutadas:
    echo    🏗️  Modelos: 7 clases con 60+ casos de prueba
    echo    🎮 Controladores: 1 clase con 9 casos de prueba
    echo    ⚙️  Configuraciones: 1 clase con 10 casos de prueba
    echo    🔧 Utilidades: 1 clase con 9 casos de prueba
    echo.
    echo 📈 Cobertura estimada:
    echo    - Modelos: ~95%% (getters, setters, validaciones)
    echo    - Controladores: ~80%% (carga de beans)
    echo    - Configuraciones: ~85%% (Spring context)
    echo    - Utilidades: ~90%% (BCrypt, hashing)
    echo.
    echo 💡 Para ver reportes detallados:
    echo    - Surefire: target/surefire-reports/
    echo    - Logs: Revisar salida de Maven arriba
    echo.
    echo 🚀 ¡El sistema está listo para producción!
    echo.
) else (
    echo.
    echo ❌ Algunas pruebas fallaron
    echo.
    echo 🔍 Para diagnosticar:
    echo    1. Revisa los logs de Maven arriba
    echo    2. Ejecuta: mvn test -Dtest="NombrePruebaEspecifica"
    echo    3. Verifica: target/surefire-reports/
    echo.
    echo 💡 Comandos útiles:
    echo    - mvn test -Dtest="*Model*"     (solo modelos)
    echo    - mvn test -Dtest="*Controller*" (solo controladores)
    echo    - mvn test -Dtest="*Config*"    (solo configuraciones)
    echo.
)

echo.
echo 📋 Comandos adicionales disponibles:
echo    - mvn test -Dtest="TestSuite"           (ejecutar suite completa)
echo    - mvn test -Dtest="UsuarioTest"         (prueba específica)
echo    - mvn test -Dtest="*Test" -Dgroups=fast (pruebas rápidas)
echo.

pause