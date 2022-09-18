package com.xwbing.service.demo.netty.encodedecode.one.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * 一次编码和解码
 */
public class NettyServer {
    public static void main(String[] args) {
        // 创建boss线程组（监听客户端连接，注册Accept事件）
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 创建work线程组（完成IO事件，完成的IO事件就会交给ChildHandler中在pipeline中添加的handler）
        EventLoopGroup workGroup = new NioEventLoopGroup();
        // 实例化ServerBootStrap
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        // bossGroup保存AbstractBootstrap的group属性中
        // workGroup保存ServerBootstrap的childGroup属性中
        serverBootstrap.group(bossGroup, workGroup)
                // 把NioServerSocketChannel.class保存到ReflectiveChannelFactory工厂对象中，将来要通过工厂实例化把NioServerSocketChannel
                .channel(NioServerSocketChannel.class)
                // boss线程要处理的业务 loggerHandler会保存到AbstractBootstrap中的handler中
                .handler(new LoggingHandler(LogLevel.INFO))
                // work线程要处理的业务 把channelInitializer保存到ServerBootstrap中的childHandler中
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        // inbound执行顺序：LengthFieldBasedFrameDecoder ­> StringDecoder ­> SimpleServerHandler
                        // outbound执行顺序：StringEncoder­>LengthFieldPrepender
                        //@formatter:off
                        ch.pipeline()
                                .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
                                .addLast(new LengthFieldPrepender(4, 0, false))
                                .addLast(new StringDecoder())
                                .addLast(new StringEncoder())
                                .addLast(new SimpleServerHandler());
                        //@formatter:on
                    }
                });
        try {
            // 1.监听8080端口，并同步返回
            ChannelFuture channelFuture = serverBootstrap.bind(8080).sync();
            // 阻止进入finally代码块,只有其他的线程调用channelFuture.channel().close()的时候，然后进入finally代码块关闭连接池
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}