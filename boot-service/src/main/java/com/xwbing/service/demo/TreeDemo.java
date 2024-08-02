package com.xwbing.service.demo;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author daofeng
 * @version $
 * @since 2024年08月02日 5:25 PM
 */
@Slf4j
public class TreeDemo {
    public static void main(String[] args) {
        InputStream inputStream = TreeDemo.class.getResourceAsStream("/data/zjPublishJdZb.json");
        String treeStr = new String(IoUtil.readBytes(inputStream), StandardCharsets.UTF_8);
        JSONArray jsonArray = JSONUtil.parseArray(treeStr);
        List<JSONObject> jsonList = jsonArray.stream().map(o -> JSONUtil.parseObj(JSONUtil.toJsonStr(o))).collect(Collectors.toList());
        List<TreeNode<String>> treeNodeList = jsonList.stream().map(entries -> new TreeNode<>(entries.getStr("id"), entries.getStr("pId"), entries.getStr("text"), entries.getInt("folderNo"))).collect(Collectors.toList());
        List<Tree<String>> treeList = TreeUtil.build(treeNodeList, "THEME_J_33urn:ddi:ZJJCKSTAT:7c0d421e-f5e7-4c66-88b6-61d0407dc73a:1");
        log.info("treeCount:{}", treeList.size());
        List<Tree<String>> bottomNodeList = new ArrayList<>();
        Map<String, String> categoryPathMap = new HashMap<>();
        treeBottomNode(treeList, bottomNodeList, categoryPathMap, null);
        log.info("bottomNodeCount:{}", bottomNodeList.size());
        AtomicInteger index = new AtomicInteger();
        bottomNodeList.forEach(node -> {
            String categoryPath = categoryPathMap.get(node.getId());
            int i = index.incrementAndGet();
        });
    }

    /**
     * 获取底层节点
     *
     * @param treeList
     * @param bottomNodeList
     * @param categoryPathMap
     * @param categoryPath
     */
    private static void treeBottomNode(List<Tree<String>> treeList, List<Tree<String>> bottomNodeList, Map<String, String> categoryPathMap, String categoryPath) {
        for (Tree<String> tree : treeList) {
            List<Tree<String>> children = tree.getChildren();
            String path;
            if (StringUtils.isEmpty(categoryPath)) {
                path = (String) tree.getName();
            } else {
                path = categoryPath + "-" + tree.getName();
            }
            if (CollectionUtils.isEmpty(children)) {
                bottomNodeList.add(tree);
                categoryPathMap.put(tree.getId(), path);
            } else {
                treeBottomNode(children, bottomNodeList, categoryPathMap, path);
            }
        }
    }
}