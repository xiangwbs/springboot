package com.xwbing.service.demo.netty.keepalive.server;

import com.xwbing.service.demo.netty.keepalive.AbstractHandler;

import io.netty.channel.ChannelHandlerContext;

public class ServerHandler extends AbstractHandler {
    public ServerHandler() {
        super(NettyServer.class.getSimpleName());
    }

    @Override
    protected void handlerReaderIdle(ChannelHandlerContext ctx) {
        super.handlerReaderIdle(ctx);
        // 模拟和服务端断开连接
        System.out.println("close connection client:" + ctx.channel().remoteAddress().toString());
        ctx.close();
    }
}