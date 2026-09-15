package org.example.ID1FiltroImagen;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Rotar {
    public static BufferedImage procesar(BufferedImage imagen) {

        int ancho = imagen.getWidth();
        int alto = imagen.getHeight();

        BufferedImage resultado = new BufferedImage(
                alto,
                ancho,
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g = resultado.createGraphics();

        g.rotate(Math.PI / 2, alto / 2.0, ancho / 2.0);

        g.drawImage(imagen, 0, 0, null);

        g.dispose();

        return resultado;
    }
}
