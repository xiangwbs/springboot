package com.xwbing.domain.entity.dto;

import lombok.Data;

/**
 * 创建时间: 2017/11/20 17:18
 * 作者: xiangwb
 * 说明:
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
