package com.xwbing.service.sys;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xwbing.constant.CommonConstant;
import com.xwbing.domain.entity.model.EmailModel;
import com.xwbing.domain.entity.sys.SysConfig;
import com.xwbing.util.RestMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 项目名称: boot-module-demo
 * 创建时间: 2018/4/10 17:58
 * 作者: xiangwb
 * 说明: 公共信息配置
 */
@Service
public class CommonConfigService {
    @Resource
    private SysConfigService sysConfigService;

    /**
     * 添加邮箱配置
     *
     * @param emailModel
     * @return
     */
    public RestMessage saveOrUpdateEmail(EmailModel emailModel) {
        SysConfig sysConfig = sysConfigService.getByCode(CommonConstant.EMAIL_KEY);
        if (sysConfig == null) {
            sysConfig = new SysConfig();
            sysConfig.setName("邮箱配置");
            sysConfig.setCode(CommonConstant.EMAIL_KEY);
            sysConfig.setEnable(CommonConstant.IS_ENABLE);
            sysConfig.setValue(JSON.toJSONString(emailModel));
            return sysConfigService.save(sysConfig);
        } else {
            sysConfig.setValue(JSON.toJSONString(emailModel));
            return sysConfigService.update(sysConfig);
        }
    }

    /**
     * 获取邮箱信息
     *
     * @return
     */
    public EmailModel getEmail() {
        SysConfig sysConfig = sysConfigService.getByCode(CommonConstant.EMAIL_KEY);
        if (sysConfig == null) {
            return new EmailModel();
        } else {
            String value = sysConfig.getValue();
            if (StringUtils.isEmpty(value)) {
                return new EmailModel();
            } else {
                return JSONObject.parseObject(value, EmailModel.class);
            }
        }
    }
}
