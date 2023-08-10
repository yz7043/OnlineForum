package org.forum.historyservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.yml")
@Getter
@Setter
public class HibernateProperty {
    @Value("${database.hibernate.driver}")
    private String driver;

    @Value("${database.hibernate.url}")
    private String url;

    @Value("${database.hibernate.username}")
    private String username;

    @Value("${database.hibernate.password}")
    private String password;

    @Value("${database.hibernate.dialect}")
    private String dialect;

    @Value("${database.hibernate.auto}")
    private String auto;
}
