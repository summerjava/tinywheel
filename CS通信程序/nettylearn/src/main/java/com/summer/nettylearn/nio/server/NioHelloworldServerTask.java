package com.summer.nettylearn.nio.server;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 服务端处理任务-采用io多路复用
 *
 * @author summer
 * @version $Id: NioHelloworldServerTask.java, v 0.1 2022年01月23日 12:40 PM summer Exp $
 */
@Slf4j
public class NioHelloworldServerTask implements Runnable {

    private Selector selector;
    private ServerSocketChannel serverSocketChannel;

    /**
     * 构造函数，执行相关初始化操作
     *
     * @param host
     * @param port
     */
    public NioHelloworldServerTask(String host, int port) {
        try {
            log.info("NioHelloworldServerTask start begin~");

            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();

            //设置为异步非阻塞模式
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(host, port), 1024);

            //将channel注册到Selector，监听OP_ACCEPT
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            log.info("NioHelloworldServerTask start success~");
        } catch (Exception e) {
            log.error("NioHelloworldServerTask start exception,", e);
        }
    }

    @Override
    public void run() {
        while (true) {
            log.info("NioHelloworldServerTask running.....");
            try {
                selector.select(2000);
                //获取就绪状态的SelectionKey
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();
                    doProcess(selectionKey);
                }
            } catch (Exception e) {
                log.error("NioHelloworldServerTask run exception,", e);
            }
        }
    }

    /**
     * 执行实际的请求处理
     *
     * @param selectionKey 已就绪的SelectionKey
     */
    private void doProcess(SelectionKey selectionKey) throws IOException {
        log.info("doProcess begin,selectionKey=" + selectionKey);

        if (!selectionKey.isValid()) {
            log.warn("doProcess,selectionKey is not valid");
            return;
        }

        //处理新连接请求
        if (selectionKey.isAcceptable()) {
            log.info("doProcess,selectionKey isAcceptable");
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel)selectionKey.channel();
            SocketChannel socketChannel = serverSocketChannel.accept();
            //设置为异步非阻塞模式
            socketChannel.configureBlocking(false);
            //注册，监听OP_READ
            socketChannel.register(selector, SelectionKey.OP_READ);
        }

        //处理读请求
        if (selectionKey.isReadable()) {
            log.info("doProcess,selectionKey isReadable");
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            int bytesCount = socketChannel.read(byteBuffer);

            //链路已关闭
            if (bytesCount < 0) {
                log.info("doProcess,channel has close");
                selectionKey.cancel();
                socketChannel.close();
            } else if (bytesCount > 0) {
                //读到数据
                byteBuffer.flip();
                byte[] bytes = new byte[byteBuffer.remaining()];
                //复制到bytes数组
                byteBuffer.get(bytes);

                String requestContent = new String(bytes, "UTF-8");
                log.info("doProcess,requestContent=" + requestContent);

                //构造返回结果（把请求参数和时间戳拼接起来返回）
                String responseContent = requestContent + System.currentTimeMillis();
                byte[] responseBytes = responseContent.getBytes();
                ByteBuffer responseByteBuffer = ByteBuffer.allocate(responseBytes.length);
                responseByteBuffer.put(responseBytes);
                responseByteBuffer.flip();
                socketChannel.write(responseByteBuffer);
            } else {
                log.info("doProcess,bytesCount is 0");
            }
        }
    }
}