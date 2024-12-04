package com.xwbing.service.demo.es.article;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户es搜索参数
 *
 * @author jifeng
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleEsDTO {
    private String searchKey;
    @ApiModelProperty("模糊匹配")
    private boolean wasMatchSearch;
    @ApiModelProperty("发文部门")
    private String issueDept;
    @ApiModelProperty(value = "发文部门代码")
    private String issueDeptCode;
    @ApiModelProperty(value = "操作状态:1=上架,0=下架")
    private OperationStatusEnum operationStatus;
    @ApiModelProperty(value = "发布时间起")
    @JsonFormat(shape = Shape.STRING, pattern = DatePattern.NORM_DATETIME_PATTERN, timezone = "GMT+8")
    private LocalDateTime publishDateStart;
    @ApiModelProperty(value = "发布时间止")
    @JsonFormat(shape = Shape.STRING, pattern = DatePattern.NORM_DATETIME_PATTERN, timezone = "GMT+8")
    private LocalDateTime publishDateEnd;
    @ApiModelProperty("排除的文章id")
    private List<Long> excludeIds;
    @ApiModelProperty("税务机关代码")
    private String swjgDm;
    private String swjgDmPath;

    @ApiModelProperty(value = "crm标签id")
    private List<Long> crmTagIdList;
    private boolean crmTagOr;
}