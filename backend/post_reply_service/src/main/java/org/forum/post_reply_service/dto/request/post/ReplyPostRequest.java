package org.forum.post_reply_service.dto.request.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReplyPostRequest {
    @NotBlank(message = "Comment cannot be empty")
    private String comment;
}
