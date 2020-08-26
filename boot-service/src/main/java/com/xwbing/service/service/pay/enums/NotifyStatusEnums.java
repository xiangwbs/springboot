package com.xwbing.service.service.pay.enums;

import java.util.HashMap;
import java.util.Map;

public enum NotifyStatusEnums {
    //@formatter:off
    NOT_NOTIFIED("not_notified", "未通知"),
    NOTIFIED("notified", "已通知");

    private String value;
    private String alias;
    private static final Map<String, NotifyStatusEnums> map = new HashMap<>();

    NotifyStatusEnums(String value, String alias) {
        this.value = value;
        this.alias = alias;
    }

    public String getValue() {
        return value;
    }

    public String getAlias() {
        return alias;
    }

    public static NotifyStatusEnums parse(String value) {
        return map.get(value);
    }

    static {
        NotifyStatusEnums[] notifyStatusEnums = values();
        for (NotifyStatusEnums notifyCodeEnum : notifyStatusEnums) {
            map.put(notifyCodeEnum.getValue(), notifyCodeEnum);
        }
    }
}
