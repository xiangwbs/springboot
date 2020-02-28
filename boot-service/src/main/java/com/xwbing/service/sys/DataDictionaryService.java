package com.xwbing.service.sys;

import com.xwbing.constant.CommonConstant;
import com.xwbing.domain.entity.sys.DataDictionary;
import com.xwbing.domain.repository.DataDictionaryRepository;
import com.xwbing.exception.BusinessException;
import com.xwbing.util.RestMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 创建时间: 2018/2/26 10:29
 * 作者: xiangwb
 * 说明: 数据字典服务层
 */
@Service
public class DataDictionaryService {
    @Resource
    private DataDictionaryRepository dataDictionaryRepository;

    public RestMessage save(DataDictionary dictionary) {
        RestMessage result = new RestMessage();
        //检查编码
        boolean b = uniqueCode(dictionary.getCode(), null);
        if (!b) {
            throw new BusinessException("该编码已存在");
        }
        dictionary.setCreateTime(new Date());
        if (StringUtils.isEmpty(dictionary.getParentId())) {
            dictionary.setParentId(CommonConstant.ROOT);
        }
        DataDictionary save = dataDictionaryRepository.save(dictionary);
        if (save != null) {
            result.setSuccess(true);
            result.setMessage("保存数据字典成功");
        } else {
            result.setMessage("保存数据字典失败");
        }
        return result;
    }

    /**
     * 更新数据字典 此处code是不允许修改的，否则要出错
     *
     * @param dictionary
     * @return
     */
    public RestMessage update(DataDictionary dictionary) {
        RestMessage result = new RestMessage();
        String id = dictionary.getId();
        //判断该字典是否存在
        DataDictionary old = getById(id);
        if (old == null) {
            throw new BusinessException("该字典不存在");
        }
        old.setName(dictionary.getName());
        old.setDescription(dictionary.getDescription());
        old.setEnable(dictionary.getEnable());
        old.setModifiedTime(new Date());
        DataDictionary save = dataDictionaryRepository.save(old);
        if (save != null) {
            result.setMessage("更新成功");
            result.setSuccess(true);
        } else {
            result.setMessage("更新失败");
        }
        return result;
    }

    public DataDictionary getById(String id) {
        return dataDictionaryRepository.findOne(id);
    }

    /**
     * true为唯一
     *
     * @param id
     * @param code
     * @return
     */
    public boolean uniqueCode(String code, String id) {
        if (StringUtils.isEmpty(code)) {
            throw new BusinessException("code不能为空");
        }
        DataDictionary one = dataDictionaryRepository.getByCode(code);
        return one == null || StringUtils.isNotEmpty(id) && id.equals(one.getId());
    }

    /**
     * 根据父类，是否启用查询
     *
     * @param parentId
     * @param enable
     * @return
     */
    public List<DataDictionary> findListByParent(String parentId, String enable) {
        if (StringUtils.isNotEmpty(enable)) {
            return dataDictionaryRepository.getByParentIdAndEnable(parentId, enable);
        } else {
            return dataDictionaryRepository.getByParentId(parentId);
        }
    }

    /**
     * 根据编码查询字典列表
     *
     * @param code
     * @param enable
     * @return
     */
    public List<DataDictionary> findListByCode(String code, String enable) {
        DataDictionary dictionary = dataDictionaryRepository.getByCodeAndEnable(code, "Y");
        if (dictionary != null) {
            String id = dictionary.getId();
            return findListByParent(id, enable);
        } else {
            return Collections.emptyList();
        }
    }
}
