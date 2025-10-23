package com.ppai.app.datos;

import java.sql.*;

/**
 * Clase para manejar la conexión a la base de datos SQLite.
 * Proporciona métodos para conectarse y ejecutar operaciones de base de datos.
 */
public class DatabaseConnection {

    private static final String DB_URL = "jdbc:sqlite:sismos.db";
    private static Connection connection;

    /**
     * Obtiene la conexión a la base de datos (singleton)
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
        }
        return connection;
    }

    /**
     * Inicializa la base de datos creando las tablas necesarias
     */
    public static void inicializarDB() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Crear tabla de EntidadEjemplo si no existe
            String sqlCrearTabla = "CREATE TABLE IF NOT EXISTS entidad_ejemplo (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nombre TEXT NOT NULL," +
                    "fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";

            stmt.execute(sqlCrearTabla);
            System.out.println("✓ Base de datos inicializada correctamente");

        } catch (SQLException e) {
            System.err.println("✗ Error inicializando base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Cierra la conexión a la base de datos
     */
    public static void cerrarConexion() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Conexión a base de datos cerrada");
            }
        } catch (SQLException e) {
            System.err.println("✗ Error cerrando conexión: " + e.getMessage());
        }
    }
}

