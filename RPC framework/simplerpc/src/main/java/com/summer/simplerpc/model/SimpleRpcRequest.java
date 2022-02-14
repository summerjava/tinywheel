package com.summer.simplerpc.model;

import lombok.Data;

import java.io.Serializable;

/**
 * RPC请求领域模型
 *
 * @author summer
 * @version $Id: SimpleRpcRequest.java, v 0.1 2022年01月16日 5:37 PM summer Exp $
 */
@Data
public class SimpleRpcRequest implements Serializable {

    private static final long serialVersionUID = -6523563004185159591L;

    /**
     * 业务流水号
     */
    private String bizNO;

    /**
     * 服务类名
     */
    private String className;

    /**
     * 服务方法名
     */
    private String methodName;

    /**
     * 服务版本号
     */
    private String serviceVersion;

    /**
     * 参数类型列表
     */
    private Class<?>[] paramTypes;

    /**
     * 参数值列表
     */
    private Object[] paramValues;
}