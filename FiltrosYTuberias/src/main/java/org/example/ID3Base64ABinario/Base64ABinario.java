package org.example.ID3Base64ABinario;

import org.example.interfaz.Filtro;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;

public class Base64ABinario implements Filtro {
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
        return "Convertir Base64 a Binario";
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

    private File procesarArchivo(File archivoBase64) {
        if (archivoBase64 == null || !archivoBase64.exists() || !archivoBase64.isFile()) {
            System.err.println("El archivo no existe o no es válido.");
            return null;
        }
        try {
            String base64 = Files.readString(
                    archivoBase64.toPath(),
                    StandardCharsets.UTF_8
            );

            // Eliminar espacios y saltos de línea
            base64 = base64.replaceAll("\\s+", "");

            // Soportar Base64 con prefijo:
            // data:application/pdf;base64,...
            if (base64.startsWith("data:")) {
                int separador = base64.indexOf(',');
                if (separador >= 0) {
                    base64 = base64.substring(separador + 1);
                }
            }
            // Decodificar
            byte[] binario = Base64.getDecoder().decode(base64);

            // Crear archivo de salida
            String nombre = archivoBase64.getName();
            String nombreSinExtension;
            int punto = nombre.lastIndexOf('.');

            if (punto > 0) {
                nombreSinExtension = nombre.substring(0, punto);
            } else {
                nombreSinExtension = nombre;
            }

            String extension = detectarExtension(binario);

            File archivoBinario = new File(
                    archivoBase64.getParent(),
                    nombreSinExtension + "-nuevo-" + extension
            );

            // Escribir binario
            Files.write(
                    archivoBinario.toPath(),
                    binario
            );
            return archivoBinario;
        } catch (IllegalArgumentException e) {
            System.err.println("El archivo no contiene un Base64 válido: " + archivoBase64.getName());
            return null;
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
