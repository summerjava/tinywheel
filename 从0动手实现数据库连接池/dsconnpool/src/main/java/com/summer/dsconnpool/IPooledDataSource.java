package com.summer.dsconnpool;

import com.summer.dsconnpool.connection.IPooledConnection;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * 池化数据源接口
 *
 * @author summer
 * @version : IPooledDataSourceConfig.java, v 0.1 2022年05月15日 10:36 PM summer Exp $
 */
public interface IPooledDataSource extends DataSource {
    /**
     * 获取连接
     * @return
     * @throws SQLException
     */
    IPooledConnection getConnection() throws SQLException;
    /**
     * 归还连接
     *
     * @param pooledConnection 连接池
     */
    void returnConnection(IPooledConnection pooledConnection);
}