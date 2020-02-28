package com.xwbing.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * 封装对象结果的json结果
 *
 * @author xiangwb
 */
@Data
public class JsonResult {
    /**
     * 是否成功
     */
    private boolean success;
    /***
     * 新增、修改主鍵返回id
     */
    private String id;
    /**
     * 消息体
     */
    private String message = "操作成功";
    /**
     * 返回数据
     */
    private Object data;

    /**
     * 查询返回值为Object
     *
     * @param o       Object/List
     * @param message
     * @return
     */
    public static JSONObject toJSONObj(Object o, String message) {
        JsonResult jsonObjResult = new JsonResult();
        jsonObjResult.setSuccess(true);
        if (StringUtils.isNotEmpty(message)) {
            jsonObjResult.setMessage(message);
        }
        jsonObjResult.setData(ConvertUtil.beanToJson(o));
        return JSON.parseObject(JSON.toJSONString(jsonObjResult, SerializerFeature.WriteMapNullValue));
    }

    /**
     * 增删改返回值为RestMessage
     *
     * @param rest
     * @return
     */
    public static JSONObject toJSONObj(RestMessage rest) {
        JsonResult jsonObjResult = new JsonResult();
        jsonObjResult.setSuccess(rest.isSuccess());
        jsonObjResult.setMessage(rest.getMessage());
        jsonObjResult.setId(rest.getId());
        return JSON.parseObject(JSON.toJSONString(jsonObjResult, SerializerFeature.WriteMapNullValue));
    }

    /**
     * 直接返回错误提示
     *
     * @param error
     * @return
     */
    public static JSONObject toJSONObj(String error) {
        JsonResult jsonObjResult = new JsonResult();
//        jsonObjResult.setSuccess(false);
        jsonObjResult.setMessage(error);
        return JSON.parseObject(JSONObject.toJSONString(jsonObjResult, SerializerFeature.WriteMapNullValue));
    }
}
