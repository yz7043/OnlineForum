package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)

public class EmailApplication {
    public static void main(String[] args) {
        System.out.println("Email service is running.");
        SpringApplication.run(EmailApplication.class, args);
    }
}