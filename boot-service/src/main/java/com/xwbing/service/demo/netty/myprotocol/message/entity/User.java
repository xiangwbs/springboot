package com.xwbing.service.demo.netty.myprotocol.message.entity;

import java.io.Serializable;

import lombok.Data;

@Data
public class User implements Serializable {
    private String name;
    private int age;
}
