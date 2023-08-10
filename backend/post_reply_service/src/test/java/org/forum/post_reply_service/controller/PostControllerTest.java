package org.forum.post_reply_service.controller;

import org.forum.post_reply_service.constant.AuthorityConstant;
import org.forum.post_reply_service.dto.request.post.SavePostRequest;
import org.forum.post_reply_service.dto.response.post.CreatePostResponseDTO;
import org.forum.post_reply_service.entity.Post;
import org.forum.post_reply_service.entity.PostStatus;
import org.forum.post_reply_service.security.AuthUserDetail;
import org.forum.post_reply_service.security.JwtProvider;
import org.forum.post_reply_service.service.PostAndReplyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

import static org.mockito.Mockito.doNothing;

@WebMvcTest(controllers = PostController.class)
@ActiveProfiles("test")
public class PostControllerTest {
    @MockBean
    private PostAndReplyService postAndReplyService;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtProvider jwtProvider;

    @BeforeEach
    public void setup() {
        AuthUserDetail userDetail = AuthUserDetail.builder()
                .username("test")
                .password("123321")
                .userID(1)
                .active(1)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(AuthorityConstant.AUTHORITY_USER)))
                .build();

        Authentication auth = new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @WithMockUser(username = "test", authorities = { "user" })
    void testCreateNewPost() throws Exception{
        AuthUserDetail userDetail =
                AuthUserDetail.builder()
                .username("test")
                .password("123321")
                .userID(1)
                .active(1)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(AuthorityConstant.AUTHORITY_USER)))
                .build();

        Post post = Post.builder()
                .id("testPostId")
                .userId(userDetail.getUserID())
                .isArchived(false)
                .status(PostStatus.UNPUBLISHED)
                .dateCreated(Date.from(Instant.now()))
                .dateModified(Date.from(Instant.now()))
                .images(new ArrayList<>())
                .attachments(new ArrayList<>())
                .postReplies(new ArrayList<>())
                .build();
        CreatePostResponseDTO createdPost = CreatePostResponseDTO
                .builder()
                .postId(post.getId())
                .userId(userDetail.getUserID())
                .build();
        Mockito.when(postAndReplyService.createNewPost(Mockito.any(AuthUserDetail.class))).thenReturn(createdPost);
        mockMvc.perform(MockMvcRequestBuilders.post("/posts"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.postId").value("testPostId"));

    }

    @Test
    @WithMockUser(username = "test", authorities =  {"user"})
    void testPublishOrSavePost() throws Exception {
        String postId = "testPostId";
        SavePostRequest postRequest = SavePostRequest
                .builder()
                .title("title")
                .content("content")
                .images(Arrays.stream(new String[]{"http://123.sdada"}).collect(Collectors.toCollection(ArrayList::new)))
                .attachments(null)
                .toPublish(false)
                .build();
        doNothing().when(postAndReplyService).saveOrPublishPost(Mockito.eq(postId), Mockito.eq(postRequest), Mockito.any(AuthUserDetail.class));
        // todo patch
        Mockito.verify(postAndReplyService, Mockito.times(1)).saveOrPublishPost(Mockito.eq(postId), Mockito.eq(postRequest), Mockito.any(AuthUserDetail.class));

    }

}
