package com.test.demo.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "files")

public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "hash_sha_256")
    private String hashSha256;

    @Column(name = "hash_sha_512")
    private String hashSha512;

    @Column(name = "last_upload")
    private LocalDateTime lastUpload;

}
