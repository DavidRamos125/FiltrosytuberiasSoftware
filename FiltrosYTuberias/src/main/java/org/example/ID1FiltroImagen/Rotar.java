package org.example.ID1FiltroImagen;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Rotar {
    public static BufferedImage procesar(BufferedImage imagen, int grados) {

        // Normalizar el ángulo a 0, 90, 180 o 270
        grados = ((grados % 360) + 360) % 360;

        int ancho = imagen.getWidth();
        int alto = imagen.getHeight();

        int nuevoAncho;
        int nuevoAlto;

        // En 90 y 270 se intercambian las dimensiones
        if (grados == 90 || grados == 270) {
            nuevoAncho = alto;
            nuevoAlto = ancho;
        } else {
            nuevoAncho = ancho;
            nuevoAlto = alto;
        }

        BufferedImage resultado = new BufferedImage(
                nuevoAncho,
                nuevoAlto,
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g = resultado.createGraphics();

        switch (grados) {

            case 0:
                g.drawImage(imagen, 0, 0, null);
                break;

            case 90:
                g.translate(alto, 0);
                g.rotate(Math.PI / 2);
                g.drawImage(imagen, 0, 0, null);
                break;

            case 180:
                g.translate(ancho, alto);
                g.rotate(Math.PI);
                g.drawImage(imagen, 0, 0, null);
                break;

            case 270:
                g.translate(0, ancho);
                g.rotate(3 * Math.PI / 2);
                g.drawImage(imagen, 0, 0, null);
                break;
        }

        g.dispose();

        return resultado;
    }
}
