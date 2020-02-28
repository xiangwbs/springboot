package com.xwbing.domain.entity.vo;

import com.xwbing.domain.entity.sys.DataDictionary;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 创建时间: 2018/2/26 15:29
 * 作者: xiangwb
 * 说明:
 */
@Data
@ApiModel
public class ListDataDictionaryVo extends RestMessageVo {
    @ApiModelProperty(value = "返回数据")
    private List<DataDictionary> data;
}
