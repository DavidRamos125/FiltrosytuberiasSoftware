package org.example.factory;

import java.io.File;

public class Fabrica {
    public static File obtenerFile(String ruta){
        return new File(ruta);
    }
}
