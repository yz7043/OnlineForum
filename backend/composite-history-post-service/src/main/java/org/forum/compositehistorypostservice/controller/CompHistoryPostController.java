package org.forum.compositehistorypostservice.controller;

import org.forum.compositehistorypostservice.dto.GeneralResponse;
import org.forum.compositehistorypostservice.service.CompHistoryPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@PreAuthorize("hasAnyAuthority('user', 'admin_user', 'super_admin_user')")
public class CompHistoryPostController {
    private CompHistoryPostService compHistoryPostService;

    @Autowired
    public CompHistoryPostController(CompHistoryPostService compHistoryPostService) {
        this.compHistoryPostService = compHistoryPostService;
    }

    @GetMapping("/history/post")
    public ResponseEntity<GeneralResponse> getHistoryPostView(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return ResponseEntity.ok(compHistoryPostService.getCurrentUserViewedPost(token));
    }
}
