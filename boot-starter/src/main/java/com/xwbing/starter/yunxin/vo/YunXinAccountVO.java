package com.xwbing.starter.yunxin.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author daofeng
 * @version $Id$
 * @since 2021年12月27日 12:32 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YunXinAccountVO {
    private String accId;
    private String token;
}
