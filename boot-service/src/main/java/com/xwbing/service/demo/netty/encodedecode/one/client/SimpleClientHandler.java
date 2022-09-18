package com.xwbing.service.demo.netty.encodedecode.one.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class SimpleClientHandler extends ChannelInboundHandlerAdapter {
    /**
     * 客户端连接成功后，就会调用此方法，然后给服务端去发送消息
     *
     * @param ctx
     *
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接成功");
        ctx.writeAndFlush("你好，我是客户端");
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String str = (String)msg;
        System.out.println("收到服务端的消息内容：" + str);
        super.channelRead(ctx, msg);
    }
}