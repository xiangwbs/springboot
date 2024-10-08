package com.xwbing.service.service.rest;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.http.HttpUtil;
import com.xwbing.service.domain.entity.rest.Xzqh;
import com.xwbing.service.domain.mapper.rest.XzqhMapper;
import com.xwbing.service.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author daofeng
 * @version $
 * @since 2024年08月15日 3:44 PM
 */
@Service
@Slf4j
public class XzqhService extends BaseService<XzqhMapper, Xzqh> {
    @Resource
    private XzqhMapper xzqhMapper;

    @Override
    protected XzqhMapper getMapper() {
        return xzqhMapper;
    }

    public String getGeo(List<String> regionList) {
        Xzqh province = this.listByXzqhCj("1").stream().filter(xzqh -> regionList.stream().anyMatch(region -> xzqh.getXzqhMc().contains(region))).findFirst().orElse(null);
        if (province != null) {
            boolean fullNameFlag = regionList.stream().anyMatch(region -> province.getXzqhMc().equals(region));
            return this.getGeoJson("100000");
        }
        Xzqh city = this.listByXzqhCj("2").stream().filter(xzqh -> regionList.stream().anyMatch(region -> xzqh.getXzqhMc().contains(region))).findFirst().orElse(null);
        if (city != null) {
            return this.getGeoJson(city.getSjxzqhDm());
        }
        Xzqh district = this.listByXzqhCj("3").stream().filter(xzqh -> regionList.stream().anyMatch(region -> xzqh.getXzqhMc().contains(region))).findFirst().orElse(null);
        if (district != null) {
            return this.getGeoJson(district.getSjxzqhDm());
        }
        return null;
    }

    public List<Tree<String>> tree() {
        List<Xzqh> list = xzqhMapper.findAll();
        TreeNodeConfig treeNodeConfig = new TreeNodeConfig();
        treeNodeConfig.setIdKey("xzqhDm");
        treeNodeConfig.setParentIdKey("sjxzqhDm");
        treeNodeConfig.setNameKey("xzqhMc");
        treeNodeConfig.setDeep(3);
        // 底层没有用到递归。先获取所有数据的id，value。再遍历基于pid去查数据，有就塞到子集里
        return TreeUtil.build(list, "100000", treeNodeConfig, (treeNode, tree) -> {
            tree.setId(treeNode.getXzqhDm());
            tree.setParentId(treeNode.getSjxzqhDm());
            tree.setWeight(treeNode.getXzqhDm());
            tree.setName(treeNode.getXzqhMc());
            tree.putExtra("xzqhCj", treeNode.getXzqhCj());
        });
    }

    public List<Xzqh> tree1() {
        List<Xzqh> treeList = new ArrayList<>();
        List<Xzqh> list = xzqhMapper.findAll();
        list.sort(Comparator.comparing(Xzqh::getXzqhDm));
        Map<String, Xzqh> xzqhMap = list.stream().collect(Collectors.toMap(Xzqh::getXzqhDm, Function.identity()));
        list.forEach(xzqh -> {
            Xzqh parent = xzqhMap.get(xzqh.getSjxzqhDm());
            if (parent == null) {
                treeList.add(xzqh);
            } else {
                List<Xzqh> children = parent.getChildren();
                if (children == null) {
                    children = new ArrayList<>();
                    parent.setChildren(children);
                }
                children.add(xzqh);
            }
        });
        return treeList;
    }

    public List<Xzqh> addressTree() {
        Map<String, List<Xzqh>> xhqhParentMap = xzqhMapper.findAll().stream().collect(Collectors.groupingBy(Xzqh::getSjxzqhDm));
        return xhqhParentMap.get("100000").stream()
                .filter(province -> {
                    // 去除港澳台
                    return !StringUtils.equalsAny(province.getXzqhDm(), "710000", "810000", "820000");
                }).peek(province -> {
                    String xzqhDm = province.getXzqhDm();
                    List<Xzqh> cityList;
                    //直辖市特殊处理
                    if (StringUtils.equalsAny(xzqhDm, "110000", "120000", "310000", "500000")) {
                        cityList = Collections.singletonList(BeanUtil.copyProperties(province, Xzqh.class));
                    } else {
                        cityList = xhqhParentMap.get(xzqhDm);
                    }
                    province.setChildren(cityList);
                    cityList.forEach(city -> city.setChildren(xhqhParentMap.get(city.getXzqhDm())));
                }).collect(Collectors.toList());
    }

    private Xzqh getByXzqhDm(String xzqhDm) {
        Map<String, Object> map = new HashMap<>();
        map.put("xzqhDm", xzqhDm);
        List<Xzqh> xzqhs = super.listByParam(map);
        return xzqhs.isEmpty() ? null : xzqhs.get(0);
    }

    private List<Xzqh> listByXzqhCj(String xzqhCj) {
        Map<String, Object> map = new HashMap<>();
        map.put("xzqhCj", xzqhCj);
        return super.listByParam(map);
    }

    private List<Xzqh> listBySjxzqhDm(String xzqhDm) {
        Map<String, Object> map = new HashMap<>();
        map.put("sjxzqhDm", xzqhDm);
        return super.listByParam(map);
    }

    private String getGeoJson(String xzqhDm) {
        return HttpUtil.get(String.format("https://geo.datav.aliyun.com/areas_v3/bound/%s_full.json", xzqhDm));
    }
}