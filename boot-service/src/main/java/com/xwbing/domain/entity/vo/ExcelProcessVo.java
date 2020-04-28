package com.xwbing.domain.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年04月25日 下午10:02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcelProcessVo {
    private boolean success;
    private int process;
    private Integer errorCount;
    private Integer successCount;
    private String msg;
}
