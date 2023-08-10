package org.forum.post_reply_service.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatusResponse {
    private int statusCode;
    private boolean success;
    private String message;
}

