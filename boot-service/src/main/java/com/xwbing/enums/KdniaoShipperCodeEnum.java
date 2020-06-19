package com.xwbing.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONObject;

/**
 * 快递鸟 快递公司编码
 *
 * @author daofeng
 * @version $Id$
 * @since 2020年06月19日 下午2:13
 */
public enum KdniaoShipperCodeEnum {
    //@formatter:off
    SF("顺丰速运", "SF"),
    HTKY("百世快递", "HTKY"),
    ZTO("中通快递", "ZTO"),
    STO("申通快递", "STO"),
    YTO("圆通速递", "YTO"),
    YD("韵达速递", "YD"),
    YZPY("邮政快递包裹", "YZPY"),
    EMS("EMS", "EMS"),
    HHTT("天天快递", "HHTT"),
    JD("京东物流", "JD"),
    AMAZON("亚马逊", "AMAZON"),
    ZJS("宅急送", "ZJS"),
    ;
    private String name;
    private String code;

    KdniaoShipperCodeEnum(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    private static final Map<String, String> ENUM_MAP = Arrays.stream(KdniaoShipperCodeEnum.values())
            .collect(Collectors.toMap(KdniaoShipperCodeEnum::getCode, KdniaoShipperCodeEnum::getName));

    public static String parse(String code) {
        return ENUM_MAP.get(code);
    }

    public static List<JSONObject> list() {
        List<JSONObject> resultVos = new ArrayList<>();
        JSONObject jsonObject;
        for (KdniaoShipperCodeEnum shipperCode : KdniaoShipperCodeEnum.values()) {
            jsonObject = new JSONObject();
            jsonObject.put("code", shipperCode.getCode());
            jsonObject.put("name", shipperCode.getName());
            resultVos.add(jsonObject);
        }
        return resultVos;
    }
}
