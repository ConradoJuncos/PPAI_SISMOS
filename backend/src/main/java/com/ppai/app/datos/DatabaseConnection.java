package com.ppai.app.datos;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {

    private static final String DB_URL = "jdbc:sqlite:redSismica.sqlite3";
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException e) {
                throw new SQLException("Error: No se encontró el driver JDBC de SQLite.", e);
            }

            File dbFile = new File("redSismica.sqlite3");
            boolean dbExists = dbFile.exists();

            connection = DriverManager.getConnection(DB_URL);

            try (Statement s = connection.createStatement()) {
                s.execute("PRAGMA foreign_keys = ON;");
            }

            if (!dbExists) {
                initDatabase();
            }
        }
        return connection;
    }

    private static void initDatabase() throws SQLException {
        try (Connection conn = getConnection()) {
            createTables(conn);
            insertSampleData(conn);
            System.out.println("Base de datos creada y datos iniciales cargados.");
        }
    }

    private static void createTables(Connection conn) throws SQLException {
        List<String> tables = new ArrayList<>();

        tables.add("CREATE TABLE IF NOT EXISTS Rol (\n" +
                "    idRol INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    nombre TEXT NOT NULL UNIQUE,\n" +
                "    descripcion TEXT\n" +
                ");");

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

        tables.add("CREATE TABLE IF NOT EXISTS Estado (\n" +
                "    ambitoEstado TEXT NOT NULL,\n" +
                "    nombreEstado TEXT NOT NULL,\n" +
                "    PRIMARY KEY (ambitoEstado, nombreEstado)\n" +
                ");");

        tables.add("CREATE TABLE IF NOT EXISTS ModeloSismografo (\n" +
                "    idModeloSismografo INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    nombre TEXT NOT NULL,\n" +
                "    marca TEXT NOT NULL,\n" +
                "    precisionGrados REAL\n" +
                ");");

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

        tables.add("CREATE TABLE IF NOT EXISTS MotivoFueraServicio (\n" +
                "    idMotivoFueraServicio INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    nombre TEXT NOT NULL,\n" +
                "    descripcion TEXT\n" +
                ");");

        tables.add("CREATE TABLE IF NOT EXISTS CambioEstado (\n" +
                "    idCambioEstado INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    fechaHoraInicio DATETIME NOT NULL,\n" +
                "    fechaHoraFin DATETIME,\n" +
                "    ambitoEstado TEXT NOT NULL,\n" +
                "    nombreEstado TEXT NOT NULL,\n" +
                "    idResponsableInspeccion INTEGER,\n" +
                "    FOREIGN KEY (ambitoEstado, nombreEstado) REFERENCES Estado(ambitoEstado, nombreEstado),\n" +
                "    FOREIGN KEY (idResponsableInspeccion) REFERENCES Empleado(idEmpleado)\n" +
                ");");

        tables.add("CREATE TABLE IF NOT EXISTS CambioEstado_MotivoFueraServicio (\n" +
                "    idCambioEstado INTEGER NOT NULL,\n" +
                "    idMotivoFueraServicio INTEGER NOT NULL,\n" +
                "    PRIMARY KEY (idCambioEstado, idMotivoFueraServicio),\n" +
                "    FOREIGN KEY (idCambioEstado) REFERENCES CambioEstado(idCambioEstado),\n" +
                "    FOREIGN KEY (idMotivoFueraServicio) REFERENCES MotivoFueraServicio(idMotivoFueraServicio)\n" +
                ");");

        tables.add("CREATE TABLE IF NOT EXISTS Sismografo_CambioEstado (\n" +
                "    identificadorSismografo INTEGER NOT NULL,\n" +
                "    idCambioEstado INTEGER NOT NULL,\n" +
                "    PRIMARY KEY (identificadorSismografo, idCambioEstado),\n" +
                "    FOREIGN KEY (identificadorSismografo) REFERENCES Sismografo(identificadorSismografo),\n" +
                "    FOREIGN KEY (idCambioEstado) REFERENCES CambioEstado(idCambioEstado)\n" +
                ");");

        tables.add("CREATE TABLE IF NOT EXISTS TareaAsignada (\n" +
                "    idTareaAsignada INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    fechaHoraAsignacion DATETIME NOT NULL,\n" +
                "    comentario TEXT,\n" +
                "    fechaHoraRealizacion DATETIME,\n" +
                "    idResponsable INTEGER NOT NULL,\n" +
                "    nroReparacion INTEGER NOT NULL,\n" +
                "    FOREIGN KEY (idResponsable) REFERENCES Empleado(idEmpleado),\n" +
                "    FOREIGN KEY (nroReparacion) REFERENCES Reparacion(nroReparacion)\n" +
                ");");

        tables.add("CREATE TABLE IF NOT EXISTS ApreciacionTipo (\n" +
                "    idApreciacionTipo INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    nombre TEXT NOT NULL,\n" +
                "    descripcion TEXT\n" +
                ");");

        tables.add("CREATE TABLE IF NOT EXISTS TareaAsignada_ApreciacionTipo (\n" +
                "    idTareaAsignada INTEGER NOT NULL,\n" +
                "    idApreciacionTipo INTEGER NOT NULL,\n" +
                "    PRIMARY KEY (idTareaAsignada, idApreciacionTipo),\n" +
                "    FOREIGN KEY (idTareaAsignada) REFERENCES TareaAsignada(idTareaAsignada),\n" +
                "    FOREIGN KEY (idApreciacionTipo) REFERENCES ApreciacionTipo(idApreciacionTipo)\n" +
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
                "    nombre TEXT NOT NULL,\n" +
                "    unidadMedida TEXT NOT NULL,\n" +
                "    umbralMaximo REAL\n" +
                ");");

        tables.add("CREATE TABLE IF NOT EXISTS EventoSismico (\n" +
                "    idEventoSismico INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    fechaHoraOcurrencia DATETIME NOT NULL,\n" +
                "    latitudEpicentro REAL NOT NULL,\n" +
                "    longitudEpicentro REAL NOT NULL,\n" +
                "    profundidadHipocentro REAL,\n" +
                "    magnitudRichter REAL NOT NULL,\n" +
                "    clasificacionSismo TEXT,\n" +
                "    origenGeneracion TEXT,\n" +
                "    alcanceSismo TEXT,\n" +
                "    ambitoEstado TEXT NOT NULL,\n" +
                "    nombreEstado TEXT NOT NULL,\n" +
                "    FOREIGN KEY (ambitoEstado, nombreEstado) REFERENCES Estado(ambitoEstado, nombreEstado)\n" +
                ");");

        tables.add("CREATE TABLE IF NOT EXISTS SerieTemporal (\n" +
                "    idSerieTemporal INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    fechaHoraInicio DATETIME NOT NULL,\n" +
                "    frecuenciaMuestreoHz REAL NOT NULL,\n" +
                "    alertaAlarma INTEGER DEFAULT 0,\n" +
                "    idEventoSismico INTEGER NOT NULL,\n" +
                "    codigoEstacion INTEGER NOT NULL,\n" +
                "    FOREIGN KEY (idEventoSismico) REFERENCES EventoSismico(idEventoSismico),\n" +
                "    FOREIGN KEY (codigoEstacion) REFERENCES EstacionSismologica(codigoEstacion)\n" +
                ");");

        tables.add("CREATE TABLE IF NOT EXISTS MuestraSismica (\n" +
                "    idMuestraSismica INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    fechaHora DATETIME NOT NULL,\n" +
                "    idSerieTemporal INTEGER NOT NULL,\n" +
                "    FOREIGN KEY (idSerieTemporal) REFERENCES SerieTemporal(idSerieTemporal)\n" +
                ");");

        tables.add("CREATE TABLE IF NOT EXISTS DetalleMuestraSismica (\n" +
                "    idDetalleMuestra INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    valor REAL NOT NULL,\n" +
                "    idMuestraSismica INTEGER NOT NULL,\n" +
                "    idTipoDeDato INTEGER NOT NULL,\n" +
                "    FOREIGN KEY (idMuestraSismica) REFERENCES MuestraSismica(idMuestraSismica),\n" +
                "    FOREIGN KEY (idTipoDeDato) REFERENCES TipoDeDato(idTipoDeDato)\n" +
                ");");

        tables.add("""
                    CREATE TABLE IF NOT EXISTS Suscripcion (
                        idSuscripcion INTEGER PRIMARY KEY AUTOINCREMENT,
                        fechaHoraInicioSuscripcion TEXT,
                        fechaHoraFinSuscripcion TEXT
                    );
                """);

        tables.add("""
                    CREATE TABLE IF NOT EXISTS Usuario_Suscripcion (
                        idUsuario INTEGER NOT NULL,
                        idSuscripcion INTEGER NOT NULL,
                        PRIMARY KEY (idUsuario, idSuscripcion),
                        FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario),
                        FOREIGN KEY (idSuscripcion) REFERENCES Suscripcion(idSuscripcion)
                    );
                """);

        tables.add("""
                    CREATE TABLE IF NOT EXISTS Permiso (
                        idPermiso INTEGER NOT NULL,
                        descripcion TEXT,
                        nombre TEXT,
                        PRIMARY KEY(idPermiso)
                    );
                """);

        tables.add("""
                    CREATE TABLE IF NOT EXISTS Perfil (
                        idPerfil INTEGER NOT NULL,
                        descripcion TEXT,
                        nombre TEXT,
                        PRIMARY KEY(idPerfil)
                    );
                """);

        tables.add("""
                    CREATE TABLE IF NOT EXISTS Perfil_Perimso (
                        idPerfil INTEGER NOT NULL,
                        idPermiso INTEGER NOT NULL,
                        PRIMARY KEY (idPerfil, idPermiso),
                        FOREIGN KEY (idPerfil) REFERENCES Perfil(idPerfil),
                        FOREIGN KEY (idPermiso) REFERENCES Permiso(idPermiso)
                    );
                """);

        tables.add("""
                    CREATE TABLE IF NOT EXISTS Usuario_Perfil (
                        idUsuario INTEGER NOT NULL,
                        idPerfil INTEGER NOT NULL,
                        PRIMARY KEY (idUsuario, idPerfil),
                        FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario),
                        FOREIGN KEY (idPerfil) REFERENCES Perfil(idPerfil)
                    );
                """);

        try (Statement s = conn.createStatement()) {
            for (String tableSql : tables) {
                s.execute(tableSql);
            }
        }
    }

    public static void insertSampleData(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();

        try {
            // ====== ESTADOS ======
            stmt.executeUpdate("""
                        INSERT OR IGNORE INTO Estado (ambitoEstado, nombreEstado) VALUES
                        ('EventoSismico', 'AutoDetectado'),
                        ('EventoSismico', 'PendienteDeRevision'),
                        ('EventoSismico', 'BloqueadoEnRevision'),
                        ('EventoSismico', 'DerivadoAExperto'),
                        ('EventoSismico', 'Confirmado'),
                        ('EventoSismico', 'Rechazado'),
                        ('EventoSismico', 'PendienteDeCierre'),
                        ('EventoSismico', 'Cerrado'),
                        ('EventoSismico', 'SinRevision');
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

            // ====== MODELOS ======
            stmt.executeUpdate("""
                        INSERT OR IGNORE INTO ModeloSismografo (nombre, marca, precisionGrados) VALUES
                        ('ZET 7152-N VER.3', 'ZETLAB', 0.01),
                        ('GeoTech A500', 'GeoTech', 0.02);
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
                        INSERT OR IGNORE INTO TipoDeDato (nombre, unidadMedida, umbralMaximo) VALUES
                        ('Frecuencia de onda', 'Hz', 15.0),
                        ('Longitud de onda', 'km/ciclo', 1.5),
                        ('Velocidad de onda', 'km/s', 8.0);
                    """);

            // ====== EVENTOS SISMICOS (12) ======
            // IDs asignados implícitamente 1..12 por el orden de inserción
            stmt.executeUpdate(
                    """
                                INSERT OR IGNORE INTO EventoSismico (
                                    fechaHoraOcurrencia, latitudEpicentro, longitudEpicentro, profundidadHipocentro,
                                    magnitudRichter, clasificacionSismo, origenGeneracion, alcanceSismo,
                                    ambitoEstado, nombreEstado
                                ) VALUES
                                -- 1..4 AutoDetectado
                                ('2025-02-21 19:05:41', -31.52, -64.19, 45.0, 4.3, 'Intermedio', 'Sismo interplaca', 'Regional', 'EventoSismico', 'AutoDetectado'),
                                ('2025-04-01 10:00:00', -31.10, -65.20, 20.0, 3.8, 'Superficial', 'Sismo cortical', 'Local', 'EventoSismico', 'AutoDetectado'),
                                ('2025-04-02 14:15:00', -31.25, -65.45, 25.0, 4.1, 'Intermedio', 'Sismo interplaca', 'Regional', 'EventoSismico', 'AutoDetectado'),
                                ('2025-04-03 09:30:00', -31.40, -65.60, 30.0, 4.5, 'Intermedio', 'Sismo cortical', 'Regional', 'EventoSismico', 'AutoDetectado'),
                                -- 5..6 PendienteRevision
                                ('2025-04-05 12:10:00', -31.70, -65.90, 40.0, 5.0, 'Profundo', 'Sismo interplaca', 'Regional', 'EventoSismico', 'PendienteDeRevision'),
                                ('2025-04-06 18:25:00', -31.85, -66.05, 45.0, 5.2, 'Profundo', 'Sismo cortical', 'Regional', 'EventoSismico', 'PendienteDeRevision'),
                                -- 7 BloqueadoRevision
                                ('2025-04-07 03:00:00', -32.00, -66.20, 30.0, 4.3, 'Intermedio', 'Sismo cortical', 'Regional', 'EventoSismico', 'BloqueadoEnRevision'),
                                -- 8 Derivado
                                ('2025-04-08 05:20:00', -32.15, -66.35, 25.0, 4.0, 'Intermedio', 'Sismo interplaca', 'Local', 'EventoSismico', 'DerivadoAExperto'),
                                -- 9..10 Confirmado
                                ('2025-04-09 21:40:00', -32.30, -66.50, 20.0, 4.6, 'Superficial', 'Sismo cortical', 'Regional', 'EventoSismico', 'Confirmado'),
                                ('2025-04-11 08:00:00', -32.40, -66.55, 18.0, 4.9, 'Superficial', 'Sismo cortical', 'Regional', 'EventoSismico', 'Confirmado'),
                                -- 11..12 Rechazado
                                ('2025-03-08 13:00:00', -32.15, -68.40, 35.0, 3.7, 'Superficial', 'Sismo cortical', 'Local', 'EventoSismico', 'Rechazado'),
                                ('2025-04-10 11:55:00', -32.45, -66.65, 15.0, 3.9, 'Superficial', 'Sismo interplaca', 'Local', 'EventoSismico', 'Rechazado');
                            """);

            // ====== CAMBIOESTADO — registro simple por evento (estado actual) ======
            // Usamos idResponsable 1 o 2 según corresponda
            stmt.executeUpdate(
                    """
                                INSERT OR IGNORE INTO CambioEstado (fechaHoraInicio, fechaHoraFin, ambitoEstado, nombreEstado, idResponsableInspeccion) VALUES
                                ('2025-02-21 19:10:00', NULL, 'EventoSismico', 'AutoDetectado', 1),
                                ('2025-04-01 10:02:00', NULL, 'EventoSismico', 'AutoDetectado', 1),
                                ('2025-04-02 14:17:00', NULL, 'EventoSismico', 'AutoDetectado', 1),
                                ('2025-04-03 09:32:00', NULL, 'EventoSismico', 'AutoDetectado', 1),
                                ('2025-04-05 12:12:00', NULL, 'EventoSismico', 'PendienteDeRevision', 2),
                                ('2025-04-06 18:27:00', NULL, 'EventoSismico', 'PendienteDeRevision', 2),
                                ('2025-04-07 03:02:00', NULL, 'EventoSismico', 'BloqueadoEnRevision', 1),
                                ('2025-04-08 05:22:00', NULL, 'EventoSismico', 'DerivadoAExperto', 1),
                                ('2025-04-09 21:42:00', NULL, 'EventoSismico', 'Confirmado', 2),
                                ('2025-04-11 08:02:00', NULL, 'EventoSismico', 'Confirmado', 2),
                                ('2025-03-08 13:06:00', NULL, 'EventoSismico', 'Rechazado', 2),
                                ('2025-04-10 11:57:00', NULL, 'EventoSismico', 'Rechazado', 2);
                            """);

            // ====== SERIES TEMPORALES (3 por evento) ====
            // Para evitar dependencia en autoincrement, ponemos explicitamente
            // idSerieTemporal = 1..36
            stmt.executeUpdate(
                    """
                                INSERT OR IGNORE INTO SerieTemporal (idSerieTemporal, fechaHoraInicio, frecuenciaMuestreoHz, alertaAlarma, idEventoSismico, codigoEstacion) VALUES
                                -- Evento 1 (idEventoSismico = 1) : series 1,2,3
                                (1, '2025-02-21 19:05:41', 50.0, 0, 1, 1),
                                (2, '2025-02-21 19:05:41', 50.0, 0, 1, 2),
                                (3, '2025-02-21 19:05:41', 50.0, 0, 1, 3),
                                -- Evento 2 (idEventoSismico = 2) : series 4,5,6
                                (4, '2025-04-01 10:00:00', 50.0, 0, 2, 1),
                                (5, '2025-04-01 10:00:00', 50.0, 0, 2, 2),
                                (6, '2025-04-01 10:00:00', 50.0, 0, 2, 3),
                                -- Evento 3 (idEventoSismico = 3) : series 7,8,9
                                (7, '2025-04-02 14:15:00', 50.0, 0, 3, 1),
                                (8, '2025-04-02 14:15:00', 50.0, 0, 3, 2),
                                (9, '2025-04-02 14:15:00', 50.0, 0, 3, 3),
                                -- Evento 4 (idEventoSismico = 4) : series 10,11,12
                                (10, '2025-04-03 09:30:00', 50.0, 0, 4, 1),
                                (11, '2025-04-03 09:30:00', 50.0, 0, 4, 2),
                                (12, '2025-04-03 09:30:00', 50.0, 0, 4, 3),
                                -- Evento 5 (idEventoSismico = 5) : series 13,14,15
                                (13, '2025-04-05 12:10:00', 50.0, 0, 5, 1),
                                (14, '2025-04-05 12:10:00', 50.0, 0, 5, 2),
                                (15, '2025-04-05 12:10:00', 50.0, 0, 5, 3),
                                -- Evento 6 (idEventoSismico = 6) : series 16,17,18
                                (16, '2025-04-06 18:25:00', 50.0, 0, 6, 1),
                                (17, '2025-04-06 18:25:00', 50.0, 0, 6, 2),
                                (18, '2025-04-06 18:25:00', 50.0, 0, 6, 3),
                                -- Evento 7 (idEventoSismico = 7) : series 19,20,21
                                (19, '2025-04-07 03:00:00', 50.0, 0, 7, 1),
                                (20, '2025-04-07 03:00:00', 50.0, 0, 7, 2),
                                (21, '2025-04-07 03:00:00', 50.0, 0, 7, 3),
                                -- Evento 8 (idEventoSismico = 8) : series 22,23,24
                                (22, '2025-04-08 05:20:00', 50.0, 0, 8, 1),
                                (23, '2025-04-08 05:20:00', 50.0, 0, 8, 2),
                                (24, '2025-04-08 05:20:00', 50.0, 0, 8, 3),
                                -- Evento 9 (idEventoSismico = 9) : series 25,26,27
                                (25, '2025-04-09 21:40:00', 50.0, 0, 9, 1),
                                (26, '2025-04-09 21:40:00', 50.0, 0, 9, 2),
                                (27, '2025-04-09 21:40:00', 50.0, 0, 9, 3),
                                -- Evento 10 (idEventoSismico = 10) : series 28,29,30
                                (28, '2025-04-11 08:00:00', 50.0, 0, 10, 1),
                                (29, '2025-04-11 08:00:00', 50.0, 0, 10, 2),
                                (30, '2025-04-11 08:00:00', 50.0, 0, 10, 3),
                                -- Evento 11 (idEventoSismico = 11) : series 31,32,33
                                (31, '2025-03-08 13:00:00', 50.0, 0, 11, 1),
                                (32, '2025-03-08 13:00:00', 50.0, 0, 11, 2),
                                (33, '2025-03-08 13:00:00', 50.0, 0, 11, 3),
                                -- Evento 12 (idEventoSismico = 12) : series 34,35,36
                                (34, '2025-04-10 11:55:00', 50.0, 0, 12, 1),
                                (35, '2025-04-10 11:55:00', 50.0, 0, 12, 2),
                                (36, '2025-04-10 11:55:00', 50.0, 0, 12, 3);
                            """);

            // ====== MUESTRAS SISMICAS (2 por serie) ======
            // idMuestraSismica explícitos 1..72 (36 series * 2)
            stmt.executeUpdate("""
                        INSERT OR IGNORE INTO MuestraSismica (idMuestraSismica, fechaHora, idSerieTemporal) VALUES
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
                        (10.05, 2, 1), (0.69, 2, 2), (7.02, 2, 3),
                        (9.98, 3, 1), (0.71, 3, 2), (6.99, 3, 3),
                        (10.02, 4, 1), (0.68, 4, 2), (7.01, 4, 3),
                        (9.95, 5, 1), (0.72, 5, 2), (7.03, 5, 3),
                        (10.10, 6, 1), (0.70, 6, 2), (7.05, 6, 3),
                        -- muestras 7..12 (evento2)
                        (9.80, 7, 1), (0.69, 7, 2), (6.95, 7, 3),
                        (9.85, 8, 1), (0.70, 8, 2), (6.98, 8, 3),
                        (9.90, 9, 1), (0.71, 9, 2), (7.00, 9, 3),
                        (10.00, 10, 1), (0.72, 10, 2), (7.02, 10, 3),
                        (10.12, 11, 1), (0.73, 11, 2), (7.05, 11, 3),
                        (9.95, 12, 1), (0.68, 12, 2), (7.01, 12, 3),
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
                        (9.80, 43, 1), (0.68, 43, 2), (6.96, 43, 3),
                        (9.85, 44, 1), (0.69, 44, 2), (6.98, 44, 3),
                        (10.00, 45, 1), (0.70, 45, 2), (7.01, 45, 3),
                        (10.02, 46, 1), (0.71, 46, 2), (7.03, 46, 3),
                        (10.08, 47, 1), (0.72, 47, 2), (7.06, 47, 3),
                        (9.95, 48, 1), (0.70, 48, 2), (7.00, 48, 3),
                        -- muestras 49..54 (evento9)
                        (10.12, 49, 1), (0.74, 49, 2), (7.10, 49, 3),
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
