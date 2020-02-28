package com.xwbing.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xwbing.BaseTest;
import com.xwbing.domain.entity.sys.SysRole;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.text.MessageFormat;

/**
 * 说明:
 * 项目名称: boot-module-pro
 * 创建时间: 2018/5/10 16:36
 * 作者:  xiangwb
 */
@Slf4j
public class SysRoleControlTest extends BaseTest {
    @Test
    public void all() throws Exception {
        //save
        SysRole sysRole = new SysRole();
        sysRole.setCode("apiTest");
        sysRole.setEnable("Y");
        sysRole.setName("apiTest");
        sysRole.setRemark("apiTest");
        String saveStr = mvc.perform(MockMvcRequestBuilders
                .post("/role/save")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSONObject.toJSONString(sysRole)))
                .andReturn().getResponse().getContentAsString();
        log.info(saveStr);
        //getById
        JSONObject response = JSONObject.parseObject(saveStr);
        String getByIdStr = mvc.perform(MockMvcRequestBuilders
                .get("/role/getById")
                .param("id", response.getString("id")))
                .andReturn().getResponse().getContentAsString();
        log.info(getByIdStr);
        response = JSONObject.parseObject(getByIdStr);
        sysRole = JSON.toJavaObject(response.getJSONObject("data"), SysRole.class);
        //listByEnable
        String listByEnableString = mvc.perform(MockMvcRequestBuilders
                .get("/role/listByEnable")
                .param("enable", "Y"))
                .andReturn().getResponse().getContentAsString();
        log.info(listByEnableString);
        //update
        sysRole.setName("updateTest");
        String updateStr = mvc.perform(MockMvcRequestBuilders
                .put("/role/update")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSONObject.toJSONString(sysRole)))
                .andReturn().getResponse().getContentAsString();
        log.info(updateStr);
        //removeById
        String removeByIdStr = mvc.perform(MockMvcRequestBuilders
                .delete(MessageFormat.format("/role/removeById/{0}", sysRole.getId())))
                .andReturn().getResponse().getContentAsString();
        log.info(removeByIdStr);
//        mvc.perform(MockMvcRequestBuilders
//                .delete(MessageFormat.format("/role/removeById/{0}", sysRole.getId())))
//                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}