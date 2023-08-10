package com.example.fileservice.service;

import com.example.fileservice.dao.S3Dao;
import com.example.fileservice.exception.S3KeyNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class S3Service {

    private S3Dao s3Dao;

    @Autowired
    public void setS3Dao(S3Dao s3Dao) {
        this.s3Dao = s3Dao;
    }




    public Path saveFileLocally(MultipartFile file, String basePath, String originalFileName) throws IOException {
        // Generate a new filename with timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String newFileName = timestamp + "_" + originalFileName;

        // Resolve the file path
        Path destinationPath = Paths.get(basePath, newFileName);

        // Save the file to the local directory
        Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

        return destinationPath;
    }


    public String uploadFile(Path filePath, MultipartFile file) {
        String objectKey = genUniqueObjectKey(file.getOriginalFilename());
        return s3Dao.uploadFile(filePath, file, objectKey);
    }

    public void deleteLocalFile(Path filePath) throws IOException {
        Files.deleteIfExists(filePath);
    }

    public String genUniqueObjectKey(String originalFileName) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uniqueId = UUID.randomUUID().toString();
        String objectKey = timestamp + "_" + uniqueId + "_" + originalFileName;
        return objectKey;
    }

    public void deleteFile(String objectUrl) throws S3KeyNotFoundException {
        String objectKey = getObjectKeyByUrl(objectUrl);
        if(!s3Dao.doesObjectExist(objectKey)) {
            throw new S3KeyNotFoundException("Object key not found: " + objectKey);
        }

        s3Dao.deleteFile(objectKey);
    }

    public String getObjectKeyByUrl(String objectUrl) {
        // 解析对象URL，提取对象键
        // 假设对象URL的格式为：https://bucket-name.s3.amazonaws.com/object-key
        String[] parts = objectUrl.split("/");
        System.out.println(parts[parts.length - 1]);
        return parts[parts.length - 1];
        //objectUrl
        //:
        //"https://s3-micsyours.s3.us-east-2.amazonaws.com/20230717133419_f7abfd95-569c-4ace-abbe-cd1b7cd842b6_WeChatffef6b8060b42fdc70422ebd1728ba4f.jpg"
    }
}


