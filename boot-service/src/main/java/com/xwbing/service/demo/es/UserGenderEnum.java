package com.xwbing.service.demo.es;

import com.xwbing.service.enums.base.BaseEnum;

/**
 * @author racoon
 * @version $id$
 * @since 2020/7/15 12:19
 */
public enum UserGenderEnum implements BaseEnum {
    UNKNOWN(0,"未知"),
    MALE(1,"男"),
    FEMALE(2,"女"),
    SECRECY(3,"未设置"),
    ;

    private final int code;
    private final String desc;

    UserGenderEnum(int code, String desc) {
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