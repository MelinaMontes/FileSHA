package com.test.demo.utils;

import java.io.InputStream;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public class HashUtils {
  
  public static Map<String, String> calculateHashes(MultipartFile file) throws Exception {
    //entrada del archivo
    InputStream inputStream = file.getInputStream();

    // elegimos el algoritmo de hash
    MessageDigest sha256Digest = MessageDigest.getInstance("SHA-256");
    MessageDigest sha512Digest = MessageDigest.getInstance("SHA-512");

    byte[] buffer = new byte[8192];
    int bytesRead;

    // Leer el archivoy actualizar los hashes
    while ((bytesRead = inputStream.read(buffer)) != -1) {
        sha256Digest.update(buffer, 0, bytesRead);
        sha512Digest.update(buffer, 0, bytesRead);
    }

    // Calcular los hashes y convertirlos
    byte[] sha256HashBytes = sha256Digest.digest();
    byte[] sha512HashBytes = sha512Digest.digest();

    StringBuilder sha256HashStringBuilder = new StringBuilder();
    StringBuilder sha512HashStringBuilder = new StringBuilder();

    for (byte hashByte : sha256HashBytes) {
        String hex = Integer.toHexString(0xff & hashByte);
        if (hex.length() == 1) {
            sha256HashStringBuilder.append('0');
        }
        sha256HashStringBuilder.append(hex);
    }

    for (byte hashByte : sha512HashBytes) {
        String hex = Integer.toHexString(0xff & hashByte);
        if (hex.length() == 1) {
            sha512HashStringBuilder.append('0');
        }
        sha512HashStringBuilder.append(hex);
    }
    inputStream.close();

    // almacenar los hashes
    Map<String, String> hashes = new HashMap<>();
    hashes.put("SHA-256", sha256HashStringBuilder.toString());
    hashes.put("SHA-512", sha512HashStringBuilder.toString());

    return hashes;
  }
}
