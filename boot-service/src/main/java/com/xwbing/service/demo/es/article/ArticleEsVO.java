package com.xwbing.service.demo.es.article;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import cn.hutool.core.date.DatePattern;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文章es信息
 *
 * @author jifeng
 */
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ArticleEsVO {
    private Long id;
    @ApiModelProperty("标题")
    private String title;
    @ApiModelProperty("封面url")
    private String coverUrl;
    @ApiModelProperty("文内容")
    private String content;
    @ApiModelProperty(value = "发文部门")
    private String issueDept;
    @ApiModelProperty(value = "发文部门代码")
    private String issueDeptCode;
    @ApiModelProperty(value = "发布时间")
    @JsonFormat(shape = Shape.STRING, pattern = DatePattern.NORM_DATETIME_PATTERN, timezone = "GMT+8")
    private LocalDateTime publishDate;
    @ApiModelProperty("推荐状态:0=否,1=是")
    private RecommendStatusEnum recommendStatus;
    @ApiModelProperty(value = "推荐时间")
    @JsonFormat(shape = Shape.STRING, pattern = DatePattern.NORM_DATETIME_PATTERN, timezone = "GMT+8")
    private LocalDateTime recommendDate;
    @ApiModelProperty(value = "操作状态:1=上架,0=下架")
    private OperationStatusEnum operationStatus;
    @ApiModelProperty(value = "crm标签id")
    private List<Long> crmTagIdList;
}