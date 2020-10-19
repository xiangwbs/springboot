package com.xwbing.service.util;

import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author daofneg
 * @version $
 * @since 2020年01月15日 16:33
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PageVO<T> {
    /**
     * 总条数q
     */
    private Long total;
    /**
     * 每页数据对象
     */
    private List<T> data;

    public static <T> PageVO<T> empty() {
        return PageVO.<T>builder().total(0L).data(Collections.emptyList()).build();
    }
}
