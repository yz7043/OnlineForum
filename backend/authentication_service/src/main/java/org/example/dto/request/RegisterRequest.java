package org.example.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
public class RegisterRequest {
    @NotNull(message = "firstname is required")
    private String firstname;

    @NotNull(message = "lastname is required")
    private String lastname;

    @NotNull(message = "Email is required")
    private String email;

    @NotNull(message = "Password is required")
    private String password;

    private String image_url;



}
