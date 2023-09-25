package com.test.demo.utils;

import com.test.demo.exceptions.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartException;

public class Responses {
    public static ErrorResponse buildErrorResponse(String message) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(System.currentTimeMillis());
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setMessage(message);
        errorResponse.setPath("/api/documents/hash");
        return errorResponse;
    }

    public static ErrorResponse buildErrorResponse(MultipartException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(System.currentTimeMillis());
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setPath("/api/documents/hash");
        return errorResponse;
    }
    public static ErrorResponse createErrorResponse(HttpStatus status, String message, String path) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(System.currentTimeMillis());
        errorResponse.setStatus(status.value());
        errorResponse.setMessage(message);
        errorResponse.setPath("api/document");
        return errorResponse;
    }

}
