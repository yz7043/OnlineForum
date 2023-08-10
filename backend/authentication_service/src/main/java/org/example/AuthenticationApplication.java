package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class AuthenticationApplication {
    public static void main(String[] args) {
        System.out.println("Authentication service is running.");
        SpringApplication.run(AuthenticationApplication.class, args);
    }
}