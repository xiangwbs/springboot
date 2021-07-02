package com.xwbing.service.util;

import com.xwbing.service.enums.ImportStatusEnum;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author daofneg
 * @version $
 * @since 2020年01月15日 16:33
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PageSearchVO extends PageParam {
    @ApiModelProperty(value = "状态:1=导入中,2=失败,3=成功")
    private ImportStatusEnum status;
}