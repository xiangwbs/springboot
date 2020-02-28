package com.xwbing.config.xxljob;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author xiangwb
 * @date 2020/1/31 21:09
 * xxl-job属性加载配置类
 */
@Data
@ConfigurationProperties(prefix = XxlJobProperties.PREFIX)
public class XxlJobProperties {
    public static final String PREFIX = "xxl.job";
    /**
     * 开启xxl-job
     */
    private Boolean enabled;
    /**
     * 调度中心部署跟地址(选填)
     * 如调度中心集群部署存在多个地址则用逗号分隔
     * 执行器将会使用该地址进行"执行器心跳注册"和"任务结果回调"；为空则关闭自动注册
     */
    private String adminAddresses;
    /**
     * 执行器通讯TOKEN(选填)
     */
    private String accessToken;
    /**
     * 执行器AppName(选填)
     * 执行器心跳注册分组依据；为空则关闭自动注册
     */
    private String executorAppName;
    /**
     * 执行器IP(选填)
     * 默认为空表示自动获取IP，多网卡时可手动设置指定IP，该IP不会绑定Host仅作为通讯实用；地址信息用于 "执行器注册" 和 "调度中心请求并触发任务"
     */
    private String executorIp;
    /**
     * 执行器端口号(选填)
     * 小于等于0则自动获取；默认端口为9999，单机部署多个执行器时，注意要配置不同执行器端口
     */
    private int executorPort;
    /**
     * 执行器运行日志文件存储磁盘路径(选填)
     * 需要对该路径拥有读写权限；为空则使用默认路径
     */
    private String executorLogPath;
    /**
     * 执行器日志保存天数(选填)
     * 大于等于3生效，启用执行器Log文件定期清理功能，否则不生效
     */
    private int executorLogRetentionDays;
}
