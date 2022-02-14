package com.summer.simplerpc.io;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 自定义解码器，解析出RPC请求对象
 *
 * @author summer
 * @version $Id: RPCDecoder.java, v 0.1 2022年01月16日 5:22 PM summer Exp $
 */
@Slf4j
public class RPCDecoder extends ByteToMessageDecoder {

    @Override
    public final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        //入参校验
        if (in.readableBytes() < 4) {
            log.error("decode fail,input ByteBuf illegal,in.readableBytes=" + in.readableBytes());
            return;
        }

        in.markReaderIndex();
        //读取长度内容
        int dataLen = in.readInt();
        //剩余可读内容小于预定长度
        if (in.readableBytes() < dataLen) {
            log.error("decode fail,input ByteBuf illegal,in.readableBytes {0} less than dataLen {1}", in.readableBytes(), dataLen);
            return;
        }

        //读取实际内容
        byte[] actualDataBytes = new byte[dataLen];
        in.readBytes(actualDataBytes);
        //反序列化
        Object dataObj = HessianUtils.deserialize(actualDataBytes);
        if (dataObj == null) {
            log.error("decode fail,input ByteBuf illegal,dataObj null,actualDataBytes={0}", actualDataBytes);
            return;
        }
        out.add(dataObj);
    }
}