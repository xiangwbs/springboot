package com.xwbing.domain.entity.sys;

import com.xwbing.domain.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 创建时间: 2017/11/7 9:46
 * 作者: xiangwb
 * 说明: 用户登录登出信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysUserLoginInOut extends BaseEntity {
    private static final long serialVersionUID = -2719103095945780275L;
    public static String table = "sys_user_login_in_out";
    @ApiModelProperty(example = "127.0.0.1", required = true)
    private String ip;
    @ApiModelProperty(value = "登录登出类型(登录:1|登出:2)", example = "1", required = true)
    private int inoutType;
    private String userId;
    //临时字段
    @ApiModelProperty(value = "用户名字")
    private transient String userIdName;
    @ApiModelProperty(value = "记录时间")
    private transient String recordTime;
    private transient String inoutTypeName;
    private transient String startDate;
    private transient String endDate;
    private transient int count;
}
