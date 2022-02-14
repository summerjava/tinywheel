package com.summer.simplerpctest.provider;

import com.summer.simplerpc.annotation.SimpleRpcProvider;
import com.summer.simplerpctest.consumer.HelloworldService;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * HelloworldService接口实现
 *
 * @author summer
 * @version $Id: HelloworldServiceImpl.java, v 0.1 2022年01月19日 9:18 AM summer Exp $
 */
@SimpleRpcProvider(serviceInterface=HelloworldService.class)
@Slf4j
public class HelloworldServiceImpl implements HelloworldService {

    @Override
    public String buildHelloworld(String param) {
        log.info("HelloworldServiceImpl begin");
        return param + "_" + UUID.randomUUID().toString();
    }
}