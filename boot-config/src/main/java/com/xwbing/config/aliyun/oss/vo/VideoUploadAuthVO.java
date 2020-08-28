package com.xwbing.config.aliyun.oss.vo;

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
@ApiModel(value = "视频上传凭证信息", description = "阿里云视频点播")
public class VideoUploadAuthVO {
    @ApiModelProperty(value = "视频ID")
    private String videoId;
    @ApiModelProperty(value = "上传凭证")
    private String uploadAuth;
    @ApiModelProperty(value = "上传地址")
    private String uploadAddress;
    @ApiModelProperty(value = "上传请求ID")
    private String requestId;
}
