package com.xwbing.web.controller.mall;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xwbing.service.mall.dto.PayOrderReqDTO;
import com.xwbing.service.mall.dto.TradeReqDTO;
import com.xwbing.service.mall.dto.TradeRespDTO;
import com.xwbing.service.mall.strategy.PayStrategyFactory;
import com.xwbing.web.response.ApiResponse;
import com.xwbing.web.response.ApiResponseUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daofeng
 * @version $Id$
 * @since 2021年09月28日 5:04 PM
 */
@Slf4j
@Api(tags = "支付模块", description = "支付模块")
@RestController
@RequestMapping("/pay")
@AllArgsConstructor
public class PayController {
    private final PayStrategyFactory payFactory;

    @ApiOperation(value = "创建支付")
    @PostMapping("/order/create")
    public ApiResponse<TradeRespDTO> createPayOrder(@RequestBody @Validated PayOrderReqDTO dto) {
        TradeReqDTO payReqDTO = TradeReqDTO.of(dto.getPayWay(), dto.getOrderNo(), "支付测试", 1L, "281430623401443343");
        TradeRespDTO pay = payFactory.getStrategy(dto.getPayType()).createTrade(payReqDTO);
        return ApiResponseUtil.success(pay);
    }
}
