package org.example.ID1FiltroImagen;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ReducirTamano {
    public static BufferedImage procesar(BufferedImage imagen) {

        int nuevoAncho = imagen.getWidth() / 2;
        int nuevoAlto = imagen.getHeight() / 2;

        BufferedImage resultado = new BufferedImage(
                nuevoAncho,
                nuevoAlto,
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g = resultado.createGraphics();

        g.drawImage(
                imagen,
                0, 0,
                nuevoAncho,
                nuevoAlto,
                null
        );

        g.dispose();

        return resultado;
    }
}
