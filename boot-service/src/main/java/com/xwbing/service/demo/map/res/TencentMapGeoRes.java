package com.xwbing.service.demo.map.res;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author daofeng
 * @version $
 * @since 2026年06月06日 10:27
 */
@Data
public class TencentMapGeoRes {
    // 最终用于坐标解析的地址或地点名称（仅供参考，不建议实际使用）
    private String title;
    // 解析到的坐标（GCJ02坐标系）
    private Location location;
    // 行政区划信息
    private AdInfo ad_info;
    // 解析后的地址部件
    private AddressComponents address_components;

    @Data
    public static class Location {
        private BigDecimal lng;
        private BigDecimal lat;
    }

    @Data
    public static class AdInfo {
        // 行政区划代码
        private String adcode;
    }

    @Data
    public static class AddressComponents {
        private String province;
        private String city;
        private String district;
    }
}