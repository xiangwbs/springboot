package com.xwbing.web.controller.mq;

/**
 * rocketmq 常量
 * 新增topic常量需要走审批，添加阿里云资源
 * 新增group常量需要走审批，添加阿里云资源
 * 新增tag常量不需要走审批
 *
 * @author daofeng
 * @version $
 * @since 2021年05月24日 14:29:02
 */
public interface MqConstant {

    interface Topic {
        String TOPIC1 = "topic1";
    }

    interface Group {
        String GID1 = "GID_1";
    }

    interface Tag {
        String TAG1 = "tag1";
        String TAG2 = "tag2";
    }
}
