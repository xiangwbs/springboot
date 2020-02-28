package com.xwbing.domain.entity.model;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * 说明: 统计属性信息完善程度
 * 创建时间: 2018/4/10 16:36
 * 作者:  xiangwb
 */
@Data
public class StatisticsModel {
    /**
     * 应用主键
     */
    private String applicationId;
    /**
     * 服务器节点主键
     */
    private String nodeId;
    /**
     * 端口
     */
    private Integer port;
    /**
     * 域名
     */
    private String domain;
    /**
     * 中间件
     */
    private String middleSoftWareId;
    /**
     * 版本
     */
    private String version;
    /**
     * 信息完善程度数值 临时字段
     */
    private transient int perfectDegree;

    public Integer getPerfectDegree() {
        int applicationIdPD = 20;
        int nodeIdPD = 10;
        int applicationPortPD = 20;
        int applicationDomainPD = 15;
        int middleSoftWarePD = 15;
        int versionPD = 10;
        int sum = 0;
        sum = sum + (StringUtils.isEmpty(this.getApplicationId()) ? 0 : applicationIdPD);
        sum = sum + (StringUtils.isEmpty(this.getNodeId()) ? 0 : nodeIdPD);
        sum = sum + (this.getPort() == null ? 0 : applicationPortPD);
        sum = sum + (StringUtils.isEmpty(this.getDomain()) ? 0 : applicationDomainPD);
        sum = sum + (StringUtils.isEmpty(this.getMiddleSoftWareId()) ? 0 : middleSoftWarePD);
        sum = sum + (StringUtils.isEmpty(this.getVersion()) ? 0 : versionPD);
        return sum;
    }

    public static void main(String[] args) {
        StatisticsModel model = new StatisticsModel();
        model.setApplicationId("11");
        model.setDomain("22");
        System.out.println(model.getPerfectDegree());
    }
}
