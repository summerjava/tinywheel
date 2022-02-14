package com.summer.simplerpc.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RPC consumer注解
 *
 * @author summer
 * @version $Id: SimpleRpcProviderBean.java, v 0.1 2022年01月16日 11:53 AM summer Exp $
 */
@Retention(RetentionPolicy.RUNTIME)
//注解打在属性上
@Target(ElementType.FIELD)
@Component
public @interface SimpleRpcConsumer {
    /**
     * 服务版本号
     * @return
     */
    String serviceVersion() default "1.0.0";

    /**
     * 注册中心类型-默认zk
     * @return
     */
    String registerType() default "zookeeper";

    /**
     * 注册中心地址
     * @return
     */
    String registerAddress() default "127.0.0.1:2181";
}