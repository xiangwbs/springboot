package com.xwbing.service.demo.netty.reactor.multi;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        new Thread(new Reactor(8080), "multi-main").start();
    }
}