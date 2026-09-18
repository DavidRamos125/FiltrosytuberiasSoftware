package org.example.ID5PersistirEnBaseDeDatos;

import org.example.interfaz.Filtro;
import org.example.util.Property;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PersistirEnBaseDeDatos implements Filtro {

    private static final String URL_BD = Property.getPropertyString("db.url");
    private static final String USUARIO = Property.getPropertyString("db.usuario");
    private static final String CLAVE = Property.getPropertyString("db.clave");
    private static final String CARPETA_DESCARGADOS = "./descargados";

    static {
        crearTablaSiNoExiste();
    }

    private static void crearTablaSiNoExiste() {
        String sql = "CREATE TABLE IF NOT EXISTS ARCHIVOS_PROCESADOS (" +
                "ID INT AUTO_INCREMENT PRIMARY KEY, " +
                "NOMBRE VARCHAR(500), " +
                "RUTA_ABSOLUTA VARCHAR(1000), " +
                "TAMANO_BYTES BIGINT, " +
                "FECHA_PROCESAMIENTO TIMESTAMP, " +
                "CONTENIDO BLOB)";

        try (Connection conexion = DriverManager.getConnection(URL_BD, USUARIO, CLAVE);
             PreparedStatement stmt = conexion.prepareStatement(sql)) {

            stmt.execute();

        } catch (SQLException e) {
            throw new RuntimeException("Error al crear la tabla en H2Database", e);
        }
    }

    @Override
    public File procesar(String ruta) {
        if (ruta == null || ruta.isBlank()) {
            System.err.println("La ruta del directorio no puede estar vacía.");
            return null;
        }
        return procesar(new File(ruta));
    }

    @Override
    public File procesar(File directorio) {

        if (directorio == null || !directorio.exists() || !directorio.isDirectory()) {
            System.err.println("El directorio no existe o no es válido.");
            return null;
        }

        List<File> archivos = new ArrayList<>();
        recolectarArchivos(directorio, archivos);

        List<String> procesados = new ArrayList<>();

        if (archivos.isEmpty()) {
            System.err.println("No se encontraron archivos en el directorio.");
        } else {
            for (File archivo : archivos) {
                if (persistirArchivo(archivo)) {
                    procesados.add(archivo.getAbsolutePath());
                }
            }
        }

        // Verifica la persistencia recuperando los archivos guardados como bytes
        // y reconstruyéndolos en la carpeta de descargados.
        descargarArchivosDesdeBD();

        return generarListado(directorio, procesados);
    }

    private void recolectarArchivos(File directorio, List<File> acumulador) {
        File[] archivos = directorio.listFiles();

        if (archivos == null) {
            return;
        }

        for (File archivo : archivos) {
            if (archivo.isFile()) {
                acumulador.add(archivo);
            } else if (archivo.isDirectory()) {
                recolectarArchivos(archivo, acumulador);
            }
        }
    }

    /**
     * Guarda el archivo completo (convertido a bytes) dentro de la base de datos,
     * no solo su ruta.
     */
    private boolean persistirArchivo(File archivo) {

        String sql = "INSERT INTO ARCHIVOS_PROCESADOS " +
                "(NOMBRE, RUTA_ABSOLUTA, TAMANO_BYTES, FECHA_PROCESAMIENTO, CONTENIDO) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conexion = DriverManager.getConnection(URL_BD, USUARIO, CLAVE);
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
     * Recupera todos los archivos guardados en la base de datos (sus bytes)
     * y los reconstruye físicamente en la carpeta de descargados.
     */
    private void descargarArchivosDesdeBD() {

        File carpetaDescargados = new File(CARPETA_DESCARGADOS);

        if (!carpetaDescargados.exists() && !carpetaDescargados.mkdirs()) {
            System.err.println("No se pudo crear la carpeta de descargados: " + carpetaDescargados.getAbsolutePath());
            return;
        }

        String sql = "SELECT NOMBRE, CONTENIDO FROM ARCHIVOS_PROCESADOS";

        try (Connection conexion = DriverManager.getConnection(URL_BD, USUARIO, CLAVE);
             PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet resultado = stmt.executeQuery()) {

            while (resultado.next()) {
                String nombre = resultado.getString("NOMBRE");
                byte[] contenido = resultado.getBytes("CONTENIDO");

                if (contenido == null) {
                    continue;
                }

                File archivoDestino = new File(carpetaDescargados, nombre);

                try (FileOutputStream salida = new FileOutputStream(archivoDestino)) {
                    salida.write(contenido);
                }
            }

        } catch (SQLException | IOException e) {
            System.err.println("Error al recuperar archivos desde la base de datos: " + e.getMessage());
        }
    }

    private File generarListado(File directorio, List<String> procesados) {

        File archivoSalida = new File(directorio, "archivos_procesados.txt");

        try (FileWriter writer = new FileWriter(archivoSalida)) {
            writer.write("Listado de archivos procesados\n");
            writer.write("==============================\n\n");

            if (procesados.isEmpty()) {
                writer.write("No se persistió ningún archivo.\n");
            } else {
                for (String ruta : procesados) {
                    writer.write(ruta + "\n");
                }
            }

            System.err.println("Listado generado correctamente: " + archivoSalida.getAbsolutePath());
            return archivoSalida;

        } catch (IOException e) {
            System.err.println("Error al crear el archivo de listado: " + e.getMessage());
            return null;
        }
    }

    @Override
    public String toString() {
        return "Persistir en base de datos";
    }
}