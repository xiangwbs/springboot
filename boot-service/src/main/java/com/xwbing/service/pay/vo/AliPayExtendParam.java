package com.xwbing.service.pay.vo;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 业务扩展参数
 *
 * @author daofeng
 * @version $Id$
 * @since 2020年07月03日 下午4:58
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AliPayExtendParam {
    @JSONField(name = "hb_fq_num")
    private String hbFqNum;
    @JSONField(name = "hb_fq_seller_percent")
    private String hbFqSellerPercent;
}
