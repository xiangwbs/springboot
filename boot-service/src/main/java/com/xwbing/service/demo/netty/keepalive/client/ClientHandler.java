package com.xwbing.service.demo.netty.keepalive.client;

import com.xwbing.service.demo.netty.myprotocol.message.enums.ReqEnum;
import com.xwbing.service.demo.netty.keepalive.AbstractHandler;

import io.netty.channel.ChannelHandlerContext;

public class ClientHandler extends AbstractHandler {
    private NettyClient nettyClient;

    public ClientHandler(NettyClient nettyClient) {
        super(NettyClient.class.getSimpleName());
        this.nettyClient = nettyClient;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        sendMsg(ctx, ReqEnum.REQ);
    }

    @Override
    protected void handlerWriterIdle(ChannelHandlerContext ctx) {
        super.handlerWriterIdle(ctx);
        // 发送心跳包
        sendMsg(ctx, ReqEnum.PING);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        // 该方法是在连接出问题会触发，所以这里重新建立连接
        nettyClient.createConnect();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("client error：" + cause.getMessage());
    }
}