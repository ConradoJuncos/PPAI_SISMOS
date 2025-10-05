@echo off
echo ================================================
echo   Iniciando Backend - Sistema PPAI
echo ================================================
echo.

cd backend

echo Compilando el proyecto...
call mvnw.cmd clean install -DskipTests

echo.
echo Iniciando el servidor...
echo El backend estará disponible en http://localhost:8080
echo.
echo Presiona Ctrl+C para detener el servidor
echo.

call mvnw.cmd exec:java -Dexec.mainClass="com.ppai.app.Main"
pause