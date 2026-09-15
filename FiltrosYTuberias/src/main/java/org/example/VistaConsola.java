package org.example;

import org.example.ID1FiltroImagen.FiltroImagen;
import org.example.ID2BinarioABase64.BinarioABase64;
import org.example.ID3Base64ABinario.Base64ABinario;
import org.example.ID4EncriptarTextoSHA256.EncriptarTextoSHA256;
import org.example.ID5PersistirEnBaseDeDatos.PersistirEnBaseDeDatos;
import org.example.interfaz.Filtro;
import org.example.tuberia.Tuberia;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

public class VistaConsola {
    private static final Scanner scanner = new Scanner(System.in);

    private static final Filtro[] filtrosDisponibles = {
            new FiltroImagen(),
            new BinarioABase64(),
            new Base64ABinario(),
            new EncriptarTextoSHA256(),
            new PersistirEnBaseDeDatos()
    };

    private static final Tuberia tuberia = new Tuberia();

    public void mostrar() {

        int opcion;

        do {
            mostrarMenu();
            opcion = leerEntero("Seleccione una opción: ");

            switch (opcion) {

                case 1:
                    mostrarFiltrosDisponibles();
                    break;

                case 2:
                    agregarFiltro();
                    break;

                case 3:
                    ejecutarTuberia();
                    break;

                case 4:
                    tuberia.limpiar();
                    System.out.println("Tubería limpiada.");
                    break;

                case 0:
                    System.out.println("Saliendo...");
                    break;

                default:
                    System.out.println("Opción no válida.");
            }

        } while (opcion != 0);

        scanner.close();
    }

    private static void mostrarMenu() {

        System.out.println();
        mostrarTuberia();
        System.out.println();
        System.out.println("===== TUBERÍA DE FILTROS =====");
        System.out.println("1. Mostrar filtros disponibles");
        System.out.println("2. Agregar filtro");
        System.out.println("3. Ejecutar tubería");
        System.out.println("4. Limpiar tubería");
        System.out.println("0. Salir");
        System.out.println("==============================");
    }

    private static void mostrarFiltrosDisponibles() {

        System.out.println();
        System.out.println("=== FILTROS DISPONIBLES ===");

        for (int i = 0; i < filtrosDisponibles.length; i++) {

            System.out.println(
                    (i + 1) + ". " +
                            filtrosDisponibles[i].toString()
            );
        }
    }

    private static void agregarFiltro() {

        mostrarFiltrosDisponibles();

        int opcion = leerEntero("Seleccione el filtro que desea agregar: ");

        if (opcion < 1 || opcion > filtrosDisponibles.length) {
            System.out.println("Filtro no válido.");
            return;
        }

        Filtro filtro = filtrosDisponibles[opcion - 1];

        tuberia.agregarFiltro(filtro);

        System.out.println(
                "Filtro agregado: " +
                        filtro.getClass().getSimpleName()
        );
    }

    private static void mostrarTuberia() {

        System.out.println();
        System.out.println("=== TUBERÍA ACTUAL ===");

        if (tuberia.getFiltros().isEmpty()) {
            System.out.println("No hay filtros seleccionados.");
            return;
        }

        int posicion = 1;

        for (Filtro filtro : tuberia.getFiltros()) {

            System.out.println(
                    posicion + ". " +
                            filtro.toString()
            );

            posicion++;
        }
    }

    private static void ejecutarTuberia() {

        if (tuberia.getFiltros().isEmpty()) {
            System.out.println("No hay filtros seleccionados.");
            return;
        }

        System.out.print("Ingrese la ruta del archivo: ");
        String ruta = scanner.nextLine();

        try {
            File resultado = tuberia.ejecutar(ruta);
            File carpetaResultados = new File("resultados");
            if (!carpetaResultados.exists()) {
                carpetaResultados.mkdirs();
            }
            System.out.println("Archivo resultado: " + resultado.getName());
            File destino = new File(
                    carpetaResultados,
                    resultado.getName()
            );

            Files.copy(
                    resultado.toPath(),
                    destino.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );

            System.out.println();
            System.out.println("Tubería ejecutada correctamente.");
            System.out.println("Resultado guardado en:");
            System.out.println(destino.getAbsolutePath());

        } catch (Exception e) {

            System.out.println(
                    "Error al ejecutar la tubería: " +
                            e.getMessage()
            );
        }
    }

    private static int leerEntero(String mensaje) {
        while (true) {

            try {

                System.out.print(mensaje);

                return Integer.parseInt(
                        scanner.nextLine()
                );

            } catch (NumberFormatException e) {

                System.out.println(
                        "Debe ingresar un número."
                );
            }
        }
    }
}
