package com.xwbing.service.demo.netty.codec.protocol.client;

import com.xwbing.service.demo.netty.codec.protocol.message.MyHeader;
import com.xwbing.service.demo.netty.codec.protocol.message.MyHeaderData;
import com.xwbing.service.demo.netty.codec.protocol.message.MyMessageRecord;
import com.xwbing.service.demo.netty.codec.protocol.message.entity.User;
import com.xwbing.service.demo.netty.codec.protocol.message.enums.LanguageEnum;
import com.xwbing.service.demo.netty.codec.protocol.message.enums.ReqEnum;
import com.xwbing.service.demo.netty.codec.protocol.message.enums.SerializerEnum;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接成功");
        // 报文格式: length |  headerLength | headerData | bodyData
        // 消息总长度 | 序列化方式 + 头消息长度 | 头数据 | 消息体
        MyMessageRecord record = new MyMessageRecord();
        MyHeader header = new MyHeader();
        MyHeaderData headerData = new MyHeaderData();
        // 版本号为1
        headerData.setVersion(1);
        // 使用语言为JAVA
        headerData.setLanguageCode(LanguageEnum.JAVA.code());
        // 是请求类型
        headerData.setReqType(ReqEnum.REQ.code());
        // 序列化方式
        headerData.setSerializableType(SerializerEnum.JSON.code());
        // 设置头数据
        header.setHeaderData(headerData);
        record.setHeader(header);
        // 设置body
        User user = new User();
        user.setName("我是客户端");
        user.setAge(18);
        record.setBody(user);

        ctx.writeAndFlush(record);
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MyMessageRecord record = (MyMessageRecord)msg;
        System.out.println("收到服务端的消息内容：" + record);
        super.channelRead(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}