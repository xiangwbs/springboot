package com.xwbing.service.demo.map.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author daofeng
 * @version $
 * @since 2026年06月17日 08:53
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MapGeoRes {
    // 经度
    private BigDecimal longitude;
    // 纬度
    private BigDecimal latitude;

    public static MapGeoRes of(BigDecimal longitude, BigDecimal latitude) {
        return new MapGeoRes(longitude, latitude);
    }
}
