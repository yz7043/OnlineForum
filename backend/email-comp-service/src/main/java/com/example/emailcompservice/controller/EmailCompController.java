package com.example.emailcompservice.controller;

import com.example.emailcompservice.service.EmailCompService;
import org.example.dto.GeneralResponse;
import org.example.dto.StatusResponse;
import org.example.dto.UuidResponse;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@PreAuthorize("hasAnyAuthority('user', 'admin_user', 'super_admin_user')")
public class EmailCompController {
    private final RabbitTemplate rabbitTemplate;
    private EmailCompService emailCompService;

    public EmailCompController(RabbitTemplate rabbitTemplate, EmailCompService emailCompService) {
        this.emailCompService = emailCompService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping("/send")
//    @PreAuthorize("hasAuthority(T(org.forum.post_reply_service.constant.AuthorityConstant).AUTHORITY_USER)")
    public GeneralResponse<UuidResponse> addToSendingList(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String uuid = emailCompService.addToSendingList(token);
        GeneralResponse<UuidResponse> response = GeneralResponse.<UuidResponse>builder()
                .status(StatusResponse.builder().success(true).message("successfully added to sending list")
                        .statusCode(HttpStatus.OK.value()).build())
                .data(UuidResponse.builder().uuid(uuid).build())
                .build();

        //return
        return response;
    }

    @PostMapping("/verify")
    //"http://localhost:9000/email-service/verify?code=%s"
    public GeneralResponse verify(@RequestParam String code) {
        boolean success = emailCompService.verify(code);
        return GeneralResponse.builder().status(StatusResponse.builder().success(success).message(
                success? "successfully verified the code":"failed to verify the code, either code expired or code is invalid, please request email verification again")
                .statusCode(HttpStatus.CONFLICT.value()).build()).build();

    }





}
