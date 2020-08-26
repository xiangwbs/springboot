package com.xwbing.service.service.rest;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.xwbing.service.domain.entity.model.ExpressInfo;
import com.xwbing.service.domain.entity.vo.ExpressInfoVo;
import com.xwbing.service.enums.KdniaoExpressStatusEnum;
import com.xwbing.service.exception.BusinessException;
import com.xwbing.service.util.KdniaoUtil;

import lombok.extern.slf4j.Slf4j;


import static com.xwbing.service.util.KdniaoUtil.urlEncoder;

/**
 * 创建时间: 2017/11/17 9:14
 * 作者: xiangwb
 * 说明: 快递查询服务层
 */
@Slf4j
@Service
@PropertySource("classpath:kdniao.properties")
public class ExpressDeliveryService {
    /**
     * 电商用户ID
     */
    @Value("${EBusinessID}")
    private String EBusinessID;
    /**
     * 电商加密私钥
     */
    @Value("${appKey}")
    private String appKey;
    /**
     * 正式请求url
     */
    @Value("${reqURL}")
    private String reqURL;

    /**
     * 快递信息查询
     *
     * @param info
     * @return
     */
    public ExpressInfoVo queryOrderTraces(ExpressInfo info) {
        String requestData = "{'ShipperCode':'" + info.getShipperCode() + "','LogisticCode':'" + info.getLogisticCode() + "'}";
        Map<String, String> params = new HashMap<>();
        try {
            params.put("RequestData", KdniaoUtil.urlEncoder(requestData, "UTF-8"));
            params.put("EBusinessID", EBusinessID);
            params.put("RequestType", "1002");
            String dataSign = KdniaoUtil.encrypt(requestData, appKey, "UTF-8");
            params.put("DataSign", urlEncoder(dataSign, "UTF-8"));
            params.put("DataType", "2");
        } catch (Exception e) {
            log.error("快递查询出错:{}", e.getMessage());
            throw new BusinessException("快递查询出错");
        }
        // 返回物流信息
        // status: 0|null 无信息 1已取件 2在途中 3已签收 4问题件 5待取件 6待派件 8已发货 9未发货
        String result = KdniaoUtil.sendPost(reqURL, params);
        ExpressInfoVo infoVo = JSONObject.parseObject(result, ExpressInfoVo.class);
        if (infoVo != null) {
            boolean success = infoVo.isSuccess();
            log.info("查询快递信息:{}", success);
            if (success) {
                String status = StringUtils.isNotEmpty(infoVo.getState()) ? infoVo.getState() : "0";
                int statusValue = Integer.valueOf(status);
                infoVo.setDescribe(KdniaoExpressStatusEnum.parse(statusValue));
                // TODO: 2017/11/16 根据公司业务处理返回的信息......
            }
        }
        return infoVo;
    }

    // DEMO
    public static void main(String[] args) {
        ExpressDeliveryService api = new ExpressDeliveryService();
        ExpressInfo info = new ExpressInfo();
        info.setShipperCode("HTKY");
        info.setLogisticCode("211386517825");
        try {
            ExpressInfoVo result = api.queryOrderTraces(info);
            // TODO: 根据公司业务处理返回的信息......
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }
}
