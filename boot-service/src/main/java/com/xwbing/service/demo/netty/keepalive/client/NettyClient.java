package com.xwbing.service.demo.netty.keepalive.client;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import com.xwbing.service.demo.netty.myprotocol.message.code.MyDecode;
import com.xwbing.service.demo.netty.myprotocol.message.code.MyEncode;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

public class NettyClient {
    private Bootstrap bootstrap;
    private Channel channel;

    public static void main(String[] args) {
        NettyClient nettyClient = new NettyClient();
        nettyClient.start();
    }

    public void start() {
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        //@formatter:off
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                // 此处只监听写事件
                                .addLast(new IdleStateHandler(0, 5, 0))
                                .addLast(new MyEncode())
                                .addLast(new MyDecode())
                                .addLast(new ClientHandler(NettyClient.this));
                    }
                });
        //@formatter:on
        // 发起连接，当连接断开时都要发起连接，也就是不停的调用这个方法
        createConnect();
    }

    void createConnect() {
        // 如果连接没有断开，则什么也不做
        if (channel != null && channel.isActive()) {
            return;
        }
        final ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress("localhost", 8080));
        try {
            // 添加一个监听器
            channelFuture.addListener((ChannelFutureListener)future -> {
                if (channelFuture.isSuccess()) {
                    channel = channelFuture.channel();
                    System.out.println("客户端连接成功");
                } else {
                    System.out.println("每隔2s重连....");
                    channelFuture.channel().eventLoop().schedule(this::createConnect, 2, TimeUnit.SECONDS);
                }
            }).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}