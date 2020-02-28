package com.xwbing.domain.entity.model;

import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.Date;

/**
 * 说明: hibernate validate  JSR303校验框架
 * 各种类型数据校验示例
 * controller层新增或更新的实体前加@Valid注解
 * 创建时间: 2017/5/17 16:13
 * 作者: xiangwb
 */
@Data
public class ValidateModel {
    @NotBlank(message = "name不能为空")
    @Length(max = 50, message = "name长度为1-50")
    private String name;

    @NotEmpty(message = "age不能为空")
    @Min(value = 0, message = "age不能为负")
    private int age;

    @NotEmpty(message = "money不能为空")
    @DecimalMin(value = "0", message = "money不能为负")
    private double money;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthDay;

    @NotBlank(message = "happyTime不能为空")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}", message = "happyTime格式为yyyy-MM-dd HH:mm:ss")
    private String happyTime;

    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "sex不能为空")
    @Pattern(regexp = "[01]", message = "sex格式为0|1,0代表女,1代表男")
    private String sex;
}
