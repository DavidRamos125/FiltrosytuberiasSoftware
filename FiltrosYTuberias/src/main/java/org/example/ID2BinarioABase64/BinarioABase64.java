package org.example.ID2BinarioABase64;

import org.example.interfaz.Filtro;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;

public class BinarioABase64 implements Filtro {
    @Override
    public File procesar(File source) {
        if (source == null || !source.exists()) {
            System.err.println("El archivo o directorio no existe.");
            return null;
        }
        if (source.isDirectory()) {
            return procesarDirectorio(source);
        }
        return procesarArchivo(source);
    }

    @Override
    public File procesar(String ruta) {
        if (ruta == null || ruta.isBlank()) {
            System.err.println("La ruta no puede estar vacía.");
            return null;
        }
        return procesar(new File(ruta));
    }

    @Override
    public String toString() {
        return "Convertir Binario a Base64";
    }

    private File procesarDirectorio(File directorio) {

        File[] archivos = directorio.listFiles(File::isFile);

        if (archivos == null || archivos.length == 0) {
            System.err.println("El directorio no contiene archivos para procesar.");
            return directorio;
        }

        for (File archivo : archivos) {
            procesarArchivo(archivo);
        }

        return directorio;
    }

    private File procesarArchivo(File archivoBinario) {
        if (archivoBinario == null || !archivoBinario.exists() || !archivoBinario.isFile()) {
            System.err.println("El archivo no existe o no es válido.");
            return null;
        }
        try {
            // Leer archivo binario
            byte[] binario = Files.readAllBytes(archivoBinario.toPath());

            // Convertir binario a Base64
            String base64 = Base64.getEncoder().encodeToString(binario);

            // Crear archivo de salida
            String nombre = archivoBinario.getName();
            int punto = nombre.lastIndexOf('.');
            String nombreSinExtension;

            if (punto > 0) {
                nombreSinExtension = nombre.substring(0, punto);
            }
            else {
                nombreSinExtension = nombre;
            }

            File archivoBase64 = new File(
                    archivoBinario.getParent(),
                    nombreSinExtension + "-nuevo-" +  ".base64"
            );

            // Escribir Base64
            Files.writeString(
                    archivoBase64.toPath(),
                    base64,
                    StandardCharsets.UTF_8
            );
            return archivoBase64;
        } catch (IOException e) {
            System.err.println("Error al leer o escribir el archivo: " + e.getMessage());
            return null;
        }
    }

    private String detectarExtension(byte[] datos) {
        if (datos.length >= 4) {
            // PDF: %PDF
            if (datos[0] == 0x25 &&
                    datos[1] == 0x50 &&
                    datos[2] == 0x44 &&
                    datos[3] == 0x46) {
                return ".pdf";
            }
            // JPG
            if ((datos[0] & 0xFF) == 0xFF &&
                    (datos[1] & 0xFF) == 0xD8 &&
                    (datos[2] & 0xFF) == 0xFF) {
                return ".jpg";
            }
            // PNG
            if ((datos[0] & 0xFF) == 0x89 &&
                    datos[1] == 0x50 &&
                    datos[2] == 0x4E &&
                    datos[3] == 0x47) {
                return ".png";
            }
            // GIF
            if (datos[0] == 'G' &&
                    datos[1] == 'I' &&
                    datos[2] == 'F' &&
                    datos[3] == '8') {
                return ".gif";
            }
            // ZIP
            if (datos[0] == 0x50 &&
                    datos[1] == 0x4B &&
                    datos[2] == 0x03 &&
                    datos[3] == 0x04) {
                return ".zip";
            }
        }

        return ".bin";
    }
}
