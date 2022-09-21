package com.xwbing.service.demo.netty.keepalive;

import com.xwbing.service.demo.netty.customprotocol.message.MyHeader;
import com.xwbing.service.demo.netty.customprotocol.message.MyHeaderData;
import com.xwbing.service.demo.netty.customprotocol.message.MyMessageRecord;
import com.xwbing.service.demo.netty.customprotocol.message.enums.ReqEnum;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

public abstract class AbstractHandler extends ChannelInboundHandlerAdapter {
    private String name;

    public AbstractHandler(String name) {
        this.name = name;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress().toString() + " 连接成功");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress().toString() + " 连接断开");
    }

    /**
     * 无论是客户端还是服务端都需要调用channelRead，
     * 然后根据不同的报文的类型选择不同的处理逻辑
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MyMessageRecord record = (MyMessageRecord)msg;
        // 获取请求类型
        byte reqType = ((MyMessageRecord)msg).getHeader().getHeaderData().getReqType();
        switch (reqType) {
            // REQ
            case (byte)0:
                // 如果是请求报文，服务端就要去处理接收数据并发送回复报文
                System.out.println("收到客户端消息：" + record);
                sendMsg(ctx, ReqEnum.RES);
                break;
            // RES
            case (byte)1:
                // 如果是回复报文，那么就是服务端发送给客户端的，打印就行
                System.out.println("收到服务端消息：" + record);
                break;
            // PING
            case (byte)2:
                // 如果是客户端发送的ping报文，回复pong报文
                sendMsg(ctx, ReqEnum.PONG);
                break;
            // PONG
            case (byte)3:
                // 如果收到的是服务端的pong报文，不做处理
                System.out.println(name + " get pong msg from " + ctx.channel().remoteAddress());
                break;
            default:
                break;
        }
        super.channelRead(ctx, msg);
    }

    /**
     * 心跳检测会调用该方法，如果你没有读写会触发此方法，从而拿到事件的类型
     *
     * @param ctx
     * @param evt
     *
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleStateEvent stateEvent = (IdleStateEvent)evt;
        switch (stateEvent.state()) {
            // 如果没有读
            case READER_IDLE:
                handlerReaderIdle(ctx);
                break;
            // 如果没有写
            case WRITER_IDLE:
                handlerWriterIdle(ctx);
                break;
            // 既没有读也没有写
            case ALL_IDLE:
                handlerAllIdle(ctx);
                break;
            default:
                break;
        }
        super.userEventTriggered(ctx, evt);
    }

    protected void handlerAllIdle(ChannelHandlerContext ctx) {
        System.out.println(name + " 没有读也没有写，触发AllIdle事件===================");
    }

    protected void handlerWriterIdle(ChannelHandlerContext ctx) {
        System.out.println(name + " 没有写，触发WriterIdle事件===================");
    }

    protected void handlerReaderIdle(ChannelHandlerContext ctx) {
        System.out.println(name + " 没有读，触发ReaderIdle事件===================");
    }

    protected void sendMsg(ChannelHandlerContext ctx, ReqEnum req) {
        MyMessageRecord record = createRecord(req.code());
        ctx.writeAndFlush(record);
        System.out.println(name + " 发送" + req.name() + "到 " + ctx.channel().remoteAddress());
    }

    private MyMessageRecord createRecord(byte reqType) {
        MyMessageRecord record = new MyMessageRecord();
        MyHeader header = new MyHeader();
        MyHeaderData headerData = new MyHeaderData();
        record.setHeader(header);
        header.setHeaderData(headerData);
        headerData.setReqType(reqType);
        return record;
    }
}