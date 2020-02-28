package com.xwbing.util;

import com.alibaba.fastjson.JSONObject;
import com.xwbing.exception.UtilException;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 说明: json格式转换
 * 作者: xiangwb
 */
public class JsonUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);

    /**
     * 实体对象不序列化转换成jsonObject
     *
     * @param obj
     * @return
     */
    public static Object beanToMap(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            return obj;
        }
        if (obj instanceof JSONObject) {
            return obj;
        } else if (obj instanceof Map) {
            return obj;
        } else if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            ArrayList<JSONObject> result = new ArrayList<>();
            for (Object o : list) {
                JSONObject javaObject = (JSONObject) beanToMap(o);
                result.add(javaObject);
            }
            return result;
        } else {
            Map<String, Object> params = new HashMap<>(20);
            try {
                PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
                PropertyDescriptor[] descriptors = propertyUtilsBean.getPropertyDescriptors(obj);
                for (PropertyDescriptor descriptor : descriptors) {
                    String name = descriptor.getName();
                    if (!"class".equals(name)) {
                        params.put(name, propertyUtilsBean.getNestedProperty(obj, name));
                    }
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                throw new UtilException("实体对象不序列化转换成object错误");
            }
            return new JSONObject(params);
        }
    }
}
