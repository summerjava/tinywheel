package com.summer.simplerpctest.consumer;

/**
 * 服务接口定义
 *
 * @author summer
 * @version $Id: HelloworldService.java, v 0.1 2022年01月19日 9:16 AM summer Exp $
 */
public interface HelloworldService {
    /**
     * 示例方法
     * @param param
     * @return
     */
    String buildHelloworld(String param);
}