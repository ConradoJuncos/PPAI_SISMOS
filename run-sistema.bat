@echo off
title Sistema PPAI - Red Sísmica (Versión Local)
color 0B

echo ============================================================
echo        🚀 INICIANDO SISTEMA PPAI RED SISMICA - LOCAL
echo ============================================================
echo.

REM Ir al directorio del proyecto
cd sistema-red-sismica

echo 🧩 Compilando el proyecto Java...
call mvnw.cmd clean install -DskipTests

if %errorlevel% neq 0 (
    echo ❌ Error en la compilación.
    pause
    exit /b
)

echo.
echo ✅ Compilación completada exitosamente.
echo.

echo 🪩 Iniciando la aplicación de escritorio...
echo ------------------------------------------------------------
echo (Cierra la ventana de la aplicación para detener el sistema)
echo ------------------------------------------------------------
echo.

call mvnw.cmd exec:java -Dexec.mainClass="com.ppai.app.Main"

echo.
echo 💤 Sistema finalizado.
pause
