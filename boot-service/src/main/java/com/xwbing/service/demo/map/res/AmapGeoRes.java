package com.xwbing.service.demo.map.res;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author daofeng
 * @version $
 * @since 2026年06月06日 10:27
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AmapGeoRes extends AmapRes {
    private List<GeoCode> geocodes;

    @Data
    public static class GeoCode {
        // 国家
        private String country;
        // 省份名
        private String province;
        // 城市名
        private String city;
        // 城市编码
        private String citycode;
        // 所在区
        private String district;
        // 街道
        private String street;
        // 门牌
        private String number;
        // 区域编码
        private String adcode;
        // 经度，纬度
        private String location;
        // 经度
        private BigDecimal longitude;
        // 纬度
        private BigDecimal latitude;
        // 匹配级别
        private String level;

    }
}
