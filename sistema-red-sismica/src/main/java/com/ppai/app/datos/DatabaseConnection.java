package com.ppai.app.datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {

    private static final String DB_URL = "jdbc:sqlite:redSismica.sqlite3";

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
        dropStatements.add("DROP TABLE IF EXISTS EventoSismico_CambioEstado;");
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

        tables.add("""
                    CREATE TABLE IF NOT EXISTS ClasificacionSismo (
                        idClasificacionSismo INTEGER NOT NULL,
                        kmProfundidadDesde REAL,
                        kmProfundidadHasta REAL,
                        nombre TEXT,
                        PRIMARY KEY (idClasificacionSismo)
                    );
                """);

        tables.add("""
                    CREATE TABLE IF NOT EXISTS AlcanceSismo (
                        idAlcanceSismo INTEGER NOT NULL,
                        descripcion TEXT,
                        nombre TEXT,
                        PRIMARY KEY (idAlcanceSismo)
                    );
                """);

        tables.add("""
                    CREATE TABLE IF NOT EXISTS OrigenDeGeneracion (
                        idOrigenDeGeneracion INTEGER NOT NULL,
                        descripcion TEXT,
                        nombre TEXT,
                        PRIMARY KEY (idOrigenDeGeneracion)
                    );
                """);

        tables.add("""
                    CREATE TABLE IF NOT EXISTS MagnitudRichter (
                        numero INTEGER NOT NULL,
                        descripcion TEXT,
                        PRIMARY KEY (numero)
                    );
                """);

        tables.add("CREATE TABLE IF NOT EXISTS Empleado (\n" +
                "    idEmpleado INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    nombre TEXT NOT NULL,\n" +
                "    apellido TEXT NOT NULL,\n" +
                "    email TEXT UNIQUE NOT NULL,\n" +
                "    telefono TEXT,\n" +
                "    legajo TEXT UNIQUE NOT NULL,\n" +
                "    idRol INTEGER,\n" +
                "    FOREIGN KEY (idRol) REFERENCES Rol(idRol)\n" +
                ");");

        tables.add("CREATE TABLE Estado (\n" +
                "    ambitoEstado TEXT NOT NULL,\n" +
                "    nombreEstado TEXT NOT NULL,\n" +
                "    PRIMARY KEY (ambitoEstado, nombreEstado)\n" +
                ");");

        tables.add("""
                    CREATE TABLE IF NOT EXISTS ModeloSismografo (
                        idModeloSismografo INTEGER PRIMARY KEY AUTOINCREMENT,
                        caracteristicas TEXT,
                        nombreModelo TEXT NOT NULL,
                        idFabricante INTEGER NOT NULL,
                        FOREIGN KEY (idFabricante) REFERENCES Fabricante(idFabricante)
                    );
                """);

        tables.add("CREATE TABLE IF NOT EXISTS EstacionSismologica (\n" +
                "    codigoEstacion INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    nombre TEXT NOT NULL,\n" +
                "    latitud REAL NOT NULL,\n" +
                "    longitud REAL NOT NULL,\n" +
                "    documentoCertificacionAdq TEXT,\n" +
                "    fechaSolicitudCertificacion TEXT,\n" +
                "    nroCertificacionAdquisicion INTEGER\n" +
                ");");

        tables.add("CREATE TABLE IF NOT EXISTS Sismografo (\n" +
                "    identificadorSismografo INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    fechaAdquisicion DATETIME NOT NULL,\n" +
                "    nroSerie INTEGER UNIQUE NOT NULL,\n" +
                "    idModelo INTEGER NOT NULL,\n" +
                "    codigoEstacion INTEGER NOT NULL,\n" +
                "    FOREIGN KEY (idModelo) REFERENCES ModeloSismografo(idModeloSismografo),\n" +
                "    FOREIGN KEY (codigoEstacion) REFERENCES EstacionSismologica(codigoEstacion)\n" +
                ");");

        tables.add("""
                    CREATE TABLE IF NOT EXISTS Usuario (
                        idUsuario INTEGER PRIMARY KEY AUTOINCREMENT,
                        nombreUsuario TEXT NOT NULL UNIQUE,
                        contraseña TEXT NOT NULL,
                        idEmpleado INTEGER NOT NULL,
                        FOREIGN KEY (idEmpleado) REFERENCES Empleado(idEmpleado)
                    );
                """);

        tables.add("CREATE TABLE IF NOT EXISTS TipoDeDato (\n" +
                "    idTipoDeDato INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    denominacion TEXT NOT NULL,\n" +
                "    nombreUnidadMedida TEXT NOT NULL,\n" +
                "    valorUmbral REAL\n" +
                ");");

        tables.add("CREATE TABLE IF NOT EXISTS EventoSismico (\n" +
                "    idEventoSismico INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    fechaHoraFin DATETIME NOT NULL,\n" +
                "    fechaHoraOcurrencia DATETIME NOT NULL,\n" +
                "    latitudEpicentro REAL NOT NULL,\n" +
                "    longitudEpicentro REAL NOT NULL,\n" +
                "    latitudHipocentro REAL,\n" +
                "    longitudHipocentro REAL,\n" +
                "    valorMagnitud REAL,\n" +
                "    magnitudRichter INTEGER NOT NULL,\n" +
                "    idClasificacionSismo INTEGER NOT NULL,\n" +
                "    idOrigenGeneracionSismo INTEGER NOT NULL,\n" +
                "    idAlcanceSismo INTEGER NOT NULL,\n" +
                "    ambitoEstado TEXT NOT NULL,\n" +
                "    nombreEstado TEXT NOT NULL,\n" +
                "    idAnalistaSupervisor INTEGER,\n" +
                "    FOREIGN KEY (idAnalistaSupervisor) REFERENCES Empleado(idEmpleado),\n" +
                "    FOREIGN KEY (ambitoEstado, nombreEstado) REFERENCES Estado(ambitoEstado, nombreEstado)\n" +
                ");");

        tables.add("CREATE TABLE IF NOT EXISTS SerieTemporal (\n" +
                "    idSerieTemporal INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    fechaHoraRegistro DATETIME NOT NULL,\n" +
                "    frecuenciaMuestreo REAL NOT NULL,\n" +
                "    condicionAlarma TEXT,\n" +
                "    nombreEstado TEXT NOT NULL,\n" +
                "    ambitoEstado TEXT NOT NULL,\n" +
                "    idEventoSismico INTEGER NOT NULL,\n" +
                "    codigoEstacion INTEGER NOT NULL,\n" +
                "    idSismografo INTEGER NOT NULL,\n" +
                "    FOREIGN KEY (idEventoSismico) REFERENCES EventoSismico(idEventoSismico),\n" +
                "    FOREIGN KEY (codigoEstacion) REFERENCES EstacionSismologica(codigoEstacion),\n" +
                "    FOREIGN KEY (idSismografo) REFERENCES Sismografo(identificadorSismografo),\n" +
                "    FOREIGN KEY (ambitoEstado, nombreEstado) REFERENCES Estado(ambitoEstado, nombreEstado)\n" +
                ");");

        tables.add("CREATE TABLE IF NOT EXISTS MuestraSismica (\n" +
                "    idMuestraSismica INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    fechaHoraMuestraSismica DATETIME NOT NULL,\n" +
                "    idSerieTemporal INTEGER NOT NULL,\n" +
                "    FOREIGN KEY (idSerieTemporal) REFERENCES SerieTemporal(idSerieTemporal)\n" +
                ");");

        tables.add("CREATE TABLE IF NOT EXISTS DetalleMuestraSismica (\n" +
                "    idDetalleMuestraSismica INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    valor REAL NOT NULL,\n" +
                "    idMuestraSismica INTEGER NOT NULL,\n" +
                "    idTipoDeDato INTEGER NOT NULL,\n" +
                "    FOREIGN KEY (idMuestraSismica) REFERENCES MuestraSismica(idMuestraSismica),\n" +
                "    FOREIGN KEY (idTipoDeDato) REFERENCES TipoDeDato(idTipoDeDato)\n" +
                ");");

        tables.add("""
                    CREATE TABLE IF NOT EXISTS SerieTemporal_MuestraSismica (
                        idSerieTemporal INTEGER NOT NULL,
                        idMuestraSismica INTEGER NOT NULL,
                        PRIMARY KEY (idSerieTemporal, idMuestraSismica),
                        FOREIGN KEY (idSerieTemporal) REFERENCES SerieTemporal(idSerieTemporal),
                        FOREIGN KEY (idMuestraSismica) REFERENCES MuestraSismica(idMuestraSismica)
                    );
                """);

        tables.add("""
                    CREATE TABLE IF NOT EXISTS MuestraSismica_DetalleMuestraSismica (
                        idMuestraSismica INTEGER NOT NULL,
                        idDetalleMuestraSismica INTEGER NOT NULL,
                        PRIMARY KEY (idMuestraSismica, idDetalleMuestraSismica),
                        FOREIGN KEY (idMuestraSismica) REFERENCES MuestraSismica(idMuestraSismica),
                        FOREIGN KEY (idDetalleMuestraSismica) REFERENCES DetalleMuestraSismica(idDetalleMuestraSismica)
                    );
                """);

        tables.add("""
                    CREATE TABLE IF NOT EXISTS EventoSismico_CambioEstado (
                        idEventoSismico INTEGER NOT NULL,
                        idCambioEstado INTEGER NOT NULL,
                        PRIMARY KEY (idEventoSismico, idCambioEstado),
                        FOREIGN KEY (idEventoSismico) REFERENCES EventoSismico(idEventoSismico),
                        FOREIGN KEY (idCambioEstado) REFERENCES CambioEstado(idCambioEstado)
                    );
                """);

        tables.add("CREATE TABLE IF NOT EXISTS CambioEstado (\n" +
                "    idCambioEstado INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    fechaHoraInicio DATETIME NOT NULL,\n" +
                "    fechaHoraFin DATETIME,\n" +
                "    ambitoEstado TEXT NOT NULL,\n" +
                "    nombreEstado TEXT NOT NULL,\n" +
                "    idResponsableInspeccion INTEGER,\n" +
                "    idEventoSismico INTEGER,\n" +
                "    FOREIGN KEY (ambitoEstado, nombreEstado) REFERENCES Estado(ambitoEstado, nombreEstado),\n" +
                "    FOREIGN KEY (idResponsableInspeccion) REFERENCES Empleado(idEmpleado),\n" +
                "    FOREIGN KEY (idEventoSismico) REFERENCES EventoSismico(idEventoSismico)\n" +
                ");");

        try (Statement s = conn.createStatement()) {
            for (String tableSql : tables) {
                s.execute(tableSql);
            }
        }
    }

    public static void insertSampleData(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();

        try {

            // ====== CLASIFICACION SISMO (3) ======
            stmt.executeUpdate(
                    """
                                INSERT OR IGNORE INTO ClasificacionSismo (idClasificacionSismo, kmProfundidadDesde, kmProfundidadHasta, nombre) VALUES
                                (1, 0.0, 70.0, 'Superficial'),
                                (2, 70.1, 300.0, 'Intermedio'),
                                (3, 300.1, 700.0, 'Profundo');
                            """);

            // ====== ALCANCE SISMO (3) ======
            stmt.executeUpdate(
                    """
                                INSERT OR IGNORE INTO AlcanceSismo (idAlcanceSismo, descripcion, nombre) VALUES
                                (1, 'Sismo perceptible solo en el área inmediata al epicentro, con efectos muy localizados.', 'Local'),
                                (2, 'Sismo perceptible en una región amplia (varios países o continentes) con efectos distribuidos.', 'Regional'),
                                (3, 'Sismo con efectos limitados que no genera daños significativos y a menudo solo es registrado por instrumentación.', 'Instrumental');
                            """);

            // ====== ORIGEN DE GENERACIÓN (2) ======
            stmt.executeUpdate(
                    """
                                INSERT OR IGNORE INTO OrigenDeGeneracion (idOrigenDeGeneracion, descripcion, nombre) VALUES
                                (1, 'Movimiento en la zona de subducción entre placas tectónicas (principalmente en el plano de la placa subducida).', 'Sismo interplaca'),
                                (2, 'Movimiento en las fallas dentro de una misma placa tectónica (generalmente en la corteza continental).', 'Sismo cortical');
                            """);

            // ====== MAGNITUD RICHTER (3) ======
            stmt.executeUpdate(
                    """
                                INSERT OR IGNORE INTO MagnitudRichter (numero, descripcion) VALUES
                                (3, 'Menor: Generalmente no se siente, pero es registrado. Daños muy leves o nulos.'),
                                (4, 'Ligero: A menudo se siente, pero solo causa daños menores.'),
                                (5, 'Moderado: Se siente ampliamente y puede causar daños mayores a edificios débiles o leves a resistentes.');
                            """);

            // ====== ESTADOS ======
            stmt.executeUpdate("""
                        INSERT OR IGNORE INTO Estado (ambitoEstado, nombreEstado) VALUES
                        ('EventoSismico', 'AutoDetectado'),
                        ('EventoSismico', 'PendienteDeRevision'),
                        ('EventoSismico', 'BloqueadoEnRevision'),
                        ('EventoSismico', 'Derivado'),
                        ('EventoSismico', 'ConfirmadoPorPersonal'),
                        ('EventoSismico', 'Rechazado'),
                        ('EventoSismico', 'PendienteDeCierre'),
                        ('EventoSismico', 'Cerrado'),
                        ('EventoSismico', 'SinRevision'),
                        ('EventoSismico', 'AutoConfirmado'),
                        ('SerieTemporal', 'Activo'),
                        ('SerieTemporal', 'Inactivo');
                    """);

            // ====== ROLES ======
            stmt.executeUpdate("""
                        INSERT OR IGNORE INTO Rol (nombre, descripcion) VALUES
                        ('Analista de Sismos', 'Realiza revisión manual y análisis de eventos sísmicos'),
                        ('Supervisor', 'Supervisa revisiones y valida eventos sísmicos');
                    """);

            // ====== EMPLEADOS ======
            stmt.executeUpdate("""
                        INSERT OR IGNORE INTO Empleado (nombre, apellido, email, telefono, legajo, idRol) VALUES
                        ('Laura', 'Pérez', 'laura.perez@ccrs.gob.ar', '+54 9 351 555-1001', 'E001', 1),
                        ('Carlos', 'Domínguez', 'carlos.dominguez@ccrs.gob.ar', '+54 9 351 555-1002', 'E002', 2);
                    """);

            // ====== USUARIOS ======
            stmt.executeUpdate("""
                        INSERT OR IGNORE INTO Usuario (nombreUsuario, contraseña, idEmpleado) VALUES
                        ('Emanuel', 'ema123', 1),
                        ('LucasSegundo', 'contra123', 2);
                    """);

            // FABRICANTES
            stmt.executeUpdate("""
                        INSERT OR IGNORE INTO Fabricante (idFabricante, nombre, razonSocial) VALUES
                        (1, 'ZETLAB', 'ZETLAB Universe'),
                        (2, 'GeoTech', 'GeoTech SA'),
                        (3, 'Kinemetrics', 'Kine metric SA');
                    """);

            // ====== MODELOS ======
            stmt.executeUpdate("""
                        INSERT OR IGNORE INTO ModeloSismografo (caracteristicas, nombreModelo, idFabricante) VALUES
                        ('Alta sensibilidad, 3 ejes, rango ±2g', 'ZET 7152-N VER.3', 1),
                        ('Sensor de alta precisión para estaciones automáticas', 'GeoTech A500', 2),
                        ('Modelo compacto para registro portátil', 'Kinemetrics Horizon', 3);
                    """);

            // ====== ESTACIONES ======
            stmt.executeUpdate(
                    """
                                INSERT OR IGNORE INTO EstacionSismologica (nombre, latitud, longitud, documentoCertificacionAdq, fechaSolicitudCertificacion, nroCertificacionAdquisicion) VALUES
                                ('Estación Córdoba', -31.4201, -64.1888, NULL, NULL, NULL),
                                ('Estación San Juan', -31.5375, -68.5364, NULL, NULL, NULL),
                                ('Estación Salta', -24.7859, -65.4117, NULL, NULL, NULL);
                            """);

            // ====== SISMOGRAFOS ======
            stmt.executeUpdate("""
                        INSERT OR IGNORE INTO Sismografo (fechaAdquisicion, nroSerie, idModelo, codigoEstacion) VALUES
                        ('2024-11-02', 1523, 1, 1),
                        ('2024-11-05', 1524, 1, 2),
                        ('2025-01-10', 5001, 2, 3);
                    """);

            // ====== TIPOS DE DATO ======
            stmt.executeUpdate("""
                        INSERT OR IGNORE INTO TipoDeDato (denominacion, nombreUnidadMedida, valorUmbral) VALUES
                        ('Frecuencia de onda', 'Hz', 15.0),
                        ('Longitud de onda', 'km/ciclo', 1.5),
                        ('Velocidad de onda', 'km/s', 8.0);
                    """);

            // ====== EVENTOS SISMICOS (12) - CON IDs DE FK ACTUALIZADOS ======
            // IDs asignados implícitamente 1..12 por el orden de inserción
            stmt.executeUpdate(
                    """
                                INSERT OR IGNORE INTO EventoSismico (
                                    fechaHoraFin, fechaHoraOcurrencia, latitudEpicentro, longitudEpicentro, latitudHipocentro, longitudHipocentro,
                                    valorMagnitud, magnitudRichter, idClasificacionSismo, idOrigenGeneracionSismo, idAlcanceSismo,
                                    ambitoEstado, nombreEstado
                                ) VALUES
                                -- (..., valorMagnitud (REAL), magnitudRichter (INT/FK), idClasificacion, idOrigenGeneracion, idAlcance, ...)

                                -- 1 (AutoDetectado) | Mag: 4.3 (FK: 4) | Clasif: 2 | Origen: 1 | Alcance: 2
                                ('2025-02-21 19:10:41', '2025-02-21 19:05:41', -31.52, -64.19, -31.52, -64.19, 4.3, 4, 2, 1, 2, 'EventoSismico', 'AutoDetectado'),

                                -- 2 (AutoDetectado) | Mag: 3.8 (FK: 3) | Clasif: 1 | Origen: 2 | Alcance: 1
                                ('2025-04-01 10:05:00', '2025-04-01 10:00:10', -31.10, -65.20, -31.10, -65.20, 3.8, 3, 1, 2, 1, 'EventoSismico', 'AutoDetectado'),

                                -- 3 (AutoDetectado) | Mag: 4.1 (FK: 4) | Clasif: 2 | Origen: 1 | Alcance: 2
                                ('2025-04-02 14:20:00', '2025-04-02 14:15:02', -31.25, -65.45, -31.25, -65.45, 4.1, 4, 2, 1, 2, 'EventoSismico', 'AutoDetectado'),

                                -- 4 (AutoDetectado) | Mag: 4.5 (FK: 4) | Clasif: 2 | Origen: 2 | Alcance: 2
                                ('2025-04-03 09:35:00', '2025-04-03 09:30:06', -31.40, -65.60, -31.40, -65.60, 4.5, 4, 2, 2, 2, 'EventoSismico', 'AutoDetectado'),

                                -- 5 (PendienteRevision) | Mag: 5.0 (FK: 5) | Clasif: 3 | Origen: 1 | Alcance: 2
                                ('2025-04-05 12:15:00', '2025-04-05 12:10:35', -31.70, -65.90, -31.70, -65.90, 5.0, 5, 3, 1, 2, 'EventoSismico', 'PendienteDeRevision'),

                                -- 6 (PendienteRevision) | Mag: 5.2 (FK: 5) | Clasif: 3 | Origen: 2 | Alcance: 2
                                ('2025-04-06 18:30:00', '2025-04-06 18:25:50', -31.85, -66.05, -31.85, -66.05, 5.2, 5, 3, 2, 2, 'EventoSismico', 'PendienteDeRevision'),

                                -- 7 (BloqueadoRevision) | Mag: 4.3 (FK: 4) | Clasif: 2 | Origen: 2 | Alcance: 2
                                ('2025-04-07 03:05:00', '2025-04-07 03:00:03', -32.00, -66.20, -32.00, -66.20, 4.3, 4, 2, 2, 2, 'EventoSismico', 'BloqueadoEnRevision'),

                                -- 8 (Derivado) | Mag: 4.0 (FK: 4) | Clasif: 2 | Origen: 1 | Alcance: 1
                                ('2025-04-08 05:25:00', '2025-04-08 05:20:07', -32.15, -66.35, -32.15, -66.35, 4.0, 4, 2, 1, 1, 'EventoSismico', 'Derivado'),

                                -- 9 (ConfirmadoPorPersonal) | Mag: 4.6 (FK: 4) | Clasif: 1 | Origen: 2 | Alcance: 2
                                ('2025-04-09 21:45:00', '2025-04-09 21:40:28', -32.30, -66.50, -32.30, -66.50, 4.6, 4, 1, 2, 2, 'EventoSismico', 'ConfirmadoPorPersonal'),

                                -- 10 (ConfirmadoPorPersonal) | Mag: 4.9 (FK: 4) | Clasif: 1 | Origen: 2 | Alcance: 2
                                ('2025-04-11 08:05:00', '2025-04-11 08:00:30', -32.40, -66.55, -32.40, -66.55, 4.9, 4, 1, 2, 2, 'EventoSismico', 'ConfirmadoPorPersonal'),

                                -- 11 (Rechazado) | Mag: 3.7 (FK: 3) | Clasif: 1 | Origen: 2 | Alcance: 1
                                ('2025-03-08 13:05:00', '2025-03-08 13:00:20', -32.15, -68.40, -32.15, -68.40, 3.7, 3, 1, 2, 1, 'EventoSismico', 'Rechazado'),

                                -- 12 (Rechazado) | Mag: 3.9 (FK: 3) | Clasif: 1 | Origen: 1 | Alcance: 1
                                ('2025-04-10 12:00:00', '2025-04-10 11:55:02', -32.45, -66.65, -32.45, -66.65, 3.9, 3, 1, 1, 1, 'EventoSismico', 'Rechazado');
                            """);

            // ====== CAMBIOESTADO — registro simple por evento (estado actual) ======
            // Usamos idResponsable 1 o 2 según corresponda
            stmt.executeUpdate(
                    """
                                INSERT OR IGNORE INTO CambioEstado (fechaHoraInicio, fechaHoraFin, ambitoEstado, nombreEstado, idResponsableInspeccion, idEventoSismico) VALUES
                                ('2025-02-21 19:10:00', NULL, 'EventoSismico', 'AutoDetectado', 1, 1),
                                ('2025-04-01 10:02:00', NULL, 'EventoSismico', 'AutoDetectado', 1, 2),
                                ('2025-04-02 14:17:00', NULL, 'EventoSismico', 'AutoDetectado', 1, 3),
                                ('2025-04-03 09:32:00', NULL, 'EventoSismico', 'AutoDetectado', 1, 4),
                                ('2025-04-05 12:12:00', NULL, 'EventoSismico', 'PendienteDeRevision', 2, 5),
                                ('2025-04-06 18:27:00', NULL, 'EventoSismico', 'PendienteDeRevision', 2, 6),
                                ('2025-04-07 03:02:00', NULL, 'EventoSismico', 'BloqueadoEnRevision', 1, 7),
                                ('2025-04-08 05:22:00', NULL, 'EventoSismico', 'Derivado', 1, 8),
                                ('2025-04-09 21:42:00', NULL, 'EventoSismico', 'ConfirmadoPorPersonal', 2, 9),
                                ('2025-04-11 08:02:00', NULL, 'EventoSismico', 'ConfirmadoPorPersonal', 2, 10),
                                ('2025-03-08 13:06:00', NULL, 'EventoSismico', 'Rechazado', 2, 11),
                                ('2025-04-10 11:57:00', NULL, 'EventoSismico', 'Rechazado', 2, 12);
                            """);

            // ====== HISTORIAL DE CAMBIOS DE ESTADO (por evento) ======
            stmt.executeUpdate(
                    """
                                INSERT OR IGNORE INTO CambioEstado (fechaHoraInicio, fechaHoraFin, ambitoEstado, nombreEstado, idResponsableInspeccion, idEventoSismico) VALUES
                                -- Evento 5: AutoDetectado → AutoConfirmado → PendienteDeRevision
                                ('2025-04-05 12:10:00', '2025-04-05 12:11:00', 'EventoSismico', 'AutoDetectado', 1, 5),
                                ('2025-04-05 12:11:00', '2025-04-05 12:12:00', 'EventoSismico', 'AutoConfirmado', 1, 5),
                                ('2025-04-05 12:12:00', NULL, 'EventoSismico', 'PendienteDeRevision', 2, 5),

                                -- Evento 6: AutoDetectado → PendienteDeRevision → BloqueadoEnRevision
                                ('2025-04-06 18:25:00', '2025-04-06 18:26:00', 'EventoSismico', 'AutoDetectado', 1, 6),
                                ('2025-04-06 18:26:00', '2025-04-06 18:27:00', 'EventoSismico', 'PendienteDeRevision', 2, 6),
                                ('2025-04-06 18:27:00', NULL, 'EventoSismico', 'BloqueadoEnRevision', 2, 6),

                                -- Evento 7: AutoDetectado → BloqueadoEnRevision → ConfirmadoPorPersonal → PendienteDeCierre
                                ('2025-04-07 03:00:00', '2025-04-07 03:01:00', 'EventoSismico', 'AutoDetectado', 1, 7),
                                ('2025-04-07 03:01:00', '2025-04-07 03:02:00', 'EventoSismico', 'BloqueadoEnRevision', 2, 7),
                                ('2025-04-07 03:02:00', '2025-04-07 03:03:00', 'EventoSismico', 'ConfirmadoPorPersonal', 2, 7),
                                ('2025-04-07 03:03:00', NULL, 'EventoSismico', 'PendienteDeCierre', 2, 7),

                                -- Evento 8: AutoDetectado → PendienteDeRevision → Derivado
                                ('2025-04-08 05:20:00', '2025-04-08 05:21:00', 'EventoSismico', 'AutoDetectado', 1, 8),
                                ('2025-04-08 05:21:00', '2025-04-08 05:22:00', 'EventoSismico', 'PendienteDeRevision', 2, 8),
                                ('2025-04-08 05:22:00', NULL, 'EventoSismico', 'Derivado', 2, 8),

                                -- Evento 9: AutoDetectado → AutoConfirmado → ConfirmadoPorPersonal → Cerrado
                                ('2025-04-09 21:40:00', '2025-04-09 21:41:00', 'EventoSismico', 'AutoDetectado', 1, 9),
                                ('2025-04-09 21:41:00', '2025-04-09 21:42:00', 'EventoSismico', 'AutoConfirmado', 1, 9),
                                ('2025-04-09 21:42:00', '2025-04-09 21:44:00', 'EventoSismico', 'ConfirmadoPorPersonal', 2, 9),
                                ('2025-04-09 21:44:00', NULL, 'EventoSismico', 'Cerrado', 2, 9),

                                -- Evento 10: AutoDetectado → AutoConfirmado → ConfirmadoPorPersonal
                                ('2025-04-11 08:00:00', '2025-04-11 08:01:00', 'EventoSismico', 'AutoDetectado', 1, 10),
                                ('2025-04-11 08:01:00', '2025-04-11 08:02:00', 'EventoSismico', 'AutoConfirmado', 1, 10),
                                ('2025-04-11 08:02:00', NULL, 'EventoSismico', 'ConfirmadoPorPersonal', 2, 10),

                                -- Evento 11: AutoDetectado → PendienteDeRevision → Rechazado
                                ('2025-03-08 13:00:00', '2025-03-08 13:01:00', 'EventoSismico', 'AutoDetectado', 1, 11),
                                ('2025-03-08 13:01:00', '2025-03-08 13:02:00', 'EventoSismico', 'PendienteDeRevision', 2, 11),
                                ('2025-03-08 13:02:00', NULL, 'EventoSismico', 'Rechazado', 2, 11),

                                -- Evento 12: AutoDetectado → AutoConfirmado → Rechazado
                                ('2025-04-10 11:55:00', '2025-04-10 11:56:00', 'EventoSismico', 'AutoDetectado', 1, 12),
                                ('2025-04-10 11:56:00', '2025-04-10 11:57:00', 'EventoSismico', 'AutoConfirmado', 1, 12),
                                ('2025-04-10 11:57:00', NULL, 'EventoSismico', 'Rechazado', 2, 12);
                            """);

            // ====== RELACIÓN EVENTO ↔ CAMBIOESTADO ======
            // Se asume que los nuevos CambioEstado siguen IDs consecutivos después de los
            // 12 iniciales (id 13..)
            // Por tanto, se vinculan ordenadamente:
            /*stmt.executeUpdate("""
                        INSERT OR IGNORE INTO EventoSismico_CambioEstado (idEventoSismico, idCambioEstado) VALUES
                        -- Evento 1
                        (1, 13),
                        -- Evento 2
                        (2, 14), (2, 15), (2, 16),
                        -- Evento 3
                        (3, 17), (3, 18), (3, 19),
                        -- Evento 4
                        (4, 20), (4, 21), (4, 22),
                        -- Evento 5
                        (5, 23), (5, 24), (5, 25),
                        -- Evento 6
                        (6, 26), (6, 27), (6, 28),
                        -- Evento 7
                        (7, 29), (7, 30), (7, 31), (7, 32),
                        -- Evento 8
                        (8, 33), (8, 34), (8, 35),
                        -- Evento 9
                        (9, 36), (9, 37), (9, 38), (9, 39),
                        -- Evento 10
                        (10, 40), (10, 41), (10, 42),
                        -- Evento 11
                        (11, 43), (11, 44), (11, 45),
                        -- Evento 12
                        (12, 46), (12, 47), (12, 48);
                    """);*/

            // ====== SERIES TEMPORALES (3 por evento) ====
            // Para evitar dependencia en autoincrement, ponemos explicitamente
            // idSerieTemporal = 1..36
            stmt.executeUpdate(
                    """
                            INSERT OR IGNORE INTO SerieTemporal (
                                idSerieTemporal, fechaHoraRegistro, frecuenciaMuestreo, condicionAlarma,
                                ambitoEstado, nombreEstado, idEventoSismico, codigoEstacion, idSismografo
                            ) VALUES
                            -- Evento 1 (idEventoSismico = 1) : series 1,2,3 con sismografos 1,2,3
                            (1, '2025-02-21 19:05:41', 50.0, 'condicion 1', 'SerieTemporal', 'Inactivo', 1, 1, 1),
                            (2, '2025-02-21 19:05:41', 50.0, 'condicion 2', 'SerieTemporal', 'Activo', 1, 2, 2),
                            (3, '2025-02-21 19:05:41', 50.0, 'condicion 1', 'SerieTemporal', 'Activo', 1, 3, 3),

                            -- Evento 2 (idEventoSismico = 2) : series 4,5,6 con sismografos 1,2,3
                            (4, '2025-04-01 10:00:00', 50.0, 'condicion 1', 'SerieTemporal', 'Inactivo', 2, 1, 1),
                            (5, '2025-04-01 10:00:00', 50.0, 'condicion 2', 'SerieTemporal', 'Activo', 2, 2, 2),
                            (6, '2025-04-01 10:00:00', 50.0, 'condicion 1', 'SerieTemporal', 'Inactivo', 2, 3, 3),

                            -- Evento 3 (idEventoSismico = 3) : series 7,8,9 con sismografos 1,2,3
                            (7, '2025-04-02 14:15:00', 50.0, 'condicion 1', 'SerieTemporal', 'Activo', 3, 1, 1),
                            (8, '2025-04-02 14:15:00', 50.0, 'condicion 2', 'SerieTemporal', 'Activo', 3, 2, 2),
                            (9, '2025-04-02 14:15:00', 50.0, 'condicion 1', 'SerieTemporal', 'Activo', 3, 3, 3),

                            -- Evento 4 (idEventoSismico = 4) : series 10,11,12 con sismografos 1,2,3
                            (10, '2025-04-03 09:30:00', 50.0, 'condicion 2', 'SerieTemporal', 'Inactivo', 4, 1, 1),
                            (11, '2025-04-03 09:30:00', 50.0, 'condicion 2', 'SerieTemporal', 'Inactivo', 4, 2, 2),
                            (12, '2025-04-03 09:30:00', 50.0, 'condicion 2', 'SerieTemporal', 'Inactivo', 4, 3, 3),

                            -- Evento 5 (idEventoSismico = 5) : series 13,14,15 con sismografos 1,2,3
                            (13, '2025-04-05 12:10:00', 50.0, 'condicion 3', 'SerieTemporal', 'Inactivo', 5, 1, 1),
                            (14, '2025-04-05 12:10:00', 50.0, 'condicion 3', 'SerieTemporal', 'Inactivo', 5, 2, 2),
                            (15, '2025-04-05 12:10:00', 50.0, 'condicion 3', 'SerieTemporal', 'Inactivo', 5, 3, 3),

                            -- Evento 6 (idEventoSismico = 6) : series 16,17,18 con sismografos 1,2,3
                            (16, '2025-04-06 18:25:00', 50.0, 'condicion 3', 'SerieTemporal', 'Activo', 6, 1, 1),
                            (17, '2025-04-06 18:25:00', 50.0, 'condicion 3', 'SerieTemporal', 'Inactivo', 6, 2, 2),
                            (18, '2025-04-06 18:25:00', 50.0, 'condicion 3', 'SerieTemporal', 'Inactivo', 6, 3, 3),

                            -- Evento 7 (idEventoSismico = 7) : series 19,20,21 con sismografos 1,2,3
                            (19, '2025-04-07 03:00:00', 50.0, 'condicion 3', 'SerieTemporal', 'Inactivo', 7, 1, 1),
                            (20, '2025-04-07 03:00:00', 50.0, 'condicion 3', 'SerieTemporal', 'Inactivo', 7, 2, 2),
                            (21, '2025-04-07 03:00:00', 50.0, 'condicion 3', 'SerieTemporal', 'Inactivo', 7, 3, 3),

                            -- Evento 8 (idEventoSismico = 8) : series 22,23,24 con sismografos 1,2,3
                            (22, '2025-04-08 05:20:00', 50.0, 'condicion 3', 'SerieTemporal', 'Inactivo', 8, 1, 1),
                            (23, '2025-04-08 05:20:00', 50.0, 'condicion 3', 'SerieTemporal', 'Inactivo', 8, 2, 2),
                            (24, '2025-04-08 05:20:00', 50.0, 'condicion 3', 'SerieTemporal', 'Inactivo', 8, 3, 3),

                            -- Evento 9 (idEventoSismico = 9) : series 25,26,27 con sismografos 1,2,3
                            (25, '2025-04-09 21:40:00', 50.0, 'condicion 2', 'SerieTemporal', 'Inactivo', 9, 1, 1),
                            (26, '2025-04-09 21:40:00', 50.0, 'condicion 3', 'SerieTemporal', 'Activo', 9, 2, 2),
                            (27, '2025-04-09 21:40:00', 50.0, 'condicion 1', 'SerieTemporal', 'Inactivo', 9, 3, 3),

                            -- Evento 10 (idEventoSismico = 10) : series 28,29,30 con sismografos 1,2,3
                            (28, '2025-04-11 08:00:00', 50.0, 'condicion 3', 'SerieTemporal', 'Inactivo', 10, 1, 1),
                            (29, '2025-04-11 08:00:00', 50.0, 'condicion 3', 'SerieTemporal', 'Inactivo', 10, 2, 2),
                            (30, '2025-04-11 08:00:00', 50.0, 'condicion 3', 'SerieTemporal', 'Inactivo', 10, 3, 3),

                            -- Evento 11 (idEventoSismico = 11) : series 31,32,33 con sismografos 1,2,3
                            (31, '2025-03-08 13:00:00', 50.0, 'condicion 3', 'SerieTemporal', 'Inactivo', 11, 1, 1),
                            (32, '2025-03-08 13:00:00', 50.0, 'condicion 3', 'SerieTemporal', 'Inactivo', 11, 2, 2),
                            (33, '2025-03-08 13:00:00', 50.0, 'condicion 3', 'SerieTemporal', 'Inactivo', 11, 3, 3),

                            -- Evento 12 (idEventoSismico = 12) : series 34,35,36 con sismografos 1,2,3
                            (34, '2025-04-10 11:55:00', 50.0, 'condicion 3', 'SerieTemporal', 'Inactivo', 12, 1, 1),
                            (35, '2025-04-10 11:55:00', 50.0, 'condicion 3', 'SerieTemporal', 'Inactivo', 12, 2, 2),
                            (36, '2025-04-10 11:55:00', 50.0, 'condicion 3', 'SerieTemporal', 'Inactivo', 12, 3, 3);
                            """);

            // ====== MUESTRAS SISMICAS (2 por serie) ======
            // idMuestraSismica explícitos 1..72 (36 series * 2)
            stmt.executeUpdate(
                    """
                                INSERT OR IGNORE INTO MuestraSismica (idMuestraSismica, fechaHoraMuestraSismica, idSerieTemporal) VALUES
                                -- series 1..3 (evento1)
                                (1, '2025-02-21 19:05:41', 1), (2, '2025-02-21 19:10:41', 1),
                                (3, '2025-02-21 19:05:41', 2), (4, '2025-02-21 19:10:41', 2),
                                (5, '2025-02-21 19:05:41', 3), (6, '2025-02-21 19:10:41', 3),
                                -- series 4..6 (evento2)
                                (7, '2025-04-01 10:00:00', 4), (8, '2025-04-01 10:05:00', 4),
                                (9, '2025-04-01 10:00:00', 5), (10, '2025-04-01 10:05:00', 5),
                                (11, '2025-04-01 10:00:00', 6), (12, '2025-04-01 10:05:00', 6),
                                -- series 7..9 (evento3)
                                (13, '2025-04-02 14:15:00', 7), (14, '2025-04-02 14:20:00', 7),
                                (15, '2025-04-02 14:15:00', 8), (16, '2025-04-02 14:20:00', 8),
                                (17, '2025-04-02 14:15:00', 9), (18, '2025-04-02 14:20:00', 9),
                                -- series 10..12 (evento4)
                                (19, '2025-04-03 09:30:00', 10), (20, '2025-04-03 09:35:00', 10),
                                (21, '2025-04-03 09:30:00', 11), (22, '2025-04-03 09:35:00', 11),
                                (23, '2025-04-03 09:30:00', 12), (24, '2025-04-03 09:35:00', 12),
                                -- series 13..15 (evento5)
                                (25, '2025-04-05 12:10:00', 13), (26, '2025-04-05 12:15:00', 13),
                                (27, '2025-04-05 12:10:00', 14), (28, '2025-04-05 12:15:00', 14),
                                (29, '2025-04-05 12:10:00', 15), (30, '2025-04-05 12:15:00', 15),
                                -- series 16..18 (evento6)
                                (31, '2025-04-06 18:25:00', 16), (32, '2025-04-06 18:30:00', 16),
                                (33, '2025-04-06 18:25:00', 17), (34, '2025-04-06 18:30:00', 17),
                                (35, '2025-04-06 18:25:00', 18), (36, '2025-04-06 18:30:00', 18),
                                -- series 19..21 (evento7)
                                (37, '2025-04-07 03:00:00', 19), (38, '2025-04-07 03:05:00', 19),
                                (39, '2025-04-07 03:00:00', 20), (40, '2025-04-07 03:05:00', 20),
                                (41, '2025-04-07 03:00:00', 21), (42, '2025-04-07 03:05:00', 21),
                                -- series 22..24 (evento8)
                                (43, '2025-04-08 05:20:00', 22), (44, '2025-04-08 05:25:00', 22),
                                (45, '2025-04-08 05:20:00', 23), (46, '2025-04-08 05:25:00', 23),
                                (47, '2025-04-08 05:20:00', 24), (48, '2025-04-08 05:25:00', 24),
                                -- series 25..27 (evento9)
                                (49, '2025-04-09 21:40:00', 25), (50, '2025-04-09 21:45:00', 25),
                                (51, '2025-04-09 21:40:00', 26), (52, '2025-04-09 21:45:00', 26),
                                (53, '2025-04-09 21:40:00', 27), (54, '2025-04-09 21:45:00', 27),
                                -- series 28..30 (evento10)
                                (55, '2025-04-11 08:00:00', 28), (56, '2025-04-11 08:05:00', 28),
                                (57, '2025-04-11 08:00:00', 29), (58, '2025-04-11 08:05:00', 29),
                                (59, '2025-04-11 08:00:00', 30), (60, '2025-04-11 08:05:00', 30),
                                -- series 31..33 (evento11)
                                (61, '2025-03-08 13:00:00', 31), (62, '2025-03-08 13:05:00', 31),
                                (63, '2025-03-08 13:00:00', 32), (64, '2025-03-08 13:05:00', 32),
                                (65, '2025-03-08 13:00:00', 33), (66, '2025-03-08 13:05:00', 33),
                                -- series 34..36 (evento12)
                                (67, '2025-04-10 11:55:00', 34), (68, '2025-04-10 12:00:00', 34),
                                (69, '2025-04-10 11:55:00', 35), (70, '2025-04-10 12:00:00', 35),
                                (71, '2025-04-10 11:55:00', 36), (72, '2025-04-10 12:00:00', 36);
                            """);

            // ======= Suscripciones =======

            // ====== DETALLES DE MUESTRA (3 por muestra) ======
            // idTipoDeDato: 1=Frecuencia, 2=Longitud, 3=Velocidad
            // Valores realistas, ligeramente variables
            stmt.executeUpdate("""
                        INSERT OR IGNORE INTO DetalleMuestraSismica (valor, idMuestraSismica, idTipoDeDato) VALUES
                        -- muestras 1..6 (evento1)
                        (10.00, 1, 1), (0.70, 1, 2), (7.00, 1, 3),
                        (10.05, 2, 1), (0.70, 2, 2), (7.02, 2, 3),
                        (9.98, 3, 1), (0.71, 3, 2), (6.99, 3, 3),
                        (10.02, 4, 1), (0.67, 4, 2), (7.01, 4, 3),
                        (9.95, 5, 1), (0.72, 5, 2), (7.03, 5, 3),
                        (10.10, 6, 1), (0.70, 6, 2), (7.05, 6, 3),
                        -- muestras 7..12 (evento2)
                        (9.80, 7, 1), (0.69, 7, 2), (6.95, 7, 3),
                        (9.85, 8, 1), (0.70, 8, 2), (6.99, 8, 3),
                        (9.90, 9, 1), (0.71, 9, 2), (7.00, 9, 3),
                        (10.00, 10, 1), (0.72, 10, 2), (7.00, 10, 3),
                        (10.12, 11, 1), (0.73, 11, 2), (7.05, 11, 3),
                        (9.95, 12, 1), (0.67, 12, 2), (7.01, 12, 3),
                        -- muestras 13..18 (evento3)
                        (10.20, 13, 1), (0.74, 13, 2), (7.10, 13, 3),
                        (10.15, 14, 1), (0.73, 14, 2), (7.08, 14, 3),
                        (10.05, 15, 1), (0.72, 15, 2), (7.06, 15, 3),
                        (9.88, 16, 1), (0.71, 16, 2), (7.00, 16, 3),
                        (9.92, 17, 1), (0.70, 17, 2), (6.98, 17, 3),
                        (10.00, 18, 1), (0.69, 18, 2), (7.02, 18, 3),
                        -- muestras 19..24 (evento4)
                        (10.30, 19, 1), (0.75, 19, 2), (7.12, 19, 3),
                        (10.25, 20, 1), (0.74, 20, 2), (7.10, 20, 3),
                        (10.22, 21, 1), (0.73, 21, 2), (7.09, 21, 3),
                        (9.99, 22, 1), (0.72, 22, 2), (7.00, 22, 3),
                        (9.97, 23, 1), (0.71, 23, 2), (7.01, 23, 3),
                        (10.02, 24, 1), (0.70, 24, 2), (7.03, 24, 3),
                        -- muestras 25..30 (evento5)
                        (10.40, 25, 1), (0.78, 25, 2), (7.20, 25, 3),
                        (10.35, 26, 1), (0.77, 26, 2), (7.18, 26, 3),
                        (10.10, 27, 1), (0.75, 27, 2), (7.12, 27, 3),
                        (9.80, 28, 1), (0.70, 28, 2), (6.95, 28, 3),
                        (9.85, 29, 1), (0.69, 29, 2), (6.98, 29, 3),
                        (10.05, 30, 1), (0.72, 30, 2), (7.05, 30, 3),
                        -- muestras 31..36 (evento6)
                        (9.90, 31, 1), (0.70, 31, 2), (7.00, 31, 3),
                        (9.95, 32, 1), (0.71, 32, 2), (7.02, 32, 3),
                        (10.00, 33, 1), (0.72, 33, 2), (7.05, 33, 3),
                        (10.10, 34, 1), (0.73, 34, 2), (7.08, 34, 3),
                        (10.20, 35, 1), (0.74, 35, 2), (7.10, 35, 3),
                        (10.05, 36, 1), (0.72, 36, 2), (7.06, 36, 3),
                        -- muestras 37..42 (evento7)
                        (9.88, 37, 1), (0.69, 37, 2), (6.99, 37, 3),
                        (9.92, 38, 1), (0.70, 38, 2), (7.00, 38, 3),
                        (10.00, 39, 1), (0.71, 39, 2), (7.02, 39, 3),
                        (10.05, 40, 1), (0.72, 40, 2), (7.04, 40, 3),
                        (9.95, 41, 1), (0.70, 41, 2), (7.00, 41, 3),
                        (10.10, 42, 1), (0.73, 42, 2), (7.08, 42, 3),
                        -- muestras 43..48 (evento8)
                        (9.80, 43, 1), (0.67, 43, 2), (6.96, 43, 3),
                        (9.85, 44, 1), (0.69, 44, 2), (6.98, 44, 3),
                        (10.00, 45, 1), (0.70, 45, 2), (7.01, 45, 3),
                        (10.02, 46, 1), (0.71, 46, 2), (7.03, 46, 3),
                        (10.08, 47, 1), (0.72, 47, 2), (7.06, 47, 3),
                        (9.95, 48, 1), (0.70, 48, 2), (7.00, 48, 3),
                        -- muestras 49..54 (evento9)
                        (10.10, 49, 1), (0.74, 49, 2), (7.10, 49, 3),
                        (10.05, 50, 1), (0.73, 50, 2), (7.08, 50, 3),
                        (9.98, 51, 1), (0.72, 51, 2), (7.02, 51, 3),
                        (10.00, 52, 1), (0.71, 52, 2), (7.01, 52, 3),
                        (9.90, 53, 1), (0.70, 53, 2), (6.99, 53, 3),
                        (9.95, 54, 1), (0.69, 54, 2), (6.98, 54, 3),
                        -- muestras 55..60 (evento10)
                        (10.20, 55, 1), (0.75, 55, 2), (7.12, 55, 3),
                        (10.18, 56, 1), (0.74, 56, 2), (7.10, 56, 3),
                        (10.10, 57, 1), (0.73, 57, 2), (7.08, 57, 3),
                        (9.98, 58, 1), (0.72, 58, 2), (7.00, 58, 3),
                        (9.96, 59, 1), (0.71, 59, 2), (6.99, 59, 3),
                        (10.00, 60, 1), (0.70, 60, 2), (7.01, 60, 3),
                        -- muestras 61..66 (evento11)
                        (9.80, 61, 1), (0.69, 61, 2), (6.95, 61, 3),
                        (9.85, 62, 1), (0.70, 62, 2), (6.98, 62, 3),
                        (9.90, 63, 1), (0.71, 63, 2), (6.99, 63, 3),
                        (9.95, 64, 1), (0.70, 64, 2), (7.00, 64, 3),
                        (10.00, 65, 1), (0.72, 65, 2), (7.03, 65, 3),
                        (10.05, 66, 1), (0.73, 66, 2), (7.05, 66, 3),
                        -- muestras 67..72 (evento12)
                        (10.10, 67, 1), (0.74, 67, 2), (7.08, 67, 3),
                        (10.00, 68, 1), (0.72, 68, 2), (7.04, 68, 3),
                        (9.95, 69, 1), (0.71, 69, 2), (7.00, 69, 3),
                        (9.92, 70, 1), (0.70, 70, 2), (6.99, 70, 3),
                        (9.98, 71, 1), (0.69, 71, 2), (6.98, 71, 3),
                        (10.02, 72, 1), (0.70, 72, 2), (7.02, 72, 3);
                    """);

            // ====== RELACIONES SERIE TEMPORAL ↔ MUESTRA SISMICA (N:N) ======
            // Vinculan cada muestra símica con su serie temporal
            // 2 muestras por serie, 36 series = 72 muestras totales
            stmt.executeUpdate("""
                        INSERT OR IGNORE INTO SerieTemporal_MuestraSismica (idSerieTemporal, idMuestraSismica) VALUES
                        -- Serie 1: muestras 1,2
                        (1, 1), (1, 2),
                        -- Serie 2: muestras 3,4
                        (2, 3), (2, 4),
                        -- Serie 3: muestras 5,6
                        (3, 5), (3, 6),
                        -- Serie 4: muestras 7,8
                        (4, 7), (4, 8),
                        -- Serie 5: muestras 9,10
                        (5, 9), (5, 10),
                        -- Serie 6: muestras 11,12
                        (6, 11), (6, 12),
                        -- Serie 7: muestras 13,14
                        (7, 13), (7, 14),
                        -- Serie 8: muestras 15,16
                        (8, 15), (8, 16),
                        -- Serie 9: muestras 17,18
                        (9, 17), (9, 18),
                        -- Serie 10: muestras 19,20
                        (10, 19), (10, 20),
                        -- Serie 11: muestras 21,22
                        (11, 21), (11, 22),
                        -- Serie 12: muestras 23,24
                        (12, 23), (12, 24),
                        -- Serie 13: muestras 25,26
                        (13, 25), (13, 26),
                        -- Serie 14: muestras 27,28
                        (14, 27), (14, 28),
                        -- Serie 15: muestras 29,30
                        (15, 29), (15, 30),
                        -- Serie 16: muestras 31,32
                        (16, 31), (16, 32),
                        -- Serie 17: muestras 33,34
                        (17, 33), (17, 34),
                        -- Serie 18: muestras 35,36
                        (18, 35), (18, 36),
                        -- Serie 19: muestras 37,38
                        (19, 37), (19, 38),
                        -- Serie 20: muestras 39,40
                        (20, 39), (20, 40),
                        -- Serie 21: muestras 41,42
                        (21, 41), (21, 42),
                        -- Serie 22: muestras 43,44
                        (22, 43), (22, 44),
                        -- Serie 23: muestras 45,46
                        (23, 45), (23, 46),
                        -- Serie 24: muestras 47,48
                        (24, 47), (24, 48),
                        -- Serie 25: muestras 49,50
                        (25, 49), (25, 50),
                        -- Serie 26: muestras 51,52
                        (26, 51), (26, 52),
                        -- Serie 27: muestras 53,54
                        (27, 53), (27, 54),
                        -- Serie 28: muestras 55,56
                        (28, 55), (28, 56),
                        -- Serie 29: muestras 57,58
                        (29, 57), (29, 58),
                        -- Serie 30: muestras 59,60
                        (30, 59), (30, 60),
                        -- Serie 31: muestras 61,62
                        (31, 61), (31, 62),
                        -- Serie 32: muestras 63,64
                        (32, 63), (32, 64),
                        -- Serie 33: muestras 65,66
                        (33, 65), (33, 66),
                        -- Serie 34: muestras 67,68
                        (34, 67), (34, 68),
                        -- Serie 35: muestras 69,70
                        (35, 69), (35, 70),
                        -- Serie 36: muestras 71,72
                        (36, 71), (36, 72);
                    """);

            // ====== RELACIONES MUESTRA SISMICA ↔ DETALLE MUESTRA SISMICA (N:N) ======
            // Vinculan cada muestra síismica con sus 3 detalles (Frecuencia, Longitud, Velocidad)
            // 72 muestras * 3 detalles = 216 detalles totales
            stmt.executeUpdate("""
                        INSERT OR IGNORE INTO MuestraSismica_DetalleMuestraSismica (idMuestraSismica, idDetalleMuestraSismica) VALUES
                        -- Muestra 1: detalles 1,2,3
                        (1, 1), (1, 2), (1, 3),
                        -- Muestra 2: detalles 4,5,6
                        (2, 4), (2, 5), (2, 6),
                        -- Muestra 3: detalles 7,8,9
                        (3, 7), (3, 8), (3, 9),
                        -- Muestra 4: detalles 10,11,12
                        (4, 10), (4, 11), (4, 12),
                        -- Muestra 5: detalles 13,14,15
                        (5, 13), (5, 14), (5, 15),
                        -- Muestra 6: detalles 16,17,18
                        (6, 16), (6, 17), (6, 18),
                        -- Muestra 7: detalles 19,20,21
                        (7, 19), (7, 20), (7, 21),
                        -- Muestra 8: detalles 22,23,24
                        (8, 22), (8, 23), (8, 24),
                        -- Muestra 9: detalles 25,26,27
                        (9, 25), (9, 26), (9, 27),
                        -- Muestra 10: detalles 28,29,30
                        (10, 28), (10, 29), (10, 30),
                        -- Muestra 11: detalles 31,32,33
                        (11, 31), (11, 32), (11, 33),
                        -- Muestra 12: detalles 34,35,36
                        (12, 34), (12, 35), (12, 36),
                        -- Muestra 13: detalles 37,38,39
                        (13, 37), (13, 38), (13, 39),
                        -- Muestra 14: detalles 40,41,42
                        (14, 40), (14, 41), (14, 42),
                        -- Muestra 15: detalles 43,44,45
                        (15, 43), (15, 44), (15, 45),
                        -- Muestra 16: detalles 46,47,48
                        (16, 46), (16, 47), (16, 48),
                        -- Muestra 17: detalles 49,50,51
                        (17, 49), (17, 50), (17, 51),
                        -- Muestra 18: detalles 52,53,54
                        (18, 52), (18, 53), (18, 54),
                        -- Muestra 19: detalles 55,56,57
                        (19, 55), (19, 56), (19, 57),
                        -- Muestra 20: detalles 58,59,60
                        (20, 58), (20, 59), (20, 60),
                        -- Muestra 21: detalles 61,62,63
                        (21, 61), (21, 62), (21, 63),
                        -- Muestra 22: detalles 64,65,66
                        (22, 64), (22, 65), (22, 66),
                        -- Muestra 23: detalles 67,68,69
                        (23, 67), (23, 68), (23, 69),
                        -- Muestra 24: detalles 70,71,72
                        (24, 70), (24, 71), (24, 72),
                        -- Muestra 25: detalles 73,74,75
                        (25, 73), (25, 74), (25, 75),
                        -- Muestra 26: detalles 76,77,78
                        (26, 76), (26, 77), (26, 78),
                        -- Muestra 27: detalles 79,80,81
                        (27, 79), (27, 80), (27, 81),
                        -- Muestra 28: detalles 82,83,84
                        (28, 82), (28, 83), (28, 84),
                        -- Muestra 29: detalles 85,86,87
                        (29, 85), (29, 86), (29, 87),
                        -- Muestra 30: detalles 88,89,90
                        (30, 88), (30, 89), (30, 90),
                        -- Muestra 31: detalles 91,92,93
                        (31, 91), (31, 92), (31, 93),
                        -- Muestra 32: detalles 94,95,96
                        (32, 94), (32, 95), (32, 96),
                        -- Muestra 33: detalles 97,98,99
                        (33, 97), (33, 98), (33, 99),
                        -- Muestra 34: detalles 100,101,102
                        (34, 100), (34, 101), (34, 102),
                        -- Muestra 35: detalles 103,104,105
                        (35, 103), (35, 104), (35, 105),
                        -- Muestra 36: detalles 106,107,108
                        (36, 106), (36, 107), (36, 108),
                        -- Muestra 37: detalles 109,110,111
                        (37, 109), (37, 110), (37, 111),
                        -- Muestra 38: detalles 112,113,114
                        (38, 112), (38, 113), (38, 114),
                        -- Muestra 39: detalles 115,116,117
                        (39, 115), (39, 116), (39, 117),
                        -- Muestra 40: detalles 118,119,120
                        (40, 118), (40, 119), (40, 120),
                        -- Muestra 41: detalles 121,122,123
                        (41, 121), (41, 122), (41, 123),
                        -- Muestra 42: detalles 124,125,126
                        (42, 124), (42, 125), (42, 126),
                        -- Muestra 43: detalles 127,128,129
                        (43, 127), (43, 128), (43, 129),
                        -- Muestra 44: detalles 130,131,132
                        (44, 130), (44, 131), (44, 132),
                        -- Muestra 45: detalles 133,134,135
                        (45, 133), (45, 134), (45, 135),
                        -- Muestra 46: detalles 136,137,138
                        (46, 136), (46, 137), (46, 138),
                        -- Muestra 47: detalles 139,140,141
                        (47, 139), (47, 140), (47, 141),
                        -- Muestra 48: detalles 142,143,144
                        (48, 142), (48, 143), (48, 144),
                        -- Muestra 49: detalles 145,146,147
                        (49, 145), (49, 146), (49, 147),
                        -- Muestra 50: detalles 148,149,150
                        (50, 148), (50, 149), (50, 150),
                        -- Muestra 51: detalles 151,152,153
                        (51, 151), (51, 152), (51, 153),
                        -- Muestra 52: detalles 154,155,156
                        (52, 154), (52, 155), (52, 156),
                        -- Muestra 53: detalles 157,158,159
                        (53, 157), (53, 158), (53, 159),
                        -- Muestra 54: detalles 160,161,162
                        (54, 160), (54, 161), (54, 162),
                        -- Muestra 55: detalles 163,164,165
                        (55, 163), (55, 164), (55, 165),
                        -- Muestra 56: detalles 166,167,168
                        (56, 166), (56, 167), (56, 168),
                        -- Muestra 57: detalles 169,170,171
                        (57, 169), (57, 170), (57, 171),
                        -- Muestra 58: detalles 172,173,174
                        (58, 172), (58, 173), (58, 174),
                        -- Muestra 59: detalles 175,176,177
                        (59, 175), (59, 176), (59, 177),
                        -- Muestra 60: detalles 178,179,180
                        (60, 178), (60, 179), (60, 180),
                        -- Muestra 61: detalles 181,182,183
                        (61, 181), (61, 182), (61, 183),
                        -- Muestra 62: detalles 184,185,186
                        (62, 184), (62, 185), (62, 186),
                        -- Muestra 63: detalles 187,188,189
                        (63, 187), (63, 188), (63, 189),
                        -- Muestra 64: detalles 190,191,192
                        (64, 190), (64, 191), (64, 192),
                        -- Muestra 65: detalles 193,194,195
                        (65, 193), (65, 194), (65, 195),
                        -- Muestra 66: detalles 196,197,198
                        (66, 196), (66, 197), (66, 198),
                        -- Muestra 67: detalles 199,200,201
                        (67, 199), (67, 200), (67, 201),
                        -- Muestra 68: detalles 202,203,204
                        (68, 202), (68, 203), (68, 204),
                        -- Muestra 69: detalles 205,206,207
                        (69, 205), (69, 206), (69, 207),
                        -- Muestra 70: detalles 208,209,210
                        (70, 208), (70, 209), (70, 210),
                        -- Muestra 71: detalles 211,212,213
                        (71, 211), (71, 212), (71, 213),
                        -- Muestra 72: detalles 214,215,216
                        (72, 214), (72, 215), (72, 216);
                    """);

            System.out.println("Datos iniciales insertados correctamente (tablas llenas).");
        } catch (SQLException e) {
            System.err.println("Error insertando datos: " + e.getMessage());
            throw e;
        } finally {
            try {
                stmt.close();
            } catch (SQLException ex) {
                // ignorar
            }
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
