package com.xwbing.domain.entity.vo;

import com.xwbing.domain.entity.sys.SysAuthority;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * 创建时间: 2017/11/14 15:43
 * 作者: xiangwb
 * 说明: 权限树信息
 */
@Data
public class SysAuthVo extends SysAuthority {
    private static final long serialVersionUID = 2292744062645600313L;

    public SysAuthVo() {
    }

    public SysAuthVo(SysAuthority orig) {
        BeanUtils.copyProperties(orig, this);
    }

    private List<SysAuthVo> children;
}
