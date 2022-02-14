package com.summer.simplerpc.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RPC provider注解
 *
 * @author summer
 * @version $Id: SimpleRpcProviderBean.java, v 0.1 2022年01月16日 11:53 AM summer Exp $
 */
@Retention(RetentionPolicy.RUNTIME)
//注解打在类上
@Target(ElementType.TYPE)
@Component
public @interface SimpleRpcProvider {
    Class<?> serviceInterface() default Object.class;
    String serviceVersion() default "1.0.0";
}