package com.xwbing.service.util;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;

import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.util.MapUtils;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.style.column.AbstractColumnWidthStyleStrategy;

/**
 * 列宽自适应
 *
 * @author daofeng
 * @version $Id$
 * @since 2023年02月08日 7:06 PM
 */
public class ExcelColumnWidthStyleStrategy extends AbstractColumnWidthStyleStrategy {
    private static final int MAX_COLUMN_WIDTH = 100;
    private final Map<Integer, Map<Integer, Integer>> cache = MapUtils.newHashMapWithExpectedSize(8);

    @Override
    protected void setColumnWidth(WriteSheetHolder writeSheetHolder, List<WriteCellData<?>> cellDataList, Cell cell,
            Head head, Integer relativeRowIndex, Boolean isHead) {
        boolean needSetWidth = isHead || !org.apache.commons.collections4.CollectionUtils.isEmpty(cellDataList);
        if (!needSetWidth) {
            return;
        }
        Map<Integer, Integer> maxColumnWidthMap = cache
                .computeIfAbsent(writeSheetHolder.getSheetNo(), key -> new HashMap<>(16));
        Integer columnWidth = dataLength(cellDataList, cell, isHead);
        if (columnWidth < 0) {
            return;
        }
        if (columnWidth > MAX_COLUMN_WIDTH) {
            columnWidth = MAX_COLUMN_WIDTH;
        }
        Integer maxColumnWidth = maxColumnWidthMap.get(cell.getColumnIndex());
        if (maxColumnWidth == null || columnWidth > maxColumnWidth) {
            maxColumnWidthMap.put(cell.getColumnIndex(), columnWidth);
            writeSheetHolder.getSheet().setColumnWidth(cell.getColumnIndex(), columnWidth * 256);
        }
    }

    private Integer dataLength(List<WriteCellData<?>> cellDataList, Cell cell, Boolean isHead) {
        if (isHead) {
            return cell.getStringCellValue().getBytes().length;
        }
        WriteCellData<?> cellData = cellDataList.get(0);
        CellDataTypeEnum type = cellData.getType();
        if (type == null) {
            return -1;
        }
        switch (type) {
            case STRING:
                try {
                    return cellData.getStringValue().getBytes("UTF-8").length;
                } catch (UnsupportedEncodingException e) {
                    return 20;
                }
            case BOOLEAN:
                try {
                    return cellData.getBooleanValue().toString().getBytes("UTF-8").length;
                } catch (UnsupportedEncodingException e) {
                    return 20;
                }
            case NUMBER:
                try {
                    return cellData.getNumberValue().toString().getBytes("UTF-8").length;
                } catch (UnsupportedEncodingException e) {
                    return 20;
                }
            default:
                return -1;
        }
    }
}