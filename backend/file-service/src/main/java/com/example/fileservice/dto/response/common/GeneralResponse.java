package com.example.fileservice.dto.response.common;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class GeneralResponse<T> {
    private StatusResponse status;
    private T data;
}
