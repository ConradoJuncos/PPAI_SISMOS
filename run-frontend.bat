@echo off
echo ================================================
echo   Iniciando Frontend - Sistema PPAI
echo ===============================================
echo.
echo IMPORTANTE: Asegurate de que el backend este corriendo
echo en http://localhost:8080 antes de usar el frontend
echo.

cd frontend

echo Compilando el proyecto...
call mvnw.cmd clean compile -DskipTests

echo.
echo Copiando dependencias...
call mvnw.cmd dependency:copy-dependencies -DoutputDirectory=target\dependency -DincludeScope=runtime

echo.
echo Ejecutando la aplicación de escritorio...
java -cp "target\classes;target\dependency\*" com.ppai.app.frontend.gui.MainFrame

pause
