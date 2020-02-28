package com.xwbing.service.sys;

import com.xwbing.domain.entity.sys.SysUserRole;
import com.xwbing.domain.repository.sys.SysUserRoleRepository;
import com.xwbing.util.PassWordUtil;
import com.xwbing.util.RestMessage;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 项目名称: boot-module-demo
 * 创建时间: 2017/11/14 14:15
 * 作者: xiangwb
 * 说明: 用户角色服务层
 */
@Service
public class SysUserRoleService {
    @Resource
    private SysUserRoleRepository sysUserRoleRepository;

    /**
     * 执行用户角色权限保存操作,保存之前先判断是否存在，存在删除
     *
     * @param list
     * @param userId
     * @return
     */
    public RestMessage saveBatch(List<SysUserRole> list, String userId) {
        RestMessage result = new RestMessage();
        //获取用户原有角色
        List<SysUserRole> sysUserRoles = listByUserId(userId);
        //删除原有角色
        if (CollectionUtils.isNotEmpty(sysUserRoles)) {
            sysUserRoleRepository.deleteInBatch(sysUserRoles);
        }
        //新增用户角色
        list.forEach(sysUserRole -> {
            sysUserRole.setId(PassWordUtil.createId());
            sysUserRole.setCreateTime(new Date());
        });
        List<SysUserRole> save = sysUserRoleRepository.save(list);
        if (CollectionUtils.isNotEmpty(save)) {
            result.setSuccess(true);
            result.setMessage("保存用户角色成功");
        } else {
            result.setMessage("保存用户角色失败");
        }
        return result;
    }

    /**
     * 根据用户主键获取
     *
     * @param userId
     * @return
     */
    public List<SysUserRole> listByUserId(String userId) {
        return sysUserRoleRepository.getByUserId(userId);
    }

    /**
     * 批量删除
     *
     * @param userRoles
     * @return
     */
    public RestMessage removeBatch(List<SysUserRole> userRoles) {
        RestMessage result = new RestMessage();
        sysUserRoleRepository.deleteInBatch(userRoles);
        result.setSuccess(true);
        result.setMessage("批量删除成功");
        return result;
    }
}
