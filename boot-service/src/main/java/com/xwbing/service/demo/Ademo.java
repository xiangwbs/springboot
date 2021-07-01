package com.xwbing.service.demo;

import com.xwbing.service.domain.entity.rest.ImportTask;
import com.xwbing.service.enums.ImportStatusEnum;
import com.xwbing.service.util.Jackson;

import lombok.extern.java.Log;

/**
 * 项目名称: boot-module-pro
 * 创建时间: 2018/1/23 14:45
 * 作者: xiangwb
 * 说明: 测试用
 */
@Log
public class Ademo {
    public static void main(String[] args) {
        ImportTask build = ImportTask.builder().status(ImportStatusEnum.FAIL).build();
        String s = Jackson.build().writeValueAsString(build);
        System.out.println("");
    }

}

