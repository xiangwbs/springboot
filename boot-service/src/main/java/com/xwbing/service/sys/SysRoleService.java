package com.xwbing.service.sys;

import com.xwbing.domain.entity.sys.SysRole;
import com.xwbing.domain.entity.sys.SysRoleAuthority;
import com.xwbing.domain.entity.sys.SysUserRole;
import com.xwbing.exception.BusinessException;
import com.xwbing.domain.repository.sys.SysRoleRepository;
import com.xwbing.util.PassWordUtil;
import com.xwbing.util.RestMessage;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 项目名称: boot-module-demo
 * 创建时间: 2017/11/14 9:24
 * 作者: xiangwb
 * 说明: 角色服务层
 */
@Service
public class SysRoleService {
    @Resource
    private SysRoleRepository sysRoleRepository;
    @Resource
    private SysUserRoleService sysUserRoleService;
    @Resource
    private SysRoleAuthorityService sysRoleAuthorityService;

    /**
     * 保存角色
     *
     * @param sysRole
     * @return
     */
    public RestMessage save(SysRole sysRole) {
        RestMessage result = new RestMessage();
        //检查编码
        boolean b = uniqueCode(sysRole.getCode(), null);
        if (!b) {
            throw new BusinessException("该编码已存在");
        }
        String id = PassWordUtil.createId();
        sysRole.setId(id);
        sysRole.setCreateTime(new Date());
        SysRole save = sysRoleRepository.save(sysRole);
        if (save != null) {
            result.setSuccess(true);
            result.setId(id);
            result.setMessage("保存角色成功");
        } else {
            result.setMessage("保存角色失败");
        }
        return result;
    }

    /**
     * 删除角色
     *
     * @param id
     * @return
     */
    public RestMessage removeById(String id) {
        RestMessage result = new RestMessage();
        SysRole one = getById(id);
        //检查角色是否存在
        if (one == null) {
            throw new BusinessException("该角色不存在");
        }
        //删除角色
        sysRoleRepository.delete(id);
        //删除角色权限
        List<SysRoleAuthority> roleAuthorities = sysRoleAuthorityService.listByRoleId(id);
        if (CollectionUtils.isNotEmpty(roleAuthorities)) {
            sysRoleAuthorityService.removeBatch(roleAuthorities);
        }
        result.setMessage("删除成功");
        result.setSuccess(true);
        return result;
    }

    /**
     * 修改角色
     *
     * @param sysRole
     * @return
     */
    public RestMessage update(SysRole sysRole) {
        RestMessage result = new RestMessage();
        String id = sysRole.getId();
        SysRole old = getById(id);
        //检查角色时候存在
        if (old == null) {
            throw new BusinessException("该角色不存在");
        }
        //检查编码
        boolean b = uniqueCode(sysRole.getCode(), id);
        if (!b) {
            throw new BusinessException("该编码已存在");
        }
        old.setModifiedTime(new Date());
        old.setName(sysRole.getName());
        old.setCode(sysRole.getCode());
        old.setEnable(sysRole.getEnable());
        old.setRemark(sysRole.getRemark());
        SysRole save = sysRoleRepository.save(old);
        if (save != null) {
            result.setSuccess(true);
            result.setId(id);
            result.setMessage("修改角色成功");
        } else {
            result.setMessage("修改角色失败");
        }
        return result;
    }

    /**
     * 根据主键查找
     *
     * @param id
     * @return
     */
    public SysRole getById(String id) {
        return sysRoleRepository.findOne(id);
    }

    /**
     * 根据是否启用列表查询
     *
     * @param enable
     * @return
     */
    public List<SysRole> listAllByEnable(String enable) {
        if (StringUtils.isNotEmpty(enable)) {
            return sysRoleRepository.getByEnable(enable);
        } else {
            return sysRoleRepository.findAll();
        }
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
        List<SysUserRole> sysUserRoles = sysUserRoleService.listByUserId(userId);
        if (sysUserRoles == null) {
            return list;
        }
        //根据角色id获取对应角色列表
        List<String> roleIds = sysUserRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(roleIds)) {
            if (StringUtils.isNotEmpty(enable)) {
                list = sysRoleRepository.getByEnableAndIdIn(enable, roleIds);
            } else {
                list = sysRoleRepository.getByIdIn(roleIds);
            }
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
        SysRole one = sysRoleRepository.getByCode(code);
        return one == null || StringUtils.isNotEmpty(id) && id.equals(one.getId());
    }
}
