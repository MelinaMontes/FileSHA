package com.test.demo.utils;

import java.io.InputStream;
import java.security.MessageDigest;

import org.springframework.web.multipart.MultipartFile;

public class HashUtils {
  
    // Método para calcular el hash del archivo
   public static String calculateHash(MultipartFile file, String hashType) throws Exception {
    // Obtener el flujo de entrada del archivo
    InputStream inputStream = file.getInputStream();

    // Seleccionar el algoritmo de hash según el tipo especificado (SHA-256 o SHA-512)
    MessageDigest messageDigest = MessageDigest.getInstance(hashType);

    byte[] buffer = new byte[8192];
    int bytesRead;

    // Leer el archivo en bloques y actualizar el hash
    while ((bytesRead = inputStream.read(buffer)) != -1) {
        messageDigest.update(buffer, 0, bytesRead);
    }

    // Calcular el hash y convertirlo a una cadena hexadecimal
    byte[] hashBytes = messageDigest.digest();
    StringBuilder hashStringBuilder = new StringBuilder();

    for (byte hashByte : hashBytes) {
        String hex = Integer.toHexString(0xff & hashByte);
        if (hex.length() == 1) {
            hashStringBuilder.append('0');
        }
        hashStringBuilder.append(hex);
    }

    // Cerrar el flujo de entrada del archivo
    inputStream.close();

    // Devolver el hash calculado como una cadena hexadecimal
    return hashStringBuilder.toString();
}

}
