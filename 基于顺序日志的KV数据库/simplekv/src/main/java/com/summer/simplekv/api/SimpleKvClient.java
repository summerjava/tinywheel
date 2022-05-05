package com.summer.simplekv.api;

/**
 * KV数据库操作Client
 *
 * @author summer
 * @version : SimpleKvClient.java, v 0.1 2022年05月05日 3:49 PM summer Exp $
 */
public interface SimpleKvClient {

    /**
     * 增加元素
     *
     * @param key k
     * @param value v
     */
    void put(String key, String value);

    /**
     * 获取指定key对于的值
     *
     * @param key k
     * @return
     */
    String get(String key);

    /**
     * 删除指定key
     *
     * @param key
     */
    void del(String key);
}