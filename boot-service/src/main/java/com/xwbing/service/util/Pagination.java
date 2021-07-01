package com.xwbing.service.util;

import java.util.Collections;
import java.util.List;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页基础类
 *
 * @author xiangwb
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class Pagination<T> {
    @ApiModelProperty(value = "当前页")
    private Integer currentPage = 1;
    @ApiModelProperty(value = "每页显示的条数")
    private Integer pageSize = 10;
    @ApiModelProperty(value = "总条数")
    private Long totalNum;
    @ApiModelProperty(value = "总页数")
    private Integer totalPage;
    @ApiModelProperty(value = "分页结果")
    private List<T> data;

    public Pagination(PageInfo pageInfo) {
        super();
        this.currentPage = pageInfo.getPageNum();
        this.pageSize = pageInfo.getPageSize();
        this.totalNum = pageInfo.getTotal();
        this.totalPage = pageInfo.getPages();
        this.data = (List<T>)ConvertUtil.beanToJson(pageInfo.getList());
        // this.hasNext = pageInfo.isHasNextPage();
    }

    public Pagination(Page page) {
        super();
        this.currentPage = page.getPageNum();
        this.pageSize = page.getPageSize();
        this.totalNum = page.getTotal();
        this.totalPage = page.getPages();
        this.data = (List<T>)ConvertUtil.beanToJson(page);
    }

    public Pagination result(Pagination page, PageInfo pageInfo) {
        page.setTotalNum(pageInfo.getTotal());
        page.setTotalPage(pageInfo.getPages());
        page.setData((List)ConvertUtil.beanToJson(pageInfo.getList()));
        // page.setHasNext(pageInfo.isHasNextPage());
        return page;
    }

    public static <T> Pagination<T> empty() {
        return Pagination.<T>builder().totalNum(0L).data(Collections.emptyList()).totalPage(0).build();
    }
}