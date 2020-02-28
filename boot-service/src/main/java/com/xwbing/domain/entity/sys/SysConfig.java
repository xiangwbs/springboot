package com.xwbing.domain.entity.sys;

import com.xwbing.domain.entity.JpaBaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.validation.constraints.Pattern;

/**
 * 说明: 系统配置
 * 创建时间: 2017/5/10 16:36
 * 作者:  xiangwb
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity(name = "system_config")
public class SysConfig extends JpaBaseEntity {
    private static final long serialVersionUID = -7587016038432881980L;
    @ApiModelProperty(value = "配置项的code", example = "email_config", required = true)
    @NotBlank(message = "配置项的code不能为空")
    @Length(min = 1, max = 50, message = "code长度为1-50")
    private String code;
    @ApiModelProperty(value = "配置项的值", example = "{}", required = true)
    @NotBlank(message = "配置项的value不能为空")
    private String value;
    @ApiModelProperty(value = "配置项的描述(名称)", example = "邮箱配置", required = true)
    @NotBlank(message = "配置项的name不能为空")
    @Length(min = 1, max = 20, message = "value长度为1-20")
    private String name;
    @ApiModelProperty(value = "是否启用", example = "Y", required = true)
    @NotBlank(message = "是否启用不能为空")
    @Pattern(regexp = "[Y|N]", message = "是否启用格式为Y|N")
    private String enable;
}
