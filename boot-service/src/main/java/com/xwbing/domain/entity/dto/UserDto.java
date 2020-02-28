package com.xwbing.domain.entity.dto;

import lombok.Data;

/**
 * 项目名称: boot-module-demo
 * 创建时间: 2017/11/20 17:18
 * 作者: xiangwb
 * 说明: 用户excel导出信息
 */
@Data
public class UserDto {
    //excel导出字段的顺序
    private String name;
    private String userName;
    private String sex;
    private String mail;
    private String isAdmin;
}
