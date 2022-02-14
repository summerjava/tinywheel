package com.summer.simplerpc.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * rpc consumer starter
 *
 * @author summer
 * @version $Id: SimplerConsumerAutoConfiguration.java, v 0.1 2022年01月16日 6:19 PM summer Exp $
 */
@Configuration
@Slf4j
public class SimplerConsumerAutoConfiguration {

    @Bean
    public static BeanFactoryPostProcessor initRpcConsumer() throws Exception {
        return new SimpleRpcConsumerPostProcessor();
    }
}