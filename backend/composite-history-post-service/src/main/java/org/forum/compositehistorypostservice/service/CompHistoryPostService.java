package org.forum.compositehistorypostservice.service;

import org.forum.compositehistorypostservice.domain.History;
import org.forum.compositehistorypostservice.domain.Post;
import org.forum.compositehistorypostservice.dto.GeneralResponse;
import org.forum.compositehistorypostservice.dto.StatusResponse;
import org.forum.compositehistorypostservice.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;


@Service
public class CompHistoryPostService {
    private JwtProvider jwtProvider;
    private RestTemplate restTemplate;

    @Autowired
    public CompHistoryPostService(JwtProvider jwtProvider, RestTemplate restTemplate) {
        this.jwtProvider = jwtProvider;
        this.restTemplate = restTemplate;
    }

    public GeneralResponse getCurrentUserViewedPost(String token) {
        int user_id = jwtProvider.extractUserID(token);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer "+token);
        HttpEntity<Object> requestEntity = new HttpEntity<>(httpHeaders);
        ResponseEntity<List<History>> historyResponse = restTemplate.exchange(
                "http://history-service/history-service/history",
                HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<History>>() {}, user_id
        );
        List<History> list = historyResponse.getBody();

        List posts = new ArrayList<>();
        for(History history: list) {
            ResponseEntity<GeneralResponse> postResponse = restTemplate.exchange(
                    "http://post-reply-service/post-reply-service/posts/{post_id}",
                    HttpMethod.GET, requestEntity, new ParameterizedTypeReference<GeneralResponse>() {}, history.getPost_id()
            );
            System.out.println(postResponse.getBody().getData());
            posts.add(postResponse.getBody().getData());
        }

        return GeneralResponse.builder()
                .status(StatusResponse.builder()
                        .message("all history")
                        .statusCode(200)
                        .success(true)
                        .build())
                .data(posts)
                .build();
    }
}
