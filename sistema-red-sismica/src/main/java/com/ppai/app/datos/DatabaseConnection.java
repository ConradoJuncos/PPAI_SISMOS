package com.ppai.app.datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {

    private static final String DB_URL = "jdbc:sqlite:redSismica.db";

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL);
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }
            return conn;
        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos: " + e.getMessage());
            return null;
        }
    }

    public static void initDatabase() {
        System.out.println("Inicializando Base de Datos...");
        try (Connection conn = getConnection()) {
            if (conn == null) {
                System.err.println("No se pudo establecer conexión con la base de datos.");
                return;
            }

            dropAllTables(conn);
            createTables(conn);
            insertSampleData(conn);
            cerrarConexion(conn);

            System.out.println("Base de datos creada y datos iniciales cargados.");
            System.out.println("Base de Datos inicializada correctamente.");
        } catch (SQLException e) {
            System.err.println("Error inicializando la base de datos: " + e.getMessage());
        }
    }

    private static void dropAllTables(Connection conn) throws SQLException {
        System.out.println("Eliminando tablas existentes...");
        List<String> dropStatements = new ArrayList<>();

        // Desactivar temporalmente las foreign keys para poder eliminar las tablas
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = OFF;");
        }

        // Eliminar tablas en orden inverso a la creación (para evitar conflictos de FK)
        dropStatements.add("DROP TABLE IF EXISTS MuestraSismica_DetalleMuestraSismica;");
        dropStatements.add("DROP TABLE IF EXISTS SerieTemporal_MuestraSismica;");
        dropStatements.add("DROP TABLE IF EXISTS DetalleMuestraSismica;");
        dropStatements.add("DROP TABLE IF EXISTS MuestraSismica;");
        dropStatements.add("DROP TABLE IF EXISTS SerieTemporal;");
        dropStatements.add("DROP TABLE IF EXISTS EventoSismico;");
        dropStatements.add("DROP TABLE IF EXISTS Usuario;");
        dropStatements.add("DROP TABLE IF EXISTS CambioEstado;");
        dropStatements.add("DROP TABLE IF EXISTS Sismografo;");
        dropStatements.add("DROP TABLE IF EXISTS EstacionSismologica;");
        dropStatements.add("DROP TABLE IF EXISTS ModeloSismografo;");
        dropStatements.add("DROP TABLE IF EXISTS Estado;");
        dropStatements.add("DROP TABLE IF EXISTS Empleado;");
        dropStatements.add("DROP TABLE IF EXISTS MagnitudRichter;");
        dropStatements.add("DROP TABLE IF EXISTS OrigenDeGeneracion;");
        dropStatements.add("DROP TABLE IF EXISTS AlcanceSismo;");
        dropStatements.add("DROP TABLE IF EXISTS ClasificacionSismo;");
        dropStatements.add("DROP TABLE IF EXISTS TipoDeDato;");

        try (Statement stmt = conn.createStatement()) {
            for (String dropSql : dropStatements) {
                stmt.execute(dropSql);
            }
        }

        // Reactivar las foreign keys
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }

        System.out.println("Tablas eliminadas correctamente.");
    }

    private static void createTables(Connection conn) throws SQLException {
        List<String> tables = new ArrayList<>();

        // BASE DE DATOS RED SISMICA

        tables.add("""
                CREATE TABLE IF NOT EXISTS AlcanceSismo (
                    idAlcanceSismo INTEGER,
                    nombre TEXT,
                    descripcion TEXT,
                    PRIMARY KEY (idAlcanceSismo)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS MagnitudRichter (
                    numero INTEGER,
                    descripcion TEXT,
                    PRIMARY KEY (numero)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS ClasificacionSismo (
                idClasificacionSismo INTEGER,
                kmProfundidadDesde REAL,
                kmProfundidadHasta REAL,
                nombre TEXT,
                PRIMARY KEY (idClasificacionSismo)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS OrigenDeGeneracion (
                idOrigenDeGeneracion INTEGER,
                descripcion TEXT,
                nombre TEXT,
                PRIMARY KEY (idOrigenDeGeneracion)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS TipoDeDato (
                idTipoDeDato INTEGER,
                denominacion TEXT,
                nombreUnidadMedida TEXT,
                valorUmbral REAL,
                PRIMARY KEY (idTipoDeDato)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS Empleado (
                idEmpleado INTEGER,
                apellido TEXT,
                mail TEXT,
                nombre TEXT,
                telefono TEXT,
                PRIMARY KEY (idEmpleado)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS Usuario (
                idUsuario INTEGER,
                contraseña TEXT,
                nombreUsuario TEXT,
                idEmpleado INTEGER,
                PRIMARY KEY (idUsuario),
                FOREIGN KEY (idEmpleado) REFERENCES Empleado(idEmpleado)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS EventoSismico (
                idEventoSismico INTEGER,
                fechaHoraFin TEXT,
                fechaHoraOcurrencia TEXT,
                latitudEpicentro TEXT,
                latitudHipocntro TEXT,
                longitudEpicentro TEXT,
                longitudHipocentro TEXT,
                valorMagnitud REAL,
                idClasificacionSismo INTEGER,
                magnitudRichter INTEGER,
                idOrigenDeGeneracion INTEGER,
                idAlcanceSismo INTEGER,
                idEstadoActual INTEGER,
                nombreEstadoActual TEXT,
                idEmpleado INTEGER,
                PRIMARY KEY (idEventoSismico),
                FOREIGN KEY (idClasificacionSismo) REFERENCES ClasificacionSismo(idClasificacionSismo),
                FOREIGN KEY (magnitudRichter) REFERENCES MagnitudRichter(numero),
                FOREIGN KEY (idOrigenDeGeneracion) REFERENCES OrigenDeGeneracion(idOrigenDeGeneracion),
                FOREIGN KEY (idAlcanceSismo) REFERENCES AlcanceSismo(idAlcanceSismo),
                FOREIGN KEY (idEmpleado) REFERENCES Empleado(idEmpleado)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS EstadoSerieTemporal (
                idEstadoSerieTemporal INTEGER,
                nombre TEXT,
                PRIMARY KEY (idEstadoSerieTemporal)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS SerieTemporal (
                idSerieTemporal INTEGER,
                condicionAlarma TEXT,
                fechaHoraRegistro TEXT,
                frecuenciaMuestreo TEXT,
                idEstadoSerieTemporal INTEGER,
                idEventoSismico INTEGER,
                idSismografo INTEGER,
                PRIMARY KEY (idSerieTemporal),
                FOREIGN KEY (idEstadoSerieTemporal) REFERENCES EstadoSerieTemporal(idEstadoSerieTemporal),
                FOREIGN KEY (idEventoSismico) REFERENCES EventoSismico(idEventoSismico),
                FOREIGN KEY (idSismografo) REFERENCES Sismografo(identificadorSismografo)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS MuestraSismica (
                idMuestraSismica INTEGER,
                fechaHoraMuestraSismica TEXT,
                idSerieTemporal INTEGER,
                PRIMARY KEY (idMuestraSismica),
                FOREIGN KEY (idSerieTemporal) REFERENCES SerieTemporal(idSerieTemporal)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS DetalleMuestraSismica (
                idDetalleMuestraSismica INTEGER,
                idTipoDeDato INTEGER,
                valor REAL,
                idMuestraSismica INTEGER,
                PRIMARY KEY (idDetalleMuestraSismica),
                FOREIGN KEY (idTipoDeDato) REFERENCES TipoDeDato(idTipoDeDato),
                FOREIGN KEY (idMuestraSismica) REFERENCES MuestraSismica(idMuestraSismica)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS EstacionSismologica (
                idEstacionSismologica INTEGER,
                documentoCertificacionAdq TEXT,
                fechaSolicitudCertificacion TEXT,
                latitud REAL,
                longitud REAL,
                nombre TEXT,
                nroCertificacionAdquisicion INTEGER,
                PRIMARY KEY (idEstacionSismologica)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS EstadoSismografo (
                idEstadoSismografo INTEGER,
                nombre TEXT,
                PRIMARY KEY (idEstadoSismografo)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS Sismografo (
                identificadorSismografo INTEGER,
                fechaAdquisicion TEXT,
                nroSerie INTEGER,
                idEstadoSismografo INTEGER,
                idEstacionSismologica INTEGER,
                PRIMARY KEY (identificadorSismografo),
                FOREIGN KEY (idEstadoSismografo) REFERENCES EstadoSismografo(idEstadoSismografo),
                FOREIGN KEY (idEstacionSismologica) REFERENCES EstacionSismologica(idEstacionSismologica)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS CambioEstadoSismografo (
                fechaHoraInicio TEXT,
                identificadorSismografo INTEGER,
                fechaHoraFin TEXT,
                idEstadoSismografo INTEGER,
                PRIMARY KEY (fechaHoraInicio, identificadorSismografo),
                FOREIGN KEY (identificadorSismografo) REFERENCES Sismografo(identificadorSismografo),
                FOREIGN KEY (idEstadoSismografo) REFERENCES EstadoSismografo(idEstadoSismografo)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS AutoDetectado (
                idAutoDetectado INTEGER,
                nombre TEXT,
                PRIMARY KEY (idAutoDetectado)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS BloqueadoEnRevision (
                idBloqueadoEnRevision INTEGER,
                nombre TEXT,
                PRIMARY KEY (idBloqueadoEnRevision)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS Rechazado (
                idRechazado INTEGER,
                nombre TEXT,
                PRIMARY KEY (idRechazado)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS Derivado (
                idDerivado INTEGER,
                nombre TEXT,
                PRIMARY KEY (idDerivado)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS ConfirmadoPorPersonal (
                idConfirmadoPorPersonal INTEGER,
                nombre TEXT,
                PRIMARY KEY (idConfirmadoPorPersonal)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS PendienteDeRevision (
                idPendienteDeRevision INTEGER,
                nombre TEXT,
                PRIMARY KEY (idPendienteDeRevision)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS SinRevision (
                idSinRevision INTEGER,
                nombre TEXT,
                PRIMARY KEY (idSinRevision)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS Cerrado (
                idCerrado INTEGER,
                nombre TEXT,
                PRIMARY KEY (idCerrado)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS PendienteDeCierre (
                idPendienteDeCierre INTEGER,
                nombre TEXT,
                PRIMARY KEY (idPendienteDeCierre)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS AutoConfirmado (
                idAutoConfirmado INTEGER,
                nombre TEXT,
                PRIMARY KEY (idAutoConfirmado)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS CambioEstado (
                idCambioEstado INTEGER,
                fechaHoraInicio TEXT,
                idEventoSismico INTEGER,
                fechaHoraFin TEXT,
                idEmpleado INTEGER,
                nombreEstado TEXT,
                PRIMARY KEY (idCambioEstado),
                FOREIGN KEY (idEventoSismico) REFERENCES EventoSismico(idEventoSismico),
                FOREIGN KEY (idEmpleado) REFERENCES Empleado(idEmpleado)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS CambioEstado_AutoDetectado (
                idCambioEstado INTEGER,
                idAutoDetectado INTEGER,
                PRIMARY KEY (idCambioEstado, idAutoDetectado),
                FOREIGN KEY (idCambioEstado) REFERENCES CambioEstado(idCambioEstado),
                FOREIGN KEY (idAutoDetectado) REFERENCES AutoDetectado(idAutoDetectado)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS CambioEstado_BloqueadoEnRevision (
                idCambioEstado INTEGER,
                idBloqueadoEnRevision INTEGER,
                PRIMARY KEY (idCambioEstado, idBloqueadoEnRevision),
                FOREIGN KEY (idCambioEstado) REFERENCES CambioEstado(idCambioEstado),
                FOREIGN KEY (idBloqueadoEnRevision) REFERENCES BloqueadoEnRevision(idBloqueadoEnRevision)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS CambioEstado_Derivado (
                idCambioEstado INTEGER,
                idDerivado INTEGER,
                PRIMARY KEY (idCambioEstado, idDerivado),
                FOREIGN KEY (idCambioEstado) REFERENCES CambioEstado(idCambioEstado),
                FOREIGN KEY (idDerivado) REFERENCES Derivado(idDerivado)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS CambioEstado_Rechazado (
                idCambioEstado INTEGER,
                idRechazado INTEGER,
                PRIMARY KEY (idCambioEstado, idRechazado),
                FOREIGN KEY (idCambioEstado) REFERENCES CambioEstado(idCambioEstado),
                FOREIGN KEY (idRechazado) REFERENCES Rechazado(idRechazado)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS CambioEstado_ConfirmadoPorPersonal (
                idCambioEstado INTEGER,
                idConfirmadoPorPersonal INTEGER,
                PRIMARY KEY (idCambioEstado, idConfirmadoPorPersonal),
                FOREIGN KEY (idCambioEstado) REFERENCES CambioEstado(idCambioEstado),
                FOREIGN KEY (idConfirmadoPorPersonal) REFERENCES ConfirmadoPorPersonal(idConfirmadoPorPersonal)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS CambioEstado_PendienteDeRevision (
                idCambioEstado INTEGER,
                idPendienteDeRevision INTEGER,
                PRIMARY KEY (idCambioEstado, idPendienteDeRevision),
                FOREIGN KEY (idCambioEstado) REFERENCES CambioEstado(idCambioEstado),
                FOREIGN KEY (idPendienteDeRevision) REFERENCES PendienteDeRevision(idPendienteDeRevision)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS CambioEstado_Cerrado (
                idCambioEstado INTEGER,
                idCerrado INTEGER,
                PRIMARY KEY (idCambioEstado, idCerrado),
                FOREIGN KEY (idCambioEstado) REFERENCES CambioEstado(idCambioEstado),
                FOREIGN KEY (idCerrado) REFERENCES Cerrado(idCerrado)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS CambioEstado_PendienteDeCierre (
                idCambioEstado INTEGER,
                idPendienteDeCierre INTEGER,
                PRIMARY KEY (idCambioEstado, idPendienteDeCierre),
                FOREIGN KEY (idCambioEstado) REFERENCES CambioEstado(idCambioEstado),
                FOREIGN KEY (idPendienteDeCierre) REFERENCES PendienteDeCierre(idPendienteDeCierre)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS CambioEstado_AutoConfirmado (
                idCambioEstado INTEGER,
                idAutoConfirmado INTEGER,
                PRIMARY KEY (idCambioEstado, idAutoConfirmado),
                FOREIGN KEY (idCambioEstado) REFERENCES CambioEstado(idCambioEstado),
                FOREIGN KEY (idAutoConfirmado) REFERENCES AutoConfirmado(idAutoConfirmado)
            );
            """);

        try (Statement s = conn.createStatement()) {
            for (String tableSql : tables) {
                s.execute(tableSql);
            }
        }
    }

    public static void insertSampleData(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // Inserciones en orden para respetar FK.

            // AlcanceSismo (opcional, no prohibida)
            stmt.executeUpdate("""
                INSERT OR IGNORE INTO AlcanceSismo (idAlcanceSismo, nombre, descripcion) VALUES
                (1, 'Local', 'Perceptible solo cerca del epicentro'),
                (2, 'Regional', 'Percibido en una región amplia'),
                (3, 'Instrumental', 'Registrado principalmente por instrumentos')
            """);

            // MagnitudRichter
            stmt.executeUpdate("""
                INSERT OR IGNORE INTO MagnitudRichter (numero, descripcion) VALUES
                (3, 'Menor: generalmente no se siente'),
                (4, 'Ligero: se siente, daños menores'),
                (5, 'Moderado: daños leves en estructuras débiles')
            """);

            // ClasificacionSismo
            stmt.executeUpdate("""
                INSERT OR IGNORE INTO ClasificacionSismo (idClasificacionSismo, kmProfundidadDesde, kmProfundidadHasta, nombre) VALUES
                (1, 0.0, 70.0, 'Superficial'),
                (2, 70.1, 300.0, 'Intermedio'),
                (3, 300.1, 700.0, 'Profundo')
            """);

            // OrigenDeGeneracion
            stmt.executeUpdate("""
                INSERT OR IGNORE INTO OrigenDeGeneracion (idOrigenDeGeneracion, descripcion, nombre) VALUES
                (1, 'Movimiento en zona de subducción entre placas', 'Sismo interplaca'),
                (2, 'Movimiento en fallas dentro de una misma placa', 'Sismo cortical')
            """);

            // TipoDeDato (frecuencia, longitud, velocidad de onda)
            stmt.executeUpdate("""
                INSERT OR IGNORE INTO TipoDeDato (idTipoDeDato, denominacion, nombreUnidadMedida, valorUmbral) VALUES
                (1, 'Frecuencia de Onda', 'Hz', 15.0),
                (2, 'Longitud de Onda', 'km/ciclo', 1.5),
                (3, 'Velocidad de Onda', 'km/s', 8.0)
            """);

            // Empleado (único registro)
            stmt.executeUpdate("""
                INSERT OR IGNORE INTO Empleado (idEmpleado, apellido, mail, nombre, telefono) VALUES
                (1, 'Trump', 'laura.perez@ccrs.gob.ar', 'Donald', '+54 9 351 555-1001')
            """);

            // Usuario (único registro, FK a Empleado)
            stmt.executeUpdate("""
                INSERT OR IGNORE INTO Usuario (idUsuario, contraseña, nombreUsuario, idEmpleado) VALUES
                (1, 'claveSegura123', 'POTUSDonaldTrump', 1)
            """);

            // EstadoSerieTemporal (Activo / Inactivo)
            stmt.executeUpdate("""
                INSERT OR IGNORE INTO EstadoSerieTemporal (idEstadoSerieTemporal, nombre) VALUES
                (1, 'Activo'),
                (2, 'Inactivo')
            """);

            // EstadoSismografo (Disponible, EnInstalacion, EnLinea, FueraDeServicio)
            stmt.executeUpdate("""
                INSERT OR IGNORE INTO EstadoSismografo (idEstadoSismografo, nombre) VALUES
                (1, 'Disponible'),
                (2, 'EnInstalacion'),
                (3, 'EnLinea'),
                (4, 'FueraDeServicio')
            """);

            // EstacionSismologica (muestras básicas)
            stmt.executeUpdate("""
                INSERT OR IGNORE INTO EstacionSismologica (idEstacionSismologica, documentoCertificacionAdq, fechaSolicitudCertificacion, latitud, longitud, nombre, nroCertificacionAdquisicion) VALUES
                (1, NULL, NULL, -31.4201, -64.1888, 'Estación Córdoba', NULL),
                (2, NULL, NULL, -31.5375, -68.5364, 'Estación San Juan', NULL)
            """);

            // Sismografo (3 equipos para los 3 eventos)
            stmt.executeUpdate("""
                INSERT OR IGNORE INTO Sismografo (identificadorSismografo, fechaAdquisicion, nroSerie, idEstadoSismografo, idEstacionSismologica) VALUES
                (1, '2024-11-02 00:00:00', 1523, 3, 1),
                (2, '2024-11-03 00:00:00', 1524, 3, 1),
                (3, '2024-11-04 00:00:00', 1525, 3, 2)
            """);

            stmt.executeUpdate("""
                INSERT OR IGNORE INTO AutoDetectado (idAutoDetectado, nombre) VALUES
                (1, 'AutoDetectado'),
                (2, 'AutoDetectado'),
                (3, 'AutoDetectado')
            """);

            // EventoSismico (3 eventos: 1 AutoDetectado, 1 BloqueadoEnRevision, 1 ConfirmadoPorPersonal)
            stmt.executeUpdate("""
                INSERT OR IGNORE INTO EventoSismico (idEventoSismico, fechaHoraFin, fechaHoraOcurrencia, latitudEpicentro, latitudHipocntro, longitudEpicentro, longitudHipocentro, valorMagnitud, idClasificacionSismo, magnitudRichter, idOrigenDeGeneracion, idAlcanceSismo, idEstadoActual, nombreEstadoActual, idEmpleado) VALUES
                (1, NULL, '2025-02-21 19:05:41', '-31.4201', '-31.4201', '-64.1888', '-64.1888', 4.3, 2, 4, 1, 2, 1, 'AutoDetectado', NULL),
                (2, NULL, '2025-03-15 14:30:20', '-31.5375', '-31.5375', '-68.5364', '-68.5364', 5.1, 3, 5, 2, 2, 2, 'AutoDetectado', NULL),
                (3, NULL, '2025-04-10 08:15:10', '-32.1234', '-32.1234', '-65.4321', '-65.4321', 3.8, 1, 3, 1, 1, 3, 'AutoDetectado', NULL)
            """);

            stmt.executeUpdate("""
                INSERT OR IGNORE INTO CambioEstado (idCambioEstado, fechaHoraInicio, idEventoSismico, fechaHoraFin, idEmpleado, nombreEstado) VALUES
                (1, '2025-02-21 19:05:41.51351', 1, NULL, NULL, 'AutoDetectado'),
                (2, '2025-03-15 14:30:20.78465', 2, NULL, NULL, 'AutoDetectado'),
                (3, '2025-04-10 08:15:10.98253', 3, NULL, NULL, 'AutoDetectado')
            """);

            stmt.executeUpdate("""
                INSERT OR IGNORE INTO CambioEstado_AutoDetectado (idCambioEstado, idAutoDetectado) VALUES
                (1, 1),
                (2, 2),
                (3, 3)
            """);

            // SerieTemporal (9 series: 3 por cada evento)
            stmt.executeUpdate("""
                INSERT OR IGNORE INTO SerieTemporal (idSerieTemporal, condicionAlarma, fechaHoraRegistro, frecuenciaMuestreo, idEstadoSerieTemporal, idEventoSismico, idSismografo) VALUES
                -- Series del Evento 1
                (1, 'umbral alto', '2025-02-21 19:05:41', '50.0', 1, 1, 1),
                (2, 'umbral medio', '2025-02-21 19:05:42', '50.0', 1, 1, 1),
                (3, 'umbral bajo', '2025-02-21 19:05:43', '50.0', 2, 1, 1),
                -- Series del Evento 2
                (4, 'umbral alto', '2025-03-15 14:30:20', '60.0', 1, 2, 2),
                (5, 'umbral medio', '2025-03-15 14:30:21', '60.0', 1, 2, 2),
                (6, 'umbral bajo', '2025-03-15 14:30:22', '60.0', 1, 2, 2),
                -- Series del Evento 3
                (7, 'umbral alto', '2025-04-10 08:15:10', '40.0', 1, 3, 3),
                (8, 'umbral medio', '2025-04-10 08:15:11', '40.0', 2, 3, 3),
                (9, 'umbral bajo', '2025-04-10 08:15:12', '40.0', 1, 3, 3)
            """);

            // MuestraSismica (27 muestras: 3 por cada serie)
            stmt.executeUpdate("""
                INSERT OR IGNORE INTO MuestraSismica (idMuestraSismica, fechaHoraMuestraSismica, idSerieTemporal) VALUES
                -- Muestras de Serie 1
                (1, '2025-02-21 19:05:41', 1), (2, '2025-02-21 19:05:46', 1), (3, '2025-02-21 19:05:51', 1),
                -- Muestras de Serie 2
                (4, '2025-02-21 19:05:42', 2), (5, '2025-02-21 19:05:47', 2), (6, '2025-02-21 19:05:52', 2),
                -- Muestras de Serie 3
                (7, '2025-02-21 19:05:43', 3), (8, '2025-02-21 19:05:48', 3), (9, '2025-02-21 19:05:53', 3),
                -- Muestras de Serie 4
                (10, '2025-03-15 14:30:20', 4), (11, '2025-03-15 14:30:25', 4), (12, '2025-03-15 14:30:30', 4),
                -- Muestras de Serie 5
                (13, '2025-03-15 14:30:21', 5), (14, '2025-03-15 14:30:26', 5), (15, '2025-03-15 14:30:31', 5),
                -- Muestras de Serie 6
                (16, '2025-03-15 14:30:22', 6), (17, '2025-03-15 14:30:27', 6), (18, '2025-03-15 14:30:32', 6),
                -- Muestras de Serie 7
                (19, '2025-04-10 08:15:10', 7), (20, '2025-04-10 08:15:15', 7), (21, '2025-04-10 08:15:20', 7),
                -- Muestras de Serie 8
                (22, '2025-04-10 08:15:11', 8), (23, '2025-04-10 08:15:16', 8), (24, '2025-04-10 08:15:21', 8),
                -- Muestras de Serie 9
                (25, '2025-04-10 08:15:12', 9), (26, '2025-04-10 08:15:17', 9), (27, '2025-04-10 08:15:22', 9)
            """);

            // DetalleMuestraSismica (81 detalles: 3 por cada muestra, uno de cada tipo)
            stmt.executeUpdate("""
                INSERT OR IGNORE INTO DetalleMuestraSismica (idDetalleMuestraSismica, idTipoDeDato, valor, idMuestraSismica) VALUES
                -- Detalles de Muestra 1
                (1, 1, 10.00, 1), (2, 2, 0.70, 1), (3, 3, 7.00, 1),
                -- Detalles de Muestra 2
                (4, 1, 10.05, 2), (5, 2, 0.69, 2), (6, 3, 7.02, 2),
                -- Detalles de Muestra 3
                (7, 1, 9.98, 3), (8, 2, 0.71, 3), (9, 3, 6.99, 3),
                -- Detalles de Muestra 4
                (10, 1, 10.10, 4), (11, 2, 0.68, 4), (12, 3, 7.05, 4),
                -- Detalles de Muestra 5
                (13, 1, 10.02, 5), (14, 2, 0.72, 5), (15, 3, 7.01, 5),
                -- Detalles de Muestra 6
                (16, 1, 9.95, 6), (17, 2, 0.73, 6), (18, 3, 6.98, 6),
                -- Detalles de Muestra 7
                (19, 1, 10.08, 7), (20, 2, 0.67, 7), (21, 3, 7.03, 7),
                -- Detalles de Muestra 8
                (22, 1, 10.12, 8), (23, 2, 0.74, 8), (24, 3, 7.06, 8),
                -- Detalles de Muestra 9
                (25, 1, 9.92, 9), (26, 2, 0.75, 9), (27, 3, 6.96, 9),
                -- Detalles de Muestra 10
                (28, 1, 11.00, 10), (29, 2, 0.80, 10), (30, 3, 7.50, 10),
                -- Detalles de Muestra 11
                (31, 1, 11.05, 11), (32, 2, 0.79, 11), (33, 3, 7.52, 11),
                -- Detalles de Muestra 12
                (34, 1, 10.98, 12), (35, 2, 0.81, 12), (36, 3, 7.49, 12),
                -- Detalles de Muestra 13
                (37, 1, 11.10, 13), (38, 2, 0.78, 13), (39, 3, 7.55, 13),
                -- Detalles de Muestra 14
                (40, 1, 11.02, 14), (41, 2, 0.82, 14), (42, 3, 7.51, 14),
                -- Detalles de Muestra 15
                (43, 1, 10.95, 15), (44, 2, 0.83, 15), (45, 3, 7.48, 15),
                -- Detalles de Muestra 16
                (46, 1, 11.08, 16), (47, 2, 0.77, 16), (48, 3, 7.53, 16),
                -- Detalles de Muestra 17
                (49, 1, 11.12, 17), (50, 2, 0.84, 17), (51, 3, 7.56, 17),
                -- Detalles de Muestra 18
                (52, 1, 10.92, 18), (53, 2, 0.85, 18), (54, 3, 7.46, 18),
                -- Detalles de Muestra 19
                (55, 1, 9.50, 19), (56, 2, 0.65, 19), (57, 3, 6.80, 19),
                -- Detalles de Muestra 20
                (58, 1, 9.55, 20), (59, 2, 0.64, 20), (60, 3, 6.82, 20),
                -- Detalles de Muestra 21
                (61, 1, 9.48, 21), (62, 2, 0.66, 21), (63, 3, 6.79, 21),
                -- Detalles de Muestra 22
                (64, 1, 9.60, 22), (65, 2, 0.63, 22), (66, 3, 6.85, 22),
                -- Detalles de Muestra 23
                (67, 1, 9.52, 23), (68, 2, 0.67, 23), (69, 3, 6.81, 23),
                -- Detalles de Muestra 24
                (70, 1, 9.45, 24), (71, 2, 0.68, 24), (72, 3, 6.78, 24),
                -- Detalles de Muestra 25
                (73, 1, 9.58, 25), (74, 2, 0.62, 25), (75, 3, 6.83, 25),
                -- Detalles de Muestra 26
                (76, 1, 9.62, 26), (77, 2, 0.69, 26), (78, 3, 6.86, 26),
                -- Detalles de Muestra 27
                (79, 1, 9.42, 27), (80, 2, 0.70, 27), (81, 3, 6.76, 27)
            """);

            System.out.println("Datos iniciales insertados correctamente (tablas llenas según requerimientos). ");
        } catch (SQLException e) {
            System.err.println("Error insertando datos: " + e.getMessage());
            throw e;
        }
    }

    public static void cerrarConexion(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Conexión cerrada.");
            }
        } catch (SQLException e) {
            System.err.println("Error cerrando conexión: " + e.getMessage());
        }
    }
}
