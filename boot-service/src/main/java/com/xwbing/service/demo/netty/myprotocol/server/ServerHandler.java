package com.xwbing.service.demo.netty.myprotocol.server;

import com.xwbing.service.demo.netty.myprotocol.message.MyHeader;
import com.xwbing.service.demo.netty.myprotocol.message.MyHeaderData;
import com.xwbing.service.demo.netty.myprotocol.message.MyMessageRecord;
import com.xwbing.service.demo.netty.myprotocol.message.entity.User;
import com.xwbing.service.demo.netty.myprotocol.message.enums.LanguageEnum;
import com.xwbing.service.demo.netty.myprotocol.message.enums.ReqEnum;
import com.xwbing.service.demo.netty.myprotocol.message.enums.SerializerEnum;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MyMessageRecord gpMessageRecord = (MyMessageRecord)msg;
        System.out.println("收到客户端消息：" + gpMessageRecord);
        // 给客户端返回数据
        MyMessageRecord record = new MyMessageRecord();
        MyHeader header = new MyHeader();
        int age = ((User)gpMessageRecord.getBody()).getAge();
        MyHeaderData headerData = new MyHeaderData();
        headerData.setReqType(ReqEnum.RES.code());
        headerData.setVersion(2);
        headerData.setLanguageCode(LanguageEnum.JAVA.code());
        headerData.setSerializableType(SerializerEnum.JSON.code());
        header.setHeaderData(headerData);
        record.setHeader(header);
        User user = new User();
        user.setName("我是服务端数据");
        user.setAge(age);
        record.setBody(user);
        ctx.writeAndFlush(record);
        super.channelRead(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
