package com.example.coremakertest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
public class CoremakerTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoremakerTestApplication.class, args);
    }

}
