package com.xwbing.config.aliyun.oss;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年08月26日 13:40
 */
@Data
@ApiModel(value = "STS临时授权", description = "访问凭证信息")
public class AccessCredentialsVO {
    private String accessKeyId;
    private String accessKeySecret;
    private String securityToken;
    private String endpoint;
    private String bucket;
    private String region;
    private String expiration;
    private String objectKey;
}
