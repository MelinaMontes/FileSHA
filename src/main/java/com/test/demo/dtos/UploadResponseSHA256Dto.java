package com.test.demo.dtos;

import java.util.List;

import lombok.Data;

@Data
public class UploadResponseSHA256Dto {

  private String algorithm = "SHA-256";
  private List<DocumentInfoDto> documents;

}
