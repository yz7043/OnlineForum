package org.forum.post_reply_service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubReply {
    private int userId;
    private String comment;
    private Boolean isActive;
    private Date dateCreated;
}
