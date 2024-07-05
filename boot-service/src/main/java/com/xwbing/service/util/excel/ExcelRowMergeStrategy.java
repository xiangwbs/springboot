package com.xwbing.service.util.excel;

import com.alibaba.excel.write.handler.RowWriteHandler;
import com.alibaba.excel.write.handler.context.RowWriteHandlerContext;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.List;

/**
 * 单元格合并
 *
 * @author daofeng
 * @version $
 * @since 2024年07月05日 2:10 PM
 */
public class ExcelRowMergeStrategy implements RowWriteHandler {
    // 需要合并的列索引列表 0开始
    private final List<Integer> mergeColumnIndexes;
    // 从哪行开始合并 0开始
    private final int mergeStartRowIndex;

    public ExcelRowMergeStrategy(int mergeStartRowIndex, List<Integer> mergeColumnIndexes) {
        this.mergeStartRowIndex = mergeStartRowIndex;
        this.mergeColumnIndexes = mergeColumnIndexes;
    }

    @Override
    public void afterRowDispose(RowWriteHandlerContext context) {
        if (context.getHead() || context.getRelativeRowIndex() == null) {
            return;
        }
        int currentRowIndex = context.getRowIndex();
        if (currentRowIndex > mergeStartRowIndex) {
            Row currentRow = context.getRow();
            for (Integer columnIndex : mergeColumnIndexes) {
                mergeWithPrevRow(context.getWriteSheetHolder().getSheet(), currentRow, currentRowIndex, columnIndex);
            }
        }
    }

    private void mergeWithPrevRow(Sheet sheet, Row currentRow, int currentRowIndex, int columnIndex) {
        String currentValue = getCellValue(currentRow, columnIndex);
        Row prevRow = sheet.getRow(currentRowIndex - 1);
        String prevValue = getCellValue(prevRow, columnIndex);
        if (currentValue.equals(prevValue)) {
            List<CellRangeAddress> mergeRegions = sheet.getMergedRegions();
            boolean isMerged = false;
            for (int i = 0; i < mergeRegions.size() && !isMerged; i++) {
                CellRangeAddress cellRangeAddr = mergeRegions.get(i);
                if (cellRangeAddr.isInRange(currentRowIndex - 1, columnIndex)) {
                    sheet.removeMergedRegion(i);
                    cellRangeAddr.setLastRow(currentRowIndex);
                    sheet.addMergedRegion(cellRangeAddr);
                    isMerged = true;
                }
            }
            if (!isMerged) {
                CellRangeAddress cellRangeAddress = new CellRangeAddress(currentRowIndex - 1, currentRowIndex, columnIndex, columnIndex);
                sheet.addMergedRegion(cellRangeAddress);
            }
        }
    }

    private String getCellValue(Row row, int columnIndex) {
        if (row.getCell(columnIndex) != null) {
            return row.getCell(columnIndex).getStringCellValue();
        }
        return "";
    }
}