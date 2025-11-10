package com.xwbing.service.domain.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年04月15日 上午9:33
 */
@Data
public class MarkDownCaseDTO {
    @ApiModelProperty("姓名")
    private String name;
    @ApiModelProperty("年龄")
    private Integer age;
}
