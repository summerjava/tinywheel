package com.summer.simplerpc.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * RPC通用配置信息，提供用户自定义的功能
 *
 * @author summer
 * @version $Id: RpcCommonProperty.java, v 0.1 2022年01月16日 6:09 PM summer Exp $
 */
@Data
@Component
@ConfigurationProperties(prefix = "summer.simplerpc")
public class RpcCommonProperty {

    /**
     * 服务提供方地址
     */
    private String serviceAddress;

    /**
     * 注册中心类型
     */
    private String registryType;

    /**
     * 注册中心地址
     */
    private String registryAddress;
}