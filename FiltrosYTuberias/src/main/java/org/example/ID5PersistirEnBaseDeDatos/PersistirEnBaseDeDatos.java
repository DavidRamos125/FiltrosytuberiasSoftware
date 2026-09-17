package org.example.ID5PersistirEnBaseDeDatos;

import org.example.interfaz.Filtro;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PersistirEnBaseDeDatos implements Filtro {

    private static final String URL_BD = "jdbc:h2:./data/filtros_db;AUTO_SERVER=TRUE";
    private static final String USUARIO = "sa";
    private static final String CLAVE = "";

    static {
        crearTablaSiNoExiste();
    }

    private static void crearTablaSiNoExiste() {
        String sql = "CREATE TABLE IF NOT EXISTS ARCHIVOS_PROCESADOS (" +
                "ID INT AUTO_INCREMENT PRIMARY KEY, " +
                "NOMBRE VARCHAR(500), " +
                "RUTA_ABSOLUTA VARCHAR(1000), " +
                "TAMANO_BYTES BIGINT, " +
                "FECHA_PROCESAMIENTO TIMESTAMP)";

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
            System.out.println("La ruta del directorio no puede estar vacía.");
            return null;
        }
        return procesar(new File(ruta));
    }

    @Override
    public File procesar(File directorio) {

        if (directorio == null || !directorio.exists() || !directorio.isDirectory()) {
            System.out.println("El directorio no existe o no es válido.");
            return null;
        }

        List<File> archivos = new ArrayList<>();
        recolectarArchivos(directorio, archivos);

        List<String> procesados = new ArrayList<>();

        if (archivos.isEmpty()) {
            System.out.println("No se encontraron archivos en el directorio.");
        } else {
            for (File archivo : archivos) {
                if (persistirArchivo(archivo)) {
                    procesados.add(archivo.getAbsolutePath());
                }
            }
        }

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

    private boolean persistirArchivo(File archivo) {

        String sql = "INSERT INTO ARCHIVOS_PROCESADOS " +
                "(NOMBRE, RUTA_ABSOLUTA, TAMANO_BYTES, FECHA_PROCESAMIENTO) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conexion = DriverManager.getConnection(URL_BD, USUARIO, CLAVE);
             PreparedStatement stmt = conexion.prepareStatement(sql)) {

            stmt.setString(1, archivo.getName());
            stmt.setString(2, archivo.getAbsolutePath());
            stmt.setLong(3, archivo.length());
            stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));

            stmt.executeUpdate();

            return true;

        } catch (SQLException e) {
            System.out.println("Error al persistir el archivo " + archivo.getName() + ": " + e.getMessage());
            return false;
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

            System.out.println("Listado generado correctamente: " + archivoSalida.getAbsolutePath());
            return archivoSalida;

        } catch (IOException e) {
            System.out.println("Error al crear el archivo de listado: " + e.getMessage());
            return null;
        }
    }

    @Override
    public String toString() {
        return "Persistir en base de datos";
    }
}