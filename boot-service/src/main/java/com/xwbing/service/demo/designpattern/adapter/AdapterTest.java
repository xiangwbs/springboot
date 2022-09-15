package com.xwbing.service.demo.designpattern.adapter;

/**
 * @author xiangwb
 * @date 2020/3/8 15:41
 */
public class AdapterTest {
    public static void main(String[] args) {
        new LogAdapter(new LogTargetService()).log();
    }
}
