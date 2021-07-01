package com.xwbing.service.util;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author daofeng
 * @version $Id$
 * @since 2021年07月01日 4:17 PM
 */
@Data
public class PageParam {
    @ApiModelProperty(value = "页码,默认从1页开始", example = "1", required = true)
    private int pageNumber = 1;
    @ApiModelProperty(value = "分页大小", example = "10", required = true)
    private int pageSize = 10;
}