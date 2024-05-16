package com.xwbing.service.service.rest;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xwbing.service.domain.entity.dto.Nl2sqlExcelDTO;
import com.xwbing.service.util.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author daofeng
 * @version $
 * @since 2024年05月16日 2:52 PM
 */
@Slf4j
@Service
public class MockService {
    public void runNl2sql(InputStream inputStream, HttpServletResponse response) {
        List<Nl2sqlExcelDTO> list = new ArrayList<>();
        ExcelUtil.read(inputStream, Nl2sqlExcelDTO.class, 0, 100, list::addAll);
        list = list.parallelStream().peek(dto -> {
            JSONObject jsonObject = this.nl2sql(dto.getQuestion());
            JSONArray choices = JSONArray.parseArray(jsonObject.getString("choices"));
            JSONObject jsonObject1 = JSONObject.parseObject(JSONObject.toJSONString(choices.get(0)));
            JSONObject message = jsonObject1.getJSONObject("message");
            String modelSql = message.getString("content");
            dto.setModelSql(modelSql);
            dto.setCorrect(dto.getSql().replaceAll("\n|\t|\u00a0| ", "").trim().equalsIgnoreCase(modelSql.replaceAll("\n|\t|\u00a0| ", "").trim()) ? "正确" : "错误");
        }).collect(Collectors.toList());
        ExcelUtil.write(response, Nl2sqlExcelDTO.class, "nl2sql跑测结果.xlsx", null, list);
    }

    public JSONObject nl2sql(String content) {
        Map<String, Object> req = new HashMap<>();
        req.put("model", "string");
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", content);
        req.put("messages", ListUtil.toList(message));
        req.put("do_sample", true);
        req.put("temperature", 0);
        req.put("top_p", 0);
        req.put("n", 1);
        req.put("max_tokens", 0);
        req.put("stop", "string");
        req.put("stream", false);
        log.info("nl2sqlV2 req:{}", JSONUtil.toJsonStr(req));
        String res = HttpUtil.post("http://172.16.20.7:8000/v1/chat/completions", JSONUtil.toJsonStr(req));
        log.info("nl2sqlV2 res:{}", res);
        return JSONObject.parseObject(res);
    }
}
