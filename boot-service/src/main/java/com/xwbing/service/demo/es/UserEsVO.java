package com.xwbing.service.demo.es;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import cn.hutool.core.date.DatePattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户es信息
 *
 * @author jifeng
 */
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserEsVO {

    private Long userId;
    /**
     * 昵称
     */
    private String nickName;
    private String name;
    /**
     * 手机号码
     */
    private String mobile;
    /**
     * 0=未知，1=男，2=女，3=保密)
     */
    private UserGenderEnum gender;
    /**
     * 钉钉CRM服务窗状态，99=未加入，10=已关注，20=已取关
     */
    private UserDingCRMStatusEnum dingCrmStatus;
    /**
     * 最近登录时间
     */
    @JsonFormat(shape = Shape.STRING, pattern = DatePattern.NORM_DATETIME_PATTERN, timezone = "GMT+8")
    private LocalDateTime latestLoginTime;
    /**
     * 创建时间
     */
    @JsonFormat(shape = Shape.STRING, pattern = DatePattern.NORM_DATETIME_PATTERN, timezone = "GMT+8")
    private LocalDateTime creationDate;
    /**
     * crm 标签 id
     */
    private List<Long> crmTagIdList;
    /**
     * 自定义标签
     */
    private List<Long> actionTagList;
}