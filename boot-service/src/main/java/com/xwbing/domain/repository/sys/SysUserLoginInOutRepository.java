package com.xwbing.domain.repository.sys;

import com.xwbing.domain.entity.sys.SysUserLoginInOut;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 项目名称: boot-module-demo
 * 创建时间: 2017/11/7 9:58
 * 作者: xiangwb
 * 说明: 用户登录登出数据访问层
 */
public interface SysUserLoginInOutRepository extends JpaRepository<SysUserLoginInOut, String> {
    List<SysUserLoginInOut> getByInoutType(int inout);
}
