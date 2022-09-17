package com.xwbing.service.demo.netty.reactor.main;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        new Thread(new MainReactor(8080), "main-main").start();
    }
}