package org.forum.post_reply_service.dto.request.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.forum.post_reply_service.entity.PostReply;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
public class SavePostRequest {
    @NotBlank(message = "Title cannot be empty")
    @Size(max = 255, message = "Title length can't exceeds {max}")
    private String title;
    @NotBlank(message = "Content cannot be empty")
    private String content;
    private List<String> images;
    private List<String> attachments;
    private boolean toPublish;
}
