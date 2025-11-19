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
                idEventoSismico INTEGER,
                PRIMARY KEY (idAutoDetectado),
                FOREIGN KEY (idEventoSismico) REFERENCES EventoSismico(idEventoSismico)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS BloqueadoEnRevision (
                idBloqueadoEnRevision INTEGER,
                nombre TEXT,
                idEventoSismico INTEGER,
                PRIMARY KEY (idBloqueadoEnRevision),
                FOREIGN KEY (idEventoSismico) REFERENCES EventoSismico(idEventoSismico)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS Rechazado (
                idRechazado INTEGER,
                nombre TEXT,
                idEventoSismico INTEGER,
                PRIMARY KEY (idRechazado),
                FOREIGN KEY (idEventoSismico) REFERENCES EventoSismico(idEventoSismico)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS Derivado (
                idDerivado INTEGER,
                nombre TEXT,
                idEventoSismico INTEGER,
                PRIMARY KEY (idDerivado),
                FOREIGN KEY (idEventoSismico) REFERENCES EventoSismico(idEventoSismico)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS ConfirmadoPorPersonal (
                idConfirmadoPorPersonal INTEGER,
                nombre TEXT,
                idEventoSismico INTEGER,
                PRIMARY KEY (idConfirmadoPorPersonal),
                FOREIGN KEY (idEventoSismico) REFERENCES EventoSismico(idEventoSismico)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS PendienteDeRevision (
                idPendienteDeRevision INTEGER,
                nombre TEXT,
                idEventoSismico INTEGER,
                PRIMARY KEY (idPendienteDeRevision),
                FOREIGN KEY (idEventoSismico) REFERENCES EventoSismico(idEventoSismico)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS SinRevision (
                idSinRevision INTEGER,
                nombre TEXT,
                idEventoSismico INTEGER,
                PRIMARY KEY (idSinRevision),
                FOREIGN KEY (idEventoSismico) REFERENCES EventoSismico(idEventoSismico)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS Cerrado (
                idCerrado INTEGER,
                nombre TEXT,
                idEventoSismico INTEGER,
                PRIMARY KEY (idCerrado),
                FOREIGN KEY (idEventoSismico) REFERENCES EventoSismico(idEventoSismico)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS PendienteDeCierre (
                idPendienteDeCierre INTEGER,
                nombre TEXT,
                idEventoSismico INTEGER,
                PRIMARY KEY (idPendienteDeCierre),
                FOREIGN KEY (idEventoSismico) REFERENCES EventoSismico(idEventoSismico)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS AutoConfirmado (
                idAutoConfirmado INTEGER,
                nombre TEXT,
                idEventoSismico INTEGER,
                PRIMARY KEY (idAutoConfirmado),
                FOREIGN KEY (idEventoSismico) REFERENCES EventoSismico(idEventoSismico)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS CambioEstado (
                fechaHoraInicio TEXT,
                idEventoSismico INTEGER,
                fechaHoraFin TEXT,
                idEmpleado INTEGER,
                nombreEstado TEXT,
                PRIMARY KEY (fechaHoraInicio, idEventoSismico),
                FOREIGN KEY (idEventoSismico) REFERENCES EventoSismico(idEventoSismico),
                FOREIGN KEY (idEmpleado) REFERENCES Empleado(idEmpleado)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS CambioEstado_AutoDetectado (
                fechaHoraInicio TEXT,
                idEventoSismico INTEGER,
                idAutoDetectado INTEGER,
                PRIMARY KEY (fechaHoraInicio, idEventoSismico, idAutoDetectado),
                FOREIGN KEY (fechaHoraInicio, idEventoSismico) REFERENCES CambioEstado(fechaHoraInicio, idEventoSismico),
                FOREIGN KEY (idAutoDetectado) REFERENCES AutoDetectado(idAutoDetectado)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS CambioEstado_BloqueadoEnRevision (
                fechaHoraInicio TEXT,
                idEventoSismico INTEGER,
                idBloqueadoEnRevision INTEGER,
                PRIMARY KEY (fechaHoraInicio, idEventoSismico, idBloqueadoEnRevision),
                FOREIGN KEY (fechaHoraInicio, idEventoSismico) REFERENCES CambioEstado(fechaHoraInicio, idEventoSismico),
                FOREIGN KEY (idBloqueadoEnRevision) REFERENCES BloqueadoEnRevision(idBloqueadoEnRevision)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS CambioEstado_Derivado (
                fechaHoraInicio TEXT,
                idEventoSismico INTEGER,
                idDerivado INTEGER,
                PRIMARY KEY (fechaHoraInicio, idEventoSismico, idDerivado),
                FOREIGN KEY (fechaHoraInicio, idEventoSismico) REFERENCES CambioEstado(fechaHoraInicio, idEventoSismico),
                FOREIGN KEY (idDerivado) REFERENCES Derivado(idDerivado)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS CambioEstado_Rechazado (
                fechaHoraInicio TEXT,
                idEventoSismico INTEGER,
                idRechazado INTEGER,
                PRIMARY KEY (fechaHoraInicio, idEventoSismico, idRechazado),
                FOREIGN KEY (fechaHoraInicio, idEventoSismico) REFERENCES CambioEstado(fechaHoraInicio, idEventoSismico),
                FOREIGN KEY (idRechazado) REFERENCES Rechazado(idRechazado)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS CambioEstado_ConfirmadoPorPersonal (
                fechaHoraInicio TEXT,
                idEventoSismico INTEGER,
                idConfirmadoPorPersonal INTEGER,
                PRIMARY KEY (fechaHoraInicio, idEventoSismico, idConfirmadoPorPersonal),
                FOREIGN KEY (fechaHoraInicio, idEventoSismico) REFERENCES CambioEstado(fechaHoraInicio, idEventoSismico),
                FOREIGN KEY (idConfirmadoPorPersonal) REFERENCES ConfirmadoPorPersonal(idConfirmadoPorPersonal)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS CambioEstado_PendienteDeRevision (
                fechaHoraInicio TEXT,
                idEventoSismico INTEGER,
                idPendienteDeRevision INTEGER,
                PRIMARY KEY (fechaHoraInicio, idEventoSismico, idPendienteDeRevision),
                FOREIGN KEY (fechaHoraInicio, idEventoSismico) REFERENCES CambioEstado(fechaHoraInicio, idEventoSismico),
                FOREIGN KEY (idPendienteDeRevision) REFERENCES PendienteDeRevision(idPendienteDeRevision)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS CambioEstado_Cerrado (
                fechaHoraInicio TEXT,
                idEventoSismico INTEGER,
                idCerrado INTEGER,
                PRIMARY KEY (fechaHoraInicio, idEventoSismico, idCerrado),
                FOREIGN KEY (fechaHoraInicio, idEventoSismico) REFERENCES CambioEstado(fechaHoraInicio, idEventoSismico),
                FOREIGN KEY (idCerrado) REFERENCES Cerrado(idCerrado)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS CambioEstado_PendienteDeCierre (
                fechaHoraInicio TEXT,
                idEventoSismico INTEGER,
                idPendienteDeCierre INTEGER,
                PRIMARY KEY (fechaHoraInicio, idEventoSismico, idPendienteDeCierre),
                FOREIGN KEY (fechaHoraInicio, idEventoSismico) REFERENCES CambioEstado(fechaHoraInicio, idEventoSismico),
                FOREIGN KEY (idPendienteDeCierre) REFERENCES PendienteDeCierre(idPendienteDeCierre)
            );
            """);

        tables.add("""
                CREATE TABLE IF NOT EXISTS CambioEstado_AutoConfirmado (
                fechaHoraInicio TEXT,
                idEventoSismico INTEGER,
                idAutoConfirmado INTEGER,
                PRIMARY KEY (fechaHoraInicio, idEventoSismico, idAutoConfirmado),
                FOREIGN KEY (fechaHoraInicio, idEventoSismico) REFERENCES CambioEstado(fechaHoraInicio, idEventoSismico),
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

            // Sismografo (1 equipo disponible vinculado a estación 1)
            stmt.executeUpdate("""
                INSERT OR IGNORE INTO Sismografo (identificadorSismografo, fechaAdquisicion, nroSerie, idEstadoSismografo, idEstacionSismologica) VALUES
                (1, '2024-11-02 00:00:00', 1523, 1, 1)
            """);

            // SerieTemporal (2 series sin evento asociado, idEventoSismico NULL) frecuenciaMuestreo de ejemplo
            stmt.executeUpdate("""
                INSERT OR IGNORE INTO SerieTemporal (idSerieTemporal, condicionAlarma, fechaHoraRegistro, frecuenciaMuestreo, idEstadoSerieTemporal, idEventoSismico, idSismografo) VALUES
                (1, 'umbral alto', '2025-02-21 19:05:41', '50.0', 1, NULL, 1),
                (2, 'umbral bajo', '2025-02-21 19:10:41', '25.0', 2, NULL, 1)
            """);

            // MuestraSismica (2 muestras por serie = 4 total)
            stmt.executeUpdate("""
                INSERT OR IGNORE INTO MuestraSismica (idMuestraSismica, fechaHoraMuestraSismica, idSerieTemporal) VALUES
                (1, '2025-02-21 19:05:41', 1),
                (2, '2025-02-21 19:10:41', 1),
                (3, '2025-02-21 19:05:41', 2),
                (4, '2025-02-21 19:10:41', 2)
            """);

            // DetalleMuestraSismica (3 detalles por muestra: frecuencia, longitud, velocidad)
            stmt.executeUpdate("""
                INSERT OR IGNORE INTO DetalleMuestraSismica (idDetalleMuestraSismica, idTipoDeDato, valor, idMuestraSismica) VALUES
                -- Muestra 1
                (1, 1, 10.00, 1), (2, 2, 0.70, 1), (3, 3, 7.00, 1),
                -- Muestra 2
                (4, 1, 10.05, 2), (5, 2, 0.69, 2), (6, 3, 7.02, 2),
                -- Muestra 3
                (7, 1, 9.98, 3), (8, 2, 0.71, 3), (9, 3, 6.99, 3),
                -- Muestra 4
                (10, 1, 10.02, 4), (11, 2, 0.67, 4), (12, 3, 7.01, 4)
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
