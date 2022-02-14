package com.summer.simplerpc.model;

import lombok.Data;

import java.io.Serializable;

/**
 * rpc处理结果
 *
 * @author summer
 * @version $Id: SimpleRpcResponse.java, v 0.1 2022年01月16日 5:52 PM summer Exp $
 */
@Data
public class SimpleRpcResponse implements Serializable {

    private static final long serialVersionUID = 7306531831668743451L;

    /**
     * 业务流水号
     */
    private String bizNO;

    /**
     * 错误结果提示消息
     */
    private String msg;

    /**
     * 实际结果
     */
    private Object data;

}