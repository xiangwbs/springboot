package com.xwbing.domain.entity.rest;

import com.xwbing.domain.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @description 
 * @author xiangwb
 * @date 2020/04/28 14:11
 */
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ImportFailLog extends BaseEntity {
    private static final long serialVersionUID = 4271781765477947729L;
    /**
     * 导入任务id
     */
    private String importId;
    /**
     * 描述
     */
    private String remark;
    /**
     * 原始数据内容
     */
    private String content;
}