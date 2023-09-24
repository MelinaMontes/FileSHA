package com.test.demo.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.test.demo.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import com.test.demo.dtos.DocumentInfoDto;
import com.test.demo.dtos.DocumentsDto;
import com.test.demo.dtos.UploadResponseDto;
import com.test.demo.entities.File;
import com.test.demo.exceptions.HashTypeException;
import com.test.demo.repos.FileRepository;
import com.test.demo.utils.Responses;

@RestController
public class FileController {

  @Autowired 
  private FileRepository fileRepository;
  @Autowired
  private FileService fileService;

    @PostMapping("api/documents/hash")
    public ResponseEntity<?> uploadDocuments(
            @RequestParam("hashType") String hashType,
            @RequestParam("files") List<MultipartFile> files) {

        try {
            if (!hashType.equals("SHA-256") && !hashType.equals("SHA-512")) {
                throw new HashTypeException("El parámetro 'hash' solo puede ser 'SHA-256' o 'SHA-512'");
            }

            List<DocumentInfoDto> documentInfoList = new ArrayList<>();
            for (MultipartFile file : files) {

                Map<String, String> hashes = fileService.calculateHashes(file);
                String sha256Hash = hashes.get("SHA-256");
                String sha512Hash = hashes.get("SHA-512");

                File existingFile = fileRepository.findByHashSha256OrHashSha512(sha256Hash, sha512Hash);

                DocumentInfoDto documentInfo = new DocumentInfoDto();
                documentInfo.setFileName(file.getOriginalFilename());
                documentInfo.setHash("SHA-256".equals(hashType) ? sha256Hash : sha512Hash);

                if (existingFile == null) {
                    File newDocument = new File();
                    newDocument.setFileName(file.getOriginalFilename());
                    newDocument.setHashSha256(sha256Hash);
                    newDocument.setHashSha512(sha512Hash);
                    newDocument.setLastUpload(LocalDateTime.now());
                    fileRepository.save(newDocument);

                } else {
                    existingFile.setLastUpload(LocalDateTime.now());
                    fileRepository.save(existingFile);
                    documentInfo.setLastUpload(existingFile.getLastUpload());
                }

                documentInfoList.add(documentInfo);
            }

            UploadResponseDto response = new UploadResponseDto();
            response.setAlgorithm(hashType);
            response.setDocuments(documentInfoList);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (MultipartException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Responses.buildErrorResponse(ex));
        } catch (HashTypeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Responses.buildErrorResponse(ex.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error en el procesamiento de archivos: " + e.getMessage());
        }
    }

    //list all files
  @GetMapping("/api/documents")
  public List<DocumentsDto> getDocuments() {
      List<File> files = fileRepository.findAll();

      List<DocumentsDto> documentInfoList = new ArrayList<>();
      for (File file : files) {
          DocumentsDto documentInfo = new DocumentsDto();
          documentInfo.setFileName(file.getFileName());
          documentInfo.setHashSha256(file.getHashSha256());
          documentInfo.setHashSha512(file.getHashSha512());
          documentInfo.setLastUpload(file.getLastUpload());
          documentInfoList.add(documentInfo);
      }
  
      return documentInfoList;
  }
  
//get file by hashtype & hash
  @GetMapping("/api/document")
public ResponseEntity<?> getDocumentByHash(
        @RequestParam("hashType") String hashType,
        @RequestParam("hash") String hash)
        {
    try {
        Optional<File> optionalFile;

        if (hashType.equals("SHA-256")) {
            optionalFile = fileRepository.findByHashSha256(hash);
        } else if (hashType.equals("SHA-512")) {
            optionalFile = fileRepository.findByHashSha512(hash);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tipo de hash no válido.");
        }

        if (optionalFile.isPresent()) {
            File file = optionalFile.get();
            DocumentsDto documentInfo = new DocumentsDto();
            documentInfo.setFileName(file.getFileName());

            if (hashType.equals("SHA-256")) {
                documentInfo.setHashSha256(file.getHashSha256());
            } else {
                documentInfo.setHashSha512(file.getHashSha512());
            }

            // Incluir la fecha de la última carga si está disponible
            if (file.getLastUpload() != null) {
                documentInfo.setLastUpload(file.getLastUpload());
            }

            return ResponseEntity.status(HttpStatus.OK).body(documentInfo);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró ningún documento con ese hash.");
        }
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error en el servidor: " + e.getMessage());
    }
}

}
