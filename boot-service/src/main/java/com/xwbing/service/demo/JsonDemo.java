package com.xwbing.service.demo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.xwbing.service.domain.entity.BaseEntity;
import com.xwbing.service.domain.entity.rest.ImportTask;
import com.xwbing.service.domain.entity.sys.SysUser;
import com.xwbing.service.enums.ImportStatusEnum;
import com.xwbing.service.util.Jackson;
import com.xwbing.service.util.RestMessage;

import cn.hutool.core.date.DatePattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建日期: 2017年3月18日 下午1:34:05
 * 作者: xiangwb
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JsonDemo {
    /**
     * jackSon注解
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DatePattern.NORM_DATETIME_PATTERN, timezone = "GMT+8")
    private LocalDateTime dateTime;
    @JsonIgnore
    private String ignore;
    private ImportStatusEnum statusEnum;
    /**
     * fastJson注解
     */
    @JSONField(serialize = false)
    private String name;
    @JSONField(name = "nick_name")
    private String nickName;
    @JSONField(format = "yyyy-MM-dd hh:mm:ss")
    private Date date;
    /**
     * gson注解
     */
    @SerializedName("user_age")
    @Expose //可以序列化和反序列化
    private String userAge;
    @Expose(serialize = false)//临时字段，可以反序列化
    private String addr;
    @Expose
    private String email;

    public static void main(String[] args) {
        serialize();
        /**
         * jackSon
         */
        JsonDemo jackSonDemo = JsonDemo.builder().dateTime(LocalDateTime.now()).ignore("ignore")
                .statusEnum(ImportStatusEnum.SUCCESS).build();
        String s1 = Jackson.build().writeValueAsString(jackSonDemo);
        JsonDemo jackSonDemo1 = Jackson.build().readValue(s1, JsonDemo.class);
        List<JsonDemo> jackSonDemoList = Jackson.build()
                .readValue(Jackson.build().writeValueAsString(Collections.singletonList(jackSonDemo)),
                        new TypeReference<List<JsonDemo>>() {
                        });

        /**
         * fastJson
         */
        String jsonStr = "{'id':'888888','resultMessage':{'msg':'200','isSuccess':'true'}}";
        Map<String, Object> map = new HashMap<>();
        map.put("id", "99999");
        List<SysUser> list = new ArrayList<>();
        /*
         * to jsonObject
         */
        JSONObject jsonObject = new JSONObject(map);// map转JSONObject
        jsonObject = JSONObject.parseObject(jsonStr);// json字符串转JSONObject
        /*
         * to javaObject
         */
        BaseEntity entiry = JSONObject.toJavaObject(jsonObject, BaseEntity.class);//jsonObject转javaObject
        entiry = JSONObject.parseObject(jsonStr, BaseEntity.class);// json字符串转javaObject
        /*
         * JSONArray <--> list
         */
        String arrayStr = JSONArray.toJSONString(list);
        list = JSONArray.parseArray(arrayStr, SysUser.class);
        /*
         * to object
         */
        Object obj = JSONObject.toJSON(entiry);// javabean转object
        /*
         * to jsonStr
         */
        jsonStr = JSONObject.toJSONString(entiry);
        /*
         * 获取jsonObject属性
         */
        String id = jsonObject.getString("id");
        RestMessage result = JSONObject.toJavaObject(jsonObject.getJSONObject("resultMessage"), RestMessage.class);

        /**
         * gson
         */
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().serializeNulls()
                .create();//不导出实体中没有用@Expose注解的属性,并把null值也转换
        JsonDemo jsonDemo = new JsonDemo();
        jsonDemo.setUserAge("18");
        jsonDemo.setAddr("西溪湿地");
        String serialize = gson.toJson(jsonDemo);
        String s = new Gson().toJson(jsonDemo);
        JsonDemo deserialize = gson.fromJson(s, JsonDemo.class);

        String gsonStr = "{'name':'John', 'sex':1,'role':{'id':'33'}}";
        /*
         * jsonStr to JsonElement
         */
        JsonElement jsonElement = new JsonParser().parse(gsonStr);
        /*
         * jsonStr to javaObject
         */
        SysUser user = new Gson().fromJson(gsonStr, SysUser.class);
        /*
         * JsonElement to javaObject
         */
        user = new Gson().fromJson(jsonElement, SysUser.class);
        /*
         * javaObject to jsonStr
         */
        gsonStr = new Gson().toJson(user);
        /*
         * 获取属性
         */
        JsonObject gsonObject = jsonElement.getAsJsonObject();
        JsonElement role = jsonElement.getAsJsonObject().get("role");
        String name = jsonElement.getAsJsonObject().get("name").getAsString();
    }

    public static void serialize() {
        ImportTask task = ImportTask.builder().status(ImportStatusEnum.FAIL).build();
        String jackson = Jackson.build().writeValueAsString(task);
        ImportTask jacksonTask = Jackson.build().readValue(jackson, ImportTask.class);
        String fast = JSONObject.toJSONString(task);
        ImportTask fasttask = JSONObject.parseObject(fast, ImportTask.class);
        System.out.println("");
    }
}
