package com.test.demo.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.test.demo.dtos.DocumentInfoDto;
import com.test.demo.dtos.FileDto;
import com.test.demo.dtos.UploadResponseSHA256Dto;
import com.test.demo.dtos.UploadResponseSHA512Dto;
import com.test.demo.entities.File;
import com.test.demo.repos.FileRepository;

import com.test.demo.utils.HashUtils;


@RestController
public class FileController {
  private static final Logger logger = LoggerFactory.getLogger(FileController.class);

  @Autowired 
  FileRepository fileRepository;
  
 @PostMapping("api/documents/hash")
public ResponseEntity<?> uploadDocuments(
    @RequestParam("hashType") String hashType,
    @RequestParam("files") List<MultipartFile> files) {
     
    if (files.isEmpty()) {
        return ResponseEntity.badRequest().body("No se subieron archivos.");
    }

    try {
        List<DocumentInfoDto> documentInfoList = new ArrayList<>();
        for (MultipartFile file : files) {
            // Verificar si el archivo se subió correctamente
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Uno o más archivos están vacíos.");
            }

            String fileHash = HashUtils.calculateHash(file, hashType);
            Optional<File> existingDocument;

            // Verificar si el hash ya existe en la BD
            if (hashType.equals("SHA-256")) {
                existingDocument = fileRepository.findByHashSha256(fileHash);
            } else if (hashType.equals("SHA-512")) {
                existingDocument = fileRepository.findByHashSha512(fileHash);
            } else {
                return ResponseEntity.badRequest().body("Tipo de hash no válido.");
            }

            DocumentInfoDto documentInfo = new DocumentInfoDto();
            documentInfo.setFileName(file.getOriginalFilename());
            
            documentInfo.setHashSha256(fileHash);
            documentInfo.setHashSha512(hashType);

            if (existingDocument.isPresent()) {
                // Actualizar la fecha de última carga
                File document = existingDocument.get();
                document.setLastUpload(LocalDateTime.now());
                fileRepository.save(document);
                documentInfo.setLastUpload(document.getLastUpload());
            } else {
                // Crear un nuevo registro
                File newDocument = new File();
                if (hashType.equals("SHA-256")) {
                    newDocument.setHashSha256(fileHash);
                } else if (hashType.equals("SHA-512")) {
                    newDocument.setHashSha512(fileHash);
                }
                newDocument.setLastUpload(LocalDateTime.now());
                fileRepository.save(newDocument);
            }

            documentInfoList.add(documentInfo);
        }

        if (hashType.equals("SHA-256")) {
            UploadResponseSHA256Dto response = new UploadResponseSHA256Dto();
            response.setDocuments(documentInfoList);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else if (hashType.equals("SHA-512")) {
            UploadResponseSHA512Dto response = new UploadResponseSHA512Dto();
            response.setDocuments(documentInfoList);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.badRequest().body("Tipo de hash no válido.");
        }
    } catch (Exception e) {
        // Manejar errores de procesamiento de archivos
        return ResponseEntity.badRequest().body("Error en el procesamiento de archivos: " + e.getMessage());
    }
}

  
  

    @GetMapping("/api/documents")
    public List<DocumentInfoDto> getDocuments() {
      List<File> files = fileRepository.findAll();
      
      // Convierte la lista de File en una lista de DocumentInfoDto
      List<DocumentInfoDto> documentInfoList = new ArrayList<>();
      for (File file : files) {
          DocumentInfoDto documentInfo = new DocumentInfoDto();
          documentInfo.setFileName(file.getFileName());
          documentInfo.setHashSha256(file.getHashSha256());
          documentInfo.setHashSha512(file.getHashSha512());
          documentInfo.setLastUpload(file.getLastUpload());
          documentInfoList.add(documentInfo);
      }
  
      return documentInfoList;
      
    }


    @GetMapping("/api/document")
    public ResponseEntity<File> getDocumentByHash(
      @RequestParam(name = "hashType", required = false) String hashType,
      @RequestParam(name = "hash", required = false) String hash) {
          try {
            // Verifica si se proporcionaron los parámetros hashType y hash
            if (hashType != null && hash != null) {
                Optional<File> optionalDocument = null;

                switch (hashType) {
                    case "SHA-256":
                        optionalDocument = fileRepository.findByHashSha256(hash);
                        break;
                    case "SHA-512":
                        optionalDocument = fileRepository.findByHashSha512(hash);
                        break;
                    default:
                        return ResponseEntity.badRequest().build(); 
                }

                if (optionalDocument.isPresent()) {
                    File document = optionalDocument.get();
                    return ResponseEntity.ok(document);
                } else {
                    return ResponseEntity.notFound().build();
                }
            } else {
                return ResponseEntity.notFound().build(); // Parámetros faltantes
            }
        } catch (Exception e) {
            logger.error("No hay ningún documento con ese nombre", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
