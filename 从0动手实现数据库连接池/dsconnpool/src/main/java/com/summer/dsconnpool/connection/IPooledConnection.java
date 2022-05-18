package com.summer.dsconnpool.connection;

import com.summer.dsconnpool.IPooledDataSource;
import java.sql.Connection;

/**
 * 池化连接接口定义
 *
 * @author summer
 * @version : IPooledConnection.java, v 0.1 2022年05月15日 10:29 PM summer Exp $
 */
public interface IPooledConnection extends Connection {
    /**
     * 是否繁忙
     *
     * @return
     */
    boolean isBusy();

    /**
     * 设置是否繁忙状态
     * @param busy
     */
    void setBusy(boolean busy);

    /**
     * 获取真正的连接
     */
    Connection getConnection();

    /**
     * 设置连接信息
     */
    void setConnection(Connection connection);

    /**
     * 设置数据源信息
     * @param dataSource
     */
    void setDataSource(final IPooledDataSource dataSource);
}