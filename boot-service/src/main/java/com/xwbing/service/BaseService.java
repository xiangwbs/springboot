package com.xwbing.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xwbing.domain.entity.BaseEntity;
import com.xwbing.domain.mapper.BaseMapper;
import com.xwbing.util.Pagination;
import com.xwbing.util.RestMessage;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author: xiangwb
 * @date: 2018/7/30 20:14
 * @description: BaseService
 */
public abstract class BaseService<M extends BaseMapper<T>, T extends BaseEntity> {
    /**
     * 获取mapper
     *
     * @return
     */
    protected abstract M getMapper();

    /**
     * 增
     *
     * @param t
     * @return
     */
    public RestMessage save(T t) {
        RestMessage result = new RestMessage();
        int row = getMapper().insert(t);
        if (row == 1) {
            result.setSuccess(true);
            result.setId(t.getId());
            result.setMessage("保存成功");
        } else {
            result.setMessage("保存失败");
        }
        return result;
    }

    /**
     * 批量新增
     *
     * @param list
     * @return
     */
    public RestMessage saveBatch(List<T> list) {
        RestMessage result = new RestMessage();
        int row = getMapper().insertBatch(list);
        if (row >= 1) {
            result.setSuccess(true);
            result.setMessage("批量保存成功");
        } else {
            result.setMessage("批量保存失败");
        }
        return result;
    }

    /**
     * 根据主键删除
     *
     * @param id
     * @return
     */
    public RestMessage removeById(String id) {
        RestMessage result = new RestMessage();
        int row = getMapper().deleteById(id);
        if (row == 1) {
            result.setSuccess(true);
            result.setMessage("删除成功");
        } else {
            result.setMessage("删除失败");
        }
        return result;
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    public RestMessage removeByIds(List<String> ids) {
        RestMessage result = new RestMessage();
        int row = getMapper().deleteByIds(ids);
        if (row >= 1) {
            result.setSuccess(true);
            result.setMessage("批量删除成功");
        } else {
            result.setMessage("批量删除失败");
        }
        return result;
    }

    /**
     * 根据条件删除
     *
     * @param params
     * @return
     */
    public RestMessage removeByParam(Map<String, Object> params) {
        RestMessage result = new RestMessage();
        int row = getMapper().delete(params);
        if (row >= 1) {
            result.setSuccess(true);
            result.setMessage("条件删除成功");
        } else {
            result.setMessage("条件删除失败");
        }
        return result;
    }

    /**
     * 修改
     *
     * @param t
     * @return
     */
    public RestMessage update(T t) {
        RestMessage result = new RestMessage();
        result.setId(t.getId());
        int update = getMapper().update(t);
        if (update == 1) {
            result.setSuccess(true);
            result.setMessage("修改成功");
        } else {
            result.setMessage("修改失败");
        }
        return result;
    }

    /**
     * 批量修改
     *
     * @param list
     * @return
     */
    public RestMessage updateBatch(List<T> list) {
        RestMessage result = new RestMessage();
        int row = getMapper().updateBatch(list);
        if (row >= 1) {
            result.setSuccess(true);
            result.setMessage("批量修改成功");
        } else {
            result.setMessage("批量修改失败");
        }
        return result;
    }

    /**
     * 根据主键查找
     *
     * @param id
     * @return
     */
    public T getById(String id) {
        if (id == null || id.length() == 0) {
            return null;
        } else {
            return getMapper().findById(id);
        }
    }

    /**
     * 根据ids列表查找
     *
     * @param ids
     * @return
     */
    public List<T> listByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.EMPTY_LIST;
        } else {
            return getMapper().findByIds(ids);
        }
    }

    /**
     * 根据条件列表查找
     *
     * @param params
     * @return
     */
    public List<T> listByParam(Map<String, Object> params) {
        return getMapper().find(params);
    }

    /**
     * 列表查询所有
     *
     * @return
     */
    public List<T> listAll() {
        return getMapper().findAll();
    }

    /**
     * 分页查询
     *
     * @param page
     * @param map
     * @return
     */
    public Pagination page(Pagination page, Map<String, Object> map) {
        PageInfo<T> pageInfo = PageHelper
                .startPage(page.getCurrentPage(), page.getPageSize())
                .doSelectPageInfo(() -> getMapper().find(map));
        return page.result(page, pageInfo);
    }
}
