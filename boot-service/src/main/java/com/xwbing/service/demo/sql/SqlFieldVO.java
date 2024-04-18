package com.xwbing.service.demo.sql;

import lombok.Data;

/**
 * @author daofeng
 * @version $
 * @since 2024年03月09日 12:53 PM
 */
@Data
public class SqlFieldVO {
    private String code;
    private String name;
    private String description;
    // 数据类型:10=整形数字,11=浮点数字,20=字符串,30=日期
    private Byte dataType;
    // 日期类型:0=未知,10=年,20=半年,30=季度,40=月,50=日
    private Byte dateType;
}
