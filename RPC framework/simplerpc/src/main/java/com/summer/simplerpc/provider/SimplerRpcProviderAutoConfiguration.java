package com.summer.simplerpc.provider;

import com.summer.simplerpc.model.RpcCommonProperty;
import com.summer.simplerpc.registry.ServiceRegistry;
import com.summer.simplerpc.registry.cache.ServiceProviderCache;
import com.summer.simplerpc.registry.cache.ServiceProviderLocalCache;
import com.summer.simplerpc.registry.zk.ZkServiceRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * rpc provider starter
 *
 * @author summer
 * @version $Id: SimplerRpcProviderAutoConfiguration.java, v 0.1 2022年01月16日 6:19 PM summer Exp $
 */
@Configuration
@Slf4j
public class SimplerRpcProviderAutoConfiguration {

    @Bean
    public SimpleRpcProviderBean initRpcProvider() throws Exception {
        RpcCommonProperty rpcCommonProperty = new RpcCommonProperty();
        rpcCommonProperty.setServiceAddress("127.0.0.1:50001");
        rpcCommonProperty.setRegistryAddress("127.0.0.1:2181");

        log.info("===================SimplerRpcProviderAutoConfiguration init，rpcCommonProperty=" + rpcCommonProperty.toString());
        ServiceProviderCache serviceProviderCache = new ServiceProviderLocalCache();
        ServiceRegistry zkServiceRegistry = new ZkServiceRegistry(rpcCommonProperty.getRegistryAddress(), serviceProviderCache);

        return new SimpleRpcProviderBean(rpcCommonProperty.getServiceAddress(), zkServiceRegistry);
    }
}