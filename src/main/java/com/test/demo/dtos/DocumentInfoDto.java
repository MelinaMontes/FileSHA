package com.test.demo.dtos;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class DocumentInfoDto {
  
    private String fileName;
    private String hashSha256;
    private String hashSha512;

    private LocalDateTime lastUpload;

}
