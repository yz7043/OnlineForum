package com.example.emailcompservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class EmailCompServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmailCompServiceApplication.class, args);
    }

}
