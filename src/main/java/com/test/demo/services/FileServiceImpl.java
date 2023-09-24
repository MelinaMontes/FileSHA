package com.test.demo.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

@Service
public class FileServiceImpl implements FileService {
    @Override
    public Map<String, String> calculateHashes(MultipartFile file) throws Exception {
        InputStream inputStream = file.getInputStream();

        // Elegimos el algoritmo de hash
        MessageDigest sha256Digest = MessageDigest.getInstance("SHA-256");
        MessageDigest sha512Digest = MessageDigest.getInstance("SHA-512");

        byte[] buffer = new byte[8192];
        int bytesRead;

        // Leer el archivo y actualizar los hashes
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            sha256Digest.update(buffer, 0, bytesRead);
            sha512Digest.update(buffer, 0, bytesRead);
        }

        // Calculamos los hashes
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

        // Almacenar los hashes
        Map<String, String> hashes = new HashMap<>();
        hashes.put("SHA-256", sha256HashStringBuilder.toString());
        hashes.put("SHA-512", sha512HashStringBuilder.toString());

        return hashes;
    }

}
