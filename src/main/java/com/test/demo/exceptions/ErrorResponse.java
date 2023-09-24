package com.test.demo.exceptions;

import lombok.Data;

@Data
public class ErrorResponse {
    private long timestamp;
    private int status;
    private String message;
    private String path;
}
