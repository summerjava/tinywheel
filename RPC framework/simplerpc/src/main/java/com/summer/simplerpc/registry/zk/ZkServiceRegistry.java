package com.summer.simplerpc.registry.zk;

import com.summer.simplerpc.registry.cache.ServiceProviderCache;
import com.summer.simplerpc.registry.model.ServiceMetaConfig;
import com.summer.simplerpc.registry.ServiceRegistry;
import com.summer.simplerpc.util.ServiceUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.*;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.apache.curator.x.discovery.strategies.RoundRobinStrategy;

/**
 * 服务注册中心-zk实现
 *
 * @author summer
 * @version $Id: ZkServiceRegistry.java, v 0.1 2022年01月16日 11:07 AM summer Exp $
 */
public class ZkServiceRegistry implements ServiceRegistry {

    /**
     * zk base path
     */
    private final static String ZK_BASE_PATH = "/simplerpc";

    /**
     * serviceProvider锁
     */
    private final Object lock = new Object();

    /**
     * zk framework client
     */
    private CuratorFramework client;

    /**
     * 服务发现
     */
    private ServiceDiscovery<ServiceMetaConfig> serviceDiscovery;

    /**
     * serviceProvider缓存
     */
    private ServiceProviderCache serviceProviderCache;

    /**
     * 构造函数
     *
     * @param address 地址
     */
    public ZkServiceRegistry(String address, ServiceProviderCache serviceProviderCache) throws Exception {
        this.client = CuratorFrameworkFactory.newClient(address, new ExponentialBackoffRetry(1000, 3));
        this.client.start();

        this.serviceProviderCache = serviceProviderCache;

        JsonInstanceSerializer<ServiceMetaConfig> serializer = new JsonInstanceSerializer<>(ServiceMetaConfig.class);
        serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceMetaConfig.class)
                .client(client)
                .serializer(serializer)
                .basePath(ZK_BASE_PATH)
                .build();
        serviceDiscovery.start();
    }

    @Override
    public void register(ServiceMetaConfig serviceMetaConfig) throws Exception {
        ServiceInstanceBuilder<ServiceMetaConfig> serviceInstanceBuilder = ServiceInstance.builder();
        ServiceInstance<ServiceMetaConfig> serviceInstance = serviceInstanceBuilder
                .name(ServiceUtils.buildServiceKey(serviceMetaConfig.getName(), serviceMetaConfig.getVersion()))
                .address(serviceMetaConfig.getAddress())
                .port(serviceMetaConfig.getPort())
                .payload(serviceMetaConfig)
                .uriSpec(new UriSpec("{scheme}://{address}:{port}"))
                .build();

        serviceDiscovery.registerService(serviceInstance);
    }

    @Override
    public void unRegister(ServiceMetaConfig serviceMetaConfig) throws Exception {
        ServiceInstanceBuilder<ServiceMetaConfig> serviceInstanceBuilder = ServiceInstance.builder();
        ServiceInstance<ServiceMetaConfig> serviceInstance = serviceInstanceBuilder
                .name(ServiceUtils.buildServiceKey(serviceMetaConfig.getName(), serviceMetaConfig.getVersion()))
                .address(serviceMetaConfig.getAddress())
                .port(serviceMetaConfig.getPort())
                .payload(serviceMetaConfig)
                .uriSpec(new UriSpec("{scheme}://{address}:{port}"))
                .build();

        serviceDiscovery.unregisterService(serviceInstance);
    }

    @Override
    public ServiceMetaConfig discovery(String serviceName) throws Exception {
        //先读缓存
        ServiceProvider<ServiceMetaConfig> serviceProvider = serviceProviderCache.queryCache(serviceName);

        //缓存miss，需要调serviceDiscovery
        if (serviceProvider == null) {
            synchronized (lock) {
                serviceProvider = serviceDiscovery.serviceProviderBuilder()
                        .serviceName(serviceName)
                        .providerStrategy(new RoundRobinStrategy<>())
                        .build();
                serviceProvider.start();

                //更新缓存
                serviceProviderCache.updateCache(serviceName, serviceProvider);
            }
        }

        ServiceInstance<ServiceMetaConfig> serviceInstance = serviceProvider.getInstance();
        return serviceInstance != null ? serviceInstance.getPayload() : null;
    }
}