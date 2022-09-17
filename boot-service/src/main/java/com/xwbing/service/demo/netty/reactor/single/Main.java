package com.xwbing.service.demo.netty.reactor.single;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        new Thread(new Reactor(8080), "single-main").start();
    }
}