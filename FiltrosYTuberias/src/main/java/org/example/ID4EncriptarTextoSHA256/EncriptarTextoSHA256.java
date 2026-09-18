package org.example.ID4EncriptarTextoSHA256;

import org.example.interfaz.Filtro;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class EncriptarTextoSHA256 implements Filtro {

    private final CalculadorSHA256 calculador = new CalculadorSHA256();
    private final EscritorHash escritor = new EscritorHash();

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
            String hash = calculador.calcular(archivo);
            File archivoSalida = escritor.guardar(archivo, hash);

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

    @Override
    public String toString() {
        return "Encriptar Texto SHA-256";
    }
}