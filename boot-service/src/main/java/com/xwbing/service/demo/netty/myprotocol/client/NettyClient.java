package com.xwbing.service.demo.netty.myprotocol.client;

import java.net.InetSocketAddress;

import com.xwbing.service.demo.netty.myprotocol.message.code.MyDecode;
import com.xwbing.service.demo.netty.myprotocol.message.code.MyEncode;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {
    public static void main(String[] args) {
        EventLoopGroup worker = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(worker)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline()
                        .addLast(new MyEncode())
                        .addLast(new MyDecode())
                        .addLast(new ClientHandler());
            }
        });
        try {
            ChannelFuture future = bootstrap.connect(new InetSocketAddress("localhost", 8080)).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            worker.shutdownGracefully();
        }
    }
}