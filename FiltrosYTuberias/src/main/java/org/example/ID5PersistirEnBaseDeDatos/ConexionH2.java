package org.example.ID5PersistirEnBaseDeDatos;

import org.example.util.Property;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class ConexionH2 {

    private static final String URL_BD = Property.getPropertyString("db.url");
    private static final String USUARIO = Property.getPropertyString("db.usuario");
    private static final String CLAVE = Property.getPropertyString("db.clave");

    static {
        crearTablaSiNoExiste();
    }

    private ConexionH2() {
        // Clase de utilidad, no se instancia
    }

    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL_BD, USUARIO, CLAVE);
    }

    private static void crearTablaSiNoExiste() {
        String sql = "CREATE TABLE IF NOT EXISTS ARCHIVOS_PROCESADOS (" +
                "ID INT AUTO_INCREMENT PRIMARY KEY, " +
                "NOMBRE VARCHAR(500), " +
                "RUTA_ABSOLUTA VARCHAR(1000), " +
                "TAMANO_BYTES BIGINT, " +
                "FECHA_PROCESAMIENTO TIMESTAMP, " +
                "CONTENIDO BLOB)";

        try (Connection conexion = obtenerConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {

            stmt.execute();

        } catch (SQLException e) {
            throw new RuntimeException("Error al crear la tabla en H2Database", e);
        }
    }
}