package com.xwbing.config.aliyun.rocketmq;

import java.io.Serializable;
import java.util.UUID;

import com.aliyun.openservices.shade.io.netty.util.internal.StringUtil;

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
     * 需要传递的领域对象
     */
    private Object domain;
    /**
     * 传递的领域对象的唯一标识,用来构建消息的唯一标识,不检测重复,可以为空,不影响消息收发
     */
    private String domainKey;
    /**
     * 事件序列ID
     */
    private String txId;
    /**
     * 事件创建时间
     */
    private long createdDate = System.currentTimeMillis();

    /**
     * 方便的生成TxId的方法
     *
     * @return
     */
    public String generateTxId() {
        if (txId == null) {
            txId = getTopic() + ":" + getTag() + ":";
            if (StringUtil.isNullOrEmpty(domainKey)) {
                txId = txId + getCreatedDate() + ":" + UUID.randomUUID().toString();
            } else {
                txId = txId + domainKey;
            }
        }
        return txId;
    }
}
