package com.summer.nettylearn;

import com.summer.nettylearn.nio.client.NioHelloworldClientTask;
import com.summer.nettylearn.nio.server.NioHelloworldServerTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class NettylearnApplication {

    public static void main(String[] args) {
        startHelloworldServer();

        SpringApplication.run(NettylearnApplication.class, args);
    }

    /**
     * 启动server
     */
    private static void startHelloworldServer() {
        try {
            String host = "127.0.0.1";
            int port = 8088;
            NioHelloworldServerTask helloworldServer = new NioHelloworldServerTask(host, port);
            //独立线程中执行
            new Thread(helloworldServer, "NioHelloworldServerTask 1").start();

            Thread.sleep(5000);

            NioHelloworldClientTask nioHelloworldClientTask = new NioHelloworldClientTask(host, port);
            new Thread(nioHelloworldClientTask, "NioHelloworldClientTask 1").start();
        } catch (Exception e) {
            log.error("startHelloworldServer exception,", e);
        }
    }
}
