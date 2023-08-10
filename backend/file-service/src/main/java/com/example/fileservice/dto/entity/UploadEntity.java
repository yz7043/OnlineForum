package com.example.fileservice.dto.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
@AllArgsConstructor
public class UploadEntity {
    private String url;
    private String key;

}
