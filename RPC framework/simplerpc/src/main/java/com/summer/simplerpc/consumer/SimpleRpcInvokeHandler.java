package com.summer.simplerpc.consumer;

import com.summer.simplerpc.model.SimpleRpcRequest;
import com.summer.simplerpc.model.SimpleRpcResponse;
import com.summer.simplerpc.registry.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * RPC调用动态代理handler实现
 *
 * @author summer
 * @version $Id: SimpleRpcInvokeHandler.java, v 0.1 2022年01月18日 9:19 AM summer Exp $
 */
@Slf4j
public class SimpleRpcInvokeHandler<T> implements InvocationHandler {

    /**
     * 服务版本号
     */
    private String serviceVersion;

    /**
     * 注册中心
     */
    private ServiceRegistry serviceRegistry;

    /**
     * 默认构造函数
     */
    public SimpleRpcInvokeHandler() {

    }

    public SimpleRpcInvokeHandler(String serviceVersion, ServiceRegistry serviceRegistry) {
        this.serviceVersion = serviceVersion;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        SimpleRpcRequest simpleRpcRequest = new SimpleRpcRequest();
        simpleRpcRequest.setBizNO(UUID.randomUUID().toString());
        simpleRpcRequest.setClassName(method.getDeclaringClass().getName());
        simpleRpcRequest.setServiceVersion(this.serviceVersion);
        simpleRpcRequest.setMethodName(method.getName());
        simpleRpcRequest.setParamTypes(method.getParameterTypes());
        simpleRpcRequest.setParamValues(args);

        log.info("begin simpleRpcRequest=" + simpleRpcRequest.toString());

        SimpleRpcConsumerNettyHandler simpleRpcConsumerNettyHandler = new SimpleRpcConsumerNettyHandler(this.serviceRegistry);
        SimpleRpcResponse simpleRpcResponse = simpleRpcConsumerNettyHandler.sendRpcRequest(simpleRpcRequest);

        log.info("result simpleRpcResponse=" + simpleRpcResponse);
        return simpleRpcResponse.getData();
    }
}