package com.example.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SpringBootApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootApiApplication.class, args);
    }
}
