package org.example.ID5PersistirEnBaseDeDatos;

import org.example.interfaz.Filtro;

import java.io.File;

public class PersistirEnBaseDeDatos implements Filtro {
    @Override
    public File procesar(String ruta) {
        return null;
    }

    @Override
    public File procesar(File archivo) {
        return null;
    }

    @Override
    public String toString() {
        return "Persistir en base de datos";
    }
}
