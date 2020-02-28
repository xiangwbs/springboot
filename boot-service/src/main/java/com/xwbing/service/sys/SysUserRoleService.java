package com.xwbing.service.sys;

import com.xwbing.domain.entity.sys.SysUserRole;
import com.xwbing.domain.mapper.sys.SysUserRoleMapper;
import com.xwbing.service.BaseService;
import com.xwbing.util.RestMessage;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 创建时间: 2017/11/14 14:15
 * 作者: xiangwb
 * 说明: 用户角色服务层
 */
@Service
public class SysUserRoleService extends BaseService<SysUserRoleMapper, SysUserRole> {
    @Resource
    private SysUserRoleMapper sysUserRoleMapper;

    @Override
    protected SysUserRoleMapper getMapper() {
        return sysUserRoleMapper;
    }

    /**
     * 执行用户角色权限保存操作,保存之前先判断是否存在，存在删除
     *
     * @param list
     * @param userId
     * @return
     */
    @Transactional
    public RestMessage saveBatch(List<SysUserRole> list, String userId) {
        //获取用户原有角色
        List<SysUserRole> sysUserRoles = listByUserId(userId);
        //删除原有角色
        if (CollectionUtils.isNotEmpty(sysUserRoles)) {
            List<String> ids = sysUserRoles.stream().map(SysUserRole::getId).collect(Collectors.toList());
            super.removeByIds(ids);
        }
        //新增用户角色
        return super.saveBatch(list);
    }

    /**
     * 根据用户主键获取
     *
     * @param userId
     * @return
     */
    public List<SysUserRole> listByUserId(String userId) {
        if (StringUtils.isEmpty(userId)) {
            return Collections.EMPTY_LIST;
        } else {
            return sysUserRoleMapper.findByUserId(userId);
        }
    }
}

