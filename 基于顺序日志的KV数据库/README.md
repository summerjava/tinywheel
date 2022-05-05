本文从0实现一个简易的KV数据库，代码行数不多，核心代码不超过200行。  

# 设计思路

- 查询语法：支持String类型的Key和Value，暂不支持其他复杂类型，不支持SQL语法解析和执行计划优化。 

- 存储引擎：基于顺序日志进行写入，每条执行命令的数据信息按行存在文本日志文件里。暂不支持类似hbase中的合并等复杂的逻辑。  

![image.png](https://github.com/xiajunhust/tinywheel/blob/main/基于顺序日志的KV数据库/整体设计.png)

# 代码模块

![image.png](https://github.com/xiajunhust/tinywheel/blob/main/基于顺序日志的KV数据库/代码结构.png)

# 性能分析

因为是追加写入，所以写入的性能非常快，比如Hbase就是采用的顺序写入的方式。关于读取，因为需要读取扫描整个文件来得到key对应的记录，因此性能较差，是O(N)。像实际生产环境使用，是会做一些优化工作的，比如把日志内容进行刷盘处理，同样的key的多条记录会进行合并处理，然后建立索引，查询性能会快很多。此处只是给大家做个demo展示，还有相当多的优化工作需要做。  



# 工程测试

测试比较简单，直接构建的实例，调用其查询、写入、删除方法即可。  

```
  import com.summer.simplekv.api.SimpleKvClient;
  import com.summer.simplekv.core.LogBasedKV;
  import lombok.extern.java.Log;
  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;

  @Log
  @SpringBootApplication
  public class SimplekvApplication {

      public static void main(String[] args) {
          String logFileName = "simplekv.log";
          SimpleKvClient kvClient = new LogBasedKV(logFileName);

          //写入数据
          for (int index = 0; index < 5; ++index) {
              kvClient.put("k-" + index, "v-" + index);
          }

          //查询数据
          for (int index = 0; index < 5; ++index) {
              String value = kvClient.get("k-" + index);
              log.info("get [" + "k-" + index + " value is [" + value + "]");
          }

          //删除一行数据
          kvClient.del("k-" + 3);

          log.info("after del 3");

          //再次查询已被删除的数据
          String value = kvClient.get("k-" + 3);
          log.info("get [" + "k-" + 3 + " value is [" + value + "]");

          SpringApplication.run(SimplekvApplication.class, args);
      }
  }
  ```


运行日志：

```
五月 05, 2022 4:59:56 下午 com.summer.simplekv.SimplekvApplication main
信息: get [k-0 value is [v-0]
五月 05, 2022 4:59:56 下午 com.summer.simplekv.SimplekvApplication main
信息: get [k-1 value is [v-1]
五月 05, 2022 4:59:56 下午 com.summer.simplekv.SimplekvApplication main
信息: get [k-2 value is [v-2]
五月 05, 2022 4:59:56 下午 com.summer.simplekv.SimplekvApplication main
信息: get [k-3 value is [v-3]
五月 05, 2022 4:59:56 下午 com.summer.simplekv.SimplekvApplication main
信息: get [k-4 value is [v-4]
五月 05, 2022 4:59:56 下午 com.summer.simplekv.SimplekvApplication main
信息: after del 3
五月 05, 2022 4:59:56 下午 com.summer.simplekv.SimplekvApplication main
信息: get [k-3 value is [null]
```
