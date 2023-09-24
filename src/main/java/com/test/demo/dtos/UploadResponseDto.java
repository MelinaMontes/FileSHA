package com.test.demo.dtos;

import java.util.List;

import lombok.Data;

@Data
public class UploadResponseDto {

  private String algorithm;
  private List<DocumentInfoDto> documents;

}
