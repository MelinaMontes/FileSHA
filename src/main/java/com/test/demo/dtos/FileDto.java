package com.test.demo.dtos;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class FileDto {
 
    private String hashType;
    private List<MultipartFile> documents;

}
