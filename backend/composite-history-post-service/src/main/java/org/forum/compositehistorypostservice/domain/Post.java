package org.forum.compositehistorypostservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Post {
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
