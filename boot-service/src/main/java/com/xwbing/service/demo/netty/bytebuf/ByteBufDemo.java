package com.xwbing.service.demo.netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;

public class ByteBufDemo {
    public static void main(String[] args) {
        // byteBuf 分配一块内存 自动判断是否分配堆内内存或堆外内存
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
        // //  堆内内存 由jvm管理内存
        // ByteBufAllocator.DEFAULT.heapBuffer();
        // // 堆外内存 java进程中系统为它分配的堆空间
        // ByteBufAllocator.DEFAULT.directBuffer();
        // 标记写指针
        byteBuf.markWriterIndex();
        // 写数据
        byteBuf.writeBytes(new byte[] { 1, 2, 3, 4 });
        printMsg(byteBuf);
        // 重置写指针
        byteBuf.resetWriterIndex();
        // 写数据
        byteBuf.writeBytes(new byte[] { 4, 3, 2, 1 });
        printMsg(byteBuf);
        // 标记读指针
        byteBuf.markReaderIndex();
        // 读数据
        byte aByte = byteBuf.readByte();
        printMsg(byteBuf);
        byteBuf.resetReaderIndex();
        printMsg(byteBuf);
    }

    public static void printMsg(ByteBuf buf) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" read index:").append(buf.readerIndex());
        stringBuilder.append(" write index:").append(buf.writerIndex());
        stringBuilder.append(" capacity:").append(buf.capacity());
        stringBuilder.append(" maxCapacity:").append(buf.maxCapacity());
        ByteBufUtil.appendPrettyHexDump(stringBuilder, buf);
        System.out.println(stringBuilder.toString());
    }
}