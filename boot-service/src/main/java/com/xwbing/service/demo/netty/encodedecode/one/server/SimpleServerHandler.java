package com.xwbing.service.demo.netty.encodedecode.one.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class SimpleServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String str = (String)msg;
        System.out.println("收到客户端的消息内容：" + str);
        // 写数据给客户端
        ctx.writeAndFlush("ok\n");
    }
}