package com.test.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

/*
 * lanzamos esto cuando el archivo no se subio o es muy grande

 * */
@ControllerAdvice
public class ExceptionHandlerCustom {
   @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(System.currentTimeMillis());
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setMessage("El tamaño del archivo excede el límite permitido.");
        errorResponse.setPath("/api/documents/hash");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ErrorResponse> handleMultipartException(MultipartException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(System.currentTimeMillis());
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setMessage("No se subieron archivos. Por favor cargue uno.");
        errorResponse.setPath("/api/documents/hash");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
