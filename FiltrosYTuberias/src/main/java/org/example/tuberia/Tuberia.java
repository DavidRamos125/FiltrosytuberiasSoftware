package org.example.tuberia;

import org.example.interfaz.Filtro;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Tuberia {

    private final List<Filtro> filtros;

    public Tuberia() {
        filtros = new ArrayList<>();
    }

    public void agregarFiltro(Filtro filtro) {
        filtros.add(filtro);
    }

    public void limpiar() {
        filtros.clear();
    }

    public List<Filtro> getFiltros() {
        return filtros;
    }

    public File ejecutar(String ruta) {
        File archivo = new File(ruta);
        return ejecutar(archivo);
    }

    public File ejecutar(File archivo) {
        File resultado = archivo;

        for (Filtro filtro : filtros) {
            resultado = filtro.procesar(resultado);
        }

        return resultado;
    }
}