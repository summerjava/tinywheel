# 利用Java NIO实现CS通信程序

常见IO模型对比：   
![常见IO模型对比](https://github.com/xiajunhust/tinywheel/blob/main/CS%E9%80%9A%E4%BF%A1%E7%A8%8B%E5%BA%8F/%E5%B8%B8%E8%A7%81IO%E6%A8%A1%E5%9E%8B%E5%AF%B9%E6%AF%94.png)

NIO Selector Model：   
![NIO Selector Model](https://github.com/xiajunhust/tinywheel/blob/main/CS%E9%80%9A%E4%BF%A1%E7%A8%8B%E5%BA%8F/NIO%20Selector%20Model.png)

服务端设计：   
![服务端设计](https://github.com/xiajunhust/tinywheel/blob/main/CS%E9%80%9A%E4%BF%A1%E7%A8%8B%E5%BA%8F/%E6%9C%8D%E5%8A%A1%E7%AB%AF%E8%AE%BE%E8%AE%A1.png)

客户端流程：  
![客户端流程](https://github.com/xiajunhust/tinywheel/blob/main/CS%E9%80%9A%E4%BF%A1%E7%A8%8B%E5%BA%8F/%E5%AE%A2%E6%88%B7%E7%AB%AF%E8%AE%BE%E8%AE%A1.png)

运行日志：  

      18:14:47.213 [main] INFO com.summer.nettylearn.nio.server.NioHelloworldServerTask - NioHelloworldServerTask start begin~
      //服务端启动成功
      18:14:47.243 [main] INFO com.summer.nettylearn.nio.server.NioHelloworldServerTask - NioHelloworldServerTask start success~
      18:14:47.244 [NioHelloworldServerTask 1] INFO com.summer.nettylearn.nio.server.NioHelloworldServerTask - NioHelloworldServerTask running.....
      18:14:49.248 [NioHelloworldServerTask 1] INFO com.summer.nettylearn.nio.server.NioHelloworldServerTask - NioHelloworldServerTask running.....
      18:14:51.253 [NioHelloworldServerTask 1] INFO com.summer.nettylearn.nio.server.NioHelloworldServerTask - NioHelloworldServerTask running.....
      //注册连接事件
      18:14:52.252 [NioHelloworldClientTask 1] INFO com.summer.nettylearn.nio.client.NioHelloworldClientTask - doConnect,register OP_CONNECT
      18:14:52.253 [NioHelloworldClientTask 1] INFO com.summer.nettylearn.nio.client.NioHelloworldClientTask - doProcessInput,selectionKey.isConnectable
      18:14:52.253 [NioHelloworldServerTask 1] INFO com.summer.nettylearn.nio.server.NioHelloworldServerTask - doProcess begin,selectionKey=sun.nio.ch.SelectionKeyImpl@769c9116
      18:14:52.253 [NioHelloworldServerTask 1] INFO com.summer.nettylearn.nio.server.NioHelloworldServerTask - doProcess,selectionKey isAcceptable
      18:14:52.254 [NioHelloworldClientTask 1] INFO com.summer.nettylearn.nio.client.NioHelloworldClientTask - doProcessInput,selectionKey.finishConnect
      18:14:52.255 [NioHelloworldServerTask 1] INFO com.summer.nettylearn.nio.server.NioHelloworldServerTask - NioHelloworldServerTask running.....
      //向服务端发起请求
      18:14:52.258 [NioHelloworldClientTask 1] INFO com.summer.nettylearn.nio.client.NioHelloworldClientTask - client doWrite success
      18:14:52.258 [NioHelloworldServerTask 1] INFO com.summer.nettylearn.nio.server.NioHelloworldServerTask - doProcess begin,selectionKey=sun.nio.ch.SelectionKeyImpl@606f8d95
      //服务端收到客户端的请求
      18:14:52.258 [NioHelloworldServerTask 1] INFO com.summer.nettylearn.nio.server.NioHelloworldServerTask - doProcess,selectionKey isReadable
      //服务端处理后向客户单返回结果
      18:14:52.259 [NioHelloworldServerTask 1] INFO com.summer.nettylearn.nio.server.NioHelloworldServerTask - doProcess,requestContent=client doWrite begin
      18:14:52.259 [NioHelloworldServerTask 1] INFO com.summer.nettylearn.nio.server.NioHelloworldServerTask - NioHelloworldServerTask running.....
      //客户端收到服务端的响应结果
      18:14:52.259 [NioHelloworldClientTask 1] INFO com.summer.nettylearn.nio.client.NioHelloworldClientTask - doProcessInput,selectionKey.isReadable
      //打印服务端的响应结果
      18:14:52.259 [NioHelloworldClientTask 1] INFO com.summer.nettylearn.nio.client.NioHelloworldClientTask - doProcessInput,requestContent=client doWrite begin1642932892259

---

致力于分享干货，为每一位计算机CS学子学习道路上带来帮助。

也欢迎大家关注我的公众号，获取更多计算机干货~

![欢迎大家关注我的公众号](https://github.com/xiajunhust/awosome-cs/blob/main/QR-CODE.jpg)

