package com.summer.simplerpc.consumer;

import com.summer.simplerpc.annotation.SimpleRpcConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * rpc consumer具体处理器。负责扫描代码中含@SimpleRpcConsumer注解的属性，进行代理实现远端网络调用。
 *
 * @author summer
 * @version $Id: SimpleRpcConsumerPostProcessor.java, v 0.1 2022年01月18日 8:28 AM summer Exp $
 */
@Slf4j
public class SimpleRpcConsumerPostProcessor implements BeanFactoryPostProcessor, BeanClassLoaderAware {

    /**
     * classloader
     */
    private ClassLoader classLoader;

    /**
     * 保存BeanDefinition列表
     */
    private Map<String, BeanDefinition> beanDefinitions = new HashMap<>();

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        log.info("SimpleRpcConsumerPostProcessor postProcessBeanFactory begin");
        //遍历bean，改些打了SimpleRpcConsumer注解的属性
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
            if (StringUtils.isEmpty(beanDefinition.getBeanClassName())) {
                continue;
            }
            Class<?> clazz = ClassUtils.resolveClassName(beanDefinition.getBeanClassName(), this.classLoader);
            ReflectionUtils.doWithFields(clazz, this::processConsumerBeanDefinition);
        }

        //将BeanDefinition重新注入spring容器
        BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry)beanFactory;
        for (Map.Entry<String, BeanDefinition> entry : this.beanDefinitions.entrySet()) {
            log.info("register BeanDefinition[" + entry.getKey() + "," + entry.getValue() + "]");
            beanDefinitionRegistry.registerBeanDefinition(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 带有rpc消费者注解的bean定义的重处理
     *
     * @param field 属性
     */
    private void processConsumerBeanDefinition(Field field) {
        SimpleRpcConsumer simpleRpcConsumer = AnnotationUtils.getAnnotation(field, SimpleRpcConsumer.class);

        //筛选出打了rpc consumer注解的属性
        if (simpleRpcConsumer == null) {
            return;
        }

        log.info("processConsumerBeanDefinition,find a simpleRpcConsumer field:" + field.toString());
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(SimpleRpcConsumerFactoryBean.class);
        beanDefinitionBuilder.setInitMethodName("init");
        beanDefinitionBuilder.addPropertyValue("interfaceClass", field.getType());
        beanDefinitionBuilder.addPropertyValue("serviceVersion", simpleRpcConsumer.serviceVersion());
        beanDefinitionBuilder.addPropertyValue("registryType", simpleRpcConsumer.registerType());
        beanDefinitionBuilder.addPropertyValue("registryAddress", simpleRpcConsumer.registerAddress());

        BeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();

        log.info("processConsumerBeanDefinition,find a simpleRpcConsumer field,result beanDefinition:" + field.toString());

        beanDefinitions.put(field.getName(), beanDefinition);
    }

    /**
     * 获取classloader
     *
     * @param classLoader
     */
    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}