package com.xwbing.service.sys;

import com.xwbing.constant.CommonEnum;
import com.xwbing.domain.entity.sys.SysUser;
import com.xwbing.domain.entity.sys.SysUserLoginInOut;
import com.xwbing.domain.repository.sys.SysUserLoginInOutRepository;
import com.xwbing.util.DateUtil2;
import com.xwbing.util.PassWordUtil;
import com.xwbing.util.RestMessage;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 项目名称: boot-module-demo
 * 创建时间: 2017/11/7 9:56
 * 作者: xiangwb
 * 说明: 用户登录登出服务层
 */
@Service
public class SysUserLoginInOutService {
    @Resource
    private SysUserLoginInOutRepository loginInOutRepository;
    @Resource
    private SysUserService sysUserService;

    /**
     * 保存
     *
     * @param inOut
     * @return
     */
    public RestMessage save(SysUserLoginInOut inOut) {
        RestMessage result = new RestMessage();
        inOut.setId(PassWordUtil.createId());
        SysUserLoginInOut save = loginInOutRepository.save(inOut);
        if (save != null) {
            result.setSuccess(true);
            result.setMessage("保存登录登出信息成功");
        } else {
            result.setMessage("保存登录登出信息失败");
        }
        return result;
    }

    /**
     * 根据类型列表查询
     *
     * @param inout
     * @return
     */
    public List<SysUserLoginInOut> listByType(int inout) {
        Map<String, SysUser> userMap = sysUserService.listAll().stream().collect(Collectors.toMap(SysUser::getId, Function.identity()));
        List<SysUserLoginInOut> list = loginInOutRepository.getByInoutType(inout);
        if (CollectionUtils.isNotEmpty(list)) {
            if (MapUtils.isNotEmpty(userMap)) {
                list = list.stream().sorted((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime())).peek(inOut -> {
                    inOut.setUserIdName(userMap.get(inOut.getUserId()).getName());//用户姓名
                    inOut.setRecordTime(DateUtil2.dateToStr(inOut.getCreateTime(), DateUtil2.YYYY_MM_DD_HH_MM_SS));//记录时间
                    CommonEnum.LoginInOutEnum inOutEnum = Arrays.stream(CommonEnum.LoginInOutEnum.values()).filter(obj -> obj.getValue() == inout).findFirst().get();//登录登出
                    inOut.setInoutTypeName(inOutEnum.getName());
                }).collect(Collectors.toList());
            }
        }
        return list;
    }
}
