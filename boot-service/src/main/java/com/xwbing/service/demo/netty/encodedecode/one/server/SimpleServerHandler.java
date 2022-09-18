package com.xwbing.service.demo.netty.encodedecode.one.server;

import java.util.UUID;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class SimpleServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String str = (String)msg;
        System.out.println("服务端接收消息：" + str);
        // 写回数据
        ctx.writeAndFlush(UUID.randomUUID() + "\n");
    }
}