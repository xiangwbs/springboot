package com.xwbing.service.sys;

import com.xwbing.constant.CommonConstant;
import com.xwbing.domain.entity.sys.SysAuthority;
import com.xwbing.domain.entity.sys.SysRoleAuthority;
import com.xwbing.domain.entity.vo.SysAuthVo;
import com.xwbing.domain.mapper.sys.SysAuthorityMapper;
import com.xwbing.exception.BusinessException;
import com.xwbing.service.BaseService;
import com.xwbing.util.Pagination;
import com.xwbing.util.RestMessage;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 创建时间: 2017/11/14 13:20
 * 作者: xiangwb
 * 说明: 权限服务层
 */
@Service
public class SysAuthorityService extends BaseService<SysAuthorityMapper, SysAuthority> {
    @Resource
    private SysAuthorityMapper authorityMapper;
    @Resource
    private SysRoleAuthorityService roleAuthorityService;

    @Override
    protected SysAuthorityMapper getMapper() {
        return authorityMapper;
    }

    /**
     * 保存权限
     *
     * @param sysAuthority
     * @return
     */
    public RestMessage save(SysAuthority sysAuthority) {
        //检查编码
        boolean b = uniqueCode(sysAuthority.getCode());
        if (!b) {
            throw new BusinessException("该编码已存在");
        }
        //排序处理
        if (sysAuthority.getSort() == null) {
            int sort = authorityMapper.getMaxSort() + 1;
            sysAuthority.setSort(sort);
        } else {
            boolean sorted = uniqueSort(sysAuthority.getSort(), null);
            if (!sorted) {
                throw new BusinessException("该排序编号已存在");
            }
        }
        if (StringUtils.isEmpty(sysAuthority.getParentId())) {
            sysAuthority.setParentId(CommonConstant.ROOT);
        }
        return super.save(sysAuthority);
    }

    /**
     * 删除权限
     *
     * @param id
     * @return
     */
    @Transactional
    public RestMessage removeById(String id) {
        //判断该权限是否存在
        SysAuthority one = getById(id);
        if (one == null) {
            throw new BusinessException("该权限不存在");
        }
        //删除自身
        RestMessage result = super.removeById(id);
        //如果有子节点,递归删除子节点
        List<SysAuthority> list = listChildrenForRemove(id);
        if (CollectionUtils.isNotEmpty(list)) {
            List<String> ids = list.stream().map(SysAuthority::getId).collect(Collectors.toList());
            super.removeByIds(ids);
        }
        return result;
    }

    /**
     * 修改权限
     *
     * @param sysAuthority
     * @return
     */
    public RestMessage update(SysAuthority sysAuthority) {
        String id = sysAuthority.getId();
        //判断该权限是否存在
        SysAuthority old = super.getById(id);
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
        return super.update(old);
    }

    /**
     * 根据状态获取所有权限
     *
     * @param enable
     * @return
     */
    public List<SysAuthority> listByEnable(String enable) {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isNotEmpty(enable)) {
            map.put("enable", enable);
        }
        return super.listByParam(map);
    }

    /**
     * 分页查询
     *
     * @param enable
     * @return
     */
    public Pagination pageByEnable(String enable, Pagination page) {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isNotEmpty(enable)) {
            map.put("enable", enable);
        }
        return super.page(page, map);
    }

    /**
     * 根据状态查询所有子节点
     *
     * @param parentId
     * @param enable
     * @return
     */
    public List<SysAuthority> listByParentEnable(String parentId, String enable) {
        Map<String, Object> map = new HashMap<>();
        map.put("parentId", parentId);
        if (StringUtils.isNotEmpty(enable)) {
            map.put("enable", enable);
        }
        return super.listByParam(map);
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
        List<SysRoleAuthority> roleAuthorities = roleAuthorityService.listByRoleId(roleId);
        if (CollectionUtils.isEmpty(roleAuthorities)) {
            return list;
        }
        //根据权限id获取对应权限列表
        List<String> authorityIds = roleAuthorities.stream().map(SysRoleAuthority::getAuthorityId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(authorityIds)) {
            Map<String, Object> map = new HashMap<>();
            map.put("authorityIds", authorityIds);
            if (StringUtils.isNotEmpty(enable)) {
                map.put("enable", enable);
            }
            list = super.listByParam(map);
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
            return super.updateBatch(sysAuthorities).isSuccess();
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
        Map<String, Object> map = new HashMap<>();
        map.put("parentId", parentId);
        if (StringUtils.isNotEmpty(enable)) {
            map.put("enable", enable);
        }
        List<SysAuthority> authorities = super.listByParam(map);
        if (CollectionUtils.isEmpty(authorities)) {
            return Collections.EMPTY_LIST;
        }
        SysAuthVo vo;
        List<SysAuthVo> list = new ArrayList<>();
        for (SysAuthority authority : authorities) {
            vo = new SysAuthVo(authority);
            vo.setChildren(listChildren(vo.getId(), enable));
            list.add(vo);
        }
        return list;
    }

    /**
     * 递归查询父节点下所有权限的id集合,并将状态设置为禁用(禁用权限时用)
     *
     * @param parentId
     * @return
     */
    private List<SysAuthority> disableChildren(String parentId) {
        //根据状态查询所有子节点
        List<SysAuthority> authorities = listByParentEnable(parentId, CommonConstant.IS_ENABLE);
        //遍历子集
        List<SysAuthority> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(authorities)) {
            authorities.forEach(sysAuthority -> {
                sysAuthority.setEnable(CommonConstant.IS_NOT_ENABLE);
                list.add(sysAuthority);
                list.addAll(disableChildren(sysAuthority.getId()));
            });
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
        List<SysAuthority> authorities = listByParentEnable(parentId, null);
        //遍历子集
        List<SysAuthority> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(authorities)) {
            authorities.forEach(sysAuthority -> {
                list.add(sysAuthority);
                list.addAll(listChildrenForRemove(sysAuthority.getId()));
            });
        }
        return list;
    }

    /**
     * 检查code是否唯一 true唯一
     *
     * @param code
     * @return
     */
    private boolean uniqueCode(String code) {
        if (StringUtils.isEmpty(code)) {
            throw new BusinessException("code不能为空");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        List<SysAuthority> authorities = super.listByParam(map);
        return authorities.size() == 0;
    }

    /**
     * 检查排序是否存在 true不存在
     *
     * @param sort
     * @param id
     * @return
     */
    private boolean uniqueSort(Integer sort, String id) {
        if (sort == null) {
            throw new BusinessException("sort不能为空");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("sort", sort);
        if (StringUtils.isNotEmpty(id)) {
            map.put("id", id);
        }
        List<SysAuthority> authorities = super.listByParam(map);
        return authorities.size() == 0;
    }
}
