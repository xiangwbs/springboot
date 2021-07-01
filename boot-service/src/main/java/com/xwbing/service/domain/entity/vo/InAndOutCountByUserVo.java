package com.xwbing.service.domain.entity.vo;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年09月02日 3:13 PM
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InAndOutCountByUserVo {
    private String userId;
    private String userName;
    private Integer count;
    @ApiModelProperty("最近登录时间")
    @JSONField(format = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime createTime;
}
