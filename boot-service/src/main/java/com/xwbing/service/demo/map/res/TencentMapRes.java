package com.xwbing.service.demo.map.res;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 腾讯地图返回结果
 *
 * @author daofeng
 * @version $
 * @since 2026年06月06日 10:27
 */
@Slf4j
@Data
public class TencentMapRes<T> {
    // 状态码，0为正常，其它为异常
    private Integer status;
    private String message;
    private T result;


    public static <T> T parse(String url, String param, String res, TypeReference<TencentMapRes<T>> typeRef) {
        log.info("map url:{}, param:{}, res:{}", url, param, res);
        if (StringUtils.isEmpty(res)) {
            return null;
        }
        if (!JSONUtil.isTypeJSON(res)) {
            return null;
        }
        TencentMapRes<T> resVO = JSON.parseObject(res, typeRef);
        if (resVO.getStatus() == 0) {
            return resVO.getResult();
        } else {
            log.error("map url:{}, param:{}, res:{} error", url, param, res);
            return null;
        }
    }
}