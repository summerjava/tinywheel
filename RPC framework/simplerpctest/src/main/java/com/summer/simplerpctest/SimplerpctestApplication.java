package com.summer.simplerpctest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class SimplerpctestApplication {
    public static void main(String[] args) {
        SpringApplication.run(SimplerpctestApplication.class, args);
    }
}
