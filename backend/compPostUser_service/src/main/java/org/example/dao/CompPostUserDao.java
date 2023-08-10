package org.example.dao;

import org.example.common.CommonResponse;
import org.example.security.AuthUserDetail;
import org.example.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

@Repository
public class CompPostUserDao {

    private JwtProvider jwtProvider;
    private RestTemplate restTemplate;

    @Autowired
    public void setJwtProvider(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public CommonResponse getAllPosts(AuthUserDetail authUserDetail) {
        HttpEntity<String> entity = tokenHeader(authUserDetail);
        ResponseEntity<CommonResponse> postResponse = restTemplate.exchange(
                "http://post-reply-service/post-reply-service/posts",
                HttpMethod.GET, entity, CommonResponse.class);

        return postResponse.getBody();
    }

    public CommonResponse getUserByUserID(AuthUserDetail authUserDetail, int userID) {
        HttpEntity<String> entity = tokenHeader(authUserDetail);
        ResponseEntity<CommonResponse> postResponse = restTemplate.exchange(
                "http://user-service/user-service/user/{userID}",
                HttpMethod.GET, entity, CommonResponse.class, userID);

        return postResponse.getBody();
    }

    public CommonResponse getPostByPostID(String postID, AuthUserDetail authUserDetail) {
        HttpEntity<String> entity = tokenHeader(authUserDetail);
        ResponseEntity<CommonResponse> postResponse = restTemplate.exchange(
                "http://post-reply-service/post-reply-service/posts/{postID}",
                HttpMethod.GET, entity, CommonResponse.class, postID);

        return postResponse.getBody();
    }

    public CommonResponse getTopReplyPost(AuthUserDetail authUserDetail) {
        HttpEntity<String> entity = tokenHeader(authUserDetail);
        ResponseEntity<CommonResponse> postResponse = restTemplate.exchange(
                "http://post-reply-service/post-reply-service/posts/replies/top",
                HttpMethod.GET, entity, CommonResponse.class);

        return postResponse.getBody();
    }

    public CommonResponse getPostByStatus(AuthUserDetail authUserDetail, String status) {

        HttpEntity<String> entity = tokenHeader(authUserDetail);
        ResponseEntity<CommonResponse> postResponse = restTemplate.exchange(
                "http://post-reply-service/post-reply-service/posts/admin/{status}",
                HttpMethod.GET, entity, CommonResponse.class, status);

        return postResponse.getBody();
    }

    //helper
    private HttpEntity<String> tokenHeader(AuthUserDetail authUserDetail) {
        String jwt_token = "Bearer " + jwtProvider.createToken(authUserDetail);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", jwt_token);
        return new HttpEntity<>(headers);
    }

    //example
    public CommonResponse getAUserByEmail(String emailaddr) {
        System.out.println("getUserByEmail: " + emailaddr);

        ResponseEntity<CommonResponse> userResponse = restTemplate.exchange(
                "http://user-service/user-service/user/email/{addr}",
                HttpMethod.GET, null, CommonResponse.class, emailaddr);

        return userResponse.getBody();
    }



}
