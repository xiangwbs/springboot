package com.xwbing.service.domain.entity.rest;

import com.xwbing.service.domain.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @description 导入任务记录
 * @author xiangwb
 * @date 2020/04/28 14:11
 */
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ImportTask extends BaseEntity {
    private static final long serialVersionUID = 1418892742203862328L;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 总条数
     */
    private Integer totalCount;
    /**
     * 失败条数
     */
    private Integer failCount;
    /**
     * 状态 export fail success
     */
    private String status;
    /**
     * 详情
     */
    private String detail;
    /**
     * 下载
     */
    private Boolean needDownload;
}