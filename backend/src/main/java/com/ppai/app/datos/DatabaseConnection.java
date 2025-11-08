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
            
            if (!dbExists) initDatabase();
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

            // ---------- TABLAS CON RELACIONES (FKs definidas INLINE para SQLite) ----------
            
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
        } catch (SQLException e) {
            System.err.println("Error al crear la base de datos: " + e.getMessage());
            throw e;
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