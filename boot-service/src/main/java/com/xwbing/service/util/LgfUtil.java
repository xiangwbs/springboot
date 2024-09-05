package com.xwbing.service.util;

import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

/**
 * lgf 表达式匹配，
 * 参考：
 * <a href="https://help.aliyun.com/zh/outboundbot/user-guide/manage-intents">阿里云 LGF 问法模板</a>，
 * <a href="https://help.aliyun.com/zh/outboundbot/use-cases/lgf-method-configuration?spm=a2c4g.11186623.0.0.796c15c3nRc6AP">LGF 问法配置</a>
 */
@Slf4j
public class LgfUtil {
    public static boolean validate(String text, String regExg) {
        return Pattern.matches(convertToRegex(regExg), text);
    }

    private static String convertToRegex(String template) {
        // 转换模板中的特定符号为正则表达式
        String regex = template;
        // 可选符
        regex = regex.replace("[", "(").replace("]", ")?");
        // 处理逻辑非(负向零宽先行断言)
        regex = regex.replace("(!", "(?!");
        // 返回转换后的正则表达式
        return regex;
    }

    public static void main(String[] args) {
        // 可选符[]
        System.out.println("可选---");
        System.out.println(validate("帮我打水", convertToRegex("[请|麻烦]帮我打水")));
        System.out.println(validate("麻烦帮我打水", convertToRegex("[请|麻烦]帮我打水")));
        System.out.println(validate("请帮我打水", convertToRegex("[请|麻烦]帮我打水")));

        // 必选()
        System.out.println("必选---");
        System.out.println(validate("今天北京的天气预报", "(今天|明天|后天)北京[的]天气预报"));
        System.out.println(validate("明天北京天气预报", "(今天|明天|后天)北京[的]天气预报"));
        System.out.println(validate("北京的天气预报", "(今天|明天|后天)北京[的]天气预报"));

        // 指定数量的文本，.{下限,上限}
        System.out.println("指定数量文本---");
        System.out.println(validate("查一下明天北京的天气", ".{0,5}北京的天气"));
        System.out.println(validate("查一下明天北京的天气", ".{0,3}北京的天气"));

        // 非
        System.out.println("非---");
        System.out.println(validate("初级", "(!初级|中级).*"));
        System.out.println(validate("高级", "(!初级|中级).*"));
    }
}