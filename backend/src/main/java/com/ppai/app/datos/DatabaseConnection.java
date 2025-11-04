package com.ppai.app.datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String DB_URL = "jdbc:sqlite:redSismica.sqlite3";
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
