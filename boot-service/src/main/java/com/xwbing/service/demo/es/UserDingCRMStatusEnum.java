package com.xwbing.service.demo.es;

import com.xwbing.service.enums.base.BaseEnum;

/**
 * <p>钉钉 crm 服务窗状态</p>
 *
 * @author jifeng
 * @version 1.0 2021/02/22 14:41
 **/
public enum UserDingCRMStatusEnum implements BaseEnum {
    /**
     * 钉钉CRM服务窗状态，99=未加入，10=已关注，20=已取关
     */
    NOT_SUBSCRIBE(99, "未关注"), SUBSCRIBE(10, "已关注"), UNSUBSCRIBE(20, "已取关");

    private final int code;
    private final String desc;

    UserDingCRMStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public int getCode() {
        return code;
    }

}