package org.example.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GeneralResponse<T> {
    private StatusResponse status;
    private T data;
}
