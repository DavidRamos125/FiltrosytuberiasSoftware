package org.example.ID1FiltroImagen;

import org.example.factory.Fabrica;
import org.example.interfaz.Filtro;
import org.example.util.Property;
import org.example.util.utilFile;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class FiltroImagen implements Filtro {

    private BufferedImage aplicarFiltros(BufferedImage imagen) {
        imagen = EscalaGrises.procesar(imagen);
        imagen = ReducirTamano.procesar(imagen, Property.getPropertyFloat("imagen.reduccion"));
        imagen = AjustarBrillo.procesar(imagen, Property.getPropertyInt("imagen.brillo"));
        imagen = Rotar.procesar(imagen, Property.getPropertyInt("imagen.rotar"));
        return imagen;
    }

    private File procesarArchivo(File archivo) {

        if (!utilFile.esImagen(archivo)) {
            return null;
        }

        try {
            BufferedImage imagen = ImageIO.read(archivo);
            imagen = aplicarFiltros(imagen);

            File archivoProcesado = Fabrica.obtenerFile(
                    utilFile.obtenerNombreProcesado(archivo));

            ImageIO.write(imagen,
                    utilFile.obtenerFormato(archivo),
                    archivoProcesado);

            System.out.println("Archivo procesado: " + archivoProcesado.getAbsolutePath());

            return archivoProcesado;

        } catch (IOException e) {
            throw new RuntimeException("Error al procesar o guardar la imagen", e);
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