package com.xwbing.service.demo.zk.jute;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.jute.BinaryInputArchive;
import org.apache.jute.BinaryOutputArchive;
import org.apache.jute.InputArchive;
import org.apache.jute.OutputArchive;
import org.apache.jute.Record;
import org.apache.zookeeper.server.ByteBufferInputStream;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Person implements Record {

    private String username;
    private Integer age;

    @Override
    public void serialize(OutputArchive archive, String tag) throws IOException {
        archive.startRecord(this, tag);
        archive.writeString(username, "username");
        archive.writeInt(age, "age");
        archive.endRecord(this, tag);
    }

    @Override
    public void deserialize(InputArchive archive, String tag) throws IOException {
        archive.startRecord(tag);
        username = archive.readString("username");
        age = archive.readInt("age");
        archive.endRecord(tag);
    }

    public static void main(String[] args) throws IOException {
        // 序列化
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BinaryOutputArchive binaryOutputArchive = BinaryOutputArchive.getArchive(byteArrayOutputStream);
        new Person("Jack", 16).serialize(binaryOutputArchive, "person");
        ByteBuffer byteBuffer = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());

        // 反序列化
        ByteBufferInputStream byteBufferInputStream = new ByteBufferInputStream(byteBuffer);
        BinaryInputArchive binaryInputArchive = BinaryInputArchive.getArchive(byteBufferInputStream);
        Person person = new Person();
        person.deserialize(binaryInputArchive, "person");
        System.out.println(person.toString());

        // 关闭资源
        byteArrayOutputStream.close();
        byteBufferInputStream.close();
    }
}