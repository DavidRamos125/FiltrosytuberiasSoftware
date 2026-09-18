package org.example.ID5PersistirEnBaseDeDatos;

import org.example.interfaz.Filtro;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PersistirEnBaseDeDatos implements Filtro {

    private static final String CARPETA_DESCARGADOS = "./descargados";

    private final RepositorioArchivos repositorio = new RepositorioArchivos();

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
                if (repositorio.guardar(archivo)) {
                    procesados.add(archivo.getAbsolutePath());
                }
            }
        }

        // Verifica la persistencia recuperando los archivos guardados como bytes
        repositorio.recuperarTodosEn(new File(CARPETA_DESCARGADOS));

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