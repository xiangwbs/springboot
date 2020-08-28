package com.xwbing.starter.alipay.vo.request;

import com.alipay.api.domain.ExtendParams;

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
    /**
     * 花呗分期数 仅支持传入 3、6、12
     */
    private String hbFqNum;
    /**
     * 卖家承担收费比例 商家承担手续费传入 100，用户承担手续费传入 0，仅支持传入 100、0 两种
     */
    private String hbFqSellerPercent;

    public ExtendParams convert() {
        ExtendParams extendParams = new ExtendParams();
        extendParams.setHbFqSellerPercent(hbFqSellerPercent);
        extendParams.setHbFqNum(hbFqNum);
        return extendParams;
    }
}
