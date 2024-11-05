package DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Modifica la URL usando el nombre de servidor que vimos: SCARLET\MSSQLSERVER01
    private static final String URL = "jdbc:sqlserver://SCARLET\\MSSQLSERVER01:1433;databaseName=NodeMap;integratedSecurity=true;encrypt=true;trustServerCertificate=true";

    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Como estás usando Windows Authentication, no necesitas USER y PASSWORD
            connection = DriverManager.getConnection(URL);
            System.out.println("Conexión exitosa a SQL Server");
        } catch (SQLException e) {
            System.err.println("Error al conectar con SQL Server: " + e.getMessage());
        }
        return connection;
    }

    public static void closeConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexión cerrada");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }

   /* public static void main(String[] args) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn != null) {
                System.out.println("Conexión establecida. Realizando operaciones...");
            }
        } catch (Exception e) {
            System.err.println("Error en la aplicación: " + e.getMessage());
        } finally {
            closeConnection(conn);
        }
    }*/
}
