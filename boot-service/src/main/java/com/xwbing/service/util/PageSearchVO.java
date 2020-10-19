package com.xwbing.service.util;

import javax.validation.constraints.Max;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author daofneg
 * @version $
 * @since 2020年01月15日 16:33
 */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PageSearchVO {
    @ApiModelProperty(value = "当前页，默认从1页开始")
    private Integer currentPage = 1;
    @Max(50)
    @ApiModelProperty(value = "每页显示的条数，默认从10")
    private Integer pageSize = 10;
}
