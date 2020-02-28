package com.xwbing.domain.mapper.sys;

import com.xwbing.domain.entity.sys.SysUserLoginInOut;
import com.xwbing.domain.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * 创建时间: 2018/6/1 21:50
 * 作者: xiangwb
 * 说明:
 */
public interface SysUserLoginInOutMapper extends BaseMapper<SysUserLoginInOut> {
    List<SysUserLoginInOut> findByInoutType(Map<String, Object> map);

    List<SysUserLoginInOut> countByType(Map<String, Object> map);
}
