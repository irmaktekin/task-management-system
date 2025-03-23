package com.irmaktekin.task.management.system.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value(("${file.upload-dir}"))
    private String uploadDir;

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        checkDirectoryExist();

        String fileName = UUID.randomUUID()+"_" + file.getOriginalFilename();
        Path path = Paths.get(uploadDir+"/"+fileName);
        Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

    private void checkDirectoryExist(){
        if(uploadDir==null){
            throw new IllegalArgumentException("Upload directory path is null");
        }
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }
}
