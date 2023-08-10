package org.forum.messageservice.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactUsRequest {
    private String email;
    private String message;
    private String subject;
}
