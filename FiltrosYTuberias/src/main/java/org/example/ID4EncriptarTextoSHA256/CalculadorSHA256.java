package org.example.ID4EncriptarTextoSHA256;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class CalculadorSHA256 {

    public String calcular(File archivo) throws IOException, NoSuchAlgorithmException {

        byte[] contenido = Files.readAllBytes(archivo.toPath());

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(contenido);

        return convertirAHexadecimal(hashBytes);
    }

    private String convertirAHexadecimal(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }
}