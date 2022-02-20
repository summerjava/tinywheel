package com.summer.simplewebsocketserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SimplewebsocketserverApplication {

    public static void main(String[] args) {
        SimpleWebsocketServer simpleWebsocketServer = new SimpleWebsocketServer();
        simpleWebsocketServer.start();

        SpringApplication.run(SimplewebsocketserverApplication.class, args);
    }
}
