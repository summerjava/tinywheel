package com.summer.simplewebsocketserver;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * websocket服务端处理handler实现
 *
 * @author summer
 * @version $Id: SimpleWebsocketServerHandler.java, v 0.1 2022年01月26日 9:44 AM summer Exp $
 */
@Slf4j
public class SimpleWebsocketServerHandler extends SimpleChannelInboundHandler<Object> {

    /**
     *websocket shake handler
     */
    private WebSocketServerHandshaker handshaker;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        try {
            log.info("SimpleWebsocketServerHandler receive msg=" + msg);

            if (msg instanceof FullHttpRequest) {
                handleHttpShakehandRequest(ctx, (FullHttpRequest)msg);
            } else if (msg instanceof WebSocketFrame) {
                handleWebsocketFrame(ctx, (WebSocketFrame)msg);
            } else {
                log.error("SimpleWebsocketServerHandler channelRead0,unkown msg");
            }
        } catch (Exception e) {
            log.error("channelRead0 exception,", e);
        }
    }

    /**
     * 处理建连握手请求
     *
     * @param ctx
     * @param fullHttpRequest
     */
    private void handleHttpShakehandRequest(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) {
        log.info("handleHttpShakehandRequest begin~");

        //http请求头合法性检查
        if (!fullHttpRequest.getDecoderResult().isSuccess() || !StringUtils.equals("websocket", fullHttpRequest.headers().get("Upgrade"))) {
            log.warn("handleHttpShakehandRequest fail,fullHttpRequest illegal,fullHttpRequest=" + fullHttpRequest.toString());
            return;
        }

        //构造握手响应返回
        String webSocketURL = SimpleWebsocketServer.host + ":" + SimpleWebsocketServer.port + "/websocket";
        WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory(webSocketURL, null, false);
        //实例化一个握手处理handler
        handshaker = factory.newHandshaker(fullHttpRequest);
        if (handshaker == null) {
            log.warn("handleHttpShakehandRequest fail,sendUnsupportedVersionResponse,fullHttpRequest=" + fullHttpRequest.toString());
            //不支持
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            log.info("handleHttpShakehandRequest success.");
            handshaker.handshake(ctx.channel(), fullHttpRequest);
        }
    }

    /**
     * 处理请求帧
     *
     * @param ctx
     * @param webSocketFrame
     */
    private void handleWebsocketFrame(ChannelHandlerContext ctx, WebSocketFrame webSocketFrame) {
        if (webSocketFrame instanceof CloseWebSocketFrame) {
            log.info("handleWebsocketFrame close frame");
            //控制帧-关闭
            handshaker.close(ctx.channel(), (CloseWebSocketFrame)webSocketFrame.retain());
            return;
        }

        if (webSocketFrame instanceof PingWebSocketFrame) {
            log.info("handleWebsocketFrame ping frame");
            //控制帧-ping
            ctx.channel().write(new PongWebSocketFrame(webSocketFrame.content().retain()));
            return;
        }

        //数据帧，仅支持文本形式
        if (!(webSocketFrame instanceof TextWebSocketFrame)) {
            log.error("handleWebsocketFrame,unsupprted data frame");
            return;
        }
        TextWebSocketFrame textWebSocketFrame = (TextWebSocketFrame)webSocketFrame;
        //构造响应结果
        String request = textWebSocketFrame.text();
        log.info("handleWebsocketFrame,receive data frame,text=" + request);

        String response = "这是响应结果[" + request + "]_" + System.currentTimeMillis();
        ctx.channel().write(new TextWebSocketFrame(response));
        log.info("handleWebsocketFrame,send response text success:[" + response + "]");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("SimpleWebsocketServerHandler exception,", cause);
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }
}