package com.example.fileservice.dto.response;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UploadResponse {
    private String objectUrl;
}
