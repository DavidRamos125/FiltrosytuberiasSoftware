package org.example.ID1FiltroImagen;

import java.awt.*;
import java.awt.image.BufferedImage;

public class AjustarBrillo {

    private static int limitar(int valor) {
        return Math.clamp(valor, 0, 255);
    }

    public static BufferedImage procesar(
            BufferedImage imagen,
            int cantidad) {

        BufferedImage resultado = new BufferedImage(
                imagen.getWidth(),
                imagen.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );

        for (int y = 0; y < imagen.getHeight(); y++) {

            for (int x = 0; x < imagen.getWidth(); x++) {

                Color color = new Color(imagen.getRGB(x, y));

                int r = limitar(color.getRed() + cantidad);
                int g = limitar(color.getGreen() + cantidad);
                int b = limitar(color.getBlue() + cantidad);

                Color nuevoColor = new Color(r, g, b);

                resultado.setRGB(x, y, nuevoColor.getRGB());
            }
        }

        return resultado;
    }
}
