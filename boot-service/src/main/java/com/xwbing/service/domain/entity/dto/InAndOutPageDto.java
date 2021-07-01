package com.xwbing.service.domain.entity.dto;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.xwbing.service.util.PageParam;

import cn.hutool.core.date.DatePattern;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author daofeng
 * @version $Id$
 * @since 2021年07月01日 4:20 PM
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InAndOutPageDto extends PageParam {
    @ApiModelProperty("类型:1=登录,2=登出")
    @NotNull(message = "类型不能为空")
    private Integer inout;
    @ApiModelProperty("开始时间")
    @DateTimeFormat(pattern = DatePattern.NORM_DATE_PATTERN)
    private LocalDate startDate;
    @ApiModelProperty("结束时间")
    @DateTimeFormat(pattern = DatePattern.NORM_DATE_PATTERN)
    private LocalDate endDate;
}
