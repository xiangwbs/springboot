package com.xwbing.service.demo.netty.customprotocol.message.code;

import java.nio.ByteBuffer;

import com.xwbing.service.demo.netty.customprotocol.message.MyMessageRecord;
import com.xwbing.service.demo.netty.customprotocol.message.serializer.SerializerUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 自定义编码器
 */
public class MyEncode extends MessageToByteEncoder<MyMessageRecord> {
    @Override
    protected void encode(ChannelHandlerContext ctx, MyMessageRecord msg, ByteBuf out) throws Exception {
        // 对头部数据进行序列化，并设置一些头部数据的值
        ByteBuffer header = encodeHead(msg);
        out.writeBytes(header);
        // 序列化body数据
        byte[] body = SerializerUtil.serializable(msg.getBody(), msg.getHeader().getHeaderData().getSerializableType());
        if (body != null && body.length > 0) {
            out.writeBytes(body);
        }
    }

    /**
     * 报文格式：length | headerLength | headerData | bodyData
     * 消息总长度 | 序列化方式 + 头消息长度 | 头数据 | 消息体
     *
     * @param msg
     *
     * @return
     */
    private ByteBuffer encodeHead(MyMessageRecord msg) {
        // headerLength+headerData+bodyData
        int dataLength = 0;
        // 头部的length字段长度
        int headerLength = 4;
        dataLength += headerLength;
        // 序列化头部信息
        byte[] headerData = SerializerUtil
                .serializable(msg.getHeader(), msg.getHeader().getHeaderData().getSerializableType());
        dataLength += headerData.length;
        // 获取到body的长度
        int bodyLength = SerializerUtil
                .serializable(msg.getBody(), msg.getHeader().getHeaderData().getSerializableType()).length;
        dataLength += bodyLength;
        // 定义一个length+headerLength+headerData数据的buffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(4 + dataLength - bodyLength);
        // 整体数据长度
        byteBuffer.putInt(dataLength);
        // 头数据长度（把序列化的方式藏在了头长度的第一个字节里面）
        byteBuffer.put(markProtocolType(headerData.length, msg.getHeader().getHeaderData().getSerializableType()));
        // 头的数据
        byteBuffer.put(headerData);
        // 切换成读模式
        byteBuffer.flip();
        return byteBuffer;
    }

    /**
     * @param headerLength：头的长度
     * @param type：序列化方式
     * 0xFF=11111111
     *
     * @return
     */
    public static byte[] markProtocolType(int headerLength, byte type) {
        byte[] result = new byte[4];
        // 第一个8位放了序列化的方式
        result[0] = type;
        // 第二部分开始就放头的长度,把头的长度的第二个字节放好了
        result[1] = (byte)((headerLength >> 16) & 0xFF);
        // 第三个字节放头的第三个字节
        result[2] = (byte)((headerLength >> 8) & 0xFF);
        // 第四个字节放头的第四个字节
        result[3] = (byte)(headerLength & 0xFF);
        return result;
    }
}