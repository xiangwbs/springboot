package com.xwbing.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xiangwb
 * @version $Id$
 * @since 2020年03月18日 下午6:51
 * 敏感词过滤引擎
 */
@Slf4j
@Component
public class SensitiveWordEngine {
    /**
     * 敏感词库
     */
    public static Map sensitiveWordMap;
    /**
     * 敏感词文件地址
     */
    private static final String path = "data/illegal_words.txt";
    /**
     * 最小匹配规则
     */
    public static final int minMatchTYpe = 1;
    /**
     * 最大匹配规则
     */
    public static final int maxMatchType = 2;

    static {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("---初始化敏感词词库---");
        Set<String> keyWordSet = readSensitiveWordFile();
        addSensitiveWordToHashMap(keyWordSet);
        stopWatch.stop();
        log.info("{}:{} ms", stopWatch.getLastTaskName(), stopWatch.getTotalTimeMillis());
    }

    /**
     * 是否包含敏感词
     *
     * @param txt
     *
     * @return
     */
    public boolean isContainSensitiveWord(String txt) {
        return isContainSensitiveWord(txt, minMatchTYpe);
    }

    /**
     * 获取文字中的敏感词
     *
     * @param matchType 匹配规则 1：最小匹配规则，2：最大匹配规则
     */
    public Set<String> getSensitiveWord(String txt, int matchType) {
        Set<String> sensitiveWordList = new HashSet<>();
        for (int i = 0; i < txt.length(); i++) {
            //判断是否包含敏感字符
            int length = CheckSensitiveWord(txt, i, matchType);
            //存在,加入list中
            if (length > 0) {
                sensitiveWordList.add(txt.substring(i, i + length));
                i = i + length - 1;//减1的原因，是因为for会自增
            }
        }
        return sensitiveWordList;
    }

    /**
     * 替换敏感字字符,默认*
     *
     * @param txt
     * @param matchType
     * @param replaceChar
     * @return
     */
    public String replaceSensitiveWord(String txt, int matchType, String replaceChar) {
        replaceChar = StringUtils.isNotEmpty(replaceChar) ? replaceChar : "*";
        String resultTxt = txt;
        //获取所有的敏感词
        Set<String> set = getSensitiveWord(txt, matchType);
        Iterator<String> iterator = set.iterator();
        String word;
        String replaceString;
        while (iterator.hasNext()) {
            word = iterator.next();
            replaceString = getReplaceChars(replaceChar, word.length());
            resultTxt = resultTxt.replaceAll(word, replaceString);
        }
        return resultTxt;
    }

    // ---------------------- private ----------------------
    /**
     * 读取敏感词库中的内容，将内容添加到set集合中
     *
     * @return
     */
    private static Set<String> readSensitiveWordFile() {
        Set<String> keyWordSet = new HashSet<>();
        ClassPathResource resource = new ClassPathResource(path);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                keyWordSet.add(line);
            }
        } catch (IOException e) {
            log.error("读取敏感词词库文件异常：{}", e.getMessage());
        }
        return keyWordSet;
    }

    /**
     * 读取敏感词库，将敏感词放入HashSet中，构建一个DFA算法模型：
     * 中 = {
     *      isEnd = 0
     *      国 = {
     *           isEnd = 1
     *           人 = {isEnd = 0
     *                民 = {isEnd = 1}
     *                }
     *           男  = {
     *                  isEnd = 0
     *                   人 = {
     *                        isEnd = 1
     *                       }
     *               }
     *           }
     *      }
     *  五 = {
     *      isEnd = 0
     *      星 = {
     *          isEnd = 0
     *          红 = {
     *              isEnd = 0
     *              旗 = {
     *                   isEnd = 1
     *                  }
     *              }
     *          }
     *      }
     */
    private static void addSensitiveWordToHashMap(Set<String> keyWordSet) {
        //初始化敏感词容器，减少扩容操作
        sensitiveWordMap = new HashMap(keyWordSet.size());
        //敏感词
        String key;
        //用来按照相应的格式保存敏感词库数据
        Map nowMap;
        //用来辅助构建敏感词库
        Map<String, String> newWorMap;
        //使用一个迭代器来循环敏感词集合
        Iterator<String> iterator = keyWordSet.iterator();
        while (iterator.hasNext()) {
            key = iterator.next();
            //等于敏感词库，HashMap对象在内存中占用的是同一个地址，所以此nowMap对象的变化，sensitiveWordMap对象也会跟着改变
            nowMap = sensitiveWordMap;
            for (int i = 0; i < key.length(); i++) {
                //截取敏感词当中的字，在敏感词库中字为HashMap对象的Key键值
                char keyChar = key.charAt(i);
                //判断这个字是否存在于敏感词库中
                Object wordMap = nowMap.get(keyChar);
                if (wordMap != null) {
                    nowMap = (Map)wordMap;
                } else {
                    //不存在则，则构建一个map，同时将isEnd设置为0，因为他不是最后一个
                    newWorMap = new HashMap<>();
                    newWorMap.put("isEnd", "0");
                    nowMap.put(keyChar, newWorMap);
                    nowMap = newWorMap;
                }
                //如果该字是当前敏感词的最后一个字，则标识为结尾字
                if (i == key.length() - 1) {
                    nowMap.put("isEnd", "1");
                }
            }
        }
    }

    /**
     * 是否包含敏感词
     *
     * @param matchType 匹配规则 1：最小匹配规则，2：最大匹配规则
     */
    private boolean isContainSensitiveWord(String txt, int matchType) {
        boolean flag = false;
        for (int i = 0; i < txt.length(); i++) {
            //判断是否包含敏感字符
            int matchFlag = this.CheckSensitiveWord(txt, i, matchType);
            //大于0存在，返回true
            if (matchFlag > 0) {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 检查敏感词数量
     *
     * @param txt
     * @param beginIndex
     * @param matchType
     *
     * @return
     */
    private int CheckSensitiveWord(String txt, int beginIndex, int matchType) {
        //敏感词结束标识位：用于敏感词只有1位的情况
        boolean flag = false;
        //记录敏感词数量
        int matchFlag = 0;
        char word;
        Map nowMap = sensitiveWordMap;
        for (int i = beginIndex; i < txt.length(); i++) {
            word = txt.charAt(i);
            //判断该字是否存在于敏感词库中
            nowMap = (Map)nowMap.get(word);
            if (nowMap != null) {//存在，则判断是否为最后一个
                //找到相应key，匹配标识+1
                matchFlag++;
                //判断是否是敏感词的结尾字，如果是结尾字则判断是否继续检测
                if ("1".equals(nowMap.get("isEnd"))) {
                    //结束标志位为true
                    flag = true;
                    //最小规则，直接返回,最大规则还需继续查找
                    if (SensitiveWordEngine.minMatchTYpe == matchType) {
                        break;
                    }
                }
            } else {//不存在，直接返回
                break;
            }
        }
        //长度必须大于等于1，为词
        if (matchFlag < 2 || !flag) {
            matchFlag = 0;
        }
        return matchFlag;
    }

    /**
     * 获取替换字符串
     *
     * @param replaceChar
     * @param length
     *
     * @return
     */
    private String getReplaceChars(String replaceChar, int length) {
        StringBuilder resultReplace = new StringBuilder(replaceChar);
        for (int i = 1; i < length; i++) {
            resultReplace.append(replaceChar);
        }
        return resultReplace.toString();
    }

    /**
     * 例如：敏感词中含有中国人、中国
     * 最小匹配规则minMatchTYpe为1时,会匹配出**人，为2时，会匹配出***
     */
    public static void main(String[] args) {
        SensitiveWordEngine filter = new SensitiveWordEngine();
        String words = "法轮功已经消失";
        boolean containSensitiveWord = filter.isContainSensitiveWord(words);
        Set<String> sensitiveWord = filter.getSensitiveWord(words, minMatchTYpe);
        String minMatchTYpeWords = filter.replaceSensitiveWord(words, minMatchTYpe, null);
        System.out.println(minMatchTYpeWords);
        String maxMatchTypeWords = filter.replaceSensitiveWord(words, maxMatchType, null);
        System.out.println(maxMatchTypeWords);
    }
}
