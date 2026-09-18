package org.example.ID4EncriptarTextoSHA256;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class EscritorHash {

    public File guardar(File archivoOriginal, String hashHexadecimal) throws IOException {

        File archivoSalida = new File(
                archivoOriginal.getParent(),
                archivoOriginal.getName() + ".sha256.txt"
        );

        try (FileWriter writer = new FileWriter(archivoSalida)) {
            writer.write(hashHexadecimal);
        }

        return archivoSalida;
    }
}