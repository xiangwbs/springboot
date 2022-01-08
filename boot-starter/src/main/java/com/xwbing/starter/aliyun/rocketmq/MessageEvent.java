package com.xwbing.starter.aliyun.rocketmq;

import java.io.Serializable;

import com.aliyun.openservices.shade.org.apache.commons.lang3.StringUtils;

import cn.hutool.core.util.IdUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author daofeg
 * @version $
 * @since 2020年08月06日 20:54
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageEvent implements Serializable {
    private static final long serialVersionUID = -2624253925403159396L;
    /**
     * 主题
     */
    private String topic;
    /**
     * 标签
     */
    private String tag;
    /**
     * 数据
     */
    private Object data;
    /**
     * 用来构建消息的唯一标识,不检测重复,可以为空,不影响消息收发
     */
    private String key;

    public String getKey() {
        String myKey;
        if (StringUtils.isEmpty(this.tag)) {
            myKey = this.topic + ":";
        } else {
            myKey = this.topic + ":" + this.tag + ":";
        }
        if (StringUtils.isEmpty(this.key)) {
            myKey += IdUtil.simpleUUID();
        } else {
            myKey += this.key;
        }
        return myKey;
    }
}