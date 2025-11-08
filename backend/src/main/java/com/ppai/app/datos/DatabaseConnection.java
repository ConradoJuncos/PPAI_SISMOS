package com.ppai.app.datos;

import java.io.File;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/* --------------------------------------------------------------
    1. DatabaseConnection – crea la BD si no existe y abre conexión
    -------------------------------------------------------------- */
public class DatabaseConnection {

        private static final String DB_URL = "jdbc:sqlite:redSismica.sqlite3";
        private static Connection connection;

        public static Connection getConnection() throws SQLException {
                if (connection == null || connection.isClosed()) {
                        // Cargar driver SQLite (puede ser opcional en JDBC 4.0+)
                        try {
                                Class.forName("org.sqlite.JDBC");
                        } catch (ClassNotFoundException e) {
                                throw new SQLException("Error: No se encontró el driver JDBC de SQLite.");
                        }

                        File dbFile = new File("redSismica.sqlite3");
                        boolean dbExists = dbFile.exists();
                        connection = DriverManager.getConnection(DB_URL);

                        // Habilitar la aplicación de claves foráneas
                        try (Statement s = connection.createStatement()) {
                                s.execute("PRAGMA foreign_keys = ON;");
                        }

                        if (!dbExists)
                                initDatabase();
                }
                return connection;
        }

        private static void initDatabase() throws SQLException {
                try (Statement s = connection.createStatement()) {

                        // ---------- TABLAS SIMPLES (Sin FKs internas) ----------
                        s.execute("CREATE TABLE IF NOT EXISTS AlcanceSismo ("
                                        + "idAlcanceSismo INTEGER PRIMARY KEY AUTOINCREMENT, "
                                        + "descripcion TEXT, nombre TEXT);");

                        s.execute("CREATE TABLE IF NOT EXISTS ApreciacionTipo ("
                                        + "idApreciacionTipo INTEGER PRIMARY KEY AUTOINCREMENT, "
                                        + "color TEXT, leyenda TEXT);");

                        s.execute("CREATE TABLE IF NOT EXISTS ClasificacionSismo ("
                                        + "idClasificacionSismo INTEGER PRIMARY KEY AUTOINCREMENT, "
                                        + "kmProfundidadDesde REAL, kmProfundidadHasta REAL, nombre TEXT);");

                        s.execute("CREATE TABLE IF NOT EXISTS Fabricante ("
                                        + "idFabricante INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT);");

                        s.execute("CREATE TABLE IF NOT EXISTS MagnitudRichter ("
                                        + "idMagnitudRichter INTEGER PRIMARY KEY AUTOINCREMENT, "
                                        + "descripcion TEXT, nombre TEXT, valorDesde REAL, valorHasta REAL);");

                        s.execute("CREATE TABLE IF NOT EXISTS MotivoFueraServicio ("
                                        + "idMotivoFueraServicio INTEGER PRIMARY KEY AUTOINCREMENT, "
                                        + "descripcion TEXT, nombre TEXT);");

                        s.execute("CREATE TABLE IF NOT EXISTS OrigenDeGeneracion ("
                                        + "idOrigenGeneracion INTEGER PRIMARY KEY AUTOINCREMENT, "
                                        + "descripcion TEXT, nombre TEXT);");

                        s.execute("CREATE TABLE IF NOT EXISTS Perfil ("
                                        + "idPerfil INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT);");

                        s.execute("CREATE TABLE IF NOT EXISTS Rol ("
                                        + "idRol INTEGER PRIMARY KEY AUTOINCREMENT, "
                                        + "descripcion TEXT, nombre TEXT);");

                        s.execute("CREATE TABLE IF NOT EXISTS TipoDeDato ("
                                        + "idTipoDeDato INTEGER PRIMARY KEY AUTOINCREMENT, "
                                        + "denominacion TEXT, nombreUnidadMedida TEXT, valorUmbral REAL);");

                        s.execute("CREATE TABLE IF NOT EXISTS TipoTareaInspeccion ("
                                        + "codigo INTEGER PRIMARY KEY AUTOINCREMENT, "
                                        + "descripcionTrabajo TEXT, duracionEstimada TEXT, nombre TEXT);");

                        s.execute("CREATE TABLE IF NOT EXISTS TipoTrabajo ("
                                        + "idTipoTrabajo INTEGER PRIMARY KEY AUTOINCREMENT, "
                                        + "descripcion TEXT, nombre TEXT);");

                        s.execute("CREATE TABLE IF NOT EXISTS Estado ("
                                        + "idEstado INTEGER PRIMARY KEY AUTOINCREMENT, "
                                        + "ambito TEXT, nombreEstado TEXT);");

                        // ---------- TABLAS CON RELACIONES (FKs definidas INLINE para SQLite)
                        // ----------

                        s.execute("CREATE TABLE IF NOT EXISTS ModeloSismografo ("
                                        + "idModelo INTEGER PRIMARY KEY AUTOINCREMENT, "
                                        + "descripcion TEXT, nombre TEXT, idFabricante INTEGER, "
                                        + "FOREIGN KEY (idFabricante) REFERENCES Fabricante(idFabricante));");

                        s.execute("CREATE TABLE IF NOT EXISTS Empleado ("
                                        + "idEmpleado INTEGER PRIMARY KEY AUTOINCREMENT, "
                                        + "apellido TEXT, mail TEXT, nombre TEXT, telefono TEXT, idRol INTEGER, "
                                        + "FOREIGN KEY (idRol) REFERENCES Rol(idRol));");

                        s.execute("CREATE TABLE IF NOT EXISTS EstacionSismologica ("
                                        + "codigoEstacion INTEGER PRIMARY KEY AUTOINCREMENT, "
                                        + "documentoCertificacionAdq TEXT, fechaSolicitudCertificacion TEXT, "
                                        + "latitud REAL, longitud REAL, nombre TEXT, nroCertificacionAdquisicion INTEGER);");

                        s.execute("CREATE TABLE IF NOT EXISTS Sismografo ("
                                        + "identificadorSismografo INTEGER PRIMARY KEY AUTOINCREMENT, "
                                        + "fechaAdquicision TEXT, nroSerie INTEGER, "
                                        + "idEstadoActual INTEGER, codigoEstacion INTEGER, idModelo INTEGER, "
                                        + "FOREIGN KEY (idEstadoActual) REFERENCES Estado(idEstado), "
                                        + "FOREIGN KEY (codigoEstacion) REFERENCES EstacionSismologica(codigoEstacion), "
                                        + "FOREIGN KEY (idModelo) REFERENCES ModeloSismografo(idModelo));");

                        s.execute("CREATE TABLE IF NOT EXISTS SerieTemporal ("
                                        + "idSerieTemporal INTEGER PRIMARY KEY AUTOINCREMENT, "
                                        + "condicionAlarma TEXT, fechaHoraRegistro TEXT, frecuenciaMuestreo TEXT, "
                                        + "idEstado INTEGER, "
                                        + "FOREIGN KEY (idEstado) REFERENCES Estado(idEstado));");

                        s.execute("CREATE TABLE IF NOT EXISTS MuestraSismica ("
                                        + "idMuestraSismica INTEGER PRIMARY KEY AUTOINCREMENT, "
                                        + "fechaHoraRegistro TEXT, idSerieTemporal INTEGER, "
                                        + "FOREIGN KEY (idSerieTemporal) REFERENCES SerieTemporal(idSerieTemporal));");

                        s.execute("CREATE TABLE IF NOT EXISTS DetalleMuestraSismica ("
                                        + "idDetalleMuestraSismica INTEGER PRIMARY KEY AUTOINCREMENT, "
                                        + "idTipoDeDato INTEGER, valor REAL, "
                                        + "FOREIGN KEY (idTipoDeDato) REFERENCES TipoDeDato(idTipoDeDato));");

                        s.execute("CREATE TABLE IF NOT EXISTS EventoSismico ("
                                        + "idEventoSismico INTEGER PRIMARY KEY AUTOINCREMENT, "
                                        + "fechaHoraFin TEXT, fechaHoraOcurrencia TEXT, "
                                        + "latitudEpicentro TEXT, latitudHipocentro TEXT, "
                                        + "longitudEpicentro TEXT, longitudHipocentro TEXT, "
                                        + "valorMagnitud REAL, idClasificacionSismo INTEGER, "
                                        + "idMagnitudRichter INTEGER, idOrigenGeneracion INTEGER, "
                                        + "idAlcanceSismo INTEGER, idEstadoActual INTEGER, idAnalistaSupervisor INTEGER, "
                                        + "FOREIGN KEY (idClasificacionSismo) REFERENCES ClasificacionSismo(idClasificacionSismo), "
                                        + "FOREIGN KEY (idMagnitudRichter) REFERENCES MagnitudRichter(idMagnitudRichter), "
                                        + "FOREIGN KEY (idOrigenGeneracion) REFERENCES OrigenDeGeneracion(idOrigenGeneracion), "
                                        + "FOREIGN KEY (idAlcanceSismo) REFERENCES AlcanceSismo(idAlcanceSismo), "
                                        + "FOREIGN KEY (idEstadoActual) REFERENCES Estado(idEstado), "
                                        + "FOREIGN KEY (idAnalistaSupervisor) REFERENCES Empleado(idEmpleado));");

                        s.execute("CREATE TABLE IF NOT EXISTS CambioEstado ("
                                        + "idCambioEstado INTEGER PRIMARY KEY AUTOINCREMENT, "
                                        + "fechaHoraFin TEXT, fechaHoraInicio TEXT, "
                                        + "idEstado INTEGER, idResponsableInspeccion INTEGER, "
                                        + "FOREIGN KEY (idEstado) REFERENCES Estado(idEstado), "
                                        + "FOREIGN KEY (idResponsableInspeccion) REFERENCES Empleado(idEmpleado));");

                        s.execute("CREATE TABLE IF NOT EXISTS Reparacion ("
                                        + "idReparacion INTEGER PRIMARY KEY AUTOINCREMENT, "
                                        + "comentarioReparacion TEXT, comentarioSolucion TEXT, "
                                        + "fechaEnvioReparacion TEXT, fechaRespuestaReparacion TEXT, nroReparacion INTEGER);");

                        s.execute("CREATE TABLE IF NOT EXISTS Suscripcion ("
                                        + "idSuscripcion INTEGER PRIMARY KEY AUTOINCREMENT, "
                                        + "fechaHoraFinSuscripcion TEXT, fechaHoraInicioSuscripcion TEXT);");

                        s.execute("CREATE TABLE IF NOT EXISTS TrabajoARealizar ("
                                        + "idTrabajoARealizar INTEGER PRIMARY KEY AUTOINCREMENT, "
                                        + "comentario TEXT, fechaFinPrevista TEXT, fechaFinReal TEXT, "
                                        + "fechaInicioPrevista TEXT, fechaInicioReal TEXT, idTipoTrabajo INTEGER, "
                                        + "FOREIGN KEY (idTipoTrabajo) REFERENCES TipoTrabajo(idTipoTrabajo));");

                        s.execute("CREATE TABLE IF NOT EXISTS Usuario ("
                                        + "idUsuario INTEGER PRIMARY KEY AUTOINCREMENT, "
                                        + "contraseña TEXT, nombreUsuario TEXT, idEmpleado INTEGER, "
                                        + "FOREIGN KEY (idEmpleado) REFERENCES Empleado(idEmpleado));");

                        s.execute("CREATE TABLE IF NOT EXISTS Inspeccion ("
                                        + "idInspeccion INTEGER PRIMARY KEY AUTOINCREMENT, "
                                        + "comentarioInspeccion TEXT, fechaHoraFinInspeccion TEXT, "
                                        + "fechaHoraInicioInspeccion TEXT, fechaHoraSolicitudInspeccion TEXT, "
                                        + "idEmpleado INTEGER, identificadorSismografo INTEGER, "
                                        + "FOREIGN KEY (idEmpleado) REFERENCES Empleado(idEmpleado), "
                                        + "FOREIGN KEY (identificadorSismografo) REFERENCES Sismografo(identificadorSismografo));");

                        // --- TABLAS FALTANTES CORREGIDAS ---

                        s.execute("CREATE TABLE IF NOT EXISTS OrdenDeInspeccion ("
                                        + "numeroOrden INTEGER PRIMARY KEY AUTOINCREMENT, "
                                        + "fechaHoraCierre TEXT, fechaHoraFinalizacion TEXT, fechaHoraInicio TEXT, "
                                        + "observacionCierre TEXT, idEmpleado INTEGER, idEstado INTEGER, codigoEstacion INTEGER, "
                                        + "FOREIGN KEY (idEmpleado) REFERENCES Empleado(idEmpleado), "
                                        + "FOREIGN KEY (idEstado) REFERENCES Estado(idEstado), "
                                        + "FOREIGN KEY (codigoEstacion) REFERENCES EstacionSismologica(codigoEstacion));");

                        s.execute("CREATE TABLE IF NOT EXISTS ReclamoGarantia ("
                                        + "nroReclamo INTEGER PRIMARY KEY AUTOINCREMENT, "
                                        + "comentario TEXT, fechaReclamo TEXT, fechaRespuesta TEXT, respuestaFabricante TEXT, "
                                        + "idFabricante INTEGER, idSismografo INTEGER, "
                                        + "FOREIGN KEY (idFabricante) REFERENCES Fabricante(idFabricante), "
                                        + "FOREIGN KEY (idSismografo) REFERENCES Sismografo(identificadorSismografo));");

                        s.execute("CREATE TABLE IF NOT EXISTS TareaAsignada ("
                                        + "idTareaAsignada INTEGER PRIMARY KEY AUTOINCREMENT, "
                                        + "comentario TEXT, fechaHoraRealizacion TEXT, codigoTarea INTEGER, numeroOrden INTEGER, "
                                        + "FOREIGN KEY (codigoTarea) REFERENCES TipoTareaInspeccion(codigo), "
                                        + "FOREIGN KEY (numeroOrden) REFERENCES OrdenDeInspeccion(numeroOrden));");

                        // ---------- TABLAS DE RELACIÓN (M:N) ----------
                        s.execute("CREATE TABLE IF NOT EXISTS EventoSismico_SerieTemporal ("
                                        + "idEventoSismico INTEGER, idSerieTemporal INTEGER, "
                                        + "PRIMARY KEY(idEventoSismico,idSerieTemporal), "
                                        + "FOREIGN KEY (idEventoSismico) REFERENCES EventoSismico(idEventoSismico), "
                                        + "FOREIGN KEY (idSerieTemporal) REFERENCES SerieTemporal(idSerieTemporal));");

                        s.execute("CREATE TABLE IF NOT EXISTS Sismografo_SerieTemporal ("
                                        + "identificadorSismografo INTEGER, idSerieTemporal INTEGER, "
                                        + "PRIMARY KEY(identificadorSismografo,idSerieTemporal), "
                                        + "FOREIGN KEY (identificadorSismografo) REFERENCES Sismografo(identificadorSismografo), "
                                        + "FOREIGN KEY (idSerieTemporal) REFERENCES SerieTemporal(idSerieTemporal));");

                        s.execute("CREATE TABLE IF NOT EXISTS Sismografo_CambioEstado ("
                                        + "identificadorSismografo INTEGER, idCambioEstado INTEGER, "
                                        + "PRIMARY KEY(identificadorSismografo,idCambioEstado), "
                                        + "FOREIGN KEY (identificadorSismografo) REFERENCES Sismografo(identificadorSismografo), "
                                        + "FOREIGN KEY (idCambioEstado) REFERENCES CambioEstado(idCambioEstado));");

                        s.execute("CREATE TABLE IF NOT EXISTS Sismografo_Reparacion ("
                                        + "identificadorSismografo INTEGER, idReparacion INTEGER, "
                                        + "PRIMARY KEY(identificadorSismografo,idReparacion), "
                                        + "FOREIGN KEY (identificadorSismografo) REFERENCES Sismografo(identificadorSismografo), "
                                        + "FOREIGN KEY (idReparacion) REFERENCES Reparacion(idReparacion));");

                        s.execute("CREATE TABLE IF NOT EXISTS CambioEstado_MotivoFueraServicio ("
                                        + "idCambioEstado INTEGER, idMotivoFueraServicio INTEGER, "
                                        + "PRIMARY KEY(idCambioEstado,idMotivoFueraServicio), "
                                        + "FOREIGN KEY (idCambioEstado) REFERENCES CambioEstado(idCambioEstado), "
                                        + "FOREIGN KEY (idMotivoFueraServicio) REFERENCES MotivoFueraServicio(idMotivoFueraServicio));");

                        s.execute("CREATE TABLE IF NOT EXISTS MuestraSismica_Detalle ("
                                        + "idMuestraSismica INTEGER, idDetalleMuestraSismica INTEGER, "
                                        + "PRIMARY KEY(idMuestraSismica,idDetalleMuestraSismica), "
                                        + "FOREIGN KEY (idMuestraSismica) REFERENCES MuestraSismica(idMuestraSismica), "
                                        + "FOREIGN KEY (idDetalleMuestraSismica) REFERENCES DetalleMuestraSismica(idDetalleMuestraSismica));");

                        s.execute("CREATE TABLE IF NOT EXISTS Suscripcion_EstacionSismologica ("
                                        + "idSuscripcion INTEGER, codigoEstacion INTEGER, "
                                        + "PRIMARY KEY(idSuscripcion,codigoEstacion), "
                                        + "FOREIGN KEY (idSuscripcion) REFERENCES Suscripcion(idSuscripcion), "
                                        + "FOREIGN KEY (codigoEstacion) REFERENCES EstacionSismologica(codigoEstacion));");

                        s.execute("CREATE TABLE IF NOT EXISTS TareaAsignada_ApreciacionTipo ("
                                        + "idTareaAsignada INTEGER, idApreciacionTipo INTEGER, "
                                        + "PRIMARY KEY(idTareaAsignada,idApreciacionTipo), "
                                        + "FOREIGN KEY (idTareaAsignada) REFERENCES TareaAsignada(idTareaAsignada), "
                                        + "FOREIGN KEY (idApreciacionTipo) REFERENCES ApreciacionTipo(idApreciacionTipo));");

                        s.execute("CREATE TABLE IF NOT EXISTS Usuario_Perfil ("
                                        + "idUsuario INTEGER, idPerfil INTEGER, "
                                        + "PRIMARY KEY(idUsuario,idPerfil), "
                                        + "FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario), "
                                        + "FOREIGN KEY (idPerfil) REFERENCES Perfil(idPerfil));");

                        s.execute("CREATE TABLE IF NOT EXISTS Usuario_Suscripcion ("
                                        + "idUsuario INTEGER, idSuscripcion INTEGER, "
                                        + "PRIMARY KEY(idUsuario,idSuscripcion), "
                                        + "FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario), "
                                        + "FOREIGN KEY (idSuscripcion) REFERENCES Suscripcion(idSuscripcion));");

                        s.execute("CREATE TABLE IF NOT EXISTS Inspeccion_TareaAsignada ("
                                        + "idInspeccion INTEGER, idTareaAsignada INTEGER, "
                                        + "PRIMARY KEY(idInspeccion,idTareaAsignada), "
                                        + "FOREIGN KEY (idInspeccion) REFERENCES Inspeccion(idInspeccion), "
                                        + "FOREIGN KEY (idTareaAsignada) REFERENCES TareaAsignada(idTareaAsignada));");

                        s.execute("CREATE TABLE IF NOT EXISTS Inspeccion_TrabajoARealizar ("
                                        + "idInspeccion INTEGER, idTrabajoARealizar INTEGER, "
                                        + "PRIMARY KEY(idInspeccion,idTrabajoARealizar), "
                                        + "FOREIGN KEY (idInspeccion) REFERENCES Inspeccion(idInspeccion), "
                                        + "FOREIGN KEY (idTrabajoARealizar) REFERENCES TrabajoARealizar(idTrabajoARealizar));");

                        s.execute("CREATE TABLE IF NOT EXISTS Inspeccion_MotivoFueraServicio ("
                                        + "idInspeccion INTEGER, idMotivoFueraServicio INTEGER, "
                                        + "PRIMARY KEY(idInspeccion,idMotivoFueraServicio), "
                                        + "FOREIGN KEY (idInspeccion) REFERENCES Inspeccion(idInspeccion), "
                                        + "FOREIGN KEY (idMotivoFueraServicio) REFERENCES MotivoFueraServicio(idMotivoFueraServicio));");

                        System.out.println("Base de datos inicializada (tablas creadas y FKs definidas).");

                        // #################################################
                        // PASO CLAVE 2: LLAMAR A LA POBLACIÓN DE DATOS
                        // #################################################
                        populateDatabase(connection);

                        System.out.println("Base de datos poblada con registros sintéticos.");

                } catch (SQLException e) {
                        System.err.println("Error al crear la base de datos: " + e.getMessage());
                        throw e;
                }
        }

        private static void populateDatabase(Connection conn) throws SQLException {

                String[] inserts = {
                                // =============================================
                                // FASE 1: Catálogo y Clasificación (10 estados, 3 roles, 3 perfiles)
                                // =============================================
                                "INSERT INTO Estado (ambito, nombreEstado) VALUES ('General', 'Activo');", // 1
                                "INSERT INTO Estado (ambito, nombreEstado) VALUES ('General', 'Inactivo');", // 2
                                "INSERT INTO Estado (ambito, nombreEstado) VALUES ('Mantenimiento', 'Pendiente');", // 3
                                "INSERT INTO Estado (ambito, nombreEstado) VALUES ('Mantenimiento', 'Cerrado');", // 4

                                "INSERT INTO Estado (ambito, nombreEstado) VALUES ('Sismico', 'Detectado');", // 5:
                                                                                                              // Inicial
                                                                                                              // (Filtro
                                                                                                              // por No
                                                                                                              // Aceptado)
                                "INSERT INTO Estado (ambito, nombreEstado) VALUES ('Sismico', 'Aceptado para Revisión');", // 6:
                                                                                                                           // Pre-condición
                                                                                                                           // (Analista)
                                "INSERT INTO Estado (ambito, nombreEstado) VALUES ('Sismico', 'Derivado para Revisión');", // 7:
                                                                                                                           // Pre-condición
                                                                                                                           // (Supervisor)
                                "INSERT INTO Estado (ambito, nombreEstado) VALUES ('Sismico', 'Confirmado');", // 8:
                                                                                                               // Salida
                                                                                                               // de CU
                                                                                                               // 23
                                                                                                               // (Filtro
                                                                                                               // por
                                                                                                               // Finalizado)
                                "INSERT INTO Estado (ambito, nombreEstado) VALUES ('Sismico', 'Rechazado');", // 9:
                                                                                                              // Salida
                                                                                                              // de CU
                                                                                                              // 23
                                                                                                              // (Filtro
                                                                                                              // por
                                                                                                              // Finalizado)
                                "INSERT INTO Estado (ambito, nombreEstado) VALUES ('Sismico', 'Cerrado');", // 10:
                                                                                                            // Finalizado/Cerrado
                                                                                                            // (Filtro
                                                                                                            // por
                                                                                                            // Finalizado)

                                "INSERT INTO Rol (descripcion, nombre) VALUES ('Personal de alto nivel que gestiona las operaciones de la red.', 'Supervisor');", // 1
                                "INSERT INTO Rol (descripcion, nombre) VALUES ('Personal encargado de monitorear y registrar eventos sísmicos.', 'Analista Sismico');", // 2
                                "INSERT INTO Rol (descripcion, nombre) VALUES ('Personal técnico a cargo de la instalación y reparación de equipos.', 'Tecnico');", // 3

                                "INSERT INTO Perfil (nombre) VALUES ('Administrativo');", // 1
                                "INSERT INTO Perfil (nombre) VALUES ('Monitoreo');", // 2
                                "INSERT INTO Perfil (nombre) VALUES ('Operacional');", // 3

                                "INSERT INTO Fabricante (nombre) VALUES ('GeoSensor S.A.');", // 1
                                "INSERT INTO Fabricante (nombre) VALUES ('WaveTech Instruments');", // 2
                                "INSERT INTO Fabricante (nombre) VALUES ('QuakeMetrics Global');", // 3

                                "INSERT INTO TipoDeDato (denominacion, nombreUnidadMedida, valorUmbral) VALUES ('Velocidad de Onda', 'm/s', 5000.0);", // 1
                                "INSERT INTO TipoDeDato (denominacion, nombreUnidadMedida, valorUmbral) VALUES ('Frecuencia de Onda', 'Hz', 20.0);", // 2
                                "INSERT INTO TipoDeDato (denominacion, nombreUnidadMedida, valorUmbral) VALUES ('Longitud de Onda', 'm', 1000.0);", // 3

                                "INSERT INTO AlcanceSismo (descripcion, nombre) VALUES ('Percibido solo por instrumentos.', 'Instrumental');", // 1
                                "INSERT INTO AlcanceSismo (descripcion, nombre) VALUES ('Sentido por pocas personas.', 'Local');", // 2
                                "INSERT INTO AlcanceSismo (descripcion, nombre) VALUES ('Daños menores, sentido por muchos.', 'Regional');", // 3
                                "INSERT INTO AlcanceSismo (descripcion, nombre) VALUES ('Daños estructurales severos.', 'Catastrofico');", // 4

                                "INSERT INTO MagnitudRichter (descripcion, nombre, valorDesde, valorHasta) VALUES ('Micro-sismo, no sentido.', 'Menor', 0.0, 1.9);", // 1
                                "INSERT INTO MagnitudRichter (descripcion, nombre, valorDesde, valorHasta) VALUES ('Sismo menor, usualmente no causa daño.', 'Bajo', 2.0, 3.9);", // 2
                                "INSERT INTO MagnitudRichter (descripcion, nombre, valorDesde, valorHasta) VALUES ('Sismo ligero a moderado, sentido por la mayoría.', 'Moderado', 4.0, 5.9);", // 3
                                "INSERT INTO MagnitudRichter (descripcion, nombre, valorDesde, valorHasta) VALUES ('Sismo fuerte, puede causar daños mayores.', 'Fuerte', 6.0, 7.9);", // 4

                                "INSERT INTO ClasificacionSismo (kmProfundidadDesde, kmProfundidadHasta, nombre) VALUES (0.0, 70.0, 'Superficial');", // 1
                                "INSERT INTO ClasificacionSismo (kmProfundidadDesde, kmProfundidadHasta, nombre) VALUES (70.1, 300.0, 'Intermedio');", // 2
                                "INSERT INTO ClasificacionSismo (kmProfundidadDesde, kmProfundidadHasta, nombre) VALUES (300.1, 700.0, 'Profundo');", // 3

                                "INSERT INTO OrigenDeGeneracion (descripcion, nombre) VALUES ('Detección y clasificación automática por software.', 'Automatico');", // 1
                                "INSERT INTO OrigenDeGeneracion (descripcion, nombre) VALUES ('Ingreso manual de datos por el analista.', 'Manual');", // 2

                                "INSERT INTO TipoTrabajo (descripcion, nombre) VALUES ('Trabajos de instalación de nuevo hardware.', 'Instalacion');", // 1
                                "INSERT INTO TipoTrabajo (descripcion, nombre) VALUES ('Inspección de rutina y calibración.', 'Mantenimiento');", // 2
                                "INSERT INTO TipoTrabajo (descripcion, nombre) VALUES ('Reemplazo de partes defectuosas.', 'Reparacion');", // 3

                                "INSERT INTO TipoTareaInspeccion (descripcionTrabajo, duracionEstimada, nombre) VALUES ('Verificación de cables y alimentación eléctrica.', '2 horas', 'Revisión Conectividad');", // 1
                                "INSERT INTO TipoTareaInspeccion (descripcionTrabajo, duracionEstimada, nombre) VALUES ('Ajuste de sensores y chequeo de calibración.', '4 horas', 'Calibración Sismografo');", // 2
                                "INSERT INTO TipoTareaInspeccion (descripcionTrabajo, duracionEstimada, nombre) VALUES ('Actualización del software del registrador.', '1 hora', 'Actualización Software');", // 3

                                "INSERT INTO MotivoFueraServicio (descripcion, nombre) VALUES ('Error de comunicación con el centro de datos.', 'Fallo de Red');", // 1
                                "INSERT INTO MotivoFueraServicio (descripcion, nombre) VALUES ('Problemas con la alimentación eléctrica del sitio.', 'Fallo de Energia');", // 2
                                "INSERT INTO MotivoFueraServicio (descripcion, nombre) VALUES ('Inspección programada que requiere el cese de datos.', 'Mantenimiento Programado');", // 3

                                "INSERT INTO ApreciacionTipo (color, leyenda) VALUES ('#4CAF50', 'Satisfactorio');", // 1
                                "INSERT INTO ApreciacionTipo (color, leyenda) VALUES ('#FFEB3B', 'Requiere Ajuste');", // 2
                                "INSERT INTO ApreciacionTipo (color, leyenda) VALUES ('#F44336', 'Fallo Crítico');", // 3

                                // =============================================
                                // FASE 2: Personal y Activos (3 Empleados, 3 Estaciones, 3 Sismógrafos)
                                // =============================================
                                "INSERT INTO ModeloSismografo (descripcion, nombre, idFabricante) VALUES ('Digital de banda ancha de alta sensibilidad.', 'BB-SR100', 1);",
                                "INSERT INTO ModeloSismografo (descripcion, nombre, idFabricante) VALUES ('Sensor compacto ideal para redes urbanas.', 'CT-W300', 2);",
                                "INSERT INTO ModeloSismografo (descripcion, nombre, idFabricante) VALUES ('Equipo de largo alcance para sismos profundos.', 'DP-Q7000', 3);",

                                "INSERT INTO Empleado (apellido, mail, nombre, telefono, idRol) VALUES ('Perez', 'jperez@redsismica.ar', 'Juan', '3514445555', 2);", // 1:
                                                                                                                                                                     // Analista
                                                                                                                                                                     // Sismico
                                "INSERT INTO Empleado (apellido, mail, nombre, telefono, idRol) VALUES ('Gomez', 'cgomez@redsismica.ar', 'Carla', '3511112222', 3);", // 2:
                                                                                                                                                                      // Tecnico
                                "INSERT INTO Empleado (apellido, mail, nombre, telefono, idRol) VALUES ('Lopez', 'alopez@redsismica.ar', 'Ana', '3519998888', 1);", // 3:
                                                                                                                                                                    // Supervisor

                                "INSERT INTO Usuario (contraseña, nombreUsuario, idEmpleado) VALUES ('hash123', 'jperez', 1);",
                                "INSERT INTO Usuario (contraseña, nombreUsuario, idEmpleado) VALUES ('hash456', 'cgomez', 2);",
                                "INSERT INTO Usuario (contraseña, nombreUsuario, idEmpleado) VALUES ('hash789', 'alopez', 3);",

                                "INSERT INTO Usuario_Perfil (idUsuario, idPerfil) VALUES (1, 2);",
                                "INSERT INTO Usuario_Perfil (idUsuario, idPerfil) VALUES (2, 3);",
                                "INSERT INTO Usuario_Perfil (idUsuario, idPerfil) VALUES (3, 1);",

                                "INSERT INTO EstacionSismologica (documentoCertificacionAdq, fechaSolicitudCertificacion, latitud, longitud, nombre, nroCertificacionAdquisicion) VALUES ('DOC-CBA-001', '2023-01-10 10:00:00', -31.4167, -64.1833, 'Estacion Cordoba Central', 1001);", // 1
                                "INSERT INTO EstacionSismologica (documentoCertificacionAdq, fechaSolicitudCertificacion, latitud, longitud, nombre, nroCertificacionAdquisicion) VALUES ('DOC-MZA-002', '2023-03-20 14:30:00', -32.8895, -68.8458, 'Estacion Mendoza Oeste', 1002);", // 2
                                "INSERT INTO EstacionSismologica (documentoCertificacionAdq, fechaSolicitudCertificacion, latitud, longitud, nombre, nroCertificacionAdquisicion) VALUES ('DOC-SJN-003', '2023-05-01 09:00:00', -31.5375, -68.5364, 'Estacion San Juan Sur', 1003);", // 3

                                "INSERT INTO Sismografo (fechaAdquicision, nroSerie, idEstadoActual, codigoEstacion, idModelo) VALUES ('2023-02-15 11:00:00', 5001, 1, 1, 1);",
                                "INSERT INTO Sismografo (fechaAdquicision, nroSerie, idEstadoActual, codigoEstacion, idModelo) VALUES ('2023-04-25 12:30:00', 5002, 1, 2, 2);",
                                "INSERT INTO Sismografo (fechaAdquicision, nroSerie, idEstadoActual, codigoEstacion, idModelo) VALUES ('2023-06-10 08:00:00', 2003, 2, 3, 3);",

                                // =============================================
                                // FASE 3: Eventos Sísmicos (10 Registros para Pruebas del CU 23)
                                // =============================================

                                // --- Escenarios de Éxito para Analista (idEstadoActual = 6)
                                "INSERT INTO EventoSismico (fechaHoraFin, fechaHoraOcurrencia, latitudEpicentro, latitudHipocentro, longitudEpicentro, longitudHipocentro, valorMagnitud, idClasificacionSismo, idMagnitudRichter, idOrigenGeneracion, idAlcanceSismo, idEstadoActual, idAnalistaSupervisor) VALUES (NULL, '2024-10-20 10:15:00', '-32.0000', '-32.0000', '-65.0000', '-65.0000', 4.5, 1, 3, 1, 3, 6, 1);", // ID
                                                                                                                                                                                                                                                                                                                                                                                                                            // 1:
                                                                                                                                                                                                                                                                                                                                                                                                                            // Aceptado
                                                                                                                                                                                                                                                                                                                                                                                                                            // (Analista
                                                                                                                                                                                                                                                                                                                                                                                                                            // 1)
                                "INSERT INTO EventoSismico (fechaHoraFin, fechaHoraOcurrencia, latitudEpicentro, latitudHipocentro, longitudEpicentro, longitudHipocentro, valorMagnitud, idClasificacionSismo, idMagnitudRichter, idOrigenGeneracion, idAlcanceSismo, idEstadoActual, idAnalistaSupervisor) VALUES (NULL, '2024-12-10 18:00:00', '-35.0000', '-35.0000', '-69.5000', '-69.5000', 2.1, 1, 2, 1, 2, 6, 1);", // ID
                                                                                                                                                                                                                                                                                                                                                                                                                            // 2:
                                                                                                                                                                                                                                                                                                                                                                                                                            // Aceptado
                                                                                                                                                                                                                                                                                                                                                                                                                            // (Analista
                                                                                                                                                                                                                                                                                                                                                                                                                            // 1)
                                "INSERT INTO EventoSismico (fechaHoraFin, fechaHoraOcurrencia, latitudEpicentro, latitudHipocentro, longitudEpicentro, longitudHipocentro, valorMagnitud, idClasificacionSismo, idMagnitudRichter, idOrigenGeneracion, idAlcanceSismo, idEstadoActual, idAnalistaSupervisor) VALUES (NULL, '2025-01-05 07:30:00', '-33.5000', '-33.5000', '-68.5000', '-68.5000', 5.8, 2, 3, 1, 3, 6, 1);", // ID
                                                                                                                                                                                                                                                                                                                                                                                                                            // 3:
                                                                                                                                                                                                                                                                                                                                                                                                                            // Aceptado
                                                                                                                                                                                                                                                                                                                                                                                                                            // (Analista
                                                                                                                                                                                                                                                                                                                                                                                                                            // 1)

                                // --- Escenarios de Éxito para Supervisor (idEstadoActual = 7)
                                "INSERT INTO EventoSismico (fechaHoraFin, fechaHoraOcurrencia, latitudEpicentro, latitudHipocentro, longitudEpicentro, longitudHipocentro, valorMagnitud, idClasificacionSismo, idMagnitudRichter, idOrigenGeneracion, idAlcanceSismo, idEstadoActual, idAnalistaSupervisor) VALUES (NULL, '2024-11-05 08:30:00', '-33.0000', '-33.0000', '-69.0000', '-69.0000', 6.2, 2, 4, 1, 4, 7, 3);", // ID
                                                                                                                                                                                                                                                                                                                                                                                                                            // 4:
                                                                                                                                                                                                                                                                                                                                                                                                                            // Derivado
                                                                                                                                                                                                                                                                                                                                                                                                                            // (Supervisor
                                                                                                                                                                                                                                                                                                                                                                                                                            // 3)
                                "INSERT INTO EventoSismico (fechaHoraFin, fechaHoraOcurrencia, latitudEpicentro, latitudHipocentro, longitudEpicentro, longitudHipocentro, valorMagnitud, idClasificacionSismo, idMagnitudRichter, idOrigenGeneracion, idAlcanceSismo, idEstadoActual, idAnalistaSupervisor) VALUES (NULL, '2025-02-20 11:15:00', '-30.5000', '-30.5000', '-66.0000', '-66.0000', 3.1, 1, 2, 1, 2, 7, 3);", // ID
                                                                                                                                                                                                                                                                                                                                                                                                                            // 5:
                                                                                                                                                                                                                                                                                                                                                                                                                            // Derivado
                                                                                                                                                                                                                                                                                                                                                                                                                            // (Supervisor
                                                                                                                                                                                                                                                                                                                                                                                                                            // 3)

                                // --- Escenarios de Fallo (Deben ser filtrados por el CU 23)
                                // 5: Detectado (Aún no Aceptado)
                                "INSERT INTO EventoSismico (fechaHoraFin, fechaHoraOcurrencia, latitudEpicentro, latitudHipocentro, longitudEpicentro, longitudHipocentro, valorMagnitud, idClasificacionSismo, idMagnitudRichter, idOrigenGeneracion, idAlcanceSismo, idEstadoActual, idAnalistaSupervisor) VALUES (NULL, '2024-12-01 15:30:00', '-29.5000', '-29.5000', '-68.0000', '-68.0000', 5.1, 2, 3, 1, 3, 5, 1);", // ID
                                                                                                                                                                                                                                                                                                                                                                                                                            // 6
                                "INSERT INTO EventoSismico (fechaHoraFin, fechaHoraOcurrencia, latitudEpicentro, latitudHipocentro, longitudEpicentro, longitudHipocentro, valorMagnitud, idClasificacionSismo, idMagnitudRichter, idOrigenGeneracion, idAlcanceSismo, idEstadoActual, idAnalistaSupervisor) VALUES (NULL, '2025-03-01 09:00:00', '-36.0000', '-36.0000', '-70.0000', '-70.0000', 1.5, 1, 1, 1, 1, 5, 1);", // ID
                                                                                                                                                                                                                                                                                                                                                                                                                            // 7
                                // 8: Confirmado (Ya revisado y cerrado)
                                "INSERT INTO EventoSismico (fechaHoraFin, fechaHoraOcurrencia, latitudEpicentro, latitudHipocentro, longitudEpicentro, longitudHipocentro, valorMagnitud, idClasificacionSismo, idMagnitudRichter, idOrigenGeneracion, idAlcanceSismo, idEstadoActual, idAnalistaSupervisor) VALUES ('2024-11-25 12:00:00', '2024-11-25 08:00:00', '-31.0000', '-31.0000', '-64.5000', '-64.5000', 3.8, 1, 2, 2, 2, 8, 1);", // ID
                                                                                                                                                                                                                                                                                                                                                                                                                                             // 8
                                "INSERT INTO EventoSismico (fechaHoraFin, fechaHoraOcurrencia, latitudEpicentro, latitudHipocentro, longitudEpicentro, longitudHipocentro, valorMagnitud, idClasificacionSismo, idMagnitudRichter, idOrigenGeneracion, idAlcanceSismo, idEstadoActual, idAnalistaSupervisor) VALUES ('2025-04-10 17:00:00', '2025-04-10 15:00:00', '-30.0000', '-30.0000', '-67.0000', '-67.0000', 7.1, 3, 4, 1, 4, 8, 3);", // ID
                                                                                                                                                                                                                                                                                                                                                                                                                                             // 9
                                // 9: Rechazado (Ya revisado y cerrado)
                                "INSERT INTO EventoSismico (fechaHoraFin, fechaHoraOcurrencia, latitudEpicentro, latitudHipocentro, longitudEpicentro, longitudHipocentro, valorMagnitud, idClasificacionSismo, idMagnitudRichter, idOrigenGeneracion, idAlcanceSismo, idEstadoActual, idAnalistaSupervisor) VALUES ('2025-05-01 10:00:00', '2025-05-01 09:00:00', '-32.5000', '-32.5000', '-65.5000', '-65.5000', 0.9, 1, 1, 2, 1, 9, 1);", // ID
                                                                                                                                                                                                                                                                                                                                                                                                                                             // 10

                                // =============================================
                                // FASE 4: Trazabilidad y Relaciones (CRÍTICO: Histórico de estados)
                                // =============================================

                                // Historial para ID 1 (Aceptado)
                                "INSERT INTO CambioEstado (fechaHoralnicio, idEstado, idEmpleado, idEventoSismico) VALUES ('2024-10-20 10:15:00', 6, 1, 1);",
                                // Historial para ID 2 (Aceptado)
                                "INSERT INTO CambioEstado (fechaHoralnicio, idEstado, idEmpleado, idEventoSismico) VALUES ('2024-12-10 18:00:00', 6, 1, 2);",
                                // Historial para ID 3 (Aceptado)
                                "INSERT INTO CambioEstado (fechaHoralnicio, idEstado, idEmpleado, idEventoSismico) VALUES ('2025-01-05 07:30:00', 6, 1, 3);",

                                // Historial para ID 4 (Derivado)
                                "INSERT INTO CambioEstado (fechaHoralnicio, idEstado, idEmpleado, idEventoSismico) VALUES ('2024-11-05 08:30:00', 7, 1, 4);",
                                // Historial para ID 5 (Derivado)
                                "INSERT INTO CambioEstado (fechaHoralnicio, idEstado, idEmpleado, idEventoSismico) VALUES ('2025-02-20 11:15:00', 7, 1, 5);",

                                // Historial para ID 6 (Detectado)
                                "INSERT INTO CambioEstado (fechaHoralnicio, idEstado, idEmpleado, idEventoSismico) VALUES ('2024-12-01 15:30:00', 5, 1, 6);",
                                // Historial para ID 7 (Detectado)
                                "INSERT INTO CambioEstado (fechaHoralnicio, idEstado, idEmpleado, idEventoSismico) VALUES ('2025-03-01 09:00:00', 5, 1, 7);",

                                // Historial para ID 8 (Confirmado)
                                "INSERT INTO CambioEstado (fechaHoralnicio, idEstado, idEmpleado, idEventoSismico) VALUES ('2024-11-25 08:00:00', 8, 1, 8);",
                                // Historial para ID 9 (Confirmado)
                                "INSERT INTO CambioEstado (fechaHoralnicio, idEstado, idEmpleado, idEventoSismico) VALUES ('2025-04-10 15:00:00', 8, 3, 9);",

                                // Historial para ID 10 (Rechazado)
                                "INSERT INTO CambioEstado (fechaHoralnicio, idEstado, idEmpleado, idEventoSismico) VALUES ('2025-05-01 09:00:00', 9, 1, 10);",

                                // --- Series Temporales (Datos de Muestras)
                                "INSERT INTO SerieTemporal (condicionAlarma, fechaHoraRegistro, frecuenciaMuestreo, idEstado) VALUES ('Normal', '2024-10-20 10:00:00', '100 Hz', 1);", // 1
                                "INSERT INTO SerieTemporal (condicionAlarma, fechaHoraRegistro, frecuenciaMuestreo, idEstado) VALUES ('Alto', '2024-11-05 08:00:00', '200 Hz', 1);", // 2
                                "INSERT INTO SerieTemporal (condicionAlarma, fechaHoraRegistro, frecuenciaMuestreo, idEstado) VALUES ('Bajo', '2024-12-10 17:55:00', '150 Hz', 1);", // 3
                                "INSERT INTO SerieTemporal (condicionAlarma, fechaHoraRegistro, frecuenciaMuestreo, idEstado) VALUES ('Normal', '2025-01-05 07:20:00', '120 Hz', 1);", // 4

                                "INSERT INTO EventoSismico_SerieTemporal (idEventoSismico, idSerieTemporal) VALUES (1, 1);",
                                "INSERT INTO EventoSismico_SerieTemporal (idEventoSismico, idSerieTemporal) VALUES (4, 2);",
                                "INSERT INTO EventoSismico_SerieTemporal (idEventoSismico, idSerieTemporal) VALUES (2, 3);",
                                "INSERT INTO EventoSismico_SerieTemporal (idEventoSismico, idSerieTemporal) VALUES (3, 4);",
                                "INSERT INTO Sismografo_SerieTemporal (identificadorSismografo, idSerieTemporal) VALUES (1, 1);",
                                "INSERT INTO Sismografo_SerieTemporal (identificadorSismografo, idSerieTemporal) VALUES (2, 2);",
                                "INSERT INTO Sismografo_SerieTemporal (identificadorSismografo, idSerieTemporal) VALUES (1, 3);",
                                "INSERT INTO Sismografo_SerieTemporal (identificadorSismografo, idSerieTemporal) VALUES (2, 4);",

                                // --- Tareas y Mantenimiento
                                "INSERT INTO OrdenDeInspeccion (fechaHoraCierre, fechaHoraFinalizacion, fechaHoraInicio, observacionCierre, idEmpleado, idEstado, codigoEstacion) VALUES (NULL, NULL, '2024-11-01 09:00:00', NULL, 2, 1, 1);",
                                "INSERT INTO TareaAsignada (comentario, fechaHoraRealizacion, codigoTarea, numeroOrden) VALUES ('Se revisó la conectividad, se encontró un cable suelto.', '2024-11-01 10:30:00', 1, 1);",
                                "INSERT INTO TareaAsignada_ApreciacionTipo (idTareaAsignada, idApreciacionTipo) VALUES (1, 2);"
                };

                conn.setAutoCommit(false);

                try (Statement s = conn.createStatement()) {
                        for (String insert : inserts) {
                                s.addBatch(insert);
                        }
                        s.executeBatch();
                        conn.commit();
                } catch (SQLException e) {
                        conn.rollback();
                        throw e;
                } finally {
                        conn.setAutoCommit(true);
                }
        }

        public static void cerrarConexion() {
                try {
                        if (connection != null && !connection.isClosed()) {
                                connection.close();
                                System.out.println("Conexión cerrada");
                        }
                } catch (SQLException e) {
                        System.err.println("Error cerrando conexión: " + e.getMessage());
                }
        }
}