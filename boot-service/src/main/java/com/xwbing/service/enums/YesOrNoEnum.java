package com.xwbing.service.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONObject;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年06月19日 下午2:05
 */
public enum YesOrNoEnum {
    //@formatter:off
    YES("是", "Y"),
    NO("否", "N"),
    ;
    private String name;
    private String code;

    YesOrNoEnum(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    private static final Map<String, String> ENUM_MAP = Arrays.stream(YesOrNoEnum.values())
            .collect(Collectors.toMap(YesOrNoEnum::getCode,YesOrNoEnum::getName));

    public static String parse(String code) {
        return ENUM_MAP.get(code);
    }

    public static List<JSONObject> list() {
        List<JSONObject> resultVos = new ArrayList<>();
        Arrays.stream(YesOrNoEnum.values()).forEach(yesOrNoEnum -> {
            JSONObject object = new JSONObject();
            object.put("code", yesOrNoEnum.getCode());
            object.put("name", yesOrNoEnum.getName());
            resultVos.add(object);
        });
        return resultVos;
    }

    public static void main(String[] args) {
        String code = "Y";
        //        for (YesOrNoEnum yesOrNoEnum : YesOrNoEnum.values()) {
        //            if (yesOrNoEnum.getCode().equals(code)) {
        //                System.out.println(yesOrNoEnum.getName());
        //                break;
        //            }
        //        }
        //不确定,先判断再取值
        Optional<YesOrNoEnum> optional = Arrays.stream(YesOrNoEnum.values()).filter(obj -> obj.getCode().equals(code)).findFirst();
        optional.ifPresent(yesOrNoEnum -> System.out.println(yesOrNoEnum.getName()));
        //确定有值,直接取
       YesOrNoEnum yesOrNoEnum = Arrays.stream(YesOrNoEnum.values()).filter(obj -> obj.getCode().equals(code)).findFirst().get();
        System.out.println(yesOrNoEnum.getName());
        //获取该枚举列表
        List<JSONObject> jsonObjects = list();
    }
}
