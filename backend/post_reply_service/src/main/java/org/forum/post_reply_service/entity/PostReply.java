package org.forum.post_reply_service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostReply {
    private int userId;
    private String comment;
    private Boolean isActive;
    private Date dateCreated;
    private List<SubReply> subReplies;
}
