package com.xwbing.service.domain.entity.model;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.xwbing.service.enums.SexEnum;
import lombok.Data;

import java.util.List;

/**
 * @author daofeng
 * @version $
 * @since 2026年06月17日 11:30
 */
@Data
public class DetailRes {
    private String name;
    private List<String> picList;
    private String sexCode;
    private String sexName;

    public static DetailRes of(DetailEntity entity) {
        DetailRes detailRes = BeanUtil.copyProperties(entity, DetailRes.class);
        detailRes.setPicList(StrUtil.split(entity.getPic(), ','));
        detailRes.setSexName(SexEnum.parse(entity.getSexCode()));
        return detailRes;
    }

    public static DetailEntity of(DetailReq req) {
        DetailEntity entity = BeanUtil.copyProperties(req, DetailEntity.class);
        entity.setPic(CollUtil.join(req.getPicList(), ","));
        return entity;
    }

    @Data
    public static class DetailEntity {
        private String name;
        private String pic;
        private String sexCode;
    }

    @Data
    public static class DetailReq {
        private String name;
        private List<String> picList;
        private String sexCode;
    }
}
