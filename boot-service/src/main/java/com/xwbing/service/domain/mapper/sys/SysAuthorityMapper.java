package com.xwbing.service.domain.mapper.sys;

import com.xwbing.service.domain.entity.sys.SysAuthority;
import com.xwbing.service.domain.mapper.BaseMapper;

/**
 * 创建时间: 2018/6/1 21:50
 * 作者: xiangwb
 * 说明:
 */
public interface SysAuthorityMapper extends BaseMapper<SysAuthority> {
    int getMaxSort();
}
