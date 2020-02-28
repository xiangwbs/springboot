package com.xwbing.service.rest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xwbing.constant.CommonConstant;
import com.xwbing.domain.entity.model.EmailModel;
import com.xwbing.domain.entity.rest.FilesUpload;
import com.xwbing.domain.entity.sys.SysConfig;
import com.xwbing.exception.BusinessException;
import com.xwbing.service.sys.SysConfigService;
import com.xwbing.util.CommonDataUtil;
import com.xwbing.util.DigestsUtil;
import com.xwbing.util.RestMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

/**
 * 创建时间: 2018/5/7 8:59
 * 作者: xiangwb
 * 说明:
 */
@Service
public class CommonService {
    @Resource
    private UploadService uploadService;
    @Resource
    private SysConfigService sysConfigService;

    /**
     * 保存信息表单提交时获取校验签名
     *
     * @param request
     * @return
     */
    public String getSign(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String sign = DigestsUtil.getSign();
        session.setAttribute("sign", sign);
        return sign;
    }

    /**
     * 保存信息表单提交时获取校验签名
     *
     * @return
     */
    public String getSign() {
        String sign = DigestsUtil.getSign();
        CommonDataUtil.setData(sign, sign, CommonDataUtil.MINUTE * 30);
        return sign;
    }

    /**
     * 上传文件
     *
     * @param file
     * @return
     */
    public RestMessage upload(MultipartFile file) {
        if (file == null) {
            throw new BusinessException("请选择文件");
        }
        FilesUpload filesUpload = new FilesUpload();
        //原始名字
        String originName = file.getOriginalFilename();
        filesUpload.setName(originName);
        //获取文件后缀名
        String fileType = originName.substring(originName.lastIndexOf(".") + 1);
        filesUpload.setType(fileType);
        byte[] data;
        try {
            InputStream is = file.getInputStream();
            data = new byte[is.available()];
            is.read(data);
            is.close();
        } catch (IOException e) {
            throw new BusinessException("读取数据错误");
        }
        //对数据字节进行base64编码
        String base64 = Base64.getEncoder().encodeToString(data);
        filesUpload.setData(base64);
        return uploadService.save(filesUpload);
    }

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
