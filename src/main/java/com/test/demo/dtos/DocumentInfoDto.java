package com.test.demo.dtos;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class DocumentInfoDto {
  
    private String fileName;
    private String hash;
    private LocalDateTime lastUpload;


}
