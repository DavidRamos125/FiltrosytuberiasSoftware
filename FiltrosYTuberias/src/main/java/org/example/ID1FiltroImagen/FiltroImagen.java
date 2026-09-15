package org.example.ID1FiltroImagen;

import org.example.interfaz.Filtro;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class FiltroImagen implements Filtro {

    private String obtenerFormato(File archivo) {
        String nombre = archivo.getName();
        int ultimoPunto = nombre.lastIndexOf('.');
        if (ultimoPunto > 0 && ultimoPunto < nombre.length() - 1) {
            return nombre.substring(ultimoPunto + 1).toLowerCase();
        }
        return "png";
    }

    private BufferedImage aplicarFiltros(BufferedImage imagen) {
        imagen = EscalaGrises.procesar(imagen);
        imagen = ReducirTamano.procesar(imagen);
        imagen = AjustarBrillo.procesar(imagen, 50);
        imagen = Rotar.procesar(imagen);
        return imagen;
    }

    private File procesarArchivo(File archivo) {
        try {
            BufferedImage imagen = ImageIO.read(archivo);
            imagen = aplicarFiltros(imagen);

            String formato = obtenerFormato(archivo);

            File carpetaProcesando = new File("procesando");

            if (!carpetaProcesando.exists()) {
                carpetaProcesando.mkdirs();
            }

            File archivoProcesado = new File(
                    carpetaProcesando,
                     "procesado-"+ System.currentTimeMillis() +"." + formato
            );

            ImageIO.write(imagen, formato, archivoProcesado);

            System.out.println(
                    "Archivo procesado: " +
                            archivoProcesado.getAbsolutePath()
            );

            return archivoProcesado;

        } catch (IOException e) {
            throw new RuntimeException(
                    "Error al procesar o guardar la imagen",
                    e
            );
        }
    }


    @Override
    public File procesar(File archivo) {
        return procesarArchivo(archivo);
    }
    @Override
    public File procesar(String ruta) {
        return procesarArchivo(new File(ruta));
    }
    @Override
    public String toString() {
        return "Filtro Imagen";
    }
}