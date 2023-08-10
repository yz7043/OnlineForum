package org.forum.post_reply_service.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "PostAndReply")
public class Post {
    @Id
    private String id;
    private Integer userId;
    private String title;
    private String content;
    private Boolean isArchived; // archived post cannot be replied
    private PostStatus status;
    private Date dateCreated;
    private Date dateModified;
    private List<String> images;
    private List<String> attachments;
    private List<PostReply> postReplies;
}
