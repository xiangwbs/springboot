package com.xwbing.service.util;

import cn.hutool.core.util.ReflectUtil;
import com.xwbing.service.domain.entity.dto.MarkDownCaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author daofeng
 * @version $
 * @since 2025年11月10日 17:02
 */
public class MarkDownUtil {
    public static void main(String[] args) {
        MarkDownCaseDTO case1 = new MarkDownCaseDTO();
        case1.setName("道风");
        case1.setAge(6);
        MarkDownCaseDTO case2 = new MarkDownCaseDTO();
        case2.setName("XIANGWE");
        case2.setAge(7);
        List<MarkDownCaseDTO> list = Arrays.asList(case1, case2);
        String markdown = markdown(MarkDownCaseDTO.class, list);
        System.out.println("");
    }
    public static <T> String markdown(Class<T> head, List<T> dataList) {
        List<MarkdownDTO> headList = Arrays.stream(head.getDeclaredFields())
                .map(field -> {
                    ApiModelProperty annotation = field.getAnnotation(ApiModelProperty.class);
                    if (annotation == null || StringUtils.isEmpty(annotation.value())) {
                        return null;
                    }
                    return MarkdownDTO.of(annotation.value(), field.getName());
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        String headStr = headList.stream().map(MarkdownDTO::getHead).collect(Collectors.joining("|"));
        headStr = "|" + headStr + "|";
        String separator = IntStream
                .range(0, headList.size())
                .mapToObj(i -> "---")
                .collect(Collectors.joining("|"));
        separator = "|" + separator + "|";
        String dataListStr = dataList.stream()
                .map(data -> {
                    String dataStr = headList.stream()
                            .map(dto -> {
                                Object fieldValue = ReflectUtil.getFieldValue(data, dto.getField());
                                return Objects.toString(fieldValue);
                            })
                            .collect(Collectors.joining("|"));
                    return "|" + dataStr + "|";
                }).collect(Collectors.joining("\n"));
        return headStr + "\n" + separator + "\n" + dataListStr;
    }

    @Data
    public static class MarkdownDTO {
        private String head;
        private String field;

        public static MarkdownDTO of(String head,String field){
            MarkdownDTO dto = new MarkdownDTO();
            dto.setHead(head);
            dto.setField(field);
            return dto;
        }
    }
}
