package com.xwbing.domain.mapper.rest;

import com.xwbing.domain.entity.rest.FilesUpload;
import com.xwbing.domain.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 创建时间: 2018/6/1 21:50
 * 作者: xiangwb
 * 说明:
 */
public interface FilesUploadMapper extends BaseMapper<FilesUpload> {
    List<FilesUpload> findByName(@Param("name") String name, @Param("type") String type);
}
