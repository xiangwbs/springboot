package com.xwbing.service.sys;

import com.xwbing.constant.CommonConstant;
import com.xwbing.domain.entity.sys.SysRole;
import com.xwbing.domain.entity.sys.SysRoleAuthority;
import com.xwbing.domain.entity.sys.SysUserRole;
import com.xwbing.domain.mapper.sys.SysRoleMapper;
import com.xwbing.exception.BusinessException;
import com.xwbing.service.BaseService;
import com.xwbing.util.Pagination;
import com.xwbing.util.RestMessage;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 创建时间: 2017/11/14 9:24
 * 作者: xiangwb
 * 说明: 角色服务层
 */
@Service
public class SysRoleService extends BaseService<SysRoleMapper,SysRole> {
    @Resource
    private SysRoleAuthorityService roleAuthorityService;
    @Resource
    private SysUserRoleService userRoleService;
    @Resource
    private SysRoleMapper roleMapper;

    @Override
    protected SysRoleMapper getMapper() {
        return roleMapper;
    }

    /**
     * 保存角色
     *
     * @param sysRole
     * @return
     */
    public RestMessage save(SysRole sysRole) {
        //检查编码
        boolean b = uniqueCode(sysRole.getCode(), null);
        if (!b) {
            throw new BusinessException("该编码已存在");
        }
        return super.save(sysRole);
    }

    /**
     * 删除角色
     *
     * @param id
     * @return
     */
    @Transactional
    public RestMessage removeById(String id) {
        SysRole one = getById(id);
        if (one == null) {
            throw new BusinessException("该角色不存在");
        }
        //删除角色
        RestMessage result = super.removeById(id);
        //删除角色权限
        List<SysRoleAuthority> roleAuthorities = roleAuthorityService.listByRoleId(id);
        if (CollectionUtils.isNotEmpty(roleAuthorities)) {
            List<String> ids = roleAuthorities.stream().map(SysRoleAuthority::getId).collect(Collectors.toList());
            result=roleAuthorityService.removeByIds(ids);
        }
        return result;
    }

    /**
     * 修改角色
     *
     * @param sysRole
     * @return
     */
    public RestMessage update(SysRole sysRole) {
        String id = sysRole.getId();
        SysRole old = super.getById(id);
        if (old == null) {
            throw new BusinessException("该角色不存在");
        }
        //检查编码
        boolean b = uniqueCode(sysRole.getCode(), id);
        if (!b) {
            throw new BusinessException("该编码已存在");
        }
        old.setName(sysRole.getName());
        old.setCode(sysRole.getCode());
        old.setEnable(sysRole.getEnable());
        old.setRemark(sysRole.getRemark());
        return super.update(old);
    }


    /**
     * 根据是否启用查询
     *
     * @param enable
     * @return
     */
    public Pagination pageByEnable(String enable, Pagination page) {
        Map<String, Object> map = new HashMap<>();
        map.put("enable", enable);
        return super.page(page, map);
    }

    /**
     * 根据用户主键，是否启用状态查询角色列表
     *
     * @param userId
     * @param enable
     * @return
     */
    public List<SysRole> listByUserIdEnable(String userId, String enable) {
        List<SysRole> list = new ArrayList<>();
        //从用户角色表中获取所有该用户id的角色
        List<SysUserRole> sysUserRoles = userRoleService.listByUserId(userId);
        if (sysUserRoles == null) {
            return list;
        }
        //根据角色id获取对应角色列表
        List<String> roleIds = sysUserRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(roleIds)) {
            Map<String, Object> map = new HashMap<>();
            map.put("ids", roleIds);
            if (StringUtils.isNotEmpty(enable)) {
                map.put("enable", CommonConstant.IS_ENABLE);
            }
            list = super.listByParam(map);
        }
        return list;
    }

    /**
     * 检查code是否唯一 true唯一
     *
     * @param code
     * @param id
     * @return
     */
    private boolean uniqueCode(String code, String id) {
        if (StringUtils.isEmpty(code)) {
            throw new BusinessException("code不能为空");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        if (StringUtils.isNotEmpty(id)) {
            map.put("id", id);
        }
        List<SysRole> sysRoles =super.listByParam(map);
        return sysRoles.size() == 0;
    }
}
