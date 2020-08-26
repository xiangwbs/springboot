package com.xwbing.service.domain.entity.model;

import java.util.List;
import java.util.Map;

import com.xwbing.service.enums.SexEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年08月23日 下午1:12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NullModel {
    private String string;
    private Boolean aBoolean;
    private Long aLong;
    private List<String> list;
    private Map<String, Object> map;
    private SexEnum sexEnum;
}
