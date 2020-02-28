package com.xwbing.domain.repository;

import com.xwbing.domain.entity.sys.DataDictionary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 创建时间: 2018/2/26 10:43
 * 作者: xiangwb
 * 说明:
 */
public interface DataDictionaryRepository extends JpaRepository<DataDictionary, String> {
    DataDictionary getByCode(String code);

    DataDictionary getByCodeAndEnable(String code, String enable);

    List<DataDictionary> getByParentIdAndEnable(String parentId, String enable);

    List<DataDictionary> getByParentId(String parentId);
}
