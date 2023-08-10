package com.example.emailcompservice.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneralResponse<T> {
    private StatusResponse status;
    private T data;
}
