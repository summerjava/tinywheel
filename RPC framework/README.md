# 徒手实现RPC框架

分为2个工程，simplerpc是rpc框架的springboot starter实现，单独抽出来的工程，只包含rpc框架核心实现。simplerpctest是用来集成simplerpc进行测试的工程，展示了如何集成和使用RPC框架。

RPC框架理论基础-5个核心模块：   
![RPC框架理论基础-5个核心模块](https://github.com/xiajunhust/tinywheel/blob/main/RPC%20framework/RPC%E6%A1%86%E6%9E%B6%E7%90%86%E8%AE%BA%E5%9F%BA%E7%A1%80-5%E4%B8%AA%E6%A0%B8%E5%BF%83%E6%A8%A1%E5%9D%97.png)

RPC框架实现架构设计：  
![RPC框架实现架构设计](https://github.com/xiajunhust/tinywheel/blob/main/RPC%20framework/RPC%E6%A1%86%E6%9E%B6%E5%AE%9E%E7%8E%B0%E6%9E%B6%E6%9E%84%E8%AE%BE%E8%AE%A1.png)

RPC调用序列图  
![RPC调用序列图](https://github.com/xiajunhust/tinywheel/blob/main/RPC%20framework/RPC%E8%B0%83%E7%94%A8%E5%BA%8F%E5%88%97%E5%9B%BE.jpeg)

工程分层和代码包结构：  
![工程分层](https://github.com/xiajunhust/tinywheel/blob/main/RPC%20framework/%E5%B7%A5%E7%A8%8B%E6%A8%A1%E5%9D%97%E5%88%86%E5%B1%82.png)

![代码包结构](https://github.com/xiajunhust/tinywheel/blob/main/RPC%20framework/%E4%BB%A3%E7%A0%81%E5%8C%85%E7%BB%93%E6%9E%84.png)

运行日志打印：  

        2022-01-25 09:27:22.581  INFO 30366 --- [nio-8004-exec-1] c.s.s.consumer.SimpleRpcInvokeHandler    : begin simpleRpcRequest=SimpleRpcRequest(bizNO=46154373-2cf7-4731-b4c0-208d6ca28b87, className=com.summer.simplerpctest.consumer.HelloworldService, methodName=buildHelloworld, serviceVersion=1.0.0, paramTypes=[class java.lang.String], paramValues=[qwert])
        2022-01-25 09:27:22.698  INFO 30366 --- [nio-8004-exec-1] c.s.s.c.SimpleRpcConsumerNettyHandler    : sendRpcRequest begin,serviceMetaConfig=ServiceMetaConfig(name=com.summer.simplerpctest.consumer.HelloworldService, version=1.0.0, address=127.0.0.1, port=50001),key=com.summer.simplerpctest.consumer.HelloworldService:1.0.0
        2022-01-25 09:27:22.715  INFO 30366 --- [ntLoopGroup-4-1] c.s.s.c.SimpleRpcConsumerNettyHandler    : rpc invoke success,
        2022-01-25 09:27:22.759  INFO 30366 --- [nio-8004-exec-1] c.s.s.c.SimpleRpcConsumerNettyHandler    : sendRpcRequest lock.wait
        2022-01-25 09:27:22.771  INFO 30366 --- [provider-pool-0] c.s.s.p.SimpleRpcProviderNettyHandler    : doHandle,simpleRpcRequest=SimpleRpcRequest(bizNO=46154373-2cf7-4731-b4c0-208d6ca28b87, className=com.summer.simplerpctest.consumer.HelloworldService, methodName=buildHelloworld, serviceVersion=1.0.0, paramTypes=[class java.lang.String], paramValues=[qwert])
        2022-01-25 09:27:22.772  INFO 30366 --- [provider-pool-0] c.s.s.provider.HelloworldServiceImpl     : HelloworldServiceImpl begin
        2022-01-25 09:27:22.774  INFO 30366 --- [ntLoopGroup-3-1] c.s.s.p.SimpleRpcProviderNettyHandler    : return response for request 46154373-2cf7-4731-b4c0-208d6ca28b87,simpleRpcResponse=SimpleRpcResponse(bizNO=46154373-2cf7-4731-b4c0-208d6ca28b87, msg=null, data=qwert_3ca010cc-cb14-49d0-b9d4-23168e7786e4)
        2022-01-25 09:27:22.774  INFO 30366 --- [ntLoopGroup-4-1] c.s.s.c.SimpleRpcConsumerNettyHandler    : rpc consumer netty handler,channelRead0,rpcResponse=SimpleRpcResponse(bizNO=46154373-2cf7-4731-b4c0-208d6ca28b87, msg=null, data=qwert_3ca010cc-cb14-49d0-b9d4-23168e7786e4)
        2022-01-25 09:27:22.775  INFO 30366 --- [ntLoopGroup-4-1] c.s.s.c.SimpleRpcConsumerNettyHandler    : channelRead0 simpleRpcResponse lock.notifyAll
        2022-01-25 09:27:22.775  INFO 30366 --- [nio-8004-exec-1] c.s.s.c.SimpleRpcConsumerNettyHandler    : get rpc response=SimpleRpcResponse(bizNO=46154373-2cf7-4731-b4c0-208d6ca28b87, msg=null, data=qwert_3ca010cc-cb14-49d0-b9d4-23168e7786e4)
        2022-01-25 09:27:22.776  INFO 30366 --- [nio-8004-exec-1] c.s.s.consumer.SimpleRpcInvokeHandler    : result simpleRpcResponse=SimpleRpcResponse(bizNO=46154373-2cf7-4731-b4c0-208d6ca28b87, msg=null, data=qwert_3ca010cc-cb14-49d0-b9d4-23168e7786e4)

---

致力于分享干货，为每一位计算机CS学子学习道路上带来帮助。

也欢迎大家关注我的公众号~

![欢迎大家关注我的公众号](https://github.com/xiajunhust/awosome-cs/blob/main/QR-CODE.jpg)

