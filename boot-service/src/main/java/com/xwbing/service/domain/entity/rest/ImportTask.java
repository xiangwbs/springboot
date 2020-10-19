package com.xwbing.service.domain.entity.rest;

import com.xwbing.service.domain.entity.BaseEntity;
import com.xwbing.service.enums.ImportStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @description 导入任务记录
 * @author xiangwb
 * @date 2020/10/15 16:34
 */
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ImportTask extends BaseEntity {
    private static final long serialVersionUID = 2310583521450992889L;
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
     * 状态 1、正在导入中2、失败3、成功
     */
    private ImportStatusEnum status;
    /**
     * 详情
     */
    private String detail;
    /**
     * 下载
     */
    private Boolean needDownload;
}