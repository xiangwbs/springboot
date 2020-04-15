package com.xwbing.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;

/**
 * analysisEventListener不能被spring管理，要每次读取excel都要new，如果里面用到springBean可以用构造方法传进去
 *
 * @author daofeng
 * @version $Id$
 * @since 2020年04月14日 下午9:51
 */
@Slf4j
public class ExcelReadListener extends AnalysisEventListener<Map<Integer, Object>> {
    /**
     * 每隔3000条处理数据,然后清理list,方便内存回收
     */
    private static final int BATCH_COUNT = 3000;
    private List<Map<Integer, Object>> list = new ArrayList<>();

    /**
     * 这个每一条数据解析都会来调用
     *
     * @param data
     * @param context
     */
    @Override
    public void invoke(Map<Integer, Object> data, AnalysisContext context) {
        log.info("解析到一条数据:{}", JSON.toJSONString(data));
        list.add(data);
        // 达到BATCH_COUNT了，需要去处理一次数据，防止数据几万条数据在内存，容易OOM
        if (list.size() > BATCH_COUNT) {
            dealData();
            //存储完成清理 list
            list.clear();
        }
    }

    /**
     * 所有数据解析完成了，都会来调用
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        //这里也要保存数据，确保最后遗留的数据也会处理
        dealData();
        log.info("所有数据解析完成！");
    }

    /**
     * 处理数据
     */
    private void dealData() {
        log.info("{}条数据，开始处理！", list.size());
        System.out.println(JSONObject.toJSONString(list));
        log.info("处理成功！");
    }
}
