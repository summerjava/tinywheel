package com.summer.simplehttpserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SimplehttpserverApplication {

    public static void main(String[] args) {
        SimpleHttpServer simpleHttpServer = new SimpleHttpServer();
        simpleHttpServer.start();

        SpringApplication.run(SimplehttpserverApplication.class, args);
    }
}
