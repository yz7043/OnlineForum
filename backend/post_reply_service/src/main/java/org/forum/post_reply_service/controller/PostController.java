package org.forum.post_reply_service.controller;

import org.forum.post_reply_service.common.CommonResponse;
import org.forum.post_reply_service.common.StatusResponse;
import org.forum.post_reply_service.dto.request.post.ReplyPostRequest;
import org.forum.post_reply_service.dto.request.post.SavePostRequest;
import org.forum.post_reply_service.dto.response.post.CreatePostResponseDTO;
import org.forum.post_reply_service.entity.Post;
import org.forum.post_reply_service.entity.PostStatus;
import org.forum.post_reply_service.security.AuthUserDetail;
import org.forum.post_reply_service.service.PostAndReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("posts")
public class PostController {
    private PostAndReplyService postAndReplyService;

    @Autowired
    public void setPostAndReplyService(PostAndReplyService postAndReplyService){
        this.postAndReplyService = postAndReplyService;
    }

    //create an unpublished post first
    @PostMapping
    @PreAuthorize("hasAuthority(T(org.forum.post_reply_service.constant.AuthorityConstant).AUTHORITY_USER)")
    public ResponseEntity<CommonResponse> createNewPost(@AuthenticationPrincipal AuthUserDetail userDetail){
        CreatePostResponseDTO data = postAndReplyService.createNewPost(userDetail);
        return ResponseEntity.ok(CommonResponse.builder().status(StatusResponse.builder().success(true).message("success").build())
                        .data(data)
                        .build());
    }

    @PatchMapping("/{postId}")
    @PreAuthorize("hasAuthority(T(org.forum.post_reply_service.constant.AuthorityConstant).AUTHORITY_USER)")
    public ResponseEntity<CommonResponse> publishOrSavePost(
            @PathVariable("postId") String postId,
            @Valid @RequestBody SavePostRequest request,
            @AuthenticationPrincipal AuthUserDetail userDetail){
        postAndReplyService.saveOrPublishPost(postId, request, userDetail);
        return ResponseEntity.ok(CommonResponse.builder().status(StatusResponse.builder().success(true).message("success").build())
                .data("success")
                .build());
    }

    @PatchMapping("/status/user/{postId}/{status}")
    @PreAuthorize("hasAuthority(T(org.forum.post_reply_service.constant.AuthorityConstant).AUTHORITY_USER)")
    public ResponseEntity<CommonResponse> deletePost(
            @PathVariable("postId") String postId,
            @PathVariable("status") PostStatus status,
            @AuthenticationPrincipal AuthUserDetail userDetail
    ){
        postAndReplyService.changePostStatus(postId, status, userDetail);
        return ResponseEntity.ok(CommonResponse.builder().status(StatusResponse.builder().success(true).message("success").build())
                .data("success")
                .build());
    }

    @PatchMapping("/status/admin/{postId}/{status}")
    @PreAuthorize("hasAuthority(T(org.forum.post_reply_service.constant.AuthorityConstant).AUTHORITY_ADMIN)" +
            "|| hasAuthority(T(org.forum.post_reply_service.constant.AuthorityConstant).AUTHORITY_SUPER_ADMIN)")
    public ResponseEntity<CommonResponse> recoverFromDeletePost(
            @PathVariable("postId") String postId,
            @PathVariable("status") PostStatus status,
            @AuthenticationPrincipal AuthUserDetail userDetail
    ){
        postAndReplyService.changePostStatus(postId, status, userDetail);
        return ResponseEntity.ok(CommonResponse.builder().status(StatusResponse.builder().success(true).message("success").build())
                .data("success")
                .build());
    }

    @PatchMapping("/archive/{postId}")
    @PreAuthorize("hasAuthority(T(org.forum.post_reply_service.constant.AuthorityConstant).AUTHORITY_USER)")
    public ResponseEntity<CommonResponse> toggleArchive(
            @PathVariable("postId") String postId,
            @AuthenticationPrincipal AuthUserDetail userDetail
    ){
        postAndReplyService.toggleArchive(postId, userDetail);
        return ResponseEntity.ok(CommonResponse.builder().status(StatusResponse.builder().success(true).message("success").build())
                .data("success")
                .build());
    }

    @GetMapping
    @PreAuthorize("hasAuthority(T(org.forum.post_reply_service.constant.AuthorityConstant).AUTHORITY_USER) " +
            "|| hasAuthority(T(org.forum.post_reply_service.constant.AuthorityConstant).AUTHORITY_ADMIN) " +
            "|| hasAuthority(T(org.forum.post_reply_service.constant.AuthorityConstant).AUTHORITY_SUPER_ADMIN)")
    public ResponseEntity<CommonResponse> getAllPost(
            @AuthenticationPrincipal AuthUserDetail userDetail
    ){
        List<Post> allPost = postAndReplyService.getAllPost(userDetail);
        return ResponseEntity.ok(CommonResponse.builder().status(StatusResponse.builder().success(true).message("success").build())
                .data(allPost)
                .build());
    }

    @GetMapping("/admin/{status}")
    @PreAuthorize("hasAuthority(T(org.forum.post_reply_service.constant.AuthorityConstant).AUTHORITY_ADMIN) " +
            "|| hasAuthority(T(org.forum.post_reply_service.constant.AuthorityConstant).AUTHORITY_SUPER_ADMIN)")
    public ResponseEntity<CommonResponse> getPostByStatus(@PathVariable("status") PostStatus status){
        List<Post> posts = postAndReplyService.getPostByStatus(status);
        return ResponseEntity.ok(CommonResponse.builder().status(StatusResponse.builder().success(true).message("success").build())
                .data(posts)
                .build());
    }

    @GetMapping("/user/{status}")
    @PreAuthorize("hasAuthority(T(org.forum.post_reply_service.constant.AuthorityConstant).AUTHORITY_USER) " +
            "|| hasAuthority(T(org.forum.post_reply_service.constant.AuthorityConstant).AUTHORITY_ADMIN) " +
            "|| hasAuthority(T(org.forum.post_reply_service.constant.AuthorityConstant).AUTHORITY_SUPER_ADMIN)")
    public ResponseEntity<CommonResponse> getPostByStatusUserID(@PathVariable("status") PostStatus status,
        @AuthenticationPrincipal AuthUserDetail userDetail
    ){
        List<Post> posts = postAndReplyService.getPostByStatusUserID(userDetail, status);
        return ResponseEntity.ok(CommonResponse.builder().status(StatusResponse.builder().success(true).message("success").build())
                .data(posts)
                .build());
    }

    @GetMapping("drafts")
    @PreAuthorize("hasAuthority(T(org.forum.post_reply_service.constant.AuthorityConstant).AUTHORITY_USER)")
    public ResponseEntity<CommonResponse> getUserAllDrafts(
            @AuthenticationPrincipal AuthUserDetail userDetail
    ){
        List<Post> userAllDrafts = postAndReplyService.getUserAllDrafts(userDetail);
        return ResponseEntity.ok(CommonResponse.builder().status(StatusResponse.builder().success(true).message("success").build())
                .data(userAllDrafts)
                .build());
    }

    @GetMapping("{postId}")
    @PreAuthorize("hasAuthority(T(org.forum.post_reply_service.constant.AuthorityConstant).AUTHORITY_USER) " +
            "|| hasAuthority(T(org.forum.post_reply_service.constant.AuthorityConstant).AUTHORITY_ADMIN) " +
            "|| hasAuthority(T(org.forum.post_reply_service.constant.AuthorityConstant).AUTHORITY_SUPER_ADMIN)")
    public ResponseEntity<CommonResponse> getPublishedPostById(@PathVariable("postId") String postId){
        Post post = postAndReplyService.getPublishedPostByID(postId);
        return ResponseEntity.ok(CommonResponse.builder().status(StatusResponse.builder().success(true).message("success").build())
                .data(post)
                .build());
    }
    @PatchMapping("{postId}/replies/{idx}")
    @PreAuthorize("hasAuthority(T(org.forum.post_reply_service.constant.AuthorityConstant).AUTHORITY_USER) " +
            "|| hasAuthority(T(org.forum.post_reply_service.constant.AuthorityConstant).AUTHORITY_ADMIN) " +
            "|| hasAuthority(T(org.forum.post_reply_service.constant.AuthorityConstant).AUTHORITY_SUPER_ADMIN)")
    public ResponseEntity<CommonResponse> replyAReply(
            @Valid @RequestBody ReplyPostRequest request,
            @PathVariable("postId") String postId,
            @PathVariable("idx") Integer replayIdx,
            @AuthenticationPrincipal AuthUserDetail userDetail){
        // TODO: check the user stats (is banned? is activated?)
        postAndReplyService.replayReplay(request, replayIdx, userDetail, postId);
        return ResponseEntity.ok(CommonResponse
                .builder()
                .status(StatusResponse.builder().success(true).build())
                .build());
    }

    @PatchMapping("replies/{postId}")
    @PreAuthorize("hasAuthority(T(org.forum.post_reply_service.constant.AuthorityConstant).AUTHORITY_USER) " +
            "|| hasAuthority(T(org.forum.post_reply_service.constant.AuthorityConstant).AUTHORITY_ADMIN) " +
            "|| hasAuthority(T(org.forum.post_reply_service.constant.AuthorityConstant).AUTHORITY_SUPER_ADMIN)")
    public ResponseEntity<CommonResponse> replyAPost(
            @PathVariable("postId") String postId,
            @Valid @RequestBody ReplyPostRequest request,
            @AuthenticationPrincipal AuthUserDetail userDetail){
        postAndReplyService.replayPost(request, userDetail, postId);
        return ResponseEntity.ok(CommonResponse
                .builder()
                .status(StatusResponse.builder().success(true).build())
                .build());
    }
    @GetMapping("replies/top")
    public ResponseEntity<CommonResponse> top3RepliedPosts(){
        List<Post> posts = postAndReplyService.getTop3RepliedPosts();
        return ResponseEntity.ok(CommonResponse
                .builder()
                .status(StatusResponse.builder().success(true).build())
                .data(posts)
                .build());
    }
}
