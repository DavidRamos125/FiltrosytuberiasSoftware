package org.example.ID4EncriptarTextoSHA256;

import org.example.interfaz.Filtro;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncriptarTextoSHA256 implements Filtro {

    @Override
    public File procesar(String ruta) {
        if (ruta == null || ruta.isBlank()) {
            System.err.println("La ruta no puede estar vacía.");
            return null;
        }
        return procesar(new File(ruta));
    }

    @Override
    public File procesar(File archivoODirectorio) {

        if (archivoODirectorio == null || !archivoODirectorio.exists()) {
            System.err.println("El archivo o directorio no existe.");
            return null;
        }

        if (archivoODirectorio.isDirectory()) {
            return procesarDirectorio(archivoODirectorio);
        }

        return procesarArchivo(archivoODirectorio);
    }

    private File procesarDirectorio(File directorio) {

        File[] archivos = directorio.listFiles(File::isFile);

        if (archivos == null || archivos.length == 0) {
            System.err.println("El directorio no contiene archivos para procesar.");
            return directorio;
        }

        for (File archivo : archivos) {
            if (archivo.getName().endsWith(".sha256.txt")) {
                continue;
            }
            procesarArchivo(archivo);
        }

        return directorio;
    }

    private File procesarArchivo(File archivo) {

        if (archivo == null || !archivo.exists() || !archivo.isFile()) {
            System.err.println("El archivo no existe o no es válido.");
            return null;
        }

        try {
            byte[] contenido = Files.readAllBytes(archivo.toPath());

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(contenido);

            String hashHexadecimal = convertirAHexadecimal(hashBytes);

            File archivoSalida = new File(
                    archivo.getParent(),
                    archivo.getName() + ".sha256.txt"
            );

            try (FileWriter writer = new FileWriter(archivoSalida)) {
                writer.write(hashHexadecimal);
            }

            System.err.println("Hash generado: " + archivoSalida.getAbsolutePath());

            return archivoSalida;

        } catch (IOException e) {
            System.err.println("Error al leer o escribir el archivo: " + e.getMessage());
            return null;
        } catch (NoSuchAlgorithmException e) {
            System.err.println("No se encontró el algoritmo SHA-256.");
            return null;
        }
    }

    private String convertirAHexadecimal(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return "Encriptar Texto SHA-256";
    }
}