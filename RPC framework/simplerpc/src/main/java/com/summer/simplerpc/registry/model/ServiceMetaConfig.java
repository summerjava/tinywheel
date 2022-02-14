package com.summer.simplerpc.registry.model;

import lombok.Data;

/**
 * 服务元数据配置领域模型
 *
 * @author summer
 * @version $Id: ServiceMetaConfig.java, v 0.1 2022年01月16日 10:58 AM summer Exp $
 */
@Data
public class ServiceMetaConfig {

    /**
     * 服务名
     */
    private String name;

    /**
     * 服务版本
     */
    private String version;

    /**
     * 服务地址
     */
    private String address;

    /**
     * 服务端口
     */
    private Integer port;
}