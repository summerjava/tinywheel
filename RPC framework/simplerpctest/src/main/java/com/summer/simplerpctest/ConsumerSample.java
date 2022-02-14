package com.summer.simplerpctest;

import com.summer.simplerpc.annotation.SimpleRpcConsumer;
import com.summer.simplerpctest.consumer.HelloworldService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 发起对HelloWorldService调用示例
 *
 * @author summer
 * @version $Id: ConsumerSample.java, v 0.1 2022年01月19日 9:31 AM summer Exp $
 */
@Slf4j
@Component
public class ConsumerSample {

    @SimpleRpcConsumer
    @Resource
    private HelloworldService helloworldService;

    public String invokeHelloworldService() {
        String result = helloworldService.buildHelloworld("qwert");
        return result;
    }
}