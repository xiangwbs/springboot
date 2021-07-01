package com.xwbing.service.service.sys;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xwbing.service.domain.entity.dto.InAndOutPageDto;
import com.xwbing.service.domain.entity.sys.SysUser;
import com.xwbing.service.domain.entity.sys.SysUserLoginInOut;
import com.xwbing.service.domain.entity.vo.InAndOutCountByDateVo;
import com.xwbing.service.domain.entity.vo.InAndOutCountByUserVo;
import com.xwbing.service.domain.mapper.sys.SysUserLoginInOutMapper;
import com.xwbing.service.domain.mapper.sys.SysUserMapper;
import com.xwbing.service.enums.LoginInOutEnum;
import com.xwbing.service.service.BaseService;
import com.xwbing.service.util.DateUtil2;
import com.xwbing.service.util.PageVO;

/**
 * 创建时间: 2017/11/7 9:56
 * 作者: xiangwb
 * 说明: 用户登录登出服务层
 */
@Service
public class SysUserLoginInOutService extends BaseService<SysUserLoginInOutMapper, SysUserLoginInOut> {
    public static final int[] ITEM = { 1, 2 };
    @Resource
    private SysUserLoginInOutMapper loginInOutMapper;
    @Resource
    private SysUserMapper userMapper;

    @Override
    protected SysUserLoginInOutMapper getMapper() {
        return loginInOutMapper;
    }

    /**
     * 根据类型分页查询
     *
     * @param pageDto
     *
     * @return
     */
    public PageVO<SysUserLoginInOut> page(InAndOutPageDto pageDto) {
        Page<Object> page = PageHelper.startPage(pageDto.getPageNumber(), pageDto.getPageSize());
        List<SysUserLoginInOut> list = loginInOutMapper
                .findByInoutType(pageDto.getInout(), pageDto.getStartDate(), pageDto.getEndDate());
        if (page.getTotal() == 0) {
            return PageVO.empty();
        }
        list.forEach(loginInOut -> loginInOut.setInoutTypeName(LoginInOutEnum.parse(loginInOut.getInoutType())));
        return PageVO.<SysUserLoginInOut>builder().data(list).total(page.getTotal()).build();
    }

    /**
     * 登录登出饼图
     *
     * @param startDate
     * @param endDate
     *
     * @return
     */
    public JSONArray pie(String startDate, String endDate) {
        JSONArray result = new JSONArray();
        //获取数据
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isNotEmpty(startDate)) {
            map.put("startDate", startDate);
        }
        if (StringUtils.isNotEmpty(endDate)) {
            map.put("endDate", endDate);
        }
        List<SysUserLoginInOut> list = loginInOutMapper.countByType(map);
        //统计数据
        Map<Integer, List<SysUserLoginInOut>> collect = list.stream()
                .collect(Collectors.groupingBy(SysUserLoginInOut::getInoutType));
        JSONObject obj;
        for (int item : ITEM) {
            obj = new JSONObject();
            String name = LoginInOutEnum.parse(item);
            obj.put("name", name);
            List<SysUserLoginInOut> sample = collect.get(item);
            if (sample != null) {
                int sum = sample.get(0).getCount();
                obj.put("value", sum);
            } else {
                obj.put("value", 0);
            }
            result.add(obj);
        }
        return result;
    }

    /**
     * 登录登出柱状图
     *
     * @param startDate
     * @param endDate
     *
     * @return
     */
    public Map<String, Object> bar(String startDate, String endDate) {
        Map<String, Object> result = new HashMap<>();
        //统计日期
        List<String> days = DateUtil2.listDate(startDate, endDate);
        result.put("xAxis", days);
        List<SysUserLoginInOut> list = loginInOutMapper
                .findByInoutType(null, LocalDate.parse(startDate), LocalDate.parse(endDate));
        //统计数据
        Map<String, List<SysUserLoginInOut>> collect = list.stream()
                .peek(loginInOut -> loginInOut.setRecordTime(loginInOut.getRecordTime().substring(0, 10)))
                .collect(Collectors.groupingBy(SysUserLoginInOut::getRecordTime));
        JSONObject obj;
        JSONArray array;
        JSONArray series = new JSONArray();
        for (int item : ITEM) {
            obj = new JSONObject();
            String name = LoginInOutEnum.parse(item);
            obj.put("name", name);
            array = new JSONArray();
            for (String day : days) {
                List<SysUserLoginInOut> sample = collect.get(day);
                if (sample != null) {
                    int sum = (int)sample.stream().filter(it -> item == it.getInoutType()).count();
                    array.add(sum);
                } else {
                    array.add(0);
                }
            }
            obj.put("data", array);
            series.add(obj);
        }
        result.put("series", series);
        return result;
    }

    public List<InAndOutCountByDateVo> countByDate(int inoutType, LocalDate startDate, LocalDate endDate) {
        List<InAndOutCountByDateVo> countByDate = loginInOutMapper.countByDate(inoutType, startDate, endDate);
        Map<String, Integer> dayCountMap = countByDate.stream()
                .collect(Collectors.toMap(InAndOutCountByDateVo::getDate, InAndOutCountByDateVo::getCount));
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        List<InAndOutCountByDateVo> stats = new ArrayList<>();
        InAndOutCountByDateVo puv;
        for (long i = 0; i <= days; i++) {
            puv = new InAndOutCountByDateVo();
            LocalDate date = startDate.plusDays(i);
            puv.setDate(date.toString());
            Integer count = Optional.ofNullable(dayCountMap.get(date.toString())).orElse(0);
            puv.setCount(count);
            stats.add(puv);
        }
        return stats;
    }

    public PageVO<InAndOutCountByUserVo> countByUser(int inoutType, int currentPage, int pageSize) {
        Page<Object> page = PageHelper.startPage(currentPage, pageSize);
        List<InAndOutCountByUserVo> countByUser = loginInOutMapper.countByUser(inoutType);
        if (page.getTotal() == 0) {
            return PageVO.empty();
        }
        List<String> userIds = countByUser.stream().map(InAndOutCountByUserVo::getUserId).collect(Collectors.toList());
        Map<String, String> userNameMap = userMapper.findByIds(userIds).stream()
                .collect(Collectors.toMap(SysUser::getId, SysUser::getUserName));
        countByUser.forEach(inAndOut -> inAndOut.setUserName(userNameMap.get(inAndOut.getUserId())));
        return PageVO.<InAndOutCountByUserVo>builder().data(countByUser).total(page.getTotal()).build();
    }
}
