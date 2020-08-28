package com.xwbing.starter.aliyun.oss.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年08月28日 上午10:29
 */
@Builder
@Getter
@ApiModel(value = "视频信息")
public class VideoPlayInfoVO {
    @ApiModelProperty(value = "播放地址")
    private String playURL;
    @ApiModelProperty(value = "大小 单位byte")
    private Long size;
    private String status;
    @ApiModelProperty(value = "播放时长 单位s")
    private Double duration;
    @ApiModelProperty(value = "清晰度 FD/HD等")
    private String definition;
}