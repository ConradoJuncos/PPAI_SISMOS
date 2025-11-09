package com.ppai.app.datos;

import java.io.File;
import java.sql.*;
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
            // Cargar driver SQLite 
            try {
                Class.forName("org.sqlite.JDBC"); 
            } catch (ClassNotFoundException e) {
                throw new SQLException("Error: No se encontró el driver JDBC de SQLite. Asegúrese de que la dependencia esté en el classpath.", e);
            }

            File dbFile = new File("redSismica.sqlite3");
            boolean dbExists = new File(DB_URL.substring(DB_URL.lastIndexOf(':') + 1)).exists();
            connection = DriverManager.getConnection(DB_URL);

            // Habilitar la aplicación de claves foráneas (PRAGMA)
            try (Statement s = connection.createStatement()) {
                s.execute("PRAGMA foreign_keys = ON;");
            }

            if (!dbExists) {
                initDatabase();
            }
        }
        return connection;
    }

    /* --------------------------------------------------------------
        2. initDatabase – crea tablas e inserta datos iniciales
        -------------------------------------------------------------- */
    private static void initDatabase() throws SQLException {
        try (Connection conn = getConnection()) { 
            createTables(conn);
            insertInitialData(conn);
            System.out.println("Base de datos creada y datos iniciales cargados.");
        }
    }

    private static void createTables(Connection conn) throws SQLException {
        List<String> tables = new ArrayList<>();
        
        tables.add("CREATE TABLE IF NOT EXISTS Empleado (\n" +
                   "    idEmpleado INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                   "    nombre TEXT NOT NULL,\n" +
                   "    apellido TEXT NOT NULL,\n" +
                   "    email TEXT UNIQUE NOT NULL,\n" +
                   "    legajo TEXT UNIQUE NOT NULL\n" +
                   ");");

        // TABLA ESTADO CORREGIDA: Sin columna 'descripcion'
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
                   "    longitud REAL NOT NULL\n" +
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

        tables.add("CREATE TABLE IF NOT EXISTS Reparacion (\n" +
                   "    nroReparacion INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                   "    fechaHoraInicio DATETIME NOT NULL,\n" +
                   "    fechaHoraFin DATETIME,\n" +
                   "    descripcion TEXT\n" +
                   ");");

        tables.add("CREATE TABLE IF NOT EXISTS Reparacion_Sismografo (\n" + 
                   "    identificadorSismografo INTEGER NOT NULL,\n" +
                   "    idReparacion INTEGER NOT NULL,\n" +
                   "    PRIMARY KEY (identificadorSismografo, idReparacion),\n" +
                   "    FOREIGN KEY (identificadorSismografo) REFERENCES Sismografo(identificadorSismografo),\n" +
                   "    FOREIGN KEY (idReparacion) REFERENCES Reparacion(nroReparacion)\n" +
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
        
        try (Statement s = conn.createStatement()) {
            for (String tableSql : tables) {
                s.execute(tableSql);
            }
        }
    }

    private static void insertInitialData(Connection conn) throws SQLException {
        String[] inserts = {
            // =======================================================
            // 1. DATA ESTATICA
            // =======================================================
            
            // ApreciacionTipo
            "INSERT INTO ApreciacionTipo (idApreciacionTipo, nombre, descripcion) VALUES (1, 'Conectividad OK', 'El problema fue de red.');",
            "INSERT INTO ApreciacionTipo (idApreciacionTipo, nombre, descripcion) VALUES (2, 'Componente defectuoso', 'Se requirió el reemplazo de una pieza.');",
            "INSERT INTO ApreciacionTipo (idApreciacionTipo, nombre, descripcion) VALUES (3, 'Falla de energía', 'Fallo temporal en la alimentación eléctrica.');",
            
            // Estado (Clave Compuesta: ambitoEstado, nombreEstado) - SIN DESCRIPCION
            "INSERT INTO Estado (ambitoEstado, nombreEstado) VALUES ('Sismografo', 'Operativo');",
            "INSERT INTO Estado (ambitoEstado, nombreEstado) VALUES ('Sismografo', 'Fuera de Servicio');",
            "INSERT INTO Estado (ambitoEstado, nombreEstado) VALUES ('Sismografo', 'En Mantenimiento');",
            "INSERT INTO Estado (ambitoEstado, nombreEstado) VALUES ('EventoSismico', 'Detectado');",
            
            // MotivoFueraServicio
            "INSERT INTO MotivoFueraServicio (idMotivoFueraServicio, nombre, descripcion) VALUES (1, 'Corte de Fibra', 'Falla en la red de transmisión de datos.');",
            "INSERT INTO MotivoFueraServicio (idMotivoFueraServicio, nombre, descripcion) VALUES (2, 'Sensor Roto', 'El sensor principal dejó de emitir datos válidos.');",
            
            // ModeloSismografo
            "INSERT INTO ModeloSismografo (idModeloSismografo, nombre, marca, precisionGrados) VALUES (1, 'GEOSENSOR X1', 'Geosensor S.A.', 12.5);",
            "INSERT INTO ModeloSismografo (idModeloSismografo, nombre, marca, precisionGrados) VALUES (2, 'TCS-PRO', 'TechCorp Systems', 8.0);",

            // EstacionSismologica
            "INSERT INTO EstacionSismologica (codigoEstacion, nombre, latitud, longitud) VALUES (1, 'Estacion Córdoba', -32.5, -64.1);",
            "INSERT INTO EstacionSismologica (codigoEstacion, nombre, latitud, longitud) VALUES (2, 'Estacion San Juan', -31.0, -68.5);",

            // Empleado
            "INSERT INTO Empleado (idEmpleado, nombre, apellido, email, legajo) VALUES (1, 'Juan', 'Perez', 'juan.perez@red.ar', '12345678');",
            "INSERT INTO Empleado (idEmpleado, nombre, apellido, email, legajo) VALUES (2, 'Maria', 'Gomez', 'maria.gomez@red.ar', '87654321');",

            // =======================================================
            // 2. ENTIDADES PRINCIPALES Y SUS RELACIONES
            // =======================================================
            
            // Sismografo
            "INSERT INTO Sismografo (identificadorSismografo, fechaAdquisicion, nroSerie, idModelo, codigoEstacion) VALUES (1, '2023-01-15 00:00:00', 1001, 1, 1);", 
            "INSERT INTO Sismografo (identificadorSismografo, fechaAdquisicion, nroSerie, idModelo, codigoEstacion) VALUES (2, '2023-03-20 00:00:00', 1002, 2, 2);", 
            
            // Reparacion
            "INSERT INTO Reparacion (nroReparacion, fechaHoraInicio, fechaHoraFin, descripcion) VALUES (1, '2024-05-01 13:00:00', '2024-05-02 18:00:00', 'Reemplazo de sensor de campo. El sensor #A falló.');",
            
            // Reparacion_Sismografo
            "INSERT INTO Reparacion_Sismografo (identificadorSismografo, idReparacion) VALUES (1, 1);", 
            
            // TareaAsignada
            "INSERT INTO TareaAsignada (idTareaAsignada, fechaHoraAsignacion, comentario, fechaHoraRealizacion, idResponsable, nroReparacion) VALUES (1, '2024-05-01 14:00:00', 'Se diagnosticó el fallo del sensor.', '2024-05-01 15:00:00', 1, 1);", 
            
            // TareaAsignada_ApreciacionTipo
            "INSERT INTO TareaAsignada_ApreciacionTipo (idTareaAsignada, idApreciacionTipo) VALUES (1, 2);", 
            
            // =======================================================
            // 3. CAMBIOS DE ESTADO (Historial y Estado Actual)
            // =======================================================
            
            // Sismografo 1 (Historial: Operativo -> FdS -> Operativo)
            // CE 1: Inicial - Operativo (Finalizado)
            "INSERT INTO CambioEstado (idCambioEstado, fechaHoraInicio, fechaHoraFin, ambitoEstado, nombreEstado, idResponsableInspeccion) VALUES (1, '2023-01-15 00:00:00', '2024-05-01 07:59:59', 'Sismografo', 'Operativo', NULL);",
            // CE 2: Falla - Fuera de Servicio (Finalizado)
            "INSERT INTO CambioEstado (idCambioEstado, fechaHoraInicio, fechaHoraFin, ambitoEstado, nombreEstado, idResponsableInspeccion) VALUES (2, '2024-05-01 08:00:00', '2024-05-03 11:59:59', 'Sismografo', 'Fuera de Servicio', 1);", 
            // CE 3: Actual - Operativo (ACTUAL)
            "INSERT INTO CambioEstado (idCambioEstado, fechaHoraInicio, fechaHoraFin, ambitoEstado, nombreEstado, idResponsableInspeccion) VALUES (3, '2024-05-03 12:00:00', NULL, 'Sismografo', 'Operativo', 1);", 
            
            // CambioEstado_MotivoFueraServicio (CE 2 con Motivo 1)
            "INSERT INTO CambioEstado_MotivoFueraServicio (idCambioEstado, idMotivoFueraServicio) VALUES (2, 1);", 

            // Sismografo_CambioEstado (Asociación de los 3 cambios al Sismografo 1)
            "INSERT INTO Sismografo_CambioEstado (identificadorSismografo, idCambioEstado) VALUES (1, 1);",
            "INSERT INTO Sismografo_CambioEstado (identificadorSismografo, idCambioEstado) VALUES (1, 2);",
            "INSERT INTO Sismografo_CambioEstado (identificadorSismografo, idCambioEstado) VALUES (1, 3);", 
            
            // Sismografo 2 
            // CE 4: Inicial - Operativo (ACTUAL)
            "INSERT INTO CambioEstado (idCambioEstado, fechaHoraInicio, fechaHoraFin, ambitoEstado, nombreEstado, idResponsableInspeccion) VALUES (4, '2023-03-20 00:00:00', NULL, 'Sismografo', 'Operativo', NULL);", 
            "INSERT INTO Sismografo_CambioEstado (identificadorSismografo, idCambioEstado) VALUES (2, 4);",
        };

        conn.setAutoCommit(false); // Iniciar transacción

        try (Statement s = conn.createStatement()) {
            for (String insert : inserts) {
                s.addBatch(insert);
            }
            s.executeBatch();
            conn.commit(); // Confirmar la transacción
        } catch (SQLException e) {
            conn.rollback(); // Deshacer si hay error
            System.err.println("--- ERROR DE SQL DETECTADO ---");
            System.err.println("Mensaje de SQL: " + e.getMessage());
            System.err.println("Se realizó un Rollback.");
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