package com.example.fileservice.exception;

public class S3KeyNotFoundException extends Exception{
    public S3KeyNotFoundException(String message) {
        super(message);
    }

}
