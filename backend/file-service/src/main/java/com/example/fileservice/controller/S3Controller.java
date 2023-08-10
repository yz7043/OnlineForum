package com.example.fileservice.controller;

import com.example.fileservice.dto.response.UploadResponse;
import com.example.fileservice.dto.response.common.GeneralResponse;
import com.example.fileservice.dto.response.common.StatusResponse;
import com.example.fileservice.exception.S3KeyNotFoundException;
import com.example.fileservice.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

@RestController
public class S3Controller {
    private S3Service s3Service;

    @Autowired
    public void setPostService(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping(value = "/upload", produces = "application/json", consumes = "multipart/form-data")
    public GeneralResponse<UploadResponse> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        System.out.println("uploading file");
        if(file.getOriginalFilename().contains("/")){
            StatusResponse status = StatusResponse.builder()
                    .statusCode(409)
                    .success(true)
                    .message("Invalid file name. File name cannot contain '/'")
                    .build();
            return GeneralResponse.<UploadResponse>builder()
                    .status(status)
                    .build();
        }
        Path filePath = s3Service.saveFileLocally(file, "file-service/src/main/resources/files", file.getOriginalFilename());

        String objectUrl = s3Service.uploadFile(filePath, file);
        System.out.println(objectUrl);


        UploadResponse data = UploadResponse.builder()
                .objectUrl(objectUrl)
                .build();
        StatusResponse status = StatusResponse.builder()
                .statusCode(200)
                .success(true)
                .message("File uploaded successfully, object key: " + objectUrl)
                .build();
//        s3Service.deleteLocalFile(filePath);
//        System.out.println(data.getObjectUrl());


        return GeneralResponse.<UploadResponse>builder()
                .status(status)
                .data(data)
                .build();

    }

//    @PostMapping(value = "/delete", produces = "application/json")
//    public GeneralResponse deleteFile(@RequestParam("objectUrl") String objectUrl) throws IOException, S3KeyNotFoundException {
//        s3Service.deleteFile(objectUrl);
//        StatusResponse status = StatusResponse.builder()
//                .statusCode(200)
//                .success(true)
//                .message("File deleted successfully")
//                .build();
//        return GeneralResponse.builder()
//                .status(status)
//                .build();
//    }
//
//    @PostMapping(value = "/change/nodelete", produces = "application/json")
//    public GeneralResponse<UploadResponse> changeFileNoDelete(@RequestParam("file") MultipartFile file, @RequestParam("oldUrl") String oldUrl) throws IOException {
//        Path filePath = s3Service.saveFileLocally(file, "file-service/src/main/resources/files", file.getOriginalFilename());
//        String objectUrl = s3Service.uploadFile(filePath, file);
//        System.out.println(objectUrl);
//
//
//        UploadResponse data = UploadResponse.builder()
//                .objectUrl(objectUrl)
//                .build();
//        StatusResponse status = StatusResponse.builder()
//                .statusCode(200)
//                .success(true)
//                .message("File uploaded successfully, object key: " + objectUrl)
//                .build();
//        s3Service.deleteLocalFile(filePath);
//        System.out.println(data.getObjectUrl());
//
//
//        return GeneralResponse.<UploadResponse>builder()
//                .status(status)
//                .data(data)
//                .build();
//
//    }
//
//    @PostMapping(value = "/change", produces = "application/json")
//    public GeneralResponse<UploadResponse> changeFile(@RequestParam("file") MultipartFile file, @RequestParam("oldUrl") String oldUrl) throws IOException, S3KeyNotFoundException {
//        Path filePath = s3Service.saveFileLocally(file, "file-service/src/main/resources/files", file.getOriginalFilename());
//        String objectUrl = s3Service.uploadFile(filePath, file);
//        System.out.println(objectUrl);
//
//
//        UploadResponse data = UploadResponse.builder()
//                .objectUrl(objectUrl)
//                .build();
//        StatusResponse status = StatusResponse.builder()
//                .statusCode(200)
//                .success(true)
//                .message("File uploaded successfully, object key: " + objectUrl)
//                .build();
//        s3Service.deleteLocalFile(filePath);
//        s3Service.deleteFile(oldUrl);
//
//
//        return GeneralResponse.<UploadResponse>builder()
//                .status(status)
//                .data(data)
//                .build();
//
//    }













}