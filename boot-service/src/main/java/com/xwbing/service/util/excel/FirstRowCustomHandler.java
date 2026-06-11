package com.xwbing.service.util.excel;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

public class FirstRowCustomHandler implements SheetWriteHandler {
    private final String firstRowText;
    private final int maxColumnNum; // 最大列索引（用于合并第一行）
    private final int columnWidth;  // 新增：列宽（与@ColumnWidth保持一致）

    // 构造器：新增 columnWidth 参数，接收@ColumnWidth的值
    public FirstRowCustomHandler(String firstRowText, int maxColumnNum, int columnWidth) {
        this.firstRowText = firstRowText;
        this.maxColumnNum = maxColumnNum;
        this.columnWidth = columnWidth; // 例如传入140
    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        Sheet sheet = writeSheetHolder.getSheet();
        Workbook workbook = writeWorkbookHolder.getWorkbook();

        // 1. 创建第一行（标题行，索引0）
        Row firstRow = sheet.createRow(0);
        Cell cell = firstRow.createCell(0);
        cell.setCellValue(firstRowText);

        // 2. 合并第一行的所有列
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, maxColumnNum));

        // 3. 设置标题行行高
        firstRow.setHeight((short) 1500);

        // 4. 设置标题行样式
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        cell.setCellStyle(style);

        // 5. 关键修复：为所有列设置宽度（覆盖表头行和数据行）
        // 循环所有列，设置宽度（单位：字符宽度 × 256，与EasyExcel默认一致）
        for (int i = 0; i < maxColumnNum; i++) {
            sheet.setColumnWidth(i, columnWidth * 256);
        }
    }

    @Override
    public void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        // 无需处理
    }
}