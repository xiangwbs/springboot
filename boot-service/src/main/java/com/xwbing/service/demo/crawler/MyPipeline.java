package com.xwbing.service.demo.crawler;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * @author daofeng
 * @version $
 * @since 2024年07月30日 2:30 PM
 */
public class MyPipeline implements Pipeline {
    @Override
    public void process(ResultItems resultItems, Task task) {
        // 处理和保存数据
        String result = resultItems.get("result");
        if (StringUtils.isEmpty(result)) {
            return;
        }
        JSONObject resultObj = JSONUtil.parseObj(result);
        System.out.println(resultObj);
    }
}