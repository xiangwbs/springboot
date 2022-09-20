package com.xwbing.service.demo.netty.codec.two.server;

import com.xwbing.service.demo.netty.codec.two.Model;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class SimpleServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Model model = (Model)msg;
        System.out.println("收到客户端的消息内容：" + model);
        // 写数据给客户端
        model = new Model("服务端", "我是服务端");
        ctx.writeAndFlush(model);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}