package com.summer.simplerpc.registry;

import com.summer.simplerpc.registry.model.ServiceMetaConfig;

/**
 * 注册中心服务接口定义
 *
 * @author summer
 * @version $Id: ServiceRegistry.java, v 0.1 2022年01月16日 10:56 AM summer Exp $
 */
public interface ServiceRegistry {

    /**
     * 注册服务
     *
     * @param serviceMetaConfig 服务元数据配置
     * @throws Exception
     */
    void register(ServiceMetaConfig serviceMetaConfig) throws Exception;

    /**
     * 取消注册服务
     *
     * @param serviceMetaConfig 服务元数据配置
     * @throws Exception
     */
    void unRegister(ServiceMetaConfig serviceMetaConfig) throws Exception;

    /**
     * 服务发现
     *
     * @param serviceName 服务名
     * @return
     * @throws Exception
     */
    ServiceMetaConfig discovery(String serviceName) throws Exception;
}