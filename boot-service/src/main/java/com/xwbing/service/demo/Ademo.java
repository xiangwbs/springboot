package com.xwbing.service.demo;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.dashscope.app.Application;
import com.alibaba.dashscope.app.ApplicationParam;
import com.alibaba.dashscope.app.ApplicationResult;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.aliyun.broadscope.bailian.sdk.AccessTokenClient;
import com.aliyun.broadscope.bailian.sdk.ApplicationClient;
import com.aliyun.broadscope.bailian.sdk.BaiLianSdkException;
import com.aliyun.broadscope.bailian.sdk.models.*;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Slf4j
public class Ademo {
    public static void main(String[] args) {

        TreeMap<String, Object> treeMap = new TreeMap<>();
        treeMap.put("appkey", "3a49f948b7834d6fbfea917f78a6cb60");
        treeMap.put("t", "1754897025537");
        String param = "";
        for (Map.Entry<String, Object> entry : treeMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            param += key + value;
        }
        param = "35640bb51d7b47dd990262a2ac3778f1" + param;
        String sign = DigestUtils.md5Hex(param);
        System.out.println("");
//        HashMap<String, Object> paramMap = new HashMap<>();
//        paramMap.put("unitId", "3297AFA2756847D79E2D8D7CCDF38DEF");
//        String result = HttpRequest
//                .post("https://xfzzgl.zjxf119.com/v1/xyxf/zzgl/checkform/readCategoryList")
//                .body(JSONUtil.toJsonStr(paramMap))
//                .header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMzQ1Njg1NDE3MCIsImF1ZCI6IndlYiIsImNyZWF0ZWQiOjE3NTQ2MzY5Njk1OTAsImFwcElkIjpudWxsLCJpc3MiOiJpY2luZm8uY24iLCJleHAiOjE3NTQ2NDc3Njl9.rLF1YAb40jUs5-EBkTXs1C42m93HyaFxBrvFA-chzluvCbzEpXAnP0cLDCyGf-uvPBfKs-3VPcKaHjO_8YdY7A")
//                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36")
//                .execute()
//                .body();
//        JSONObject resultObj = JSONUtil.parseObj(result);
//        List<JSONObject> collect = resultObj.getJSONArray("data").stream().map(o -> {
//            JSONObject a = new JSONObject();
//            JSONObject jsonObject = (JSONObject) o;
//            String str = jsonObject.getStr("categoryName");
//            a.putOpt("name", str);
//            List<String> ss = ss(jsonObject.getStr("id"), jsonObject.getStr("formInstanceId"));
//            a.putOpt("subName", ss);
//            return a;
//        }).collect(Collectors.toList());
//        String regex = "```sql\\s*(.*?)\\s*```";
//        String regex = "(?i)(?:SELECT|INSERT|UPDATE|DELETE|CREATE|ALTER|DROP|TRUNCATE|WITH)\\s+.*?(?:;|$)";
//        String s = "```sql\n" +
//                "SELECT taxpayname, regdate, regcapital FROM dws_declare_domain_zdsyqyjbxx WHERE regdate > '2024-05-01' AND regcapital > 10000;\n" +
//                "```";
//        String s = "<think>\n" +
//                "好的，我现在要帮用户把一个问题转化为SQL查询。问题是：“登记日期在2024年5月之后，注册资本大于1亿的企业有哪些？”首先，我得理解这个问题的需求。\n" +
//                "\n" +
//                "从数据库模式来看，相关表格是“dws_declare_domain_zdsyqyjbxx”，里面有三个列：regdate（登记日期）、regcapital（注册资本）、taxpayname（纳税人名称）。这三个都是维度或度量，适合用来做查询。\n" +
//                "\n" +
//                "接下来分析用户的提问。“登记日期”对应的是regdate，“注册资本”对应的是regcapital。用户要求的是两个条件：登记日期在2024年5月之后，以及注册资本超过1亿元的企业有哪些。那么，我的目标就是构建一个SELECT语句，选出满足这两个条件的记录，并且只显示必要的信息。\n" +
//                "\n" +
//                "先看看每个部分怎么处理。“登记日期在2024年5月之后”这个条件对应的WHERE子句应该是regdate > '2024-05-01'。因为2024年的5月开始于这一天，所以用>可以包括整个五月及其以后的时间。\n" +
//                "\n" +
//                "然后是“注册资本大于1亿”。这里要注意单位的问题。在数据库里，注册资本是以“万元”为单位存储的，比如例子中有10000代表一千万元。因此，1亿元等于10000万元。所以在WHERE子句中，应该写成regcapital > 10000。\n" +
//                "\n" +
//                "现在确定SELECT的部分。用户想知道企业的名字，所以我只需要选taxpayname作为结果。同时，由于regdate和regcapital用于过滤条件，它们也需要出现在SELECT列表中吗？按照之前的规则，如果这些字段被用在了WHERE或者GROUP BY等地方，是否一定要放在SELECT里呢？不过在这个案例中，用户并没有明确要求展示登记日期和注册资本的具体值，只是用来过滤。为了简洁起见，我可以只选纳税人的名称，这样结果更清晰。\n" +
//                "\n" +
//                "最后整合起来，SQL语句大致如下：\n" +
//                "\n" +
//                "SELECT taxpayname FROM dws_declare_domain_zdsyqyjbxx WHERE regdate > '2024-05-01' AND regcapital > 10000;\n" +
//                "\n" +
//                "检查一下有没有遗漏的地方。确保时间格式正确，单位换算准确，列名拼写无误。另外，考虑到当前时间为2024年8月9日，这样的查询不会有问题，因为截止时间还没过。\n" +
//                "\n" +
//                "总结一下步骤：\n" +
//                "1. 解析用户需求。\n" +
//                "2. 对应数据表中的列。\n" +
//                "3. 处理时间和金额的单位转换。\n" +
//                "4. 构建正确的WHERE条件。\n" +
//                "5. 确定SELECT的内容，避免冗余。\n" +
//                "6. 组合并验证SQL语法。\n" +
//                "</think>\n" +
//                "\n" +
//                "```sql\n" +
//                "SELECT taxpayname, regdate, regcapital FROM dws_declare_domain_zdsyqyjbxx WHERE regdate > '2024-05-01' AND regcapital > 10000;\n" +
//                "```";
//        String s = "<think>\\n好的，我现在要帮用户把他们的查询转化为正确的SQL语句。首先，我得仔细理解用户的提问：“2023年全年西湖区类目编码为2010204支出明细”。然后，结合提供的数据库模式来分析。\\n\\n首先，分解一下这个问题的关键部分：\\n\\n1. 时间范围：2023年全年。这意味着YEARMONTH应该从202301到202312。\\n2. 区域：西湖区，对应MOFDIVNAME字段。\\n3. 类目编码：2010204，对应BUDGETSUBJECTCODE。\\n4. 支出类型：支出，即INCEXPTYPE应该是1。\\n\\n接下来，查看数据库中的表格结构。目标表是DWS_BUDGET_DOMAIN_SRZC_AI，其中包含了所需的各个字段：YEARMONTH、MOFDIVNAME、BUDGETSUBJECTCODE、INCEXPYTYPE等。\\n\\n现在考虑如何构建WHERE子句：\\n\\n- 年份是2023年，所以YEARMONTH需要满足介于202301和202312之间。\\n- MOFDIVNAME等于“西湖区”。\\n- BUDGETSUBJECTCODE等于“2010204”。\\n- INCEXPTYPE等于1，因为这是支出。\\n\\n关于SELECT的部分，按照规则，如果涉及到BUDGETSUBJECTCODE或BUDGETSUBJECTNAME，两者都需要出现在结果中。这里我们明确提到了类目编码，因此需要包括这些字段，并且还需要包括AMT作为金额。\\n\\n最后，确保所有的条件都正确地应用了比较运算符，特别是时间范围需要用>=和<=来覆盖整个年度的数据。\\n\\n综合以上分析，我可以构造出符合要求的SQL语句，确保它能够准确地从数据库中提取所需的信息。\\n</think>\\n\\nsql\\nSELECT yearmonth, mofdivname, incexptype, budgetsubjectcode, amt FROM dws_budget_domain_srzc_ai WHERE yearmonth >= 202301 AND yearmonth <= 202312 AND mofdivname = '西湖区' AND budgetsubjectcode = '2010204' AND incexptype = '1'";
//        String output = s.replaceAll("<think>[\\s\\S]*?</think>", "").trim();
//        String substring = output.substring(output.indexOf("sql")+3);
//        boolean matches = Pattern.matches(regex, s);
//        if (matches) {
//            String result = ReUtil.get(regex, s, 1).trim();
//            log.info("dsf");
//        }
    }

    public static void streamCall() throws NoApiKeyException, InputRequiredException {
        ApplicationParam param = ApplicationParam.builder()
                .apiKey("xxx")
                .appId("xxx")
                .prompt("如何做土豆炖猪脚?")
                .build();

        Application application = new Application();
        Flowable<ApplicationResult> result = application.streamCall(param);
        result.blockingForEach(data -> {
            System.out.printf("requestId: %s, text: %s, finishReason: %s\n",
                    data.getRequestId(), data.getOutput().getText(), data.getOutput().getFinishReason());

        });
    }

    /**
     * 流式响应使用示例
     */
    public static void testCompletionsWithStream() {
        String accessKeyId = "xxx";
        String accessKeySecret = "xxx";
        String agentKey = "xxx";
        String appId = "xxx";

        AccessTokenClient accessTokenClient = new AccessTokenClient(accessKeyId, accessKeySecret, agentKey);
        String token = accessTokenClient.getToken();
        ApplicationClient client = ApplicationClient.builder()
                .token(token)
                .build();

        List<ChatRequestMessage> messages = new ArrayList<>();
        messages.add(new ChatSystemMessage("你是一名天文学家, 能够帮助小学生回答宇宙与天文方面的问题"));
        messages.add(new ChatUserMessage("宇宙中为什么会存在黑洞"));

        CompletionsRequest request = new CompletionsRequest()
                .setAppId(appId)
                .setMessages(messages)
                .setParameters(new CompletionsRequest.Parameter()
                        //开启增量输出模式，后面输出不会包含已经输出的内容
                        .setIncrementalOutput(true)
                        //返回choice message结果
                        .setResultFormat("message")
                );

        CountDownLatch latch = new CountDownLatch(1);
        Flux<CompletionsResponse> response = client.streamCompletions(request);

        response.subscribe(
                data -> {
                    if (data.isSuccess()) {
                        System.out.printf("%s", data.getData().getChoices().get(0).getMessage().getContent());
                    } else {
                        System.out.printf("failed to create completion, requestId: %s, code: %s, message: %s\n",
                                data.getRequestId(), data.getCode(), data.getMessage());
                    }
                },
                err -> {
                    System.out.printf("failed to create completion, err: %s\n", ExceptionUtils.getStackTrace(err));
                    latch.countDown();
                },
                () -> {
                    System.out.println("\ncreate completion completely");
                    latch.countDown();
                }
        );

        try {
            latch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new BaiLianSdkException(e);
        }
    }

    public static List<String> ss(String categoryId, String formInstanceId) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("categoryId", categoryId);
        paramMap.put("formInstanceId", formInstanceId);
        String result = HttpRequest
                .get("https://xfzzgl.zjxf119.com/v1/xyxf/checkform/categoryInstance/readCategoryTreeWithLeafTree")
                .form(paramMap)
                .header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMzQ1Njg1NDE3MCIsImF1ZCI6IndlYiIsImNyZWF0ZWQiOjE3NTQ2MzY5Njk1OTAsImFwcElkIjpudWxsLCJpc3MiOiJpY2luZm8uY24iLCJleHAiOjE3NTQ2NDc3Njl9.rLF1YAb40jUs5-EBkTXs1C42m93HyaFxBrvFA-chzluvCbzEpXAnP0cLDCyGf-uvPBfKs-3VPcKaHjO_8YdY7A")
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36")
                .execute()
                .body();
        JSONObject resultObj = JSONUtil.parseObj(result);
        return resultObj.getJSONArray("data").stream().map(o -> {
            JSONObject jsonObject = (JSONObject) o;
            return jsonObject.getStr("categoryName");
        }).collect(Collectors.toList());
    }
}