package com.xwbing.service.demo;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
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
        byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue(),"freeswitch_server_config".getBytes()).getEncoded();
        AES aes = SecureUtil.aes(key);
        String s = aes.encryptBase64("ClueCon");
        String s1 = aes.decryptStr(s);
        System.out.println("");


    }
}

