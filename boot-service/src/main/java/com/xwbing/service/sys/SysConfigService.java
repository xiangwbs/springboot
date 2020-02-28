package com.xwbing.service.sys;

import com.xwbing.domain.entity.sys.SysConfig;
import com.xwbing.domain.repository.SysConfigRepository;
import com.xwbing.exception.BusinessException;
import com.xwbing.util.RestMessage;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 说明: 系统配置服务层
 * 创建时间: 2017/5/5 16:44
 * 作者:  xiangwb
 */
@Service
public class SysConfigService {
    @Resource
    private SysConfigRepository sysConfigRepository;

    /**
     * 保存
     *
     * @param sysConfig
     * @return
     */
    public RestMessage save(SysConfig sysConfig) {
        RestMessage result = new RestMessage();
        if (sysConfig == null) {
            throw new BusinessException("配置数据不能为空");
        }
        //检查code是否存在
        SysConfig old = getByCode(sysConfig.getCode());
        if (old != null) {
            throw new BusinessException(sysConfig.getCode() + "已存在");
        }
        sysConfig.setCreateTime(new Date());
        SysConfig one = sysConfigRepository.save(sysConfig);
        if (one != null) {
            result.setSuccess(true);
            result.setMessage("保存配置成功");
        } else {
            result.setMessage("保存配置失败");
        }
        return result;
    }

    /**
     * 根据code删除配置
     *
     * @param code
     * @return
     */
    public RestMessage removeByCode(String code) {
        RestMessage result = new RestMessage();
        SysConfig old = getByCode(code);
        if (old == null) {
            throw new BusinessException("该配置项不存在");
        }
        sysConfigRepository.delete(old.getId());
        result.setSuccess(true);
        result.setMessage("删除配置成功");
        return result;
    }

    /**
     * 根据code查找配置
     *
     * @param code
     * @return
     */
    public SysConfig getByCode(String code) {
        return sysConfigRepository.getByCode(code);
    }

    /**
     * 修改
     *
     * @param sysConfig
     * @return
     */
    public RestMessage update(SysConfig sysConfig) {
        RestMessage result = new RestMessage();
        SysConfig old = getByCode(sysConfig.getCode());
        if (old == null) {
            throw new BusinessException("该配置项不存在");
        }
        old.setValue(sysConfig.getValue());
        old.setName(sysConfig.getName());
        old.setEnable(sysConfig.getEnable());
        old.setModifiedTime(new Date());
        SysConfig save = sysConfigRepository.save(old);
        if (save != null) {
            result.setMessage("更新成功");
            result.setSuccess(true);
        } else {
            result.setMessage("更新失败");
        }
        return result;
    }

    /**
     * 根据状态获取配置列表
     *
     * @param enable
     * @return
     */
    public List<SysConfig> listByEnable(String enable) {
        return sysConfigRepository.getByEnable(enable);
    }
}
