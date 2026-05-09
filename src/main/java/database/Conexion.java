package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private static final String URL = "jdbc:postgresql://localhost:5432/sistema_inventario";
    private static final String USUARIO = "developer_h";
    private static final String PASSWORD = "laquesea1";

    private static Connection instancia = null;

    private Conexion() {}

    public static Connection getInstancia() {
        try {
            if (instancia == null || instancia.isClosed()) {
                instancia = DriverManager.getConnection(URL, USUARIO, PASSWORD);
                System.out.println("Conexion establecida.");
            }
        } catch (SQLException e) {
            System.err.println("Error al conectar: " + e.getMessage());
        }
        return instancia;
    }

    public static void cerrarConexion() {
        try {
            if (instancia != null && !instancia.isClosed()) {
                instancia.close();
                instancia = null;
                System.out.println("Conexion cerrada.");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar conexion: " + e.getMessage());
        }
    }
}
