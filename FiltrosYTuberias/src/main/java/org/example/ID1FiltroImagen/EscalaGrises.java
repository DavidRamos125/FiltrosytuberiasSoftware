package org.example.ID1FiltroImagen;

import java.awt.*;
import java.awt.image.BufferedImage;

public class EscalaGrises {
    public static BufferedImage procesar(BufferedImage imagen){
        BufferedImage resultado = new BufferedImage(
                imagen.getWidth(),
                imagen.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY
        );

        Graphics2D g = resultado.createGraphics();

        g.drawImage(imagen, 0, 0, null);

        g.dispose();

        return resultado;
    }
}
