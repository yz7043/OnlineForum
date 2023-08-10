package org.example.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GeneralResponse<T> {
    private StatusResponse status;
    private T data;
}
