package com.xwbing.domain.entity.sys;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * 项目名称: boot-module-demo
 * 创建时间: 2017/11/7 9:46
 * 作者: xiangwb
 * 说明: 用户登录登出信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity(name = "sys_user_login_in_out")
public class SysUserLoginInOut extends BaseEntity {
    private static final long serialVersionUID = -2719103095945780275L;
    public static String table = "sys_user_login_in_out";
    private String ip;
    @Column(name = "inout_type")
    private int inoutType;
    @Column(name = "user_id")
    private String userId;
    //临时字段
    private transient String userIdName;
    private transient String recordTime;
    private transient String inoutTypeName;
//    private transient String startDate;
//    private transient String endDate;
}
