package com.xwbing.service.sys;

import com.xwbing.constant.CommonConstant;
import com.xwbing.domain.entity.sys.SysAuthority;
import com.xwbing.domain.entity.sys.SysRoleAuthority;
import com.xwbing.domain.entity.vo.SysAuthVo;
import com.xwbing.exception.BusinessException;
import com.xwbing.domain.repository.sys.SysAuthorityRepository;
import com.xwbing.util.PassWordUtil;
import com.xwbing.util.RestMessage;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 项目名称: boot-module-demo
 * 创建时间: 2017/11/14 13:20
 * 作者: xiangwb
 * 说明: 权限服务层
 */
@Service
public class SysAuthorityService {
    @Resource
    private SysAuthorityRepository sysAuthorityRepository;
    @Resource
    private SysRoleAuthorityService sysRoleAuthorityService;

    /**
     * 保存权限
     *
     * @param sysAuthority
     * @return
     */
    public RestMessage save(SysAuthority sysAuthority) {
        RestMessage result = new RestMessage();
        //检查编码
        boolean b = uniqueCode(sysAuthority.getCode(), null);
        if (!b) {
            throw new BusinessException("该编码已存在");
        }
        //排序处理
        if (sysAuthority.getSort() == null) {
            int sort = getSort() + 1;
            sysAuthority.setSort(sort);
        } else {
            boolean sorted = uniqueSort(sysAuthority.getSort(), null);
            if (!sorted) {
                throw new BusinessException("该排序编号已存在");
            }
        }
        //添加必要参数
        String id = PassWordUtil.createId();
        sysAuthority.setId(id);
        sysAuthority.setCreateTime(new Date());
        if (StringUtils.isEmpty(sysAuthority.getParentId())) {
            sysAuthority.setParentId(CommonConstant.ROOT);
        }
        //保存
        SysAuthority save = sysAuthorityRepository.save(sysAuthority);
        if (save != null) {
            result.setSuccess(true);
            result.setId(id);
            result.setMessage("保存权限成功");
        } else {
            result.setMessage("保存权限失败");
        }
        return result;
    }

    /**
     * 删除权限
     *
     * @param id
     * @return
     */
    public RestMessage removeById(String id) {
        RestMessage result = new RestMessage();
        //判断该权限是否存在
        SysAuthority one = getById(id);
        if (one == null) {
            throw new BusinessException("该权限不存在");
        }
        //删除自身
        sysAuthorityRepository.delete(id);
        //如果有子节点,递归删除子节点
        List<SysAuthority> list = listChildrenForRemove(id);
        if (CollectionUtils.isNotEmpty(list)) {
            sysAuthorityRepository.deleteInBatch(list);
        }
        result.setMessage("删除成功");
        result.setSuccess(true);
        return result;
    }

    /**
     * 修改权限
     *
     * @param sysAuthority
     * @return
     */
    public RestMessage update(SysAuthority sysAuthority) {
        RestMessage result = new RestMessage();
        String id = sysAuthority.getId();
        //判断该权限是否存在
        SysAuthority old = getById(id);
        if (old == null) {
            throw new BusinessException("该权限不存在");
        }
        //检查排序是否重复
        boolean sorted = uniqueSort(sysAuthority.getSort(), id);
        if (!sorted) {
            throw new BusinessException("该排序编号已存在");
        }
        old.setSort(sysAuthority.getSort());
        //其他参数更新
        old.setName(sysAuthority.getName());
        old.setEnable(sysAuthority.getEnable());
        old.setUrl(sysAuthority.getUrl());
//        old.setCode(sysAuthority.getCode());编码不能修改
        old.setType(sysAuthority.getType());
        SysAuthority save = sysAuthorityRepository.save(old);
        if (save != null) {
            result.setSuccess(true);
            result.setId(id);
            result.setMessage("修改权限成功");
        } else {
            result.setMessage("修改权限失败");
        }
        return result;
    }

    /**
     * 根据主键查找
     *
     * @param id
     * @return
     */
    public SysAuthority getById(String id) {
        return sysAuthorityRepository.findOne(id);
    }

    /**
     * 根据状态获取所有权限
     *
     * @param enable
     * @return
     */
    public List<SysAuthority> listByEnable(String enable) {
        if (StringUtils.isNotEmpty(enable)) {
            return sysAuthorityRepository.getByEnableOrderBySort(enable);
        } else {
            return sysAuthorityRepository.findAll(new Sort(Sort.Direction.ASC, "sort"));
        }
    }

    /**
     * 根据状态查询所有子节点
     *
     * @param parentId
     * @param enable
     * @return
     */
    public List<SysAuthority> listByParentEnable(String parentId, String enable) {
        if (StringUtils.isNotEmpty(enable)) {
            return sysAuthorityRepository.getByParentIdAndEnableOrderBySort(parentId, enable);
        } else {
            return sysAuthorityRepository.getByParentIdOrderBySort(parentId);
        }
    }

    /**
     * 根据角色id，是否启用查询权限列表
     *
     * @param roleId
     * @param enable
     * @return
     */
    public List<SysAuthority> listByRoleIdEnable(String roleId, String enable) {
        List<SysAuthority> list = new ArrayList<>();
        // 从角色权限表中获取所有该角色id的权限
        List<SysRoleAuthority> roleAuthorities = sysRoleAuthorityService.listByRoleId(roleId);
        if (CollectionUtils.isEmpty(roleAuthorities)) {
            return list;
        }
        //根据权限id获取对应权限列表
        List<String> authorityIds = roleAuthorities.stream().map(SysRoleAuthority::getAuthorityId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(authorityIds)) {
            if (StringUtils.isNotEmpty(enable)) {
                list = sysAuthorityRepository.getByEnableAndIdInOrderBySort(enable, authorityIds);
            } else {
                list = sysAuthorityRepository.getByIdInOrderBySort(authorityIds);
            }
        }
        return list;
    }


    /**
     * 根据父节点禁用所有子节点
     *
     * @param parentId
     * @return
     */
    public boolean disableChildrenByParentId(String parentId) {
        //递归查询父节点下所有权限的id集合,并将状态设置为禁用
        List<SysAuthority> sysAuthorities = disableChildren(parentId);
        //批量修改权限
        if (CollectionUtils.isNotEmpty(sysAuthorities)) {
            List<SysAuthority> save = sysAuthorityRepository.save(sysAuthorities);
            return CollectionUtils.isNotEmpty(save);
        }
        return false;
    }

    /**
     * 根据状态递归查询所有节点
     *
     * @param parentId
     * @param enable
     * @return
     */
    public List<SysAuthVo> listChildren(String parentId, String enable) {
        List<SysAuthVo> list = new ArrayList<>();
        List<SysAuthority> authoritys;
        if (StringUtils.isNotEmpty(enable)) {
            authoritys = sysAuthorityRepository.getByParentIdAndEnableOrderBySort(parentId, enable);
        } else {
            authoritys = sysAuthorityRepository.getByParentIdOrderBySort(parentId);
        }
        if (CollectionUtils.isEmpty(authoritys)) {
            return list;
        }
        SysAuthVo vo;
        for (SysAuthority authority : authoritys) {
            vo = new SysAuthVo(authority);
            vo.setChildren(listChildren(vo.getId(), enable));
            list.add(vo);
        }
        return list;
    }

    /**
     * 获取最大顺序
     *
     * @return
     */
    private int getSort() {
        Sort sort = new Sort(Sort.Direction.DESC, "sort");
        List<SysAuthority> all = sysAuthorityRepository.findAll(sort);
        if (CollectionUtils.isNotEmpty(all)) {
            return all.get(0).getSort();
        } else {
            return 0;
        }
    }

    /**
     * 递归查询父节点下所有权限的id集合,并将状态设置为禁用(禁用权限时用)
     *
     * @param parentId
     * @return
     */
    private List<SysAuthority> disableChildren(String parentId) {
        //根据状态查询所有子节点
        List<SysAuthority> sysAuthoritys = listByParentEnable(parentId, CommonConstant.IS_ENABLE);
        //遍历子集
        List<SysAuthority> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(sysAuthoritys)) {
            for (SysAuthority sysAuthority : sysAuthoritys) {
                sysAuthority.setEnable(CommonConstant.IS_NOT_ENABLE);
                sysAuthority.setModifiedTime(new Date());
                list.add(sysAuthority);
                list.addAll(disableChildren(sysAuthority.getId()));
            }
        }
        return list;
    }

    /**
     * 刪除时递归获取所有子节点
     *
     * @param parentId
     * @return
     */
    private List<SysAuthority> listChildrenForRemove(String parentId) {
        //根据状态查询所有子节点
        List<SysAuthority> sysAuthoritys = listByParentEnable(parentId, null);
        //遍历子集
        List<SysAuthority> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(sysAuthoritys)) {
            for (SysAuthority sysAuthority : sysAuthoritys) {
                list.add(sysAuthority);
                list.addAll(listChildrenForRemove(sysAuthority.getId()));
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
        SysAuthority one = sysAuthorityRepository.getByCode(code);
        return one == null || StringUtils.isNotEmpty(id) && id.equals(one.getId());
    }

    /**
     * 检查排序是否存在
     *
     * @param sort
     * @param id
     * @return
     */
    private boolean uniqueSort(Integer sort, String id) {
        if (sort == null) {
            throw new BusinessException("sort不能为空");
        }
        SysAuthority one = sysAuthorityRepository.getBySort(sort);
        return one == null || StringUtils.isNotEmpty(id) && id.equals(one.getId());
    }
}
