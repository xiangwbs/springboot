package com.xwbing.starter.aliyun.rocketmq;

/**
 * 顺序消息,消息分区类型
 *
 * @author daofeg
 * @version $
 * @since 2020年08月06日 20:54
 */
public enum MessageOrderTypeEnum {
    //@formatter:off
    /**
     * 全局顺序消息
     */
    GLOBAL((byte)1),
    /**
     * topic 作为分区的顺序消息,即同一topic消息保证为顺序消息
     */
    TOPIC((byte)2),
    /**
     * topic 和tag 作为分区的顺序消息,即同一topic和tag消息保证为顺序消息
     */
    TAG((byte)3);

    private byte code;

    MessageOrderTypeEnum(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }
}
