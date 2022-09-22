package com.xwbing.service.demo.netty.keepalive.server;

import com.xwbing.service.demo.netty.myprotocol.message.code.MyDecode;
import com.xwbing.service.demo.netty.myprotocol.message.code.MyEncode;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * 实现长连接
 */
public class NettyServer {
    public static void main(String[] args) {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        //@formatter:off
        bootstrap.group(boss, work)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                // 心跳检测
                                // 此处只监听读事件
                                // readerIdleTimeSeconds：Channel多久没有读取数据会触发IdleStateEvent，其中IdleState为IdleState.READER_IDLE。
                                // writerIdleTimeSeconds：Channel多久没有写数据会触发IdleStateEvent，其中IdleState为IdleState.WRITER_IDLE
                                // allIdleTimeSeconds：Channel多久没有读和写数据会触发IdleStateEvent，其中IdleState为IdleState.ALL_IDLE
                                .addLast(new IdleStateHandler(5, 0, 0))
                                .addLast(new MyEncode())
                                .addLast(new MyDecode())
                                .addLast(new ServerHandler());
                    }
                });
        //@formatter:on
        try {
            ChannelFuture channelFuture = bootstrap.bind(8080).sync();
            System.out.println("服务端启动成功");
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            work.shutdownGracefully();
            boss.shutdownGracefully();
        }
    }
}
