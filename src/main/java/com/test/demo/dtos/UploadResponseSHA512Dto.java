package com.test.demo.dtos;

import java.util.List;

import lombok.Data;

@Data
public class UploadResponseSHA512Dto {
  
  private String algorithm = "SHA-512";
  private List<DocumentInfoDto> documents;
  
}
