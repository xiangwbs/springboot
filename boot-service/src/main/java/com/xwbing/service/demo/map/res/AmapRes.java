package com.xwbing.service.demo.map.res;

import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 高德地图返回结果
 *
 * @author daofeng
 * @version $
 * @since 2026年06月06日 10:27
 */
@Slf4j
@Data
public class AmapRes {
    // 返回值为 0 或 1，0 表示请求失败；1 表示请求成功。
    private String status;
    private String count;
    // 当 status 为 0 时，info 会返回具体错误原因，否则返回“OK”
    private String info;
    private String infocode;

    public static <T extends AmapRes> T parseRes(String url, String param, String res, Class<T> tClass) {
        log.info("amap url:{} param:{} res:{}", url, param, res);
        if (StringUtils.isEmpty(res)) {
            return null;
        }
        if (!JSONUtil.isTypeJSON(res)) {
            return null;
        }
        T bean = JSONUtil.toBean(res, tClass);
        if ("1".equals(bean.getStatus())) {
            return bean;
        } else {
            log.error("amap url:{} param:{} res:{} error", url, param, res);
            return null;
        }
    }
}