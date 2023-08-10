package org.example.controller;

import org.example.common.CommonResponse;
import org.example.common.StatusResponse;
import org.example.constant.PostStatus;
import org.example.exception.NoStatusMatchException;
import org.example.security.AuthUserDetail;
import org.example.service.CompPostUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.LinkedHashMap;
import java.util.List;

@RestController
@RequestMapping("posts")
public class CompPostUserController {

    private CompPostUserService compPostUserService;

    @Autowired
    private void setCompPostUserService(CompPostUserService compPostUserService) {
        this.compPostUserService = compPostUserService;
    }

    @GetMapping()
    @PreAuthorize("hasAnyAuthority('user', 'admin_user', 'super_admin_user')")
    public ResponseEntity<CommonResponse> getAllPosts(@AuthenticationPrincipal AuthUserDetail authUserDetail) {
        //Get all post first
        List<LinkedHashMap<String, Object>> posts_arr = compPostUserService.getAllPosts(authUserDetail);

        return ResponseEntity.ok(CommonResponse.builder().
                status(StatusResponse.builder()
                        .success(true).message("success")
                        .build())
                .data(posts_arr)
                .build());
    }

    @GetMapping("/{postID}")
    @PreAuthorize("hasAnyAuthority('user', 'admin_user', 'super_admin_user')")
    public ResponseEntity<CommonResponse> getAllPosts(@PathVariable("postID") String postID,
                                                      @AuthenticationPrincipal AuthUserDetail authUserDetail) {
        //Get all post first
        LinkedHashMap<String, Object> posts_arr = compPostUserService.getPostByPostID(postID, authUserDetail);

        return ResponseEntity.ok(CommonResponse.builder().
                status(StatusResponse.builder()
                        .success(true).message("success")
                        .build())
                .data(posts_arr)
                .build());
    }

    @GetMapping("/replies/top")
    @PreAuthorize("hasAnyAuthority('user', 'admin_user', 'super_admin_user')")
    public ResponseEntity<CommonResponse> getTopReplyPosts(@AuthenticationPrincipal AuthUserDetail authUserDetail) {
        //Get all post first
        List<LinkedHashMap<String, Object>> posts_arr = compPostUserService.getTopReplyPost(authUserDetail);

        return ResponseEntity.ok(CommonResponse.builder().
                status(StatusResponse.builder()
                        .success(true).message("success")
                        .build())
                .data(posts_arr)
                .build());
    }

    @GetMapping("/admin/{status}")
    @PreAuthorize("hasAnyAuthority('admin_user', 'super_admin_user')")
    public ResponseEntity<CommonResponse> getPostByStatus(@PathVariable("status") String status,
                                                          @AuthenticationPrincipal AuthUserDetail authUserDetail)
            throws NoStatusMatchException {

        if(!status.equals(PostStatus.PUBLISHED.toString())
                && !status.equals(PostStatus.UNPUBLISHED.toString())
                && !status.equals(PostStatus.HIDDEN.toString())
                && !status.equals(PostStatus.BANNED.toString())
                && !status.equals(PostStatus.DELETED.toString())) {
            throw new NoStatusMatchException("There is no such status.");
        }

        //Get all post first
        List<LinkedHashMap<String, Object>> posts_arr = compPostUserService.getPostByStatus(authUserDetail, status);

        return ResponseEntity.ok(CommonResponse.builder().
                status(StatusResponse.builder()
                        .success(true).message("success")
                        .build())
                .data(posts_arr)
                .build());
    }

}
