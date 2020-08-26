package com.xwbing.service.demo.DesignPattern.adapter;

/**
 * @author xiangwb
 * @date 2020/3/8 15:25
 */
public class LogTargetService implements ILogTargetService {
    @Override
    public void log() {
        System.out.println("将日志写入到文件中....");
    }
}
