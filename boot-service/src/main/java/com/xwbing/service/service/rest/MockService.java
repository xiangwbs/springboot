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
            String llamaSql = this.llamaNl2sql2(dto.getQuestion());
            String ldlSql = this.ldlNl2sql(dto.getQuestion());
            dto.setLlamaSql(llamaSql);
            dto.setLdlSql(ldlSql);
            dto.setLlamaCorrect(dto.getSql().replaceAll("\n|\t|\u00a0| ", "").trim().equalsIgnoreCase(llamaSql.replaceAll("\n|\t|\u00a0| ", "").trim()) ? "正确" : "错误");
            dto.setLdlCorrect(dto.getSql().replaceAll("\n|\t|\u00a0| ", "").trim().equalsIgnoreCase(ldlSql.replaceAll("\n|\t|\u00a0| ", "").trim()) ? "正确" : "错误");
        }).collect(Collectors.toList());
        ExcelUtil.write(response, Nl2sqlExcelDTO.class, "nl2sql跑测结果.xlsx", null, list);
    }

    public void runChatglmNl2sql(InputStream inputStream, HttpServletResponse response) {
        List<Nl2sqlExcelDTO> list = new ArrayList<>();
        ExcelUtil.read(inputStream, Nl2sqlExcelDTO.class, 0, 100, list::addAll);
        list = list.parallelStream().peek(dto -> {
            String sql = this.chatglmNl2sql(dto.getQuestion());
            dto.setLlamaSql(sql);
            dto.setLlamaCorrect(dto.getSql().replaceAll("\n|\t|\u00a0| ", "").trim().equalsIgnoreCase(sql.replaceAll("\n|\t|\u00a0| ", "").trim()) ? "正确" : "错误");
        }).collect(Collectors.toList());
        ExcelUtil.write(response, Nl2sqlExcelDTO.class, "智谱跑测结果.xlsx", null, list);
    }

    public String llamaNl2sql2(String content) {
        Map<String, Object> req = new HashMap<>();
        req.put("model", "string");
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", content);
        req.put("messages", ListUtil.toList(message));
        req.put("do_sample", false);
        req.put("temperature", 0.1);
        req.put("top_p", 0.1);
        req.put("n", 1);
        req.put("max_tokens", 0);
        req.put("stop", "string");
        req.put("stream", false);
        log.info("llamaNl2sql2 req:{}", JSONUtil.toJsonStr(req));
        String res = HttpUtil.post("http://172.16.20.7:8000/v1/chat/completions", JSONUtil.toJsonStr(req));
        log.info("llamaNl2sql2 res:{}", res);
        JSONObject jsonObject = JSONObject.parseObject(res);
        JSONArray choices = JSONArray.parseArray(jsonObject.getString("choices"));
        JSONObject jsonObject1 = JSONObject.parseObject(JSONObject.toJSONString(choices.get(0)));
        return jsonObject1.getJSONObject("message").getString("content");
    }

    public String ldlNl2sql(String content) {
        Map<String, Object> req = new HashMap<>();
        req.put("question", content);
        req.put("taxAuthorityCode", ListUtil.toList("4000000000000", "4330000000000"));
        req.put("categoryIds", ListUtil.toList(1));
        req.put("similarityThreshold", 1.0);
        req.put("answerType", 20);
        log.info("ldlNl2sql req:{}", JSONUtil.toJsonStr(req));
        String res = HttpUtil.post("http://172.16.20.7:8080/events/nl2sql", JSONUtil.toJsonStr(req));
        log.info("ldlNl2sql res:{}", res);
        JSONObject jsonObject = JSONObject.parseObject(res);
        JSONObject data = jsonObject.getJSONObject("data");
        String sql = data.getString("sql");
        if (sql.contains("\u00a0")) {
            sql = sql.replaceAll("\u00a0", "");
        }
        return sql.toLowerCase();
    }

    public String chatglmNl2sql(String content) {
        String postParam = "### 此查询将在其模式由以下字符串表示的数据库上运行，相关的表结构：\n" +
                "{\n" +
                "    \"synonym_infos\": \"财政的按月支出明细表\",\n" +
                "    \"schema_infos\": [\n" +
                "        {\n" +
                "            \"columns\": [\n" +
                "                {\n" +
                "                    \"col_caption\": \"地区\",\n" +
                "                    \"col_name\": \"mofdivname\",\n" +
                "                    \"col_desc\": \"一个特定的地理区域，可以是一个国家、省份、城市、区县、街道。\",\n" +
                "                    \"value_example\": [\n" +
                "                        \"上城区\",\n" +
                "                        \"临安区\",\n" +
                "                        \"临平区\",\n" +
                "                        \"余杭区\",\n" +
                "                        \"全市合计\",\n" +
                "                        \"各县合计\",\n" +
                "                        \"富阳区\",\n" +
                "                        \"市本级\",\n" +
                "                        \"建德市\",\n" +
                "                        \"拱墅区\",\n" +
                "                        \"杭州市区合计\",\n" +
                "                        \"桐庐县\",\n" +
                "                        \"淳安县\",\n" +
                "                        \"滨江区\",\n" +
                "                        \"萧山区\",\n" +
                "                        \"西湖区\",\n" +
                "                        \"西湖名胜区\",\n" +
                "                        \"钱塘区\"\n" +
                "                    ],\n" +
                "                    \"vale_type\": \"字符\",\n" +
                "                    \"col_type\": \"维度\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"col_caption\": \"年月\",\n" +
                "                    \"col_name\": \"yearmonth\",\n" +
                "                    \"col_desc\": \"代表的是数据汇总的年月，只支持到月\",\n" +
                "                    \"value_example\": [\n" +
                "                        \"202301\",\n" +
                "                        \"202301\",\n" +
                "                        \"202403\"\n" +
                "                    ],\n" +
                "                    \"vale_type\": \"日期\",\n" +
                "                    \"col_type\": \"维度\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"col_caption\": \"收入支出类型\",\n" +
                "                    \"col_name\": \"incexptype\",\n" +
                "                    \"col_desc\": \"0=收入,1=支出\",\n" +
                "                    \"value_example\": [\n" +
                "                        \"0\",\n" +
                "                        \"1\"\n" +
                "                    ],\n" +
                "                    \"vale_type\": \"字符\",\n" +
                "                    \"col_type\": \"维度\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"col_caption\": \"类目编码\",\n" +
                "                    \"col_name\": \"budgetsubjectcode\",\n" +
                "                    \"col_desc\": \"指的是类目名称字段的唯一code值\",\n" +
                "                    \"value_example\": [\n" +
                "                        \"2010204\",\n" +
                "                        \"20103\",\n" +
                "                        \"2010399\",\n" +
                "                        \"201\",\n" +
                "                        \"20101\",\n" +
                "                        \"2010101\"\n" +
                "                    ],\n" +
                "                    \"vale_type\": \"字符\",\n" +
                "                    \"col_type\": \"维度\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"col_caption\": \"类目名称\",\n" +
                "                    \"col_name\": \"budgetsubjectname\",\n" +
                "                    \"col_desc\": \"指的是财政的某项收入支出的类目\",\n" +
                "                    \"value_example\": [\n" +
                "                        \"机关服务\",\n" +
                "                        \"人大监督\",\n" +
                "                        \"公共卫生\",\n" +
                "                        \"体育\",\n" +
                "                        \"体育场馆\",\n" +
                "                        \"“两房”建设\",\n" +
                "                        \"一般债务收入\",\n" +
                "                        \"一般公共服务\",\n" +
                "                        \"一般公共服务支出\",\n" +
                "                        \"一般公共预算支出合计\",\n" +
                "                        \"一般公共预算收入合计\",\n" +
                "                        \"一般劳动防护用品检验费\",\n" +
                "                        \"一般罚没收入\",\n" +
                "                        \"一般行政管理事务\",\n" +
                "                        \"上划中央收入合计\",\n" +
                "                        \"上缴管理费用\",\n" +
                "                        \"不动产登记费\",\n" +
                "                        \"干部教育\",\n" +
                "                        \"应急救援\",\n" +
                "                        \"应急救治机构\",\n" +
                "                        \"应急管理\",\n" +
                "                        \"关税\",\n" +
                "                        \"税收收入\",\n" +
                "                        \"非税收入\",\n" +
                "                        \"烟叶税\",\n" +
                "                        \"环境保护税\",\n" +
                "                        \"环境保护税税款滞纳金、罚款收入\",\n" +
                "                        \"私营企业土地增值税\",\n" +
                "                        \"高等教育\",\n" +
                "                        \"高等职业教育\",\n" +
                "                        \"预备役部队\"\n" +
                "                    ],\n" +
                "                    \"vale_type\": \"字符\",\n" +
                "                    \"col_type\": \"维度\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"col_caption\": \"金额\",\n" +
                "                    \"col_name\": \"amt\",\n" +
                "                    \"col_desc\": \"代表每个类目名称对应的金额\",\n" +
                "                    \"value_example\": [\n" +
                "                        2509555,\n" +
                "                        7012,\n" +
                "                        4522\n" +
                "                    ],\n" +
                "                    \"vale_type\": \"数值\",\n" +
                "                    \"unit\": \"万元\",\n" +
                "                    \"col_type\": \"度量\"\n" +
                "                }\n" +
                "            ],\n" +
                "            \"table_name\": \"dws_budget_domain_srzc_ai\",\n" +
                "            \"table_desc\": \"收入支出信息表\"\n" +
                "        }\n" +
                "    ]\n" +
                "}\n" +
                "### 你需要参考下列的行业知识\n" +
                "[\n" +
                "    {\n" +
                "        \"terminology\": \"杭州市区\",\n" +
                "        \"terminology_desc\": \"在财政行业算杭州的所有区县，可以理解成独立的一个地区\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"terminology\": \"突击时期\",\n" +
                "        \"terminology_desc\": \"指的是2023年1月到2023年5月的时间\"\n" +
                "    }\n" +
                "]\n" +
                "\n" +
                "你的任务是根据给定的SQLite数据库模式将问题转换为SQL查询。遵守以下规则：\n" +
                "- **逐字仔细阅读问题和数据库模式** ，以适当回答问题；\n" +
                "- 生成sql用的字段需要在数据库模式columns内，但columns字段可能会超出问句范畴，请选择合适字段，无需要使用所有字段；\n" +
                "- 用户问中未指定收入支出类型，则不设置默认值；\n" +
                "- select中不需要进行函数计算，直接返回where中涉及的所有字段；\n" +
                "- budgetsubjectname字段，where条件请使用like，如LIKE \"%税收收入%\"，其他维度字段基于用户问判断；\n" +
                "- **使用表别名** 来防止歧义。例如， `SELECT table1.col1, table2.col1 FROM table1 JOIN table2 ON table1.id = table2.id`；\n" +
                "- **提取到的条件信息不要翻译成英文**，例如， `WHERE type=\"风能\"`；\n" +
                "- 若where中包含budgetsubjectname字段时，selec中必须包含budgetsubjectcode字段，反之也同理，这两个字段需共存；\n" +
                "- 必须注意，一定只能回答SQL语句，不能包含其他任何内容；\n" +
                "\n" +
                "### 输入：" + content + "\n" +
                "\n" +
                "### 回答：\n" +
                "```sql";
        Map<String, String> map = new HashMap<>();
        map.put("prompt", postParam);
        log.info("chatglmNl2sql req:{}", JSONUtil.toJsonStr(map));
        String res = HttpUtil.post("http://172.16.20.7:8080/events/nl2sql_new", JSONUtil.toJsonStr(map));
        log.info("chatglmNl2sql res:{}", res);
        return JSONObject.parseObject(res).getJSONObject("data").getString("answer");
    }
}
