package com.xwbing.service.mall.strategy;

import java.beans.Introspector;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;

import com.xwbing.service.exception.BusinessException;
import com.xwbing.service.mall.enums.PayTypeEnum;

import lombok.extern.slf4j.Slf4j;

/**
 * @author daofeng
 * @version $Id$
 * @since 2021年07月06日 4:44 PM
 */
@Slf4j
@Component
public class PayStrategyFactory {
    private final Map<String, IPayStrategy> strategyMap;
    private final Map<PayTypeEnum, String> payTypeMap = Arrays.stream(PayTypeEnum.values())
            .collect(Collectors.toMap(Function.identity(), PayTypeEnum::getHandleName));

    public PayStrategyFactory(Map<String, IPayStrategy> strategyMap) {
        this.strategyMap = strategyMap;
        if (MapUtils.isEmpty(strategyMap)) {
            throw new RuntimeException("未配置任何支付处理实例");
        }
    }

    public IPayStrategy getStrategy(PayTypeEnum payType) {
        IPayStrategy payStrategy = strategyMap.get(Introspector.decapitalize(payTypeMap.get(payType)));
        if (payStrategy == null) {
            log.error("getStrategy null");
            throw new BusinessException("暂不支持该支付类型");
        }
        return payStrategy;
    }
}
