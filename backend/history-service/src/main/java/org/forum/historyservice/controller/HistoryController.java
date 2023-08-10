package org.forum.historyservice.controller;

import org.forum.historyservice.dto.request.ViewRequest;
import org.forum.historyservice.dto.response.GeneralResponse;
import org.forum.historyservice.entity.History;
import org.forum.historyservice.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@PreAuthorize("hasAnyAuthority('user', 'admin_user', 'super_admin_user')")
public class HistoryController {
    private HistoryService historyService;

    @Autowired
    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @PostMapping("/view")
    public ResponseEntity<GeneralResponse> viewPostView(
            @RequestBody ViewRequest body,
            HttpServletRequest request
            ) {
        String token = request.getHeader("Authorization").substring(7);
        return ResponseEntity.ok(historyService.viewPost(body, token));
    }

    @GetMapping("/history")
    public ResponseEntity<List<History>> getHistoryView(
            HttpServletRequest request
    ) {
        String token = request.getHeader("Authorization").substring(7);
        return ResponseEntity.ok(historyService.getHistoryByUserID(token));
    }
}
