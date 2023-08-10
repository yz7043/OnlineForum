package org.forum.post_reply_service.common;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse<T> {
    private StatusResponse status;
    private T data;
}
