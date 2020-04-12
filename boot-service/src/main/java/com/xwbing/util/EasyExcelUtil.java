package com.xwbing.util;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;

import lombok.extern.slf4j.Slf4j;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年04月12日 上午12:04
 */
@Slf4j
public class EasyExcelUtil {
    /**
     * 文件下载
     * 自定义head {@ExcelProperty}
     * EasyExcel.write(response.getOutputStream(), DownloadData.class).sheet("模板").doWrite(data());
     *
     * @param response
     * @param fileName
     * @param sheetName
     * @param heads
     * @param excelData
     */
    public static void download(HttpServletResponse response, String fileName, String sheetName, List<String> heads,
            List<List<Object>> excelData) throws IOException {
        ServletOutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/octet-stream");
            //防止中文乱码
            fileName = URLEncoder.encode(fileName, "UTF-8");
            response.setHeader("Content-Disposition",
                    "attachment; filename=" + fileName + ExcelTypeEnum.XLSX.getValue());
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            //自动列宽,不关闭流
            EasyExcel.write(outputStream).head(getHead(heads))
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).autoCloseStream(Boolean.FALSE)
                    .sheet(sheetName).doWrite(excelData);
        } catch (Exception e) {
            log.error("excel download error:{}", e.getMessage());
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().println("下载文件失败");
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    /**
     * 生成excel
     *
     * @param basedir
     * @param fileName
     * @param sheetName
     * @param heads
     * @param excelData
     */
    public static void write(String basedir, String fileName, String sheetName, List<String> heads,
            List<List<Object>> excelData) {
        Path path = FileSystems.getDefault().getPath(basedir, fileName + ExcelTypeEnum.XLSX.getValue());
        try (OutputStream out = Files.newOutputStream(path)) {
            //自动列宽
            EasyExcel.write(out).head(getHead(heads)).registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet(sheetName).doWrite(excelData);
        } catch (IOException e) {
            log.error("excel write error:{}", e.getMessage());
        }
    }

    /**
     * 获取表头
     *
     * @param heads
     *
     * @return
     */
    private static List<List<String>> getHead(List<String> heads) {
        List<List<String>> list = new ArrayList<>();
        heads.forEach(title -> list.add(Collections.singletonList(title)));
        return list;
    }
}
