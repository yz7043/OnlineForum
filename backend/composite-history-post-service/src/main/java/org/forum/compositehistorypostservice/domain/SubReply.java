package org.forum.compositehistorypostservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

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
