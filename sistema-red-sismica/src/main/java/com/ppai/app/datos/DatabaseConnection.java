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
                CREATE TABLE AlcanceSismo (
                    idAlcanceSismo INTEGER,
                    nombre TEXT,
                    descripcion TEXT,
                    PRIMARY KEY (idAlcanceSismo)
            );
            """);

        tables.add("""
                CREATE TABLE MagnitudRichter (
                    numero INTEGER,
                    descripcion TEXT,
                    PRIMARY KEY (numero)
            );
            """);

        tables.add("""
                CREATE TABLE ClasificacionSismo (
                idClasificacionSismo INTEGER,
                kmProfundidadDesde REAL,
                kmProfundidadHasta REAL,
                nombre TEXT,
                PRIMARY KEY (idClasificacionSismo)
            );
            """);

        tables.add("""
                CREATE TABLE OrigenDeGeneracion (
                idOrigenDeGeneracion INTEGER,
                descripcion TEXT,
                nombre TEXT,
                PRIMARY KEY (idOrigenDeGeneracion)
            );
            """);

        tables.add("""
                CREATE TABLE TipoDeDato (
                idTipoDeDato INTEGER,
                denominacion TEXT,
                nombreUnidadMedida TEXT,
                valorUmbral REAL,
                PRIMARY KEY (idTipoDeDato)
            );
            """);

        tables.add("""
                CREATE TABLE Empleado (
                idEmpleado INTEGER,
                apellido TEXT,
                mail TEXT,
                nombre TEXT,
                telefono TEXT,
                PRIMARY KEY (idEmpleado)
            );
            """);

        tables.add("""
                CREATE TABLE Usuario (
                idUsuario INTEGER,
                contraseña TEXT,
                nombreUsuario TEXT,
                idEmpleado INTEGER,
                PRIMARY KEY (idUsuario),
                FOREIGN KEY (idEmpleado) REFERENCES Empleado(idEmpleado)
            );
            """);

        tables.add("""
                CREATE TABLE EventoSismico (
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
                CREATE TABLE EstadoSerieTemporal (
                idEstadoSerieTemporal INTEGER,
                nombre TEXT,
                PRIMARY KEY (idEstadoSerieTemporal)
            );
            """);

        tables.add("""
                CREATE TABLE SerieTemporal (
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
                FOREIGN KEY (idSismografo) REFERENCES Sismografo(idSismografo)
            );
            """);

        tables.add("""
                CREATE TABLE MuestraSismica (
                idMuestraSismica INTEGER,
                fechaHoraMuestraSismica TEXT,
                idSerieTemporal INTEGER,
                PRIMARY KEY (idMuestraSismica),
                FOREIGN KEY (idSerieTemporal) REFERENCES SerieTemporal(idSerieTemporal)
            );
            """);

        tables.add("""
                CREATE TABLE DetalleMuestraSismica (
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
                CREATE TABLE EstacionSismologica (
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
                CREATE TABLE EstadoSismografo (
                idEstadoSismografo INTEGER,
                nombre TEXT,
                PRIMARY KEY (idEstadoSismografo)
            );
            """);

        tables.add("""
                CREATE TABLE Sismografo (
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
                CREATE TABLE CambioEstadoSismografo (
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
                CREATE TABLE AutoDetectado (
                idAutoDetectado INTEGER,
                nombre TEXT,
                PRIMARY KEY (idAutoDetectado)
            );
            """);

        tables.add("""
                CREATE TABLE BloqueadoEnRevision (
                idBloqueadoEnRevision INTEGER,
                nombre TEXT,
                PRIMARY KEY (idBloqueadoEnRevision)
            );
            """);

        tables.add("""
                CREATE TABLE Rechazado (
                idRechazado INTEGER,
                nombre TEXT,
                PRIMARY KEY (idRechazado)
            );
            """);

        tables.add("""
                CREATE TABLE Derivado (
                idDerivado INTEGER,
                nombre TEXT,
                PRIMARY KEY (idDerivado)
            );
            """);

        tables.add("""
                CREATE TABLE ConfirmadoPorPersonal (
                idConfirmadoPorPersonal INTEGER,
                nombre TEXT,
                PRIMARY KEY (idConfirmadoPorPersonal)
            );
            """);

        tables.add("""
                CREATE TABLE PendienteDeRevision (
                idPendienteDeRevision INTEGER,
                nombre TEXT,
                PRIMARY KEY (idPendienteDeRevision)
            );
            """);

        tables.add("""
                CREATE TABLE SinRevision (
                idSinRevision INTEGER,
                nombre TEXT,
                PRIMARY KEY (idSinRevision)
            );
            """);

        tables.add("""
                CREATE TABLE Cerrado (
                idCerrado INTEGER,
                nombre TEXT,
                PRIMARY KEY (idCerrado)
            );
            """);

        tables.add("""
                CREATE TABLE PendienteDeCierre (
                idPendienteDeCierre INTEGER,
                nombre TEXT,
                PRIMARY KEY (idPendienteDeCierre)
            );
            """);

        tables.add("""
                CREATE TABLE AutoConfirmado (
                idAutoConfirmado INTEGER,
                nombre TEXT,
                PRIMARY KEY (idAutoConfirmado)
            );
            """);

        tables.add("""
                CREATE TABLE CambioEstado (
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
                CREATE TABLE CambioEstado_AutoDetectado (
                fechaHoraInicio TEXT,
                idEventoSismico INTEGER,
                idAutoDetectado INTEGER,
                PRIMARY KEY (fechaHoraInicio, idEventoSismico, idAutoDetectado),
                FOREIGN KEY (fechaHoraInicio, idEventoSismico) REFERENCES CambioEstado(fechaHoraInicio, idEventoSismico),
                FOREIGN KEY (idAutoDetectado) REFERENCES AutoDetectado(idAutoDetectado)
            );
            """);

        tables.add("""
                CREATE TABLE CambioEstado_BloqueadoEnRevision (
                fechaHoraInicio TEXT,
                idEventoSismico INTEGER,
                idBloqueadoEnRevision INTEGER,
                PRIMARY KEY (fechaHoraInicio, idEventoSismico, idBloqueadoEnRevision),
                FOREIGN KEY (fechaHoraInicio, idEventoSismico) REFERENCES CambioEstado(fechaHoraInicio, idEventoSismico),
                FOREIGN KEY (idBloqueadoEnRevision) REFERENCES BloqueadoEnRevision(idBloqueadoEnRevision)
            );
            """);

        tables.add("""
                CREATE TABLE CambioEstado_Derivado (
                fechaHoraInicio TEXT,
                idEventoSismico INTEGER,
                idDerivado INTEGER,
                PRIMARY KEY (fechaHoraInicio, idEventoSismico, idDerivado),
                FOREIGN KEY (fechaHoraInicio, idEventoSismico) REFERENCES CambioEstado(fechaHoraInicio, idEventoSismico),
                FOREIGN KEY (idDerivado) REFERENCES Derivado(idDerivado)
            );
            """);

        tables.add("""
                CREATE TABLE CambioEstado_Rechazado (
                fechaHoraInicio TEXT,
                idEventoSismico INTEGER,
                idRechazado INTEGER,
                PRIMARY KEY (fechaHoraInicio, idEventoSismico, idRechazado),
                FOREIGN KEY (fechaHoraInicio, idEventoSismico) REFERENCES CambioEstado(fechaHoraInicio, idEventoSismico),
                FOREIGN KEY (idRechazado) REFERENCES Rechazado(idRechazado)
            );
            """);

        tables.add("""
                CREATE TABLE CambioEstado_ConfirmadoPorPersonal (
                fechaHoraInicio TEXT,
                idEventoSismico INTEGER,
                idConfirmadoPorPersonal INTEGER,
                PRIMARY KEY (fechaHoraInicio, idEventoSismico, idConfirmadoPorPersonal),
                FOREIGN KEY (fechaHoraInicio, idEventoSismico) REFERENCES CambioEstado(fechaHoraInicio, idEventoSismico),
                FOREIGN KEY (idConfirmadoPorPersonal) REFERENCES ConfirmadoPorPersonal(idConfirmadoPorPersonal)
            );
            """);

        tables.add("""
                CREATE TABLE CambioEstado_PendienteDeRevision (
                fechaHoraInicio TEXT,
                idEventoSismico INTEGER,
                idPendienteDeRevision INTEGER,
                PRIMARY KEY (fechaHoraInicio, idEventoSismico, idPendienteDeRevision),
                FOREIGN KEY (fechaHoraInicio, idEventoSismico) REFERENCES CambioEstado(fechaHoraInicio, idEventoSismico),
                FOREIGN KEY (idPendienteDeRevision) REFERENCES PendienteDeRevision(idPendienteDeRevision)
            );
            """);

        tables.add("""
                CREATE TABLE CambioEstado_Cerrado (
                fechaHoraInicio TEXT,
                idEventoSismico INTEGER,
                idCerrado INTEGER,
                PRIMARY KEY (fechaHoraInicio, idEventoSismico, idCerrado),
                FOREIGN KEY (fechaHoraInicio, idEventoSismico) REFERENCES CambioEstado(fechaHoraInicio, idEventoSismico),
                FOREIGN KEY (idCerrado) REFERENCES Cerrado(idCerrado)
            );
            """);

        tables.add("""
                CREATE TABLE CambioEstado_PendienteDeCierre (
                fechaHoraInicio TEXT,
                idEventoSismico INTEGER,
                idPendienteDeCierre INTEGER,
                PRIMARY KEY (fechaHoraInicio, idEventoSismico, idPendienteDeCierre),
                FOREIGN KEY (fechaHoraInicio, idEventoSismico) REFERENCES CambioEstado(fechaHoraInicio, idEventoSismico),
                FOREIGN KEY (idPendienteDeCierre) REFERENCES PendienteDeCierre(idPendienteDeCierre)
            );
            """);

        tables.add("""
                CREATE TABLE CambioEstado_AutoConfirmado (
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


            System.out.println("Datos iniciales insertados correctamente (tablas llenas).");
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
