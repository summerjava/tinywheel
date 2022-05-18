本文给大家介绍连接池的原理，以及带大家动手从0实现一个数据库连接池。



# 为什么需要连接池

传统数据库连接生命周期：

1. 使用驱动打开数据库连接
2. 打开TCP socket用于后续读写数据
3. 读写数据
4. 关闭连接
5. 关闭socket

可以看出数据库连接创建是一个非常昂贵的操作，我们应该尽可能避免频繁创建连接。传统的数据库访问方式，每次操作数据库都需要打开、关闭物理连接，非常耗费系统资源，性能差。而且数据库支持的连接数是有限的，创建大量的连接会导致数据库僵死。

解决方案：使用连接池，系统初始化后会创建容器，会申请一些连接对象，当需要访问数据库时，从容器中取连接对象直接使用，数据库操作结束后归还给容器。优点是节约资源，提高访问数据库的性能。

下图是使用连接池和不使用的对比：

![](https://cdn.nlark.com/yuque/0/2022/png/640636/1651937389736-d47dc3f4-c9d0-4fdb-bf13-eacfbfff5766.png)

![](https://cdn.nlark.com/yuque/0/2022/png/640636/1651937397162-394cbbdb-a44e-4e1e-80e8-7d7f1cb6b1f3.png)

一次SQL执行的过程：

![](https://cdn.nlark.com/yuque/0/2022/png/640636/1652232383727-f1ee91d1-d7c6-4361-ab77-7099f5e036da.png)

# 业界连接池及对比

业界常见的连接池实现有：

- DBCP
- C3P0。2015年后已停止维护。
- Druid
- HikariCP
- Tomcat的内置连接池

性能比较：

![](https://cdn.nlark.com/yuque/0/2022/png/640636/1652232897572-b3908467-5fec-4c41-b612-1d3f2adcbf5f.png)

![](https://cdn.nlark.com/yuque/0/2022/png/640636/1652232912462-a70fe532-c4f7-4c5e-beb3-c964e93f9b30.png)

# 如何实现一个连接池

## 技术方案

核心要点：

- 实现连接重用。

核心步骤：

- 初始化连接池，提前创建一定数量的空闲连接

- 调用getConnection方法获取连接

- 如果连接池中有空闲的连接，直接取空闲连接返回

- 如果无，则判断当前连接池中的连接数是否超过最大阈值，如果是，等待，通过wait/notifyAll来实现

- 如果未超过最大阈值，创建新连接，存入连接池

- 调用returnConnection释放连接

- 判断当前连接是否可用

- 将连接置位空闲。

## 代码结构

类图：

<img src="https://cdn.nlark.com/yuque/0/2022/png/640636/1652833825778-6b40bcbf-9839-4576-a04a-a168310cf7ee.png" title="" alt="" width="532">

核心类说明：

- ILifeCycle接口定义数据源生命周期
- IPooledConnection接口扩展自Connection，但是额外定义了为了实现池化功能新增的几个方法
- PooledConnection是IPooledConnection接口的实现类，封装Connection，实现池化功能，可以理解为一个代理，主要是校验连接状态，将请求调用直接代理到实际的Connection
- IPooledDataSource接口扩展自DataSource，但是额外定义了为了实现池化功能新增的几个方法
- AbstractDataSource：数据源默认实现抽象类
- DefaultDataSource：本次实现的池化数据源，负责连接的获取和回收，包含一个List<IPooledConnection>类型的成员属性，用来维护连接
- DataSourceConfig和PooledDataSourceConfig定义了数据源基础配置和为了实现池化功能新增的配置
- ConnPoolException：自定义异常
- DriverUtil：驱动加载工具类

代码包结构：

<img src="https://cdn.nlark.com/yuque/0/2022/png/640636/1652833851687-c2a69708-37e6-41b0-87a1-0c316b5e8ff0.png" title="" alt="" width="272">

## 测试

测试代码在类PooledDataSourceTest里。比如第一个测试方法，简单的先初始化，然后进行获取连接、释放连接的操作：



```java
/Library/Java/JavaVirtualMachines/jdk1.8.0_321.jdk/Contents/Home/bin/java -ea -Didea.test.cyclic.buffer.size=1048576 -javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=59862:/Applications/IntelliJ IDEA.app/Contents/bin -Dfile.encoding=UTF-8 -classpath /Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar:/Applications/IntelliJ IDEA.app/Contents/plugins/junit/lib/junit5-rt.jar:/Applications/IntelliJ IDEA.app/Contents/plugins/junit/lib/junit-rt.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_321.jdk/Contents/Home/jre/lib/charsets.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_321.jdk/Contents/Home/jre/lib/deploy.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_321.jdk/Contents/Home/jre/lib/ext/cldrdata.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_321.jdk/Contents/Home/jre/lib/ext/dnsns.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_321.jdk/Contents/Home/jre/lib/ext/jaccess.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_321.jdk/Contents/Home/jre/lib/ext/jfxrt.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_321.jdk/Contents/Home/jre/lib/ext/localedata.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_321.jdk/Contents/Home/jre/lib/ext/nashorn.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_321.jdk/Contents/Home/jre/lib/ext/sunec.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_321.jdk/Contents/Home/jre/lib/ext/sunjce_provider.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_321.jdk/Contents/Home/jre/lib/ext/sunpkcs11.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_321.jdk/Contents/Home/jre/lib/ext/zipfs.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_321.jdk/Contents/Home/jre/lib/javaws.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_321.jdk/Contents/Home/jre/lib/jce.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_321.jdk/Contents/Home/jre/lib/jfr.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_321.jdk/Contents/Home/jre/lib/jfxswt.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_321.jdk/Contents/Home/jre/lib/jsse.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_321.jdk/Contents/Home/jre/lib/management-agent.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_321.jdk/Contents/Home/jre/lib/plugin.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_321.jdk/Contents/Home/jre/lib/resources.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_321.jdk/Contents/Home/jre/lib/rt.jar:/Users/xiajun/Documents/mycode/dsconnpool/target/test-classes:/Users/xiajun/Documents/mycode/dsconnpool/target/classes:/Users/xiajun/.m2/repository/org/springframework/boot/spring-boot-starter/2.6.7/spring-boot-starter-2.6.7.jar:/Users/xiajun/.m2/repository/org/springframework/boot/spring-boot/2.6.7/spring-boot-2.6.7.jar:/Users/xiajun/.m2/repository/org/springframework/spring-context/5.3.19/spring-context-5.3.19.jar:/Users/xiajun/.m2/repository/org/springframework/spring-aop/5.3.19/spring-aop-5.3.19.jar:/Users/xiajun/.m2/repository/org/springframework/spring-beans/5.3.19/spring-beans-5.3.19.jar:/Users/xiajun/.m2/repository/org/springframework/spring-expression/5.3.19/spring-expression-5.3.19.jar:/Users/xiajun/.m2/repository/org/springframework/boot/spring-boot-autoconfigure/2.6.7/spring-boot-autoconfigure-2.6.7.jar:/Users/xiajun/.m2/repository/org/springframework/boot/spring-boot-starter-logging/2.6.7/spring-boot-starter-logging-2.6.7.jar:/Users/xiajun/.m2/repository/ch/qos/logback/logback-classic/1.2.11/logback-classic-1.2.11.jar:/Users/xiajun/.m2/repository/ch/qos/logback/logback-core/1.2.11/logback-core-1.2.11.jar:/Users/xiajun/.m2/repository/org/apache/logging/log4j/log4j-to-slf4j/2.17.2/log4j-to-slf4j-2.17.2.jar:/Users/xiajun/.m2/repository/org/apache/logging/log4j/log4j-api/2.17.2/log4j-api-2.17.2.jar:/Users/xiajun/.m2/repository/org/slf4j/jul-to-slf4j/1.7.36/jul-to-slf4j-1.7.36.jar:/Users/xiajun/.m2/repository/jakarta/annotation/jakarta.annotation-api/1.3.5/jakarta.annotation-api-1.3.5.jar:/Users/xiajun/.m2/repository/org/springframework/spring-core/5.3.19/spring-core-5.3.19.jar:/Users/xiajun/.m2/repository/org/springframework/spring-jcl/5.3.19/spring-jcl-5.3.19.jar:/Users/xiajun/.m2/repository/org/yaml/snakeyaml/1.29/snakeyaml-1.29.jar:/Users/xiajun/.m2/repository/org/springframework/boot/spring-boot-starter-test/2.6.7/spring-boot-starter-test-2.6.7.jar:/Users/xiajun/.m2/repository/org/springframework/boot/spring-boot-test/2.6.7/spring-boot-test-2.6.7.jar:/Users/xiajun/.m2/repository/org/springframework/boot/spring-boot-test-autoconfigure/2.6.7/spring-boot-test-autoconfigure-2.6.7.jar:/Users/xiajun/.m2/repository/com/jayway/jsonpath/json-path/2.6.0/json-path-2.6.0.jar:/Users/xiajun/.m2/repository/net/minidev/json-smart/2.4.8/json-smart-2.4.8.jar:/Users/xiajun/.m2/repository/net/minidev/accessors-smart/2.4.8/accessors-smart-2.4.8.jar:/Users/xiajun/.m2/repository/org/ow2/asm/asm/9.1/asm-9.1.jar:/Users/xiajun/.m2/repository/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar:/Users/xiajun/.m2/repository/jakarta/xml/bind/jakarta.xml.bind-api/2.3.3/jakarta.xml.bind-api-2.3.3.jar:/Users/xiajun/.m2/repository/jakarta/activation/jakarta.activation-api/1.2.2/jakarta.activation-api-1.2.2.jar:/Users/xiajun/.m2/repository/org/assertj/assertj-core/3.21.0/assertj-core-3.21.0.jar:/Users/xiajun/.m2/repository/org/hamcrest/hamcrest/2.2/hamcrest-2.2.jar:/Users/xiajun/.m2/repository/org/junit/jupiter/junit-jupiter/5.8.2/junit-jupiter-5.8.2.jar:/Users/xiajun/.m2/repository/org/junit/jupiter/junit-jupiter-api/5.8.2/junit-jupiter-api-5.8.2.jar:/Users/xiajun/.m2/repository/org/opentest4j/opentest4j/1.2.0/opentest4j-1.2.0.jar:/Users/xiajun/.m2/repository/org/junit/platform/junit-platform-commons/1.8.2/junit-platform-commons-1.8.2.jar:/Users/xiajun/.m2/repository/org/apiguardian/apiguardian-api/1.1.2/apiguardian-api-1.1.2.jar:/Users/xiajun/.m2/repository/org/junit/jupiter/junit-jupiter-params/5.8.2/junit-jupiter-params-5.8.2.jar:/Users/xiajun/.m2/repository/org/junit/jupiter/junit-jupiter-engine/5.8.2/junit-jupiter-engine-5.8.2.jar:/Users/xiajun/.m2/repository/org/junit/platform/junit-platform-engine/1.8.2/junit-platform-engine-1.8.2.jar:/Users/xiajun/.m2/repository/org/mockito/mockito-core/4.0.0/mockito-core-4.0.0.jar:/Users/xiajun/.m2/repository/net/bytebuddy/byte-buddy/1.11.22/byte-buddy-1.11.22.jar:/Users/xiajun/.m2/repository/net/bytebuddy/byte-buddy-agent/1.11.22/byte-buddy-agent-1.11.22.jar:/Users/xiajun/.m2/repository/org/objenesis/objenesis/3.2/objenesis-3.2.jar:/Users/xiajun/.m2/repository/org/mockito/mockito-junit-jupiter/4.0.0/mockito-junit-jupiter-4.0.0.jar:/Users/xiajun/.m2/repository/org/skyscreamer/jsonassert/1.5.0/jsonassert-1.5.0.jar:/Users/xiajun/.m2/repository/com/vaadin/external/google/android-json/0.0.20131108.vaadin1/android-json-0.0.20131108.vaadin1.jar:/Users/xiajun/.m2/repository/org/springframework/spring-test/5.3.19/spring-test-5.3.19.jar:/Users/xiajun/.m2/repository/org/xmlunit/xmlunit-core/2.8.4/xmlunit-core-2.8.4.jar:/Users/xiajun/.m2/repository/org/projectlombok/lombok/1.16.22/lombok-1.16.22.jar:/Users/xiajun/.m2/repository/junit/junit/4.12/junit-4.12.jar:/Users/xiajun/.m2/repository/org/hamcrest/hamcrest-core/2.2/hamcrest-core-2.2.jar:/Users/xiajun/.m2/repository/org/apache/commons/commons-lang3/3.4/commons-lang3-3.4.jar:/Users/xiajun/.m2/repository/mysql/mysql-connector-java/8.0.29/mysql-connector-java-8.0.29.jar com.intellij.rt.junit.JUnitStarter -ideVersion5 -junit4 com.summer.dsconnpool.PooledDataSourceTest,simpleTest
Loading class `com.mysql.jdbc.Driver'. This is deprecated. The new driver class is `com.mysql.cj.jdbc.Driver'. The driver is automatically registered via the SPI and manual loading of the driver class is generally unnecessary.
五月 18, 2022 8:14:14 上午 com.summer.dsconnpool.DefaultDataSource createConnection
信息: 创建新连接.....
五月 18, 2022 8:14:15 上午 com.summer.dsconnpool.DefaultDataSource getConnection
信息: getConnection begin....
五月 18, 2022 8:14:15 上午 com.summer.dsconnpool.DefaultDataSource logConnPoolDigest
信息: pooledConnectionList status:[1,busy status:[false,]]
五月 18, 2022 8:14:15 上午 com.summer.dsconnpool.DefaultDataSource getFreeConnectionFromPool
信息: 从连接池中获取连接
db_summer_1
五月 18, 2022 8:14:15 上午 com.summer.dsconnpool.DefaultDataSource getConnection
信息: getConnection begin....
五月 18, 2022 8:14:15 上午 com.summer.dsconnpool.DefaultDataSource logConnPoolDigest
信息: pooledConnectionList status:[1,busy status:[true,]]
五月 18, 2022 8:14:15 上午 com.summer.dsconnpool.DefaultDataSource getConnection
信息: 开始扩容连接池大小...
五月 18, 2022 8:14:15 上午 com.summer.dsconnpool.DefaultDataSource createConnection
信息: 创建新连接.....
五月 18, 2022 8:14:15 上午 com.summer.dsconnpool.DefaultDataSource getConnection
信息: 扩容完成...
五月 18, 2022 8:14:15 上午 com.summer.dsconnpool.DefaultDataSource logConnPoolDigest
db_summer_1
信息: pooledConnectionList status:[2,busy status:[true,true,]]
五月 18, 2022 8:14:15 上午 com.summer.dsconnpool.DefaultDataSource checkValid
信息: 开始校验连接
五月 18, 2022 8:14:15 上午 com.summer.dsconnpool.DefaultDataSource logConnPoolDigest
信息: pooledConnectionList status:[2,busy status:[false,true,]]
```

注意运行本次测试需要依赖本机安装mysql数据库。安装完毕后创建样例数据库。这里测试类里的相关配置可以根据需要进行修改：



```java
dataSourceConfig.setDriverClass("com.mysql.jdbc.Driver");
dataSourceConfig.setJdbcUrl("jdbc:mysql://localhost:3306/db_summer_1?useUnicode=true&characterEncoding=utf-8");
dataSourceConfig.setUser("root");
dataSourceConfig.setPassword("summer");
```
