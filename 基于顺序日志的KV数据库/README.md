本文从0实现一个简易的KV数据库，代码行数不多，核心代码不超过200行。  

# 设计思路

- 查询语法：支持String类型的Key和Value，暂不支持其他复杂类型，不支持SQL语法解析和执行计划优化。 

- 存储引擎：基于顺序日志进行写入，每条执行命令的数据信息按行存在文本日志文件里。暂不支持类似hbase中的合并等复杂的逻辑。  

![image.png](https://cdn.nlark.com/yuque/0/2022/png/640636/1651741930510-9bc226dd-3040-4d14-8718-650f46ed6a8d.png)

# 代码模块

![image.png](https://cdn.nlark.com/yuque/0/2022/png/640636/1651740751385-36cd396c-741d-4a3f-890e-3c2f2594ae63.png)

# 性能分析

因为是追加写入，所以写入的性能非常快，比如Hbase就是采用的顺序写入的方式。关于读取，因为需要读取扫描整个文件来得到key对应的记录，因此性能较差，是O(N)。像实际生产环境使用，是会做一些优化工作的，比如把日志内容进行刷盘处理，同样的key的多条记录会进行合并处理，然后建立索引，查询性能会快很多。此处只是给大家做个demo展示，还有相当多的优化工作需要做。  



# 工程测试

测试比较简单，直接构建的实例，调用其查询、写入、删除方法即可。  

![](/Users/xiajun/Library/Application%20Support/marktext/images/2022-05-05-17-16-27-image.png)

运行日志：



![](/Users/xiajun/Library/Application%20Support/marktext/images/2022-05-05-17-16-38-image.png)
