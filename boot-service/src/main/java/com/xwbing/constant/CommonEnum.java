package com.xwbing.constant;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 说明: 公共枚举
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

    public enum CodeEnum {
        OK("成功", 200),
        UNAUTHORIZED("请求要求身份验证", 401),
        FORBIDDEN("服务器拒绝请求", 403),
        NOT_FOUND("服务器找不到请求的网页", 404),
        ERROR("服务器遇到错误,无法完成请求", 500),
        SERVICE_UNAVAILABLE("服务器暂不可用", 503),
        GATEWAY_TIME_OUT("网关超时", 504);
        // 成员变量
        private String name;
        private int value;

        CodeEnum(String name, int value) {
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

    /**
     * 快递鸟物流状态
     */
    public enum ExpressStatusEnum {
        NO_MSG("无信息", 0),
        HAS_TAKE("已取件", 1),
        ON_THE_WAY("在途中", 2),
        RECEIVED("已签收", 3),
        QUESTION("问题件", 4),
        TO_TAKE("待取件", 5),
        TO_SEND("待派件", 6),
        HAS_SHIPPED("已发货", 8),
        UN_SHIPPED("未发货", 9);
        private String name;
        private int value;

        ExpressStatusEnum(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * 快递鸟 快递公司编码
     */
    public enum ShipperCodeEnum {
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
        ZJS("宅急送", "ZJS");
        private String name;
        private String code;

        ShipperCodeEnum(String name, String code) {
            this.name = name;
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public String getCode() {
            return code;
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
        optional.ifPresent(yesOrNoEnum -> System.out.println(yesOrNoEnum.getName()));
        //确定有值,直接取
        YesOrNoEnum yesOrNoEnum = Arrays.stream(YesOrNoEnum.values()).filter(obj -> obj.getCode().equals(code)).findFirst().get();
        System.out.println(yesOrNoEnum.getName());
        //获取该枚举列表
        List<JSONObject> jsonObjects = listYesOrNo();
    }
}
