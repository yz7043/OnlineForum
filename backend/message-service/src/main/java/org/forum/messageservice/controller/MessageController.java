package org.forum.messageservice.controller;

import org.forum.messageservice.dto.request.ContactUsRequest;
import org.forum.messageservice.dto.response.GeneralResponse;
import org.forum.messageservice.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class MessageController {
    private MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PreAuthorize("hasAnyAuthority('user', 'admin_user', 'super_admin_user')")
    @PostMapping("/contact")
    public ResponseEntity<GeneralResponse> contactUsView(
            @RequestBody ContactUsRequest body,
            HttpServletRequest request
            ) {
        String token = request.getHeader("Authorization").substring(7);
        return ResponseEntity.ok(messageService.contactUs(body, token));
    }

    @PreAuthorize("hasAnyAuthority('admin_user', 'super_admin_user')")
    @GetMapping("/messages")
    public ResponseEntity<GeneralResponse> allMessagesView() {
        return ResponseEntity.ok(messageService.getAllMessages());
    }

    @PreAuthorize("hasAnyAuthority('admin_user', 'super_admin_user')")
    @PatchMapping("/message/status/{message_id}")
    public ResponseEntity<GeneralResponse> changeMessageStatusView(
            @PathVariable("message_id") int message_id
    ) {
        return ResponseEntity.ok(messageService.changeStatus(message_id));
    }
}
