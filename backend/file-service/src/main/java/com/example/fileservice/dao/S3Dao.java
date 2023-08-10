package com.example.fileservice.dao;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

@Repository
public class S3Dao {

    S3Client s3Client;
    String bucketName;

    public S3Dao() {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                "AKIASJR5IXJTXORQQOVT",
                "oDQ4znGrzbs1TwnLZqZuBmh9EPZzj1BrXQZ9lO9u");

        s3Client = S3Client.builder()
                .region(Region.of("us-east-2")) // Specify your region
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();

        bucketName = "s3-micsyours";

    }

    public boolean doesObjectExist(String objectKey) {
        try {
            s3Client.headObject(HeadObjectRequest.builder().bucket(bucketName).key(objectKey).build());
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        }
    }

    public String uploadFile(Path filePath, MultipartFile file, String objectKey) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        try (InputStream inputStream = file.getInputStream()) {
            PutObjectResponse response = s3Client.putObject(request, RequestBody.fromInputStream(inputStream, file.getSize()));
            String objectUrl = s3Client.utilities().getUrl(GetUrlRequest.builder().bucket(bucketName).key(objectKey).build()).toString();
            return objectUrl;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public InputStream downloadFile(String objectKey) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        ResponseInputStream<GetObjectResponse> responseInputStream = s3Client.getObject(getObjectRequest);
        return responseInputStream;
    }


    public void deleteFile(String objectKey) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }
}