package org.forum.historyservice.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViewRequest {
    private String post_id;
}
