package com.test.demo.services;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface FileService {
   public Map<String, String> calculateHashes(MultipartFile file) throws Exception;
}

