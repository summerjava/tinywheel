package com.summer.simplerpc.io;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * 自动以编码器，将rpc返回结果编码为字节流
 *
 * @author summer
 * @version $Id: RPCEncoder.java, v 0.1 2022年01月16日 5:29 PM summer Exp $
 */
@Slf4j
public class RPCEncoder extends MessageToByteEncoder {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        byte[] data = HessianUtils.serialize(o);
        if (data == null) {
            log.error("encode fail,result data null,result object=" + o);
        }

        byteBuf.writeInt(data.length);
        byteBuf.writeBytes(data);
    }
}