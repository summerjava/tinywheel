package com.summer.simplerpc.util;

/**
 * 服务相关通用工具类
 *
 * @author summer
 * @version $Id: ServiceUtils.java, v 0.1 2022年01月16日 11:28 AM summer Exp $
 */
public class ServiceUtils {

    /**
     * 分隔符
     */
    public final static String SPLIT_CHAR = ":";

    /**
     * 服务唯一标识key组装
     *
     * @param serviceName 服务名
     * @param serviceVersion 服务版本
     * @return
     */
    public final static String buildServiceKey(String serviceName, String serviceVersion) {
        return String.join(SPLIT_CHAR, serviceName, serviceVersion);
    }
}