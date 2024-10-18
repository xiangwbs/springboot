package com.xwbing.service.domain.entity.vo;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author daofeng
 * @version $Id$
 * @since 2022年04月11日 4:09 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScanCodeVO {
    @ApiModelProperty("二维码地址")
    private String qrcode;
    @ApiModelProperty("二维码标识")
    private String qrcodeKey;
    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN, timezone = "GMT+8")
    @ApiModelProperty("到期时间")
    private LocalDateTime expireDate;
}