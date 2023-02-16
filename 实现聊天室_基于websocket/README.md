# 聊天室实现-基于netty websocket

## 基础知识

http和websocket连接处理对比：   
![http和websocket连接处理对比](https://github.com/xiajunhust/tinywheel/blob/main/%E5%AE%9E%E7%8E%B0%E8%81%8A%E5%A4%A9%E5%AE%A4_%E5%9F%BA%E4%BA%8Ewebsocket/http%20VS%20websocket.png)

websocket连接生命周期：   
![websocket连接生命周期](https://github.com/xiajunhust/tinywheel/blob/main/%E5%AE%9E%E7%8E%B0%E8%81%8A%E5%A4%A9%E5%AE%A4_%E5%9F%BA%E4%BA%8Ewebsocket/websocket%E8%BF%9E%E6%8E%A5%E7%94%9F%E5%91%BD%E5%91%A8%E6%9C%9F.png)

## 项目实现

聊天室实现：   
![聊天室实现](https://github.com/xiajunhust/tinywheel/blob/main/%E5%AE%9E%E7%8E%B0%E8%81%8A%E5%A4%A9%E5%AE%A4_%E5%9F%BA%E4%BA%8Ewebsocket/%E8%81%8A%E5%A4%A9%E5%AE%A4%E5%AE%9E%E7%8E%B0.png)

启动运行：  
服务端启动：直接启动sofaboot工程，服务端就启动成功了。

客户端：  
为了方便测试，我们直接利用js来发起请求，直接打开websocket.html文件，即可发起请求。

运行日志：  
![运行日志](https://github.com/xiajunhust/tinywheel/blob/main/%E5%AE%9E%E7%8E%B0%E8%81%8A%E5%A4%A9%E5%AE%A4_%E5%9F%BA%E4%BA%8Ewebsocket/%E8%BF%90%E8%A1%8C%E6%97%A5%E5%BF%97.jpg)

## 拓展&优化

- 目前实现比较简单，客户端发消息，服务端直接返回相应给客户端。可以拓展实现多个客户端和服务端建立连接，客户端之间相互发送消息。
- 服务端实现消息的存储和转发。
- 登录登出逻辑，密码的加密存储
- 群聊的实现
- 展示在线人员的状态

## 参考资料

- [web聊天室开发过程及重难点整理](https://blog.csdn.net/ZhangHahaaha/article/details/118356193)
- [基于 SpringBoot + Vue 框架开发的网页版聊天室项目](https://blog.51cto.com/zhongmayisheng/3313630)
- [网页版聊天室实现](https://github.com/JustCoding-Hai/subtlechat)
- [黑马-聊天室毕设项目](https://yun.itheima.com/course/110.html)
 
---

致力于分享干货，为每一位计算机CS学子学习道路上带来帮助。

也欢迎大家关注我的公众号~

![欢迎大家关注我的公众号](https://github.com/xiajunhust/awosome-cs/blob/main/QR-CODE.jpg)

