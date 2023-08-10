package org.example.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
public class LoginRequest {
    @NotNull(message = "Email is required")
    private String email;

    @NotNull(message = "Password is required")
    private String password;
}
