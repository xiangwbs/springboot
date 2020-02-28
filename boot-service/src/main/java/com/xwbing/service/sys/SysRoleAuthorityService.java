package com.xwbing.service.sys;

import com.xwbing.domain.entity.sys.SysRoleAuthority;
import com.xwbing.domain.mapper.sys.SysRoleAuthorityMapper;
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
 * 创建时间: 2017/11/14 15:15
 * 作者: xiangwb
 * 说明: 角色权限服务层
 */
@Service
public class SysRoleAuthorityService extends BaseService<SysRoleAuthorityMapper, SysRoleAuthority> {
    @Resource
    private SysRoleAuthorityMapper sysRoleAuthorityMapper;

    @Override
    protected SysRoleAuthorityMapper getMapper() {
        return sysRoleAuthorityMapper;
    }

    /**
     * 执行用户角色权限保存操作,保存之前先判断是否存在，存在删除
     *
     * @param list
     * @param roleId
     * @return
     */
    @Transactional
    public RestMessage saveBatch(List<SysRoleAuthority> list, String roleId) {
        //获取角色原有权限
        List<SysRoleAuthority> roleAuthorities = listByRoleId(roleId);
        //删除原有权限
        if (CollectionUtils.isNotEmpty(roleAuthorities)) {
            List<String> ids = roleAuthorities.stream().map(SysRoleAuthority::getId).collect(Collectors.toList());
            super.removeByIds(ids);
        }
        //新增角色权限
        return super.saveBatch(list);
    }


    /**
     * 根据角色主键获取
     *
     * @param roleId
     * @return
     */
    public List<SysRoleAuthority> listByRoleId(String roleId) {
        if (StringUtils.isEmpty(roleId)) {
            return Collections.EMPTY_LIST;
        } else {
            return sysRoleAuthorityMapper.findByRoleId(roleId);
        }
    }
}

