package com.xwbing.service.domain.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年09月02日 3:13 PM
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InAndOutCountByDateVo {
    private String date;
    private Integer count;
}
