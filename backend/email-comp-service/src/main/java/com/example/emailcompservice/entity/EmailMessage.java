package com.example.emailcompservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Builder
public class EmailMessage implements Serializable {
    private String email;
    private String code;
    private String activeUrl;
}

