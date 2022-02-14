package com.summer.simplerpc.registry.cache;

import com.summer.simplerpc.registry.model.ServiceMetaConfig;
import org.apache.curator.x.discovery.ServiceProvider;

/**
 *
 * @author summer
 * @version $Id: ServiceProviderCache.java, v 0.1 2022年01月16日 11:41 AM summer Exp $
 */
public interface ServiceProviderCache {

    /**
     * 查询缓存
     * @param serviceName
     * @return
     */
    ServiceProvider<ServiceMetaConfig> queryCache(String serviceName);

    /**
     * 更新缓存
     *
     * @param serviceName 服务名
     * @param serviceProvider 服务provider
     * @return
     */
    void updateCache(String serviceName, ServiceProvider<ServiceMetaConfig> serviceProvider);
}