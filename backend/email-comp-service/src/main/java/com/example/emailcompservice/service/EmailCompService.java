package com.example.emailcompservice.service;

import com.example.emailcompservice.entity.EmailMessage;
import com.example.emailcompservice.security.JwtProvider;
import org.example.dto.GeneralResponse;
import org.example.dto.UuidResponse;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class EmailCompService {
    @Autowired
    private final JavaMailSender mailSender;

    private final RabbitTemplate rabbitTemplate;
    private JwtProvider jwtProvider;
    private RestTemplate restTemplate;

    public EmailCompService(JavaMailSender mailSender, RabbitTemplate rabbitTemplate, JwtProvider jwtProvider, RestTemplate restTemplate) {
        this.mailSender = mailSender;
        this.rabbitTemplate = rabbitTemplate;
        this.jwtProvider = jwtProvider;
        this.restTemplate = restTemplate;
    }

    public void sendMail(EmailMessage emailMessage) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailMessage.getEmail());
        message.setSubject("mic's yours 激活邮件");
        message.setText("\npost方法，访问以下endpoint以激活：\n" + emailMessage.getActiveUrl());

        mailSender.send(message);
    }

    public String addToSendingList(String token) {
        int user_id = jwtProvider.extractUserID(token);
        String email = jwtProvider.extractUserEmail(token);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer "+token);
        HttpEntity<Object> requestEntity = new HttpEntity<>(httpHeaders);
        GeneralResponse<UuidResponse> emailResponse= restTemplate.exchange("http://email-service/email-service/save",
                HttpMethod.POST, requestEntity,
                new ParameterizedTypeReference<GeneralResponse<UuidResponse>>() {}, user_id).getBody();
        String uuid = emailResponse.getData().getUuid();
        String activeUrl = String.format("http://localhost:9000/email-service/verify?code=%s", uuid);
        EmailMessage emailMessage = EmailMessage.builder()
                .email(email)
                .activeUrl(activeUrl)
                .build();
        rabbitTemplate.convertAndSend("emailQueue", emailMessage);
        return uuid;
    }

    public boolean verify(String code) {
        String email = getEmailFromEmailService(code);
        if (email == null) {
            return false;
        }
        System.out.println(email+"ovneornfwoivnwpeo[a");
        String superAdminToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJxaWFubWlhbi5nYWlAZ21haWwuY29tIiwicGVybWlzc2lvbnMiOlt7ImF1dGhvcml0eSI6InN1cGVyX2FkbWluX3VzZXIifV0sInVzZXJJRCI6MTMsImFjdGl2ZSI6MH0.JmcJH5-IZJW0idkusbEqTUBWe-zFmkshc5a_lkEOSxY";
        boolean isUserStatusChanged = changeUserStatusFromUserService(superAdminToken, email);
        if(!isUserStatusChanged){
            return false;
        }
        return true;
    }

    public boolean changeUserStatusFromUserService(String token, String email){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer "+token);
        HttpEntity<Object> requestEntity = new HttpEntity<>(httpHeaders);
        GeneralResponse<Integer> emailResponse= restTemplate.exchange("http://user-service/user-service/user/email?email="+email,
                HttpMethod.POST, requestEntity,
                new ParameterizedTypeReference<GeneralResponse>() {}).getBody();
        return emailResponse.getStatus().isSuccess();
    }

    public String getEmailFromEmailService(String code){
        String url = "http://email-service/email-service/verify";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("uuid", code);
        // No headers or parameters are needed, so just pass an empty HttpEntity
        HttpEntity<?> entity = new HttpEntity<>(new HttpHeaders());
        // Send GET request and receive response
        ResponseEntity<GeneralResponse> responseEntity = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, GeneralResponse.class);
        String email = "";
        try{email = (String) responseEntity.getBody().getData();}
        catch (Exception e){return null;
        }
        System.out.println(email);
        return email;
    }




}
