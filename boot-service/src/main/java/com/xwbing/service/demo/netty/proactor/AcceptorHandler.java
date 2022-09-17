package com.xwbing.service.demo.netty.proactor;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AcceptorHandler implements CompletionHandler<AsynchronousSocketChannel, Proactor> {
    @Override
    public void completed(AsynchronousSocketChannel channel, Proactor proactor) {
        // 每接收一个连接之后，再执行一次异步连接请求，这样就能一直处理多个连接
        proactor.serverSocketChannel.accept(proactor, this);
        // 创建新的Buffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        // 异步读，第三个参数为接收消息回调的业务Handler
        channel.read(byteBuffer, byteBuffer, new ReadHandler(channel));
    }

    @Override
    public void failed(Throwable exc, Proactor attachment) {
        attachment.latch.countDown();
    }
}