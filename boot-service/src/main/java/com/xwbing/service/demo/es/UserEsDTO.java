package com.xwbing.service.demo.es;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户es搜索参数
 *
 * @author jifeng
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEsDTO {
    private String searchKey;
    private Long userId;

    private List<Long> excludeUserIds;
    /**
     * 性别:0=未知,1=男,2=女,3=保密
     */
    private UserGenderEnum gender;
    private boolean wasMatchSearch;
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
     * 最近登录时间-开始时间
     */
    private LocalDateTime latestLoginBeginTime;
    /**
     * 最近登录时间-结束时间
     */
    private LocalDateTime latestLoginEndTime;
    /**
     * 创建时间-开始时间
     */
    private LocalDateTime creationBeginTime;
    /**
     * 创建时间-结束时间
     */
    private LocalDateTime creationEndTime;
    /**
     * 行为标签
     */
    private List<Long> actionTagList;
    /**
     * crm标签
     */
    private List<Long> crmTagIdList;
    /**
     * 页号
     */
    private Integer pageNumber;
    /**
     * 页大小
     */
    private Integer pageSize;
}