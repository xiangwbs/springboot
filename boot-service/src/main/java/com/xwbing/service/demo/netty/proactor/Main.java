package com.xwbing.service.demo.netty.proactor;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        new Thread(new Proactor(8080), "Main-Thread").start();
    }
}
