package org.example.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
public class EditProfileRequest {

    private String firstname;

    private String lastname;

    private String password;

    private String email;

    private String avatar;
}
