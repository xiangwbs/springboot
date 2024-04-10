package com.xwbing.service.domain.entity;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.xwbing.service.domain.entity.sys.SysUser;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daofeng
 * @version $Id$
 * @since 2021年07月16日 2:00 PM
 */
@Slf4j
@Data
public class ResVO<T> {
    private String code;
    private String msg;
    private boolean success;
    private T data;

    public static <T> T parse(String res, TypeReference<ResVO<T>> typeRef) {
        if (!JSONUtil.isTypeJSON(res)) {
            return null;
        }
        ResVO<T> resVO = JSON.parseObject(res, typeRef);
        if (resVO.isSuccess()) {
            return resVO.getData();
        } else {
            return null;
        }
    }

    public static void main(String[] args) {
        ResVO<SysUser> sysUserResVO = new ResVO<>();
        sysUserResVO.setCode("212");
        SysUser sysUser = new SysUser();
        sysUser.setName("anm");
        sysUserResVO.setData(sysUser);
        sysUser = parse(JSONUtil.toJsonStr(sysUserResVO), new TypeReference<ResVO<SysUser>>() {
        });
    }
}