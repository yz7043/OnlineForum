package org.example.Controller;

import org.example.dto.GeneralResponse;
import org.example.dto.StatusResponse;
import org.example.dto.UuidResponse;
import org.example.security.AuthUserDetail;
import org.example.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
//prerequisite: email setup

@RestController
public class EmailController {

    private EmailService emailService;

    @Autowired
    public void setLinkDao(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("save")
    @PreAuthorize("hasAnyAuthority('user', 'admin_user', 'super_admin_user')")
    public GeneralResponse<UuidResponse> save(@AuthenticationPrincipal AuthUserDetail userDetail) {
        String email = userDetail.getUsername();
        String uuid = emailService.save(email);
        return GeneralResponse.<UuidResponse>builder()
                .status(StatusResponse.builder().success(true).message("successfully recorded this email")
                        .statusCode(HttpStatus.OK.value()).build())
                .data(UuidResponse.builder().uuid(uuid).build())
                .build();
    }

    @GetMapping("verify")
    public GeneralResponse verify(@RequestParam String uuid){
        String email = emailService.verify(uuid);
        if(email != null) {
            return GeneralResponse.builder()
                    .status(StatusResponse.builder().success(true).message("successfully verified this email")
                            .statusCode(HttpStatus.OK.value()).build())
                    .data(email)
                    .build();
        }

        return GeneralResponse.builder()
                .status(StatusResponse.builder().success(false).message("failed to verify this email")
                        .statusCode(HttpStatus.FORBIDDEN.value()).build())
                .build();

    }
}
