package com.xwbing.service.pay.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 交易退款请求
 *
 * @author daofeng
 * @version $Id$
 * @since 2020年07月24日 10:18
 */
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class RefundRespDTO {
    private LocalDateTime refundTime;
    private boolean success;

    public static RefundRespDTO ofSuccess(LocalDateTime refundTime) {
        return RefundRespDTO.builder().success(true).refundTime(refundTime).build();
    }

    public static RefundRespDTO ofFail() {
        return RefundRespDTO.builder().success(false).build();
    }
}
