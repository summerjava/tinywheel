package com.summer.simplerpc.provider;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.summer.simplerpc.annotation.SimpleRpcProvider;
import com.summer.simplerpc.io.RPCDecoder;
import com.summer.simplerpc.io.RPCEncoder;
import com.summer.simplerpc.registry.ServiceRegistry;
import com.summer.simplerpc.registry.model.ServiceMetaConfig;
import com.summer.simplerpc.util.ServiceUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.Map;
import java.util.concurrent.*;

/**
 * rpc provider功能实现。
 *
 * 负责扫描服务provider注解bean，注册服务到注册中心，启动netty监听。
 * 提供RPC请求实际处理。
 *
 * @author summer
 * @version $Id: SimpleRpcProviderBean.java, v 0.1 2022年01月16日 12:19 PM summer Exp $
 */
@Slf4j
public class SimpleRpcProviderBean implements InitializingBean, BeanPostProcessor {

    /**
     * 地址
     */
    private String          address;

    /**
     * 服务注册中心
     */
    private ServiceRegistry serviceRegistry;

    /**
     * 服务提供bean的缓存map
     */
    private Map<String, Object> providerBeanMap = new ConcurrentHashMap<>(64);

    /**
     * 处理实际rpc请求的线程池
     */
    private static ThreadPoolExecutor rpcThreadPoolExecutor;

    private static ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("simplerpc-provider-pool-%d").build();

    /**
     * netty相关
     */
    private EventLoopGroup bossGroup   = null;
    private EventLoopGroup workerGroup = null;

    /**
     * 构造函数
     *
     * @param address 地址
     * @param serviceRegistry 服务注册中心
     */
    public SimpleRpcProviderBean(String address, ServiceRegistry serviceRegistry) {
        this.address = address;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //启动netty服务监听
        new Thread(() -> {
            try {
                startNettyServer();
            } catch (InterruptedException e) {
                log.error("startNettyServer exception,", e);
            }
        }).start();
    }

    /**
     * 提交rpc处理任务
     *
     * @param task 任务
     */
    public static void submit(Runnable task) {
        if (rpcThreadPoolExecutor == null) {
            synchronized (SimpleRpcProviderBean.class) {
                if (rpcThreadPoolExecutor == null) {
                    rpcThreadPoolExecutor = new ThreadPoolExecutor(100, 100,
                            600L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000),
                            threadFactory);
                }
            }
        }
        rpcThreadPoolExecutor.submit(task);
    }

    /**
     * 启动netty服务监听
     *
     * @throws InterruptedException
     */
    private void startNettyServer() throws InterruptedException {
        if (workerGroup != null && bossGroup != null) {
            return;
        }

        log.info("startNettyServer begin");

        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        socketChannel.pipeline()
                                .addLast(new LengthFieldBasedFrameDecoder(65535,0,4,0,0))
                                .addLast(new RPCDecoder())
                                .addLast(new RPCEncoder())
                                .addLast(new SimpleRpcProviderNettyHandler(providerBeanMap))
                        ;
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 512)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        String[] array = address.split(":");
        String host = array[0];
        int port = Integer.parseInt(array[1]);

        //启动服务
        ChannelFuture future = serverBootstrap.bind(host, port).sync();

        log.info(String.format("startNettyServer,host=%s,port=%s", host, port));

        future.channel().closeFuture().sync();
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        //获取bean上的注解
        SimpleRpcProvider simpleRpcProvider = bean.getClass().getAnnotation(SimpleRpcProvider.class);
        if (simpleRpcProvider == null) {
            //无注解直接return原始的bean
            return bean;
        }

        //缓存保存
        String serviceName = simpleRpcProvider.serviceInterface().getName();
        String version = simpleRpcProvider.serviceVersion();
        providerBeanMap.put(ServiceUtils.buildServiceKey(serviceName, version), bean);

        log.info("postProcessAfterInitialization find a simpleRpcProvider[" + serviceName + "," + version + "]");

        //将服务注册到注册中心
        String[] addressArray = address.split(ServiceUtils.SPLIT_CHAR);
        String host = addressArray[0];
        String port = addressArray[1];

        ServiceMetaConfig serviceMetaConfig = new ServiceMetaConfig();
        serviceMetaConfig.setAddress(host);
        serviceMetaConfig.setName(serviceName);
        serviceMetaConfig.setVersion(version);
        serviceMetaConfig.setPort(Integer.parseInt(port));

        try {
            serviceRegistry.register(serviceMetaConfig);
            log.info("register service success,serviceMetaConfig=" + serviceMetaConfig.toString());
        } catch (Exception e) {
            log.error("register service fail,serviceMetaConfig=" + serviceMetaConfig.toString(), e);
        }

        return bean;
    }
}