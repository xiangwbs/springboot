package com.xwbing.service.domain.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.xwbing.service.domain.entity.sys.SysUser;
import com.xwbing.service.exception.BusinessException;
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
    private T data;

    public static <T> T checkRes(ResVO<T> res) {
        String code = res.getCode();
        if (!"200".equals(code)) {
            log.error("code:{} msg:{}", res.getCode(), res.getMsg());
            throw new BusinessException(res.getMsg());
        }
        return res.getData();
    }

    public static boolean isSuccess(ResVO resVO) {
        return resVO != null && "200".equals(resVO.getCode());
    }

    public static void main(String[] args) {
        ResVO<SysUser> sysUserResVO = JSON.parseObject("", new TypeReference<ResVO<SysUser>>() {
        });
    }
}