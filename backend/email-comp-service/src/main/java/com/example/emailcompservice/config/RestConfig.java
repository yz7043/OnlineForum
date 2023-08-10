package com.example.emailcompservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestConfig {
//    private RestTemplateBuilder restTemplateBuilder;
//
//    @Autowired
//    public void setRestTemplateBuilder(RestTemplateBuilder restTemplateBuilder) {
//        this.restTemplateBuilder = restTemplateBuilder;
//    }
//
//    @LoadBalanced
//    @Bean
//    public RestTemplate restTemplate(){
//        return restTemplateBuilder
//                .build();
//    }

    @LoadBalanced
    @Bean
    RestTemplate restTemplate(){
        return new RestTemplate();
    }

}