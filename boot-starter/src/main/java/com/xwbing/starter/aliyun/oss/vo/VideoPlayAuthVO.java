package com.xwbing.starter.aliyun.oss.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年08月28日 上午9:40
 */
@Builder
@Getter
@ApiModel(value = "视频播放凭证信息")
public class VideoPlayAuthVO {
    @ApiModelProperty(value = "视频ID")
    private String playAuth;
    @ApiModelProperty(value = "视频基础信息")
    private VideoMeta videoMeta;

    @Builder
    @Getter
    public static class VideoMeta {
        @ApiModelProperty(value = "视频封面")
        private String coverURL;
        @ApiModelProperty(value = "视频时长")
        private Float duration;
        @ApiModelProperty(value = "视频状态")
        private String status;
        @ApiModelProperty(value = "视频标题")
        private String title;
        @ApiModelProperty(value = "视频ID")
        private String videoId;
    }
}
