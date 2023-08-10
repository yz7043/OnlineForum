package org.forum.historyservice.dto.response;

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
