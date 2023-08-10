package com.example.fileservice.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
public class DownloadResponse {
    private MultipartFile file;
}
