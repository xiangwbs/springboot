package com.xwbing.service.demo.map;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.TypeReference;
import com.xwbing.service.demo.map.res.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * @author daofeng
 * @version $
 * @since 2026年06月06日 10:19
 */
@Slf4j
@Service
public class MapHelper {
    private static final Integer TIMEOUT = 15 * 1000;
    public static final String HOST = "https://restapi.amap.com";
    public static final String GEO_URL = "/v3/geocode/geo";
    @Value("${yqa.urban.amapKey:486343e313d675e5532c736b5802c169,08242938c26c3a9bbf48ff124c85f17d}")
    private List<String> amapKey;
    @Value("${yqa.urban.tencentMapKey:HVKBZ-PZGCU-H5KV2-G7WJE-MTULZ-EWFWN,6JWBZ-6WOKC-4ZP27-APVQ6-VS5PH-NLBKA}")
    private List<String> tencentMapKey;

    public AmapGeoRes.GeoCode geo(String address, String city) {
        try {
            // 调用量5000次/天，并发量3次/s
            HashMap<String, Object> paramMap = new HashMap<>();
            paramMap.put("key", RandomUtil.randomEle(amapKey));
            paramMap.put("address", address);
            paramMap.put("city", city);
            String res = HttpUtil.get(HOST + GEO_URL, paramMap, TIMEOUT);
            AmapGeoRes geoRes = AmapRes.parseRes(GEO_URL, JSONUtil.toJsonStr(paramMap), res, AmapGeoRes.class);
            if (geoRes == null) {
                return null;
            }
            List<AmapGeoRes.GeoCode> geocodes = geoRes.getGeocodes();
            if (CollectionUtil.isEmpty(geocodes)) {
                return null;
            }
            AmapGeoRes.GeoCode geoCode = geocodes.get(0);
            String[] locationArray = geoCode.getLocation().split(",");
            geoCode.setLongitude(new BigDecimal(locationArray[0]));
            geoCode.setLatitude(new BigDecimal(locationArray[1]));
            return geoCode;
        } catch (Exception e) {
            log.error("amap geo address:{} error", address, e);
            return null;
        }
    }

    public TencentMapGeoRes tencentGeo(String address) {
        try {
            // 调用量6000次/天，并发量5次/s
            HashMap<String, Object> paramMap = new HashMap<>();
            paramMap.put("key", RandomUtil.randomEle(tencentMapKey));
            paramMap.put("address", address);
            String res = HttpUtil.get("https://apis.map.qq.com/ws/geocoder/v1/", paramMap, TIMEOUT);
            return TencentMapRes.parse("/ws/geocoder/v1", JSONUtil.toJsonStr(paramMap), res, new TypeReference<TencentMapRes<TencentMapGeoRes>>() {
            });
        } catch (Exception e) {
            log.error("map geocoder address:{} error", address, e);
            return null;
        }
    }

    public MapGeoRes allGeo(String address, String city) {
        TencentMapGeoRes tencentMapGeoRes = this.tencentGeo(address);
        if (tencentMapGeoRes != null) {
            return MapGeoRes.of(tencentMapGeoRes.getLocation().getLng(), tencentMapGeoRes.getLocation().getLat());
        } else {
            AmapGeoRes.GeoCode geo = this.geo(address, city);
            if (geo != null) {
                return MapGeoRes.of(geo.getLongitude(), geo.getLatitude());
            } else {
                return null;
            }
        }
    }
}