package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CompPostUserApplication {
    public static void main(String[] args) {
        System.out.println("comp:user_post is running");
        SpringApplication.run(CompPostUserApplication.class,args);
    }
}