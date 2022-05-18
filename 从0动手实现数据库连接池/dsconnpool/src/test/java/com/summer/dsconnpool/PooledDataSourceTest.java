package com.summer.dsconnpool;

import com.summer.dsconnpool.config.PooledDataSourceConfig;
import com.summer.dsconnpool.connection.IPooledConnection;
import com.summer.dsconnpool.exception.ConnPoolException;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

public class PooledDataSourceTest {

    @Test
    public void simpleTest() throws Exception {
        DefaultDataSource source = new DefaultDataSource();
        PooledDataSourceConfig dataSourceConfig = new PooledDataSourceConfig();
        dataSourceConfig.setDriverClass("com.mysql.jdbc.Driver");
        dataSourceConfig.setJdbcUrl("jdbc:mysql://localhost:3306/db_summer_1?useUnicode=true&characterEncoding=utf-8");
        dataSourceConfig.setUser("root");
        dataSourceConfig.setPassword("summer");
        dataSourceConfig.setMinSize(1);
        dataSourceConfig.setMaxSize(1);

        source.setDataSourceConfig(dataSourceConfig);

        // 初始化
        source.init();

        IPooledConnection connection = source.getConnection();
        System.out.println(connection.getCatalog());

        //释放连接
        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            source.returnConnection(connection);
        }).start();

        IPooledConnection connection2 = source.getConnection();
        System.out.println(connection2.getCatalog());
    }

    @Test
    public void notWaitTest() throws Exception {
        DefaultDataSource source = new DefaultDataSource();
        PooledDataSourceConfig dataSourceConfig = new PooledDataSourceConfig();
        dataSourceConfig.setDriverClass("com.mysql.jdbc.Driver");
        dataSourceConfig.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/db_summer_1?useUnicode=true&characterEncoding=utf-8");
        dataSourceConfig.setUser("root");
        dataSourceConfig.setPassword("summer");
        dataSourceConfig.setMinSize(1);
        dataSourceConfig.setMaxSize(1);
        dataSourceConfig.setMaxWaitMills(0);

        source.setDataSourceConfig(dataSourceConfig);

        // 初始化
        source.init();

        Connection connection = source.getConnection();
        System.out.println(connection.getCatalog());

        // 新的线程执行
        newThreadExec(source);

        Thread.sleep(1000);
    }

    private void newThreadExec(final DefaultDataSource source) {
        // 另起一个线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 预期报错
                Connection connection2 = null;
                try {
                    connection2 = source.getConnection();
                    System.out.println(connection2.getCatalog());
                } catch (SQLException e) {
                    throw new ConnPoolException(e);
                }
            }
        }).start();
    }

    @Test
    public void waitTest() throws Exception {
        System.out.println("waitTest......");

        DefaultDataSource source = new DefaultDataSource();
        PooledDataSourceConfig dataSourceConfig = new PooledDataSourceConfig();
        dataSourceConfig.setDriverClass("com.mysql.jdbc.Driver");
        dataSourceConfig.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/db_summer_1?useUnicode=true&characterEncoding=utf-8");
        dataSourceConfig.setUser("root");
        dataSourceConfig.setPassword("summer");
        dataSourceConfig.setMinSize(1);
        dataSourceConfig.setMaxSize(1);
        dataSourceConfig.setMaxWaitMills(100);

        source.setDataSourceConfig(dataSourceConfig);

        // 初始化
        source.init();

        Connection connection = source.getConnection();
        System.out.println("getConnection success,connection.getCatalog=" + connection.getCatalog());

        // 新的线程执行
        newThreadExec(source);

        Thread.sleep(1000);
        System.out.println("释放连接begin");
        connection.close();
        System.out.println("释放连接end");

        connection = source.getConnection();
        System.out.println("getConnection success,connection.getCatalog=" + connection.getCatalog());

        connection = source.getConnection();
        System.out.println("getConnection success,connection.getCatalog=" + connection.getCatalog());

        Thread.sleep(1000);
    }

    @Test
    public void testOnIdleTest() throws Exception {
        DefaultDataSource source = new DefaultDataSource();
        PooledDataSourceConfig dataSourceConfig = new PooledDataSourceConfig();
        dataSourceConfig.setDriverClass("com.mysql.jdbc.Driver");
        dataSourceConfig.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/db_summer_1?useUnicode=true&characterEncoding=utf-8");
        dataSourceConfig.setUser("root");
        dataSourceConfig.setPassword("summer");
        dataSourceConfig.setMinSize(1);
        dataSourceConfig.setMaxSize(1);
        dataSourceConfig.setMaxWaitMills(0);

        source.setDataSourceConfig(dataSourceConfig);

        // 初始化配置
        source.init();

        Connection connection = source.getConnection();
        System.out.println(connection.getCatalog());

        Thread.sleep(10 * 1000);

        connection.close();
    }
}
