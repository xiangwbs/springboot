package com.xwbing.util;

import com.xwbing.exception.UtilException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * excel导出工具类
 *
 * @author xiangwb
 */
@Slf4j
public class ExcelUtil {
    /**
     * Excel 2003
     */
    public final static String XLS = "xls";
    /**
     * Excel 2007
     */
    public final static String XLSX = "xlsx";
    /**
     * 分隔符
     */
    public final static String SEPARATOR = ";";

    /**
     * 获取工作簿
     *
     * @param title
     * @param columns
     * @param list
     * @return
     */
    public static HSSFWorkbook Export(String title, String[] columns, List<String[]> list) {
        // 声明一个工作薄
        HSSFWorkbook wb = new HSSFWorkbook();
        // 声明一个单子并命名
        HSSFSheet sheet = wb.createSheet(title);
        // 给单子名称一个长度
        sheet.setDefaultColumnWidth((short) 15);
        // 生成一个样式
        HSSFCellStyle style = wb.createCellStyle();
        // 创建第一行（也可以称为表头）
        HSSFRow row = sheet.createRow(0);
        // 样式字体居中
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        //设置字体
//        HSSFFont font = wb.createFont();
//        font.setFontName("仿宋_GB2312");
//        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//粗体显示
//        font.setFontHeightInPoints((short) 12);
//        style.setFont(font);//选择需要用到的字体格式
        // 给表头第一行一次创建单元格
        if (null != columns && columns.length > 0) {
            for (int i = 0; i < columns.length; i++) {
                HSSFCell cell = row.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(style);
            }
        }
        if (CollectionUtils.isNotEmpty(list)) {
            // 向单元格里填充数据
            for (int i = 0; i < list.size(); i++) {
                row = sheet.createRow(i + 1);
                assert columns != null;
                for (int j = 0; j < columns.length; j++) {
                    row.createCell(j).setCellValue(list.get(i)[j]);
                    row.setRowStyle(style);//没有效果。。。。
                }
            }
        }
        return wb;
    }

    /**
     * 将数据list转换成excel导出的形式
     *
     * @param list
     * @return
     */
    public static <T> List<String[]> convert2List(List<T> list) {
        List<String[]> result = new ArrayList<>(list.size());
        T obj4Class = list.get(0);
        Class<?> classOfT = obj4Class.getClass();
        Field[] declaredFields = classOfT.getDeclaredFields();
        String[] temp;
        for (T obj : list) {
            temp = new String[declaredFields.length];
            for (int i = 0; i < declaredFields.length; i++) {
                String fieldName = declaredFields[i].getName();
                StringBuffer getMethodStr;
                // 拼接get方法
                getMethodStr = new StringBuffer();
                getMethodStr.append("get");
                getMethodStr.append(fieldName.substring(0, 1).toUpperCase());
                getMethodStr.append(fieldName.substring(1));
                String value = "";
                Method getMethod;
                try {
                    getMethod = classOfT.getMethod(getMethodStr.toString());
                } catch (NoSuchMethodException e1) {
                    log.error(e1.getMessage());
                    continue;
                } catch (SecurityException e1) {
                    log.error(e1.getMessage());
                    continue;
                }
                // 执行get方法
                try {
                    value = String.valueOf(getMethod.invoke(obj, new Object[0]));
                    value = Objects.equals("null", value) ? null : value;
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    // TODO Auto-generated catch block
                    log.error(e.getMessage());
                }
                temp[i] = value;
            }
            result.add(temp);
        }
        return result;
    }

    /**
     * 将导入的excel文件生成类
     *
     * @param file     excel文件
     * @param sheetNum excel页签
     * @param classVO  要导出的类型
     * @return
     */
    public static <T> List<T> getObjectFromExcel(File file, int sheetNum, Class<T> classVO) {
        List<T> resultList = new ArrayList<>();
        T obj;
        try {
            // 获取解析结果：每行是一个String
            // 格式：[Name;Sex;Email;NeedRePswd;Status;UserName;NickName;UnitId;Rank;Office,
            // INPUT111;1.0;INPUT111@sss.com;0.0;1.0;INPUT111;INPUT111;asdfasdfasdfasdf;1.0,
            // INPUT222;1.0;INPUT111@sss.com;0.0;1.0;INPUT111;INPUT111;asdfasdfasdfasdf;1.0]
            List<String> importObjects = exportListFromExcel(file, sheetNum);
            if (CollectionUtils.isNotEmpty(importObjects)) {
                // Class<? extends Object> classVO = obj.getClass();
                Constructor<?> cons[] = classVO.getConstructors();
                // 取出列名称用来设置set方法
                String colNames = importObjects.get(0);
                // 解析列名
                String[] colName = colNames.split(";");
                String setMethodStr;
                for (int i = 1; i < importObjects.size(); i++) {
                    // 解析数据
                    String[] values = importObjects.get(i).split(SEPARATOR);
                    // 初始化对象
                    try {
                        obj = (T) cons[0].newInstance();
                    } catch (Exception e3) {
                        e3.printStackTrace();
                        return null;
                    }
                    for (int j = 0; j < values.length; j++) {
                        // set方法名
                        setMethodStr = "set" + colName[j];
                        Method setMethod;
                        // 尝试调用set方法，因无法确定参数,尝试参数类型，目前只有String和Integer两种
                        try {
                            setMethod = classVO.getMethod(setMethodStr, String.class);
                            setMethod.invoke(obj, String.valueOf(values[j]));
                        } catch (Exception e) {
                            try {
                                setMethod = classVO.getMethod(setMethodStr, Integer.class);
                                setMethod.invoke(obj, Double.valueOf(values[j]).intValue());
                            } catch (Exception e1) {
                                // 目前没有bool值参数暂不添加
                                // try
                                // {
                                // setMethod =
                                // classVO.getMethod(setMethodStr,Boolean.class);
                                // setMethod.invoke(obj,
                                // Boolean.valueOf(values[j]));
                                // }
                                // catch (Exception e3)
                                // {
                                // }
                                System.out.println("Exception" + e);
                                continue;
                            }
                        }
                    }
                    resultList.add(obj);
                }
            }
            return resultList;
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new UtilException("excel文件生成类失败");
        }
    }

    /**
     * 由Excel文件的Sheet导出至List
     *
     * @param file
     * @param sheetNum
     * @return
     */
    private static List<String> exportListFromExcel(File file, int sheetNum) throws IOException {
        return exportListFromExcel(new FileInputStream(file), FilenameUtils.getExtension(file.getName()), sheetNum);
    }

    /**
     * 由Excel流的Sheet导出至List
     *
     * @param is
     * @param extensionName
     * @param sheetNum
     * @return
     * @throws IOException
     */
    private static List<String> exportListFromExcel(InputStream is, String extensionName, int sheetNum) throws IOException {
        Workbook workbook = null;
        if (extensionName.toLowerCase().equals(XLS)) {
            workbook = new HSSFWorkbook(is);
        } else if (extensionName.toLowerCase().equals(XLSX)) {
            workbook = new XSSFWorkbook(is);
        }
        return exportListFromExcel(workbook, sheetNum);
    }

    /**
     * 由指定的Sheet导出至List
     *
     * @param workbook
     * @param sheetNum
     * @return
     */
    private static List<String> exportListFromExcel(Workbook workbook, int sheetNum) {
        Sheet sheet = workbook.getSheetAt(sheetNum);
        // 解析公式结果
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        List<String> list = new ArrayList<>();
        int minRowIx = sheet.getFirstRowNum();
        int maxRowIx = sheet.getLastRowNum();
        for (int rowIx = minRowIx; rowIx <= maxRowIx; rowIx++) {
            org.apache.poi.ss.usermodel.Row row = sheet.getRow(rowIx);
            StringBuilder sb = new StringBuilder();
            short minColIx = row.getFirstCellNum();
            short maxColIx = row.getLastCellNum();
            for (short colIx = minColIx; colIx <= maxColIx; colIx++) {
                Cell cell = row.getCell((int) colIx);
                CellValue cellValue = evaluator.evaluate(cell);
                if (cellValue == null) {
                    continue;
                }
                // 经过公式解析，最后只存在Boolean、Numeric和String三种数据类型，此外就是Error了
                // 其余数据类型，根据官方文档，完全可以忽略http://poi.apache.org/spreadsheet/eval.html
                switch (cellValue.getCellType()) {
                    case Cell.CELL_TYPE_BOOLEAN:
                        sb.append(SEPARATOR + cellValue.getBooleanValue());
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                        // 这里的日期类型会被转换为数字类型，需要判别后区分处理
                        if (DateUtil.isCellDateFormatted(cell)) {
                            sb.append(SEPARATOR + cell.getDateCellValue());
                        } else {
                            sb.append(SEPARATOR + cellValue.getNumberValue());
                        }
                        break;
                    case Cell.CELL_TYPE_STRING:
                        sb.append(SEPARATOR + cellValue.getStringValue());
                        break;
                    case Cell.CELL_TYPE_FORMULA:
                        break;
                    case Cell.CELL_TYPE_BLANK:
                        break;
                    case Cell.CELL_TYPE_ERROR:
                        break;
                    default:
                        break;
                }
            }
            list.add(sb.substring(1, sb.length()));
        }
        return list;
    }
}
