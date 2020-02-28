package com.xwbing.constant;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 说明: 公共枚举
 * 项目名称: boot-module-demo
 * 创建日期: 2016年12月9日 上午9:50:30
 * 作者: xiangwb
 */
public class CommonEnum {
    private CommonEnum() {
    }

    public enum YesOrNoEnum {
        YES("是", "Y"), NO("否", "N");
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
    }

    public static List<JSONObject> listYesOrNo() {
        List<JSONObject> resultVos = new ArrayList<>();
        Arrays.stream(YesOrNoEnum.values()).forEach(yesOrNoEnum -> {
            JSONObject object = new JSONObject();
            object.put("code", yesOrNoEnum.getCode());
            object.put("name", yesOrNoEnum.getName());
            resultVos.add(object);
        });
        return resultVos;
    }

    public enum SexEnum {
        MAN("男", "1"), WOMAN("女", "0");
        private String name;
        private String code;

        SexEnum(String name, String code) {
            this.name = name;
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        public String getName() {
            return name;
        }
    }

    public enum LoginInOutEnum {
        IN("登录", 1), OUT("登出", 2);
        // 成员变量
        private String name;
        private int value;

        LoginInOutEnum(String name, int value) {
            this.value = value;
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }

    public enum MenuOrButtonEnum {
        MENU("菜单", 1), BUTTON("按钮", 2);
        private int code;
        private String name;

        MenuOrButtonEnum(String name, int code) {
            this.code = code;
            this.name = name;
        }

        public int getCode() {
            return code;
        }

        public String getName() {
            return name;
        }
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
        if (optional.isPresent()) {
            String code1 = optional.get().getCode();
        }
        //确定有值,直接取
        YesOrNoEnum yesOrNoEnum = Arrays.stream(YesOrNoEnum.values()).filter(obj -> obj.getCode().equals(code)).findFirst().get();
        System.out.println(yesOrNoEnum.getName());
        //获取该枚举列表
        List<JSONObject> jsonObjects = listYesOrNo();
    }
}
