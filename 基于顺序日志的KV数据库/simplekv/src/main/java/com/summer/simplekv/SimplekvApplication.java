package com.summer.simplekv;

import com.summer.simplekv.api.SimpleKvClient;
import com.summer.simplekv.core.LogBasedKV;
import lombok.extern.java.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Log
@SpringBootApplication
public class SimplekvApplication {

    public static void main(String[] args) {
        String logFileName = "simplekv.log";
        SimpleKvClient kvClient = new LogBasedKV(logFileName);

        //写入数据
        for (int index = 0; index < 5; ++index) {
            kvClient.put("k-" + index, "v-" + index);
        }

        //查询数据
        for (int index = 0; index < 5; ++index) {
            String value = kvClient.get("k-" + index);
            log.info("get [" + "k-" + index + " value is [" + value + "]");
        }

        //删除一行数据
        kvClient.del("k-" + 3);

        log.info("after del 3");

        //再次查询已被删除的数据
        String value = kvClient.get("k-" + 3);
        log.info("get [" + "k-" + 3 + " value is [" + value + "]");

        SpringApplication.run(SimplekvApplication.class, args);
    }

}
