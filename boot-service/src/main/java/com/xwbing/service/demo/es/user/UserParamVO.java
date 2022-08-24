package com.xwbing.service.demo.es.user;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xwbing.service.enums.base.BaseEnum;

import cn.hutool.core.date.DatePattern;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserParamVO {
    @ApiModelProperty("关系")
    private SearchRelationTypeEnum relationType;

    //-------------------------------条件----------------------------------//
    @EsField("creationDate")
    @ApiModelProperty("注册开始时间，格式：yyyy-MM-dd")
    @JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN, timezone = "GMT+8")
    private LocalDate registerBeginTime;
    @EsField("creationDate")
    @ApiModelProperty("注册结束时间，格式：yyyy-MM-dd")
    @JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN, timezone = "GMT+8")
    private LocalDate registerEndTime;
    @EsField("dingCrmUnsubscribeDate")
    @ApiModelProperty("服务窗取关时间-开始时间，格式：yyyy-MM-dd")
    @JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN, timezone = "GMT+8")
    private LocalDate crmUnsubscribeBeginTime;
    @EsField("dingCrmUnsubscribeDate")
    @ApiModelProperty("服务窗取关时间-结束时间，格式：yyyy-MM-dd")
    @JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN, timezone = "GMT+8")
    private LocalDate crmUnsubscribeEndTime;
    @EsField("dsbJobList.code")
    @ApiModelProperty("丁税宝角色列表")
    private List<TaxpayerEnum> dsbJobList;
    @EsField("industryCodeList")
    @ApiModelProperty("所在行业")
    private List<String> industryList;
    @EsField("dingCrmStatus.code")
    @ApiModelProperty("钉钉CRM服务窗状态，99=未加入，10=已关注，20=已取关")
    private UserDingCRMStatusEnum userDingCrmStatus;
    @EsField("latestLoginTime")
    @ApiModelProperty("上次登录开始时间，格式：yyyy-MM-dd")
    @JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN, timezone = "GMT+8")
    private LocalDate latestLoginBeginTime;
    @EsField("latestLoginTime")
    @ApiModelProperty("上次登录结束时间，格式：yyyy-MM-dd")
    @JsonFormat(pattern = DatePattern.NORM_DATE_PATTERN, timezone = "GMT+8")
    private LocalDate latestLoginEndTime;
    @EsField("nickName")
    @ApiModelProperty("昵称")
    private String nickName;

    public enum UserDingCRMStatusEnum implements BaseEnum {
        NOT_SUBSCRIBE(99, "未关注"),
        SUBSCRIBE(10, "已关注"),
        UNSUBSCRIBE(20, "已取关");

        private final int code;
        private final String desc;

        UserDingCRMStatusEnum(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }

        @Override
        public int getCode() {
            return code;
        }
    }

    public enum TaxpayerEnum implements BaseEnum {
        LEGAL_PERSON(1, "法定代表人"),
        FINANCIAL_MANAGER(2, "财务负责人"),
        TAXER(3, "办税人"),
        TAXER_OTHER(4, "其他办税人"),
        TICKET_BUYER(5, "购票员");

        private final int code;
        private final String desc;

        TaxpayerEnum(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }

        @Override
        public int getCode() {
            return code;
        }
    }
}
