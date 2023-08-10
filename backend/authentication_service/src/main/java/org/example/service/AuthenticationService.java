package org.example.service;

import org.example.dao.UserDao;
import org.example.dto.response.GeneralResponse;
import org.example.domain.User;
import org.example.security.AuthUserDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthenticationService implements UserDetailsService {

    private RestTemplate restTemplate;
    private UserDao userDao;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Autowired
    public void setUserDao(UserDao userDao) {this.userDao = userDao;}

    @Override
    public UserDetails loadUserByUsername(String username){
        User user = userDao.getUserUsingEmail(username); //username == email

        if (user == null){
            System.out.println("No data found");
            throw new UsernameNotFoundException("user does not exist");
        }

        return AuthUserDetail.builder() // spring security's userDetail
                .username(user.getEmail())
                .userID(user.getUser_id())
                .active(user.getActive())
                .password(new BCryptPasswordEncoder().encode(user.getPassword()))
                .authorities(getAuthoritiesFromUser(user))
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();
    }

    private List<GrantedAuthority> getAuthoritiesFromUser(User user){
        List<GrantedAuthority> userAuthorities = new ArrayList<>();
        String permission = user.getType();

        userAuthorities.add(new SimpleGrantedAuthority(permission));

        return userAuthorities;
    }

    public GeneralResponse getAUserByEmail(String emailaddr) {
        System.out.println("getUserByEmail: " + emailaddr);

        ResponseEntity<GeneralResponse> userResponse = restTemplate.exchange(
                "http://user-service/user-service/user/email/{addr}",
                HttpMethod.GET, null, GeneralResponse.class, emailaddr);

        return userResponse.getBody();
    }

    public boolean registerAuser(User user) {
        return userDao.registerAuser(user);
    }
}
