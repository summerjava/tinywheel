package com.summer.simplerpc.provider;

import com.summer.simplerpc.model.SimpleRpcRequest;
import com.summer.simplerpc.model.SimpleRpcResponse;
import com.summer.simplerpc.util.ServiceUtils;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.reflect.FastClass;

import java.util.Map;

/**
 * RPC核心处理逻辑handler
 *
 * @author summer
 * @version $Id: SimpleRpcProviderNettyHandler.java, v 0.1 2022年01月16日 5:36 PM summer Exp $
 */
@Slf4j
public class SimpleRpcProviderNettyHandler extends SimpleChannelInboundHandler<SimpleRpcRequest> {

    /**
     * 提供rpc服务的实例缓存map
     */
    private Map<String, Object> handlerMap;

    /**
     * 构造函数
     *
     * @param handlerMap
     */
    public SimpleRpcProviderNettyHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, SimpleRpcRequest simpleRpcRequest) throws Exception {
        SimpleRpcProviderBean.submit(() -> {
            log.debug("Receive rpc request {}", simpleRpcRequest.getBizNO());
            SimpleRpcResponse simpleRpcResponse = new SimpleRpcResponse();
            simpleRpcResponse.setBizNO(simpleRpcRequest.getBizNO());
            try {
                Object result = doHandle(simpleRpcRequest);
                simpleRpcResponse.setData(result);
            } catch (Throwable throwable) {
                simpleRpcResponse.setMsg(throwable.toString());
                log.error("handle rpc request error", throwable);
            }
            channelHandlerContext.writeAndFlush(simpleRpcResponse).addListener(
                    (ChannelFutureListener) channelFuture ->
                            log.info("return response for request " + simpleRpcRequest.getBizNO() + ",simpleRpcResponse=" + simpleRpcResponse));
        });
    }

    /**
     * 通过反射，执行实际的rpc请求
     * @param simpleRpcRequest
     * @return
     */
    private Object doHandle(SimpleRpcRequest simpleRpcRequest) throws Exception {
        String key = ServiceUtils.buildServiceKey(simpleRpcRequest.getClassName(), simpleRpcRequest.getServiceVersion());
        if (handlerMap == null || handlerMap.get(key) == null) {
            log.error("doHandle,the provider {0} not exist,", simpleRpcRequest.getClassName(), simpleRpcRequest.getServiceVersion());
            throw new RuntimeException("the provider not exist");
        }

        log.info("doHandle,simpleRpcRequest=" + simpleRpcRequest.toString());

        Object provider = handlerMap.get(key);

        //通过动态代理执行实际的调用
        FastClass fastClass = FastClass.create(provider.getClass());
        return fastClass.invoke(fastClass.getIndex(simpleRpcRequest.getMethodName(), simpleRpcRequest.getParamTypes()),
                provider, simpleRpcRequest.getParamValues());
    }
}