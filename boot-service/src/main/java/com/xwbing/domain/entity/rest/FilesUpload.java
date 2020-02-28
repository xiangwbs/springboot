package com.xwbing.domain.entity.rest;

import com.xwbing.domain.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: xiangwb
 * @date: 2018/06/13 21:54
 * @description:
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FilesUpload extends BaseEntity {
    private static final long serialVersionUID = 3284231281346882055L;
    public static String table = "file_upload";
    private String name;
    private String type;
    private String data;
}
