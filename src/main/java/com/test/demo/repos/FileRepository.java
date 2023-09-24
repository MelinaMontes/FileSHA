package com.test.demo.repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.test.demo.entities.File;

public interface FileRepository extends JpaRepository<File, Long>{

  Optional<File> findByHashSha256(String hashSha256);
  Optional<File> findByHashSha512(String hashSha512);
  public File findByHashSha256OrHashSha512(String sha256Hash,String sha512Hash);

}
