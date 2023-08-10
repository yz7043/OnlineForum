package org.forum.messageservice.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GeneralResponse <T> {
    private String message;
    private T data;
}
