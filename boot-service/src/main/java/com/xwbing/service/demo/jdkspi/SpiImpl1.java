package com.xwbing.service.demo.jdkspi;

public class SpiImpl1 implements SPIService {
    @Override
    public void doSomething() {
        System.out.println("第一个实现类干活。。。");
    }
}
