package com.xwbing.domain.entity.vo;

import com.xwbing.domain.entity.sys.DataDictionary;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 创建时间: 2018/2/26 15:27
 * 作者: xiangwb
 * 说明:
 */
@Data
@ApiModel
public class DataDictionaryVo extends RestMessageVo {
    @ApiModelProperty(value = "返回数据")
    private DataDictionary data;
}
