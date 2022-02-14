package com.summer.simplerpc.consumer;

import com.summer.simplerpc.registry.ServiceRegistry;
import com.summer.simplerpc.registry.cache.ServiceProviderCache;
import com.summer.simplerpc.registry.cache.ServiceProviderLocalCache;
import com.summer.simplerpc.registry.zk.ZkServiceRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

/**
 * 生成rpc consumer代理bean的FactoryBean
 *
 * @author summer
 * @version $Id: SimpleRpcConsumerFactoryBean.java, v 0.1 2022年01月18日 8:58 AM summer Exp $
 */
@Slf4j
public class SimpleRpcConsumerFactoryBean implements FactoryBean {

    /**
     * 调用的服务接口类
     */
    private Class<?> interfaceClass;

    /**
     * 服务版本号
     */
    private String serviceVersion;

    /**
     * 注册中心类型
     */
    private String registryType;

    /**
     * 注册中心地址
     */
    private String registryAddress;

    /**
     * 实际的bean
     */
    private Object object;

    /**
     * init方法，通过动态代理生成bean
     *
     * @throws Exception
     */
    public void init() throws Exception {
        ServiceProviderCache serviceProviderCache = new ServiceProviderLocalCache();
        ServiceRegistry zkServiceRegistry = new ZkServiceRegistry(registryAddress, serviceProviderCache);

        //动态代理
        this.object = Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[] {interfaceClass},
                new SimpleRpcInvokeHandler<>(this.serviceVersion, zkServiceRegistry));
        log.info("SimpleRpcConsumerFactoryBean getObject {}", interfaceClass.getName());
    }

    /**
     * 返回创建的bean实例
     *
     * @return
     * @throws Exception
     */
    @Override
    public Object getObject() throws Exception {
        return this.object;
    }

    /**
     * 创建的bean实例的类型
     *
     * @return
     */
    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }

    /**
     * 创建的bean实例的作用域
     *
     * @return
     */
    @Override
    public boolean isSingleton() {
        return true;
    }

    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public void setRegistryType(String registryType) {
        this.registryType = registryType;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }
}