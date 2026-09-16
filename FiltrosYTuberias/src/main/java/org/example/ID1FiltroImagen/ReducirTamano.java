package org.example.ID1FiltroImagen;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ReducirTamano {
    public static BufferedImage procesar(BufferedImage imagen, float tamano) {
        if (tamano <= 0.0 || tamano > 1.0) {
            return imagen;
        }
        int nuevoAncho = (int)(imagen.getWidth() * tamano);
        int nuevoAlto = (int)(imagen.getHeight() * tamano);

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
