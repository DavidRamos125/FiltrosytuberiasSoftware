package org.example.ID5PersistirEnBaseDeDatos;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Responsable únicamente de leer/escribir archivos en la tabla ARCHIVOS_PROCESADOS.
 * No sabe nada de recorrer directorios ni de generar listados de texto.
 */
public class RepositorioArchivos {

    /**
     * Convierte el archivo a bytes y lo guarda completo en la base de datos.
     */
    public boolean guardar(File archivo) {

        String sql = "INSERT INTO ARCHIVOS_PROCESADOS " +
                "(NOMBRE, RUTA_ABSOLUTA, TAMANO_BYTES, FECHA_PROCESAMIENTO, CONTENIDO) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionH2.obtenerConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {

            byte[] contenido = Files.readAllBytes(archivo.toPath());

            stmt.setString(1, archivo.getName());
            stmt.setString(2, archivo.getAbsolutePath());
            stmt.setLong(3, archivo.length());
            stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            stmt.setBytes(5, contenido);

            stmt.executeUpdate();

            return true;

        } catch (IOException e) {
            System.err.println("Error al leer el archivo " + archivo.getName() + ": " + e.getMessage());
            return false;
        } catch (SQLException e) {
            System.err.println("Error al persistir el archivo " + archivo.getName() + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Recupera todos los archivos guardados y los reconstruye físicamente
     * dentro del directorio destino indicado.
     */
    public void recuperarTodosEn(File directorioDestino) {

        if (!directorioDestino.exists() && !directorioDestino.mkdirs()) {
            System.err.println("No se pudo crear la carpeta: " + directorioDestino.getAbsolutePath());
            return;
        }

        String sql = "SELECT NOMBRE, CONTENIDO FROM ARCHIVOS_PROCESADOS";

        try (Connection conexion = ConexionH2.obtenerConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet resultado = stmt.executeQuery()) {

            while (resultado.next()) {
                String nombre = resultado.getString("NOMBRE");
                byte[] contenido = resultado.getBytes("CONTENIDO");

                if (contenido == null) {
                    continue;
                }

                File archivoDestino = new File(directorioDestino, nombre);

                try (FileOutputStream salida = new FileOutputStream(archivoDestino)) {
                    salida.write(contenido);
                }
            }

        } catch (SQLException | IOException e) {
            System.err.println("Error al recuperar archivos desde la base de datos: " + e.getMessage());
        }
    }
}