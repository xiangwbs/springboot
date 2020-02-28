package com.xwbing.util;

import com.alibaba.fastjson.JSONObject;
import com.xwbing.exception.UtilException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.util.*;

/**
 * 实体转换
 *
 * @author xiangwb
 */
@Slf4j
public class ConvertUtil {
    /**
     * 实体对象不序列化转换成jsonObject
     *
     * @param obj
     * @return
     */
    public static Object beanToJson(Object obj) {
        if (obj == null) {
            return null;
        } else if (obj instanceof String || obj instanceof JSONObject || obj instanceof Map) {
            return obj;
        } else if (obj instanceof List) {
            ArrayList<JSONObject> result = new ArrayList<>();
            ((List<?>) obj).forEach(one -> result.add((JSONObject) beanToJson(one)));
            return result;
        } else {
            Map<String, Object> params = new HashMap<>(20);
            PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
            PropertyDescriptor[] descriptors = propertyUtilsBean.getPropertyDescriptors(obj);
            Arrays.stream(descriptors)
                    .map(PropertyDescriptor::getName)
                    .filter(name -> !"class".equals(name))
                    .forEach(name -> {
                        try {
                            params.put(name, propertyUtilsBean.getNestedProperty(obj, name));
                        } catch (Exception e) {
                            log.error(e.getMessage());
                            throw new UtilException("nonSerialize convert error");
                        }
                    });
            return new JSONObject(params);
        }
    }

    /**
     * object赋值
     *
     * @param fromObj
     * @param toClass
     * @param <F>
     * @param <T>
     * @return
     */
    public static <F, T> T convert(F fromObj, Class<T> toClass) {
        if (null == fromObj) {
            return null;
        }
        try {
            T toObject = toClass.newInstance();
            copyProperties(fromObj, toObject);
            return toObject;
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    /**
     * list赋值
     *
     * @param fromList
     * @param toClass
     * @param <F>
     * @param <T>
     * @return
     */
    public static <F, T> List<T> convertList(List<F> fromList, Class<T> toClass) {
        if (fromList == null || fromList.isEmpty()) {
            return Collections.emptyList();
        }
        List<T> toList = new ArrayList<>();
        fromList.forEach(fromObj -> toList.add(convert(fromObj, toClass)));
        return toList;
    }

    /**
     * 对象转化
     *
     * @param source
     * @param target
     */
    private static void copyProperties(Object source, Object target) {
        try {
            BeanUtils.copyProperties(source, target);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new UtilException("convert error");
        }
    }
}
