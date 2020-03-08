package com.xwbing.demo.DesignPattern.adapter;

/**
 * @author xiangwb
 * @date 2020/3/8 15:27
 * 对象适配器(组合模式)
 * 支持新老版本兼容，新增扩展性
 */
public class LogAdapter implements ILogTargetService {
    private LogTargetService targetService;

    public LogAdapter(LogTargetService targetService) {
        this.targetService = targetService;
    }

    /**
     * 新增的扩展功能，既能支持写入本地文件，也能写入数据库
     */
    @Override
    public void log() {
        targetService.log();
        System.out.println("将日志写入到数据库中....");
    }
}
