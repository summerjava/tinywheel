package com.summer.nettylearn.nio.client;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 客户端程序
 *
 * @author summer
 * @version $Id: NioHelloworldClientTask.java, v 0.1 2022年01月23日 4:35 PM summer Exp $
 */
@Slf4j
public class NioHelloworldClientTask implements Runnable {

    private String host;
    private int port;

    private Selector      selector;
    private SocketChannel socketChannel;

    private volatile boolean isStop = false;

    /**
     * 构造函数
     *
     * @param host
     * @param port
     */
    public NioHelloworldClientTask(String host, int port) {
        this.host = host;
        this.port = port;

        try {
            selector = Selector.open();
            socketChannel = socketChannel.open();
            socketChannel.configureBlocking(false);
        } catch (Exception e) {
            log.error("NioHelloworldClientTask start fail,", e);
        }
    }

    @Override
    public void run() {
        //发起连接请求
        try {
            doConnect();
        } catch (Exception e) {
            log.error("connect fail,", e);
            return;
        }

        while (!isStop) {
            //获取就绪状态的SelectionKey
            try {
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();

                    doProcessInput(selectionKey);
                }
            } catch (Exception e) {
                log.error("handle input fail,", e);
            }
        }
    }

    /**
     * 执行连接操作
     *
     * @throws IOException
     */
    private void doConnect() throws IOException {
        if (socketChannel.connect(new InetSocketAddress(host, port))) {
            log.info("doConnect,register OP_READ");

            //已连接成功：注册读事件，执行写操作
            socketChannel.register(selector, SelectionKey.OP_READ);

            doWrite(socketChannel);
        } else {
            log.info("doConnect,register OP_CONNECT");
            //注册连接事件，等待TCP ack返回
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
        }
    }


    /**
     * 处理请求
     * @param selectionKey
     */
    private void doProcessInput(SelectionKey selectionKey) throws IOException {
        if (!selectionKey.isValid()) {
            log.warn("doProcess,selectionKey is not valid");
            return;
        }

        SocketChannel socketChannel = (SocketChannel)selectionKey.channel();
        if (selectionKey.isConnectable()) {
            log.info("doProcessInput,selectionKey.isConnectable");
            if (socketChannel.finishConnect()) {
                log.info("doProcessInput,selectionKey.finishConnect");
                //注册读事件
                socketChannel.register(selector, SelectionKey.OP_READ);
                //发送请求
                doWrite(socketChannel);
            } else {
                log.error("socketChannel.finishConnect false");
                return;
            }
        }

        if (selectionKey.isReadable()) {
            log.info("doProcessInput,selectionKey.isReadable");
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            int byteCount = socketChannel.read(byteBuffer);
            if (byteCount > 0) {
                byteBuffer.flip();
                byte[] bytes = new byte[byteBuffer.remaining()];
                byteBuffer.get(bytes);
                String requestContent = new String(bytes, "UTF-8");
                log.info("doProcessInput,server return requestContent=" + requestContent);
                this.isStop = true;
            } else if (byteCount < 0) {
                log.info("doProcessInput,selectionKey.close");
                selectionKey.cancel();
                socketChannel.close();
            } else {
                log.info("doProcessInput,byteCount 0");
            }
        }
    }

    /**
     * 写响应
     * @param socketChannel
     * @throws IOException
     */
    private void doWrite(SocketChannel socketChannel) throws IOException {
        String response = "client doWrite begin";
        byte[] bytes = response.getBytes();
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
        byteBuffer.put(bytes);
        byteBuffer.flip();
        socketChannel.write(byteBuffer);

        if (!byteBuffer.hasRemaining()) {
            log.info("client doWrite success");
        }
    }
}