package com.xwbing.service.demo.netty.customprotocol.message.code;

import java.nio.ByteBuffer;

import com.xwbing.service.demo.netty.customprotocol.message.MyHeader;
import com.xwbing.service.demo.netty.customprotocol.message.MyMessageRecord;
import com.xwbing.service.demo.netty.customprotocol.message.entity.User;
import com.xwbing.service.demo.netty.customprotocol.message.serializer.SerializerUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 自定义解码器
 */
public class MyDecode extends LengthFieldBasedFrameDecoder {
    public MyDecode() {
        // 定义解码的规则，第一个4代表长度字段为4，第4个参数代表读取数据去掉长度字段
        super(Integer.MAX_VALUE, 0, 4, 0, 4);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        // 调用父类的decode方法，其实主要是根据规则获取到我们真正需要的数据封装成ByteBuf返回
        ByteBuf byteBuf = (ByteBuf)super.decode(ctx, in);
        if (byteBuf == null) {
            return null;
        }
        ByteBuffer byteBuffer = byteBuf.nioBuffer();
        // 开始解码
        return MyDecode.decode(byteBuffer);
    }

    /**
     * 报文格式：length | headerLength | headerData | bodyData
     * 消息总长度 | 序列化方式 + 头消息长度 | 头数据 | 消息体
     *
     * @param byteBuffer
     *
     * @return
     */
    private static MyMessageRecord decode(ByteBuffer byteBuffer) {
        // headerLength | headerData | bodyData（定义的解码规则是去除长度字段）
        // 获取到byteBuf的长度
        int length = byteBuffer.limit();
        // 头和序列化方式组成的字节的长度
        int oriHeaderLen = byteBuffer.getInt();
        // headerData | bodyData
        // 头真正的长度
        int headerLength = getHeaderLength(oriHeaderLen);
        // 头的字节数据
        byte[] headerDataByte = new byte[headerLength];
        byteBuffer.get(headerDataByte);
        // bodyData
        MyMessageRecord record = new MyMessageRecord();
        record.setLength(length);
        // 反序列化头数据
        MyHeader myHeader = SerializerUtil
                .deserializable(MyHeader.class, headerDataByte, getProtocolType(oriHeaderLen));
        myHeader.setHeaderLength(headerLength);
        record.setHeader(myHeader);
        // 反序列化body数据
        int bodyLength = length - 4 - headerLength;
        byte[] bodyData = null;
        if (bodyLength > 0) {
            bodyData = new byte[bodyLength];
            byteBuffer.get(bodyData);
            // []
        }
        record.setBody(SerializerUtil.deserializable(User.class, bodyData, getProtocolType(oriHeaderLen)));
        return record;
    }

    private static int getHeaderLength(int oriHeaderLen) {
        return oriHeaderLen & 0xFFFFFF;
    }

    private static byte getProtocolType(int oriHeaderLen) {
        return (byte)((oriHeaderLen >> 24) & 0xFF);
    }
}