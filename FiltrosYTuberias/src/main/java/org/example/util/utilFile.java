package org.example.util;

import java.io.File;

public class utilFile {

    public static String obtenerFormato(File archivo) {
        String nombre = archivo.getName();
        int ultimoPunto = nombre.lastIndexOf('.');
        if (ultimoPunto > 0 && ultimoPunto < nombre.length() - 1) {
            return nombre.substring(ultimoPunto + 1).toLowerCase();
        }
        return "png";
    }

    public static boolean esImagen(File archivo) {
        String nombre = archivo.getName();
        int ultimoPunto = nombre.lastIndexOf('.');
        if (ultimoPunto > 0 && ultimoPunto < nombre.length() - 1) {
            nombre =  nombre.substring(ultimoPunto + 1).toLowerCase();
        }
        return (nombre.equals("png") || nombre.equals("jpg")) ;
    }

    public static String obtenerNombreSinFormato(File archivo) {
        String nombre = archivo.getName();
        int punto = nombre.lastIndexOf('.');
        if (punto > 0) {
            return nombre.substring(0, punto);
        }
        return nombre;
    }

    public static String obtenerNombreProcesado(File archivo) {
        return obtenerNombreSinFormato(archivo)
                + "-"
                + System.currentTimeMillis()
                + "."
                + obtenerFormato(archivo);
    }
}
