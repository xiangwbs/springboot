package com.xwbing.service.demo;

import cn.hutool.core.collection.IterUtil;
import cn.hutool.extra.tokenizer.Result;
import cn.hutool.extra.tokenizer.TokenizerEngine;
import cn.hutool.extra.tokenizer.TokenizerUtil;

/**
 * @author daofeng
 * @version $
 * @since 2024年09月04日 10:09 AM
 */
public class TokenizerDemo {
    public static void main(String[] args) {
        //自动根据用户引入的分词库的jar来自动选择使用的引擎
        TokenizerEngine engine = TokenizerUtil.createEngine();
//        TokenizerEngine engine = new IKAnalyzerEngine();
        String text = "我是中国人";
        Result result = engine.parse(text);
        String resultStr = IterUtil.join(result, " ");
        System.out.println(resultStr);
    }
}