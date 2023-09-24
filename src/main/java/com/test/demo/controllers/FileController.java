package com.test.demo.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import com.test.demo.exceptions.ErrorResponse;
import com.test.demo.exceptions.HashTypeException;
import com.test.demo.repos.FileRepository;

import com.test.demo.utils.HashUtils;


@RestController
public class FileController {

  @Autowired 
  FileRepository fileRepository;
  
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

            Map<String, String> hashes = HashUtils.calculateHashes(file);

            String sha256Hash = hashes.get("SHA-256"); 
            String sha512Hash = hashes.get("SHA-512"); 

            Optional<File> existingDocument;

            // Verificar si el hash ya existe en la BD
            if (hashType.equals("SHA-256")) {
                existingDocument = fileRepository.findByHashSha256(sha256Hash);
            } else {
                existingDocument = fileRepository.findByHashSha512(sha512Hash);
            }

            DocumentInfoDto documentInfo = new DocumentInfoDto();
            documentInfo.setFileName(file.getOriginalFilename());
            documentInfo.setLastUpload(LocalDateTime.now());
            documentInfo.setHash("SHA-256".equals(hashType) ? sha256Hash : sha512Hash);

            if (existingDocument.isPresent()) {
                // El documento ya existe en la BD, actualizar lastUpload
                File document = existingDocument.get();
                document.setLastUpload(LocalDateTime.now());
                fileRepository.save(document);
                documentInfo.setLastUpload(document.getLastUpload());
            } else {
                // Crear un nuevo registro
                File newDocument = new File();
                newDocument.setFileName(file.getOriginalFilename()); 
                if (hashType.equals("SHA-256")) {
                    newDocument.setHashSha256(sha256Hash);
                } else {
                    newDocument.setHashSha512(sha512Hash);
                }
                newDocument.setLastUpload(null); 
                fileRepository.save(newDocument);
            }

            documentInfoList.add(documentInfo);
        }

        
        UploadResponseDto response = new UploadResponseDto();
        response.setAlgorithm(hashType);
        response.setDocuments(documentInfoList);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (MultipartException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildErrorResponse(ex));

    } catch (HashTypeException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildErrorResponse(ex.getMessage()));

    } catch (Exception e) {
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error en el procesamiento de archivos: " + e.getMessage());
    }
}

  
  //list all files 
  @GetMapping("/api/documents")
  public List<DocumentsDto> getDocuments() {
      List<File> files = fileRepository.findAll();
      
      // Convierte la lista de File en una lista de DocumentInfoDto
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
  
//list file by hashtype & hash
  @GetMapping("/api/document")
public ResponseEntity<?> getDocumentByHash(
        @RequestParam("hashType") String hashType,
        @RequestParam("hash") String hash) {
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

            // Incluir el hash especificado en la respuesta
            if (hashType.equals("SHA-256")) {
                documentInfo.setHashSha256(file.getHashSha256());
            } else if (hashType.equals("SHA-512")) {
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

  
  

  

    private ErrorResponse buildErrorResponse(String message) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(System.currentTimeMillis());
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setMessage(message);
        errorResponse.setPath("/api/documents/hash");
        return errorResponse;
    }

    private ErrorResponse buildErrorResponse(MultipartException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(System.currentTimeMillis());
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setPath("/api/documents/hash");
        return errorResponse;
    }

}
