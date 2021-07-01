package com.xwbing.service.domain.mapper.sys;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xwbing.service.domain.entity.sys.SysUserLoginInOut;
import com.xwbing.service.domain.entity.vo.InAndOutCountByDateVo;
import com.xwbing.service.domain.entity.vo.InAndOutCountByUserVo;
import com.xwbing.service.domain.mapper.BaseMapper;

/**
 * 创建时间: 2018/6/1 21:50
 * 作者: xiangwb
 * 说明:
 */
public interface SysUserLoginInOutMapper extends BaseMapper<SysUserLoginInOut> {
    List<SysUserLoginInOut> findByInoutType(@Param("inoutType") Integer inoutType, @Param("startDate") LocalDate beginDate,
            @Param("endDate") LocalDate endDate);

    List<SysUserLoginInOut> countByType(Map<String, Object> map);

    List<InAndOutCountByDateVo> countByDate(@Param("inoutType") int inoutType, @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    List<InAndOutCountByUserVo> countByUser(@Param("inoutType") int inoutType);
}
