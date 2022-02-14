package com.summer.simplerpc.registry.cache;

import com.summer.simplerpc.registry.model.ServiceMetaConfig;
import org.apache.curator.x.discovery.ServiceProvider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地缓存实现
 *
 * @author summer
 * @version $Id: ServiceProviderLocalCache.java, v 0.1 2022年01月16日 11:43 AM summer Exp $
 */
public class ServiceProviderLocalCache implements ServiceProviderCache {

    /**
     * 本地缓存map
     */
    private Map<String, ServiceProvider<ServiceMetaConfig>> serviceProviderMap = new ConcurrentHashMap<>();

    @Override
    public ServiceProvider<ServiceMetaConfig> queryCache(String serviceName) {
        return serviceProviderMap.get(serviceName);
    }

    @Override
    public void updateCache(String serviceName, ServiceProvider<ServiceMetaConfig> serviceProvider) {
        serviceProviderMap.put(serviceName, serviceProvider);
    }
}