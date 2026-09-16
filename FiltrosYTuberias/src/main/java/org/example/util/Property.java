package org.example.util;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Property {

    private static final Properties propiedades = new Properties();

    static {
        try (InputStream input = Property.class
                .getClassLoader()
                .getResourceAsStream("application.properties")) {

            if (input == null) {
                throw new RuntimeException(
                        "No se encontró application.properties"
                );
            }
            propiedades.load(input);

        } catch (IOException e) {
            throw new RuntimeException(
                    "Error al cargar application.properties",
                    e
            );
        }
    }

    public static int getPropertyInt(String property) {
        return Integer.parseInt(
                getPropertyString(property)
        );
    }

    public static float getPropertyFloat(String property) {
        return Float.parseFloat(
                getPropertyString(property)
        );
    }

    public static String getPropertyString(String property) {

        String valor = propiedades.getProperty(property);

        if (valor == null) {
            throw new RuntimeException(
                    "La propiedad no existe: " + property
            );
        }
        return valor;
    }
}