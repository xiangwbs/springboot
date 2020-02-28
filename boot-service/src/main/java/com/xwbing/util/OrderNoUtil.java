package com.xwbing.util;

import com.xwbing.constant.Base;
import com.xwbing.config.redis.RedisService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.Date;

/**
 * @author xiangwb
 * @date 20/1/18 20:38
 * 订单号生产工具类
 */
@Component
public class OrderNoUtil {
    @Resource
    private RedisService redisService;

    public String createOrderId() {
        int businessType;
        String env = EnvUtil.getEnv();
        if (StringUtils.equals(env, Base.ENV_DEV)) {
            businessType = Base.BUSINESS_LEASE_DEV;
        } else if (StringUtils.equals(env, Base.ENV_TEST)) {
            businessType = Base.BUSINESS_LEASE_TEST;
        } else if (StringUtils.equals(env, Base.ENV_SANDBOX)) {
            businessType = Base.BUSINESS_LEASE_TEST;
        } else {
            businessType = Base.BUSINESS_LEASE_PROD;
        }
        Long seq = redisService.incr("order-sequence");
        boolean flag = false;
        if (seq > Base.ORDER_MAX_SEQ) {
            seq = 0L;
            flag = true;
        }
        if (flag) {
            redisService.decrBy("order-sequence", Base.ORDER_MAX_SEQ);
        }
        String sequence = new DecimalFormat("00000").format(seq);
        String date = DateUtil2.dateToStr(new Date(), "yyMMddHHmm");
        return businessType + date + sequence;
    }
}
