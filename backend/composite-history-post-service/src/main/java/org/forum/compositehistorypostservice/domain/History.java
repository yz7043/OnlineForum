package org.forum.compositehistorypostservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class History {
    private Integer history_id;
    private Integer user_id;
    private String post_id;
    private Date viewdate;
}
