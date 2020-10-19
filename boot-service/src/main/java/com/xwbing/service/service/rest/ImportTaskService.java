package com.xwbing.service.service.rest;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xwbing.service.enums.ImportStatusEnum;
import com.xwbing.service.domain.entity.rest.ImportTask;
import com.xwbing.service.domain.mapper.rest.ImportTaskMapper;
import com.xwbing.service.service.BaseService;
import com.xwbing.service.util.Pagination;
import com.xwbing.service.util.RestMessage;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年04月27日 下午9:45
 */
@Service
public class ImportTaskService extends BaseService<ImportTaskMapper, ImportTask> {
    @Resource
    private ImportTaskMapper importTaskMapper;

    @Override
    protected ImportTaskMapper getMapper() {
        return importTaskMapper;
    }

    public Pagination page(Pagination page) {
        PageInfo<Object> pageInfo = PageHelper.startPage(page.getCurrentPage(), page.getPageSize())
                .doSelectPageInfo(() -> importTaskMapper.findAll());
        return page.result(page, pageInfo);
    }

    public RestMessage updateExceptionFail(String id) {
        ImportTask fail = ImportTask.builder().id(id).status(ImportStatusEnum.FAIL).detail("系统异常，请重新导入")
                .build();
        return super.update(fail);
    }
}
