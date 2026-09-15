package org.example.interfaz;

import java.io.File;

public interface Filtro {
    File procesar(String ruta);
    File procesar(File archivo);
}
