package com.summer.dsconnpool.config;

import lombok.Data;

/**
 * 数据源基础配置
 *
 * @author summer
 * @version : DataSourceConfig.java, v 0.1 2022年05月15日 10:19 PM summer Exp $
 */
@Data
public class DataSourceConfig {

    /**
     * 驱动类
     */
    protected String driverClass;

    /**
     * jdbc url
     */
    protected String jdbcUrl;

    /**
     * 用户名
     */
    protected String user;

    /**
     * 密码
     */
    protected String password;
}