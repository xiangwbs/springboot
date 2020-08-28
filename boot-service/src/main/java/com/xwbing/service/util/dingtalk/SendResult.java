package com.xwbing.service.util.dingtalk;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiangwb
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendResult {
    private boolean success;
    private String messageId;
    private Integer errorCode;
    private String errorMsg;

    @Override
    public String toString() {
        Map<String, Object> items = new HashMap<>(4);
        items.put("errorCode", this.errorCode);
        items.put("errorMsg", this.errorMsg);
        items.put("success", this.success);
        items.put("messageId", this.messageId);
        return JSON.toJSONString(items);
    }
}