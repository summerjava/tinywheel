package com.summer.simplehttpserver;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * http服务端处理handler实现
 *
 * @author summer
 * @version $Id: SimpleHttpServerHandler.java, v 0.1 2022年01月26日 9:44 AM summer Exp $
 */
@Slf4j
public class SimpleHttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) {
        try {
            log.info("SimpleHttpServerHandler receive fullHttpRequest=" + fullHttpRequest);

            String result = doHandle(fullHttpRequest);
            log.info("SimpleHttpServerHandler,result=" + result);
            byte[] responseBytes = result.getBytes(StandardCharsets.UTF_8);

            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_0, HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(responseBytes));
            response.headers().set("Content-Type", "text/html; charset=utf-8");
            response.headers().setInt("Content-Length", response.content().readableBytes());

            boolean isKeepAlive = HttpUtil.isKeepAlive(response);
            if (!isKeepAlive) {
                ctx.write(response).addListener(ChannelFutureListener.CLOSE);
            } else {
                response.headers().set("Connection", "keep-alive");
                ctx.write(response);
            }
        } catch (Exception e) {
            log.error("channelRead0 exception,", e);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("SimpleHttpServerHandler exception,", cause);
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    /**
     * 实际处理
     *
     * @param fullHttpRequest HTTP请求参数
     * @return
     */
    private String doHandle(FullHttpRequest fullHttpRequest) {
        if (HttpMethod.GET == fullHttpRequest.method()) {
            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(fullHttpRequest.uri());
            Map<String, List<String>> params = queryStringDecoder.parameters();
            return JSON.toJSONString(params);
        } else if (HttpMethod.POST == fullHttpRequest.method()) {
            return fullHttpRequest.content().toString();
        }

        return "";
    }
}