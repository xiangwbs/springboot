package com.xwbing.service.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.xwbing.domain.entity.rest.ImportFailLog;
import com.xwbing.domain.mapper.rest.ImportFailLogMapper;
import com.xwbing.service.BaseService;
import com.xwbing.util.RestMessage;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年04月28日 上午10:51
 */
@Service
public class ImportFailLogService extends BaseService<ImportFailLogMapper, ImportFailLog> {
    @Resource
    private ImportFailLogMapper importFailLogMapper;

    @Override
    protected ImportFailLogMapper getMapper() {
        return importFailLogMapper;
    }

    public List<ImportFailLog> listByImportId(String importId) {
        Map<String, Object> map = new HashMap<>();
        map.put("importId", importId);
        return super.listByParam(map);
    }

    public RestMessage save(String importId, String content, String remark) {
        ImportFailLog build = ImportFailLog.builder().importId(importId).content(content).remark(remark).build();
        return super.save(build);
    }
}
