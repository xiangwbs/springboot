package com.xwbing.config.util.dingTalk;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xiangwb
 */
@Data
public class SendResult {
    private boolean success;
    private Integer errorCode;
    private String errorMsg;

    public SendResult() {
    }

    public String toString() {
        Map<String, Object> items = new HashMap<>();
        items.put("errorCode", this.errorCode);
        items.put("errorMsg", this.errorMsg);
        items.put("success", this.success);
        return JSON.toJSONString(items);
    }
}
