package org.forum.post_reply_service.controller;


//import org.forum.security.JwtFilter;
import org.forum.post_reply_service.security.AuthUserDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.security.authorization.AuthorityReactiveAuthorizationManager.hasAuthority;

@RestController
@RequestMapping("test")
public class TestController {

    @GetMapping
    @PreAuthorize("hasAuthority(T(org.forum.post_reply_service.constant.AuthorityConstant).AUTHORITY_USER) " +
            "|| hasAuthority(T(org.forum.post_reply_service.constant.AuthorityConstant).AUTHORITY_ADMIN) " +
            "|| hasAuthority(T(org.forum.post_reply_service.constant.AuthorityConstant).AUTHORITY_SUPER_ADMIN)")
    public ResponseEntity<String> test(@AuthenticationPrincipal AuthUserDetail userDetail){
        System.out.println(userDetail);
        return ResponseEntity.ok("Ok");
    }
    @GetMapping("/1")
    @PreAuthorize("hasAuthority(T(org.forum.post_reply_service.constant.AuthorityConstant).AUTHORITY_ADMIN) " +
            "|| hasAuthority(T(org.forum.post_reply_service.constant.AuthorityConstant).AUTHORITY_SUPER_ADMIN)")
    public ResponseEntity<String> test1(){
        return ResponseEntity.ok("sdasdadcdxa");
    }
}
