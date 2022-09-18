package com.xwbing.service.demo.netty.encodedecode.two.client;

import com.xwbing.service.demo.netty.encodedecode.two.Model;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class SimpleClientHandler extends ChannelInboundHandlerAdapter {
    private int counter = 0;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Model model = new Model("xiangwb", "welcome to Netty");
        ctx.writeAndFlush(model);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("this is " + (++counter) + " times receive server [" + msg.toString() + "]");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }


}

