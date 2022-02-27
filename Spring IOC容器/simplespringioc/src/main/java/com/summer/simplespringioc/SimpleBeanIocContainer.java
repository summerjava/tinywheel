package com.summer.simplespringioc;

import com.summer.simplespringioc.model.BeanPropertyConfig;
import com.summer.simplespringioc.model.ConstructorPropertyConfig;
import com.summer.simplespringioc.util.BaseUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * spring ioc容器实现
 *
 * @author summer
 * @version $Id: SimpleBeanIocContainer.java, v 0.1 2022年02月10日 9:12 AM summer Exp $
 */
@Slf4j
public class SimpleBeanIocContainer {

    //bean定义相关标签
    private final static String BEAN_CONFIG_ATTR_ID = "id";
    private final static String BEAN_CONFIG_ATTR_CLASS_NAME = "class";
    private final static String BEAN_CONFIG_ATTR_CONSTRUCTOR_ARG = "constructor-arg";
    private final static String BEAN_CONFIG_ATTR_REF = "ref";
    private final static String BEAN_CONFIG_ATTR_PROPERTY = "property";
    private final static String BEAN_CONFIG_ATTR_NAME = "name";
    private final static String BEAN_CONFIG_ATTR_VALUE = "value";
    private final static String BEAN_CONFIG_ATTR_BEAN = "bean";

    /**
     * 存放bean定义的map,key-bean id
     */
    private final Map<String, SimpleBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    /**
     * 存放单例bean的本地缓存
     */
    private final Map<String, Object> singletonBeans = new ConcurrentHashMap<>();

    /**
     * 初始化
     *
     * @param file 配置文件
     */
    public void init(String file) {
        //加载xml文件，解析bean的定义
        this.loadBeanDefinitionFromFile(file);

        //注册bean到容器
        registerBean();

        log.info("singletonBeans=" + singletonBeans);
    }

    /**
     * 注册bean到容器
     */
    private void registerBean() {
        for (Map.Entry<String, SimpleBeanDefinition> entry : beanDefinitionMap.entrySet()) {
            String beanId = entry.getKey();

            getSingletonBean(beanId);
        }
    }

    /**
     * 获取bean（单例）
     *
     * @param beanId bean id
     * @return
     */
    private Object getSingletonBean(String beanId) {
        Object beanObject = singletonBeans.get(beanId);
        if (beanObject != null) {
            return beanObject;
        }

        SimpleBeanDefinition beanDefinition = beanDefinitionMap.get(beanId);
        log.info("create singleton bean begin....beanid=" + beanId + ",beanDefinition=" + beanDefinition);

        beanObject = createBean(beanDefinition);

        singletonBeans.put(beanId, beanObject);

        return beanObject;
    }

    /**
     * 创建bean实例
     *
     * @param beanDefinition
     * @return
     */
    private Object createBean(SimpleBeanDefinition beanDefinition) {
        //初始化bean
        Object beanInstance = initBean(beanDefinition);

        //bean属性注入
        populateBeanProps(beanInstance, beanDefinition);

        return beanInstance;
    }

    /**
     * bean属性注入
     *
     * @param beanInstance bean
     * @param beanDefinition bean定义
     */
    private void populateBeanProps(Object beanInstance, SimpleBeanDefinition beanDefinition) {
        try {
            List<BeanPropertyConfig> propertyConfigs = beanDefinition.getProperties();
            Method[] methods = beanInstance.getClass().getDeclaredMethods();
            for (Method method : methods) {
                for (BeanPropertyConfig beanPropertyConfig : propertyConfigs) {
                    if (StringUtils.equals(method.getName(), "set" + BaseUtils.upperFirstChar(beanPropertyConfig.getName()))) {
                        //通过反射调用set方法进行属性值的设置。有可能是基础类型值，也可能是另一个bean。
                        Object propValue = null;
                        if (beanPropertyConfig.getIsValueRef()) {
                            propValue = getSingletonBean(beanPropertyConfig.getValue());
                        } else {
                            //beanDefinition中存的值是String，需要转换为特定的基础类型
                            Type[] types = method.getParameterTypes();
                            propValue = BaseUtils.convertBaseDataType(beanPropertyConfig.getValue(), types[0].getTypeName());
                        }

                        log.info("method=" + method.getName() + ",propValue=" + propValue + ",beanPropertyConfig=" + beanPropertyConfig + ",beanDefinition=" + beanDefinition);

                        method.invoke(beanInstance, propValue);
                    }
                }
            }
        } catch (Exception e) {
            log.error("populateBeanProps exception,beanDefinition=" + beanDefinition, e);
        }
    }

    /**
     * 初始化bean
     *
     * @param beanDefinition bean定义
     * @return
     */
    private Object initBean(SimpleBeanDefinition beanDefinition) {
        //如果有构造函数配置，调用构造进行实例化
        if (CollectionUtils.isNotEmpty(beanDefinition.getConstructArgs())) {
            return createBeanByConstructor(beanDefinition);
        }

        return createBeanByClassloader(beanDefinition);
    }

    /**
     * 通过构造函数创建bean
     *
     * @param beanDefinition bean定义
     * @return
     */
    private Object createBeanByConstructor(SimpleBeanDefinition beanDefinition) {
        log.info("createBeanByConstructor begin....");
        try {
            Constructor<?> constructor = null;//构造函数匹配
            Object[] args = null;//参数

            Class<?> beanClass = Thread.currentThread().getContextClassLoader().loadClass(beanDefinition.getClassName());
            Constructor<?>[] constructors = beanClass.getDeclaredConstructors();

            //根据构造函数参数长度匹配对应的构造函数
            for (int i = 0;i < constructors.length;++i) {
                Class<?>[] paramTypes = constructors[i].getParameterTypes();
                if (paramTypes.length != beanDefinition.getConstructArgs().size()) {
                    continue;
                }

                constructor = constructors[i];

                //参数组装（可能是基础类型，也可能是bean注入）
                args = new Object[paramTypes.length];
                for (int j = 0;j < beanDefinition.getConstructArgs().size(); ++j) {
                    ConstructorPropertyConfig constructorPropertyConfig = beanDefinition.getConstructArgs().get(j);
                    if (constructorPropertyConfig.getIsValueRef()) {
                        args[j] = getSingletonBean(constructorPropertyConfig.getValue());
                    } else {
                        args[j] = constructorPropertyConfig.getValue();
                    }
                }
                break;
            }

            //创建bean
            return constructor.newInstance(args);
        } catch (Exception e) {
            log.error("createBeanByConstructor exception,beanDefinition=" + beanDefinition);
            return null;
        }
    }

    /**
     * get bean object by bean id
     *
     * @param beanDefinition
     * @return
     */
    public static Object createBeanByClassloader(SimpleBeanDefinition beanDefinition) {
        log.info("createBeanByClassloader begin....");
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Class<?> clz = classLoader.loadClass(beanDefinition.getClassName());
            return clz.newInstance();
        } catch (Exception e) {
            log.error("createBeanByClassloader exception", e);
            return null;
        }
    }

    /**
     * 解析XML文件加载bean定义
     *
     * @param file 文件名
     */
    private void loadBeanDefinitionFromFile(String file) {
        log.info("loadBeanDefinitionFromFile begin");

        InputStream inputStream = null;
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

            //获取配置文件的文件流
            inputStream = classLoader.getResourceAsStream(file);

            SAXReader reader = new SAXReader();
            Document document = reader.read(inputStream);

            Element rootElement = document.getRootElement();
            Iterator iterator = rootElement.elementIterator();

            //遍历bean的定义配置
            while (iterator.hasNext()) {
                Element loopElement = (Element) iterator.next();
                String beanId = loopElement.attributeValue(BEAN_CONFIG_ATTR_ID);
                String beanclass = loopElement.attributeValue(BEAN_CONFIG_ATTR_CLASS_NAME);
                if (StringUtils.isBlank(beanId) || StringUtils.isBlank(beanclass)) {
                    log.error("loadBeanDefinitionFromFile xml parse,bean definition illegal:[" + beanId + "," + beanclass + "]");
                    continue;
                }

                //SimpleBeanDefinition构造
                SimpleBeanDefinition simpleBeanDefinition = new SimpleBeanDefinition();
                simpleBeanDefinition.setId(beanId);
                simpleBeanDefinition.setClassName(beanclass);

                //构造函数定义解析
                parseConstructorArg(loopElement, simpleBeanDefinition);

                //property定义解析
                parseProperties(loopElement, simpleBeanDefinition);

                beanDefinitionMap.put(beanId, simpleBeanDefinition);
            }

            log.info("loadBeanDefinitionFromFile,eanDefinitionMap=" + beanDefinitionMap);
        } catch (Exception e) {
            log.error("loadBeanDefinitionFromFile exception,file=" + file, e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("close inputstream exception,file=" + file, e);
                }
            }
        }
    }

    /**
     * get bean definition by bean id
     *
     * @param beanId
     * @return
     */
    public SimpleBeanDefinition getBeanDefinition(String beanId) {
        return beanDefinitionMap.get(beanId);
    }

    /**
     * 构造函数配置解析
     *
     * @param element
     * @param simpleBeanDefinition
     */
    private void parseConstructorArg(Element element, SimpleBeanDefinition simpleBeanDefinition) {
        Iterator iterator = element.elementIterator(BEAN_CONFIG_ATTR_CONSTRUCTOR_ARG);
        while (iterator.hasNext()) {
            Element propertyElement = (Element) iterator.next();
            String ref = propertyElement.attributeValue(BEAN_CONFIG_ATTR_REF);
            String value = propertyElement.attributeValue(BEAN_CONFIG_ATTR_REF);
            if (StringUtils.isBlank(ref) && StringUtils.isBlank(value)) {
                log.error("parseConstructorArg config illegal,propertyElement=" + propertyElement);
                return;
            }

            ConstructorPropertyConfig constructorPropertyConfig = new ConstructorPropertyConfig();
            if (StringUtils.isNotBlank(ref)) {
                constructorPropertyConfig.setValue(ref);
                constructorPropertyConfig.setIsValueRef(true);
            } else {
                constructorPropertyConfig.setValue(value);
            }

            simpleBeanDefinition.getConstructArgs().add(constructorPropertyConfig);
        }
    }

    /**
     * property配置解析
     *
     * @param beanElem
     * @param simpleBeanDefinition
     */
    private void parseProperties(Element beanElem, SimpleBeanDefinition simpleBeanDefinition) {
        Iterator iterator = beanElem.elementIterator(BEAN_CONFIG_ATTR_PROPERTY);
        //遍历所有property配置
        while (iterator.hasNext()) {
            Element propElem = (Element) iterator.next();
            String propertyName = propElem.attributeValue(BEAN_CONFIG_ATTR_NAME);
            if (StringUtils.isBlank(propertyName)) {
                return;
            }

            BeanPropertyConfig beanPropertyConfig = new BeanPropertyConfig();
            beanPropertyConfig.setName(propertyName);

            //property的配置值可能是value也可能是ref
            String value = propElem.attributeValue(BEAN_CONFIG_ATTR_VALUE);
            if (StringUtils.isNotBlank(value)) {
                beanPropertyConfig.setValue(value);
                beanPropertyConfig.setIsValueRef(false);
            } else {
                Iterator propertyRefIterator = propElem.elementIterator(BEAN_CONFIG_ATTR_REF);
                Element refElem = (Element) propertyRefIterator.next();
                String refBean = refElem.attributeValue(BEAN_CONFIG_ATTR_BEAN);
                beanPropertyConfig.setValue(refBean);
                beanPropertyConfig.setIsValueRef(true);
            }

            simpleBeanDefinition.getProperties().add(beanPropertyConfig);
        }
    }
}