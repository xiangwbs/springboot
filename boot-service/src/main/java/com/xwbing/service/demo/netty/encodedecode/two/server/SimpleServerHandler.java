package com.xwbing.service.demo.netty.encodedecode.two.server;

import com.xwbing.service.demo.netty.encodedecode.two.Model;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class SimpleServerHandler extends ChannelInboundHandlerAdapter {
    private int counter = 0;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Model model = (Model)msg;
        System.out.println("this is " + (++counter) + " times receive client [" + model.getModelName() + "]");
        ctx.writeAndFlush(model);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}