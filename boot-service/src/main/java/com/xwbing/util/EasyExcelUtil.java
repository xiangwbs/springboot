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
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.metadata.ReadSheet;
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
        try (ServletOutputStream outputStream = response.getOutputStream()) {
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
            //自动列宽,自动关闭流
            EasyExcel.write(out).head(getHead(heads)).registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet(sheetName).doWrite(excelData);
        } catch (IOException e) {
            log.error("excel write error:{}", e.getMessage());
        }
    }

    /**
     * 生成加密excel
     *
     * @param basedir
     * @param fileName
     * @param sheetName
     * @param password
     * @param heads
     * @param excelData
     */
    public static void encryptWrite(String basedir, String fileName, String sheetName, String password,
            List<String> heads, List<List<Object>> excelData) throws IOException {
        Path path = FileSystems.getDefault().getPath(basedir, fileName + ExcelTypeEnum.XLSX.getValue());
        OPCPackage opc = null;
        try (OutputStream out = Files.newOutputStream(path);
                OutputStream encryptOut = Files.newOutputStream(path);
                POIFSFileSystem fs = new POIFSFileSystem()) {
            //自动列宽,自动关闭流
            EasyExcel.write(out).head(getHead(heads)).registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet(sheetName).doWrite(excelData);
            //添加密码保护
            EncryptionInfo info = new EncryptionInfo(EncryptionMode.agile);
            Encryptor enc = info.getEncryptor();
            enc.confirmPassword(password);
            //加密文件
            opc = OPCPackage.open(path.toFile(), PackageAccess.READ_WRITE);
            opc.save(enc.getDataStream(fs));
            fs.writeFilesystem(encryptOut);
        } catch (Exception e) {
            log.error("excel write error:{}", e.getMessage());
        } finally {
            if (opc != null) {
                opc.close();
            }
        }
    }

    /**
     * 读取excel
     * analysisEventListener不能被spring管理，要每次读取excel都要new，如果里面用到springBean可以用构造方法传进去
     *
     * @param filePath
     * @param sheetNo
     * @param analysisEventListener
     */
    public static void read(String filePath, int sheetNo,
            AnalysisEventListener<Map<String, Object>> analysisEventListener) {
        String fileName = FileSystems.getDefault().getPath(filePath).toString();
        ExcelReader excelReader = EasyExcel.read(fileName, analysisEventListener).build();
        ReadSheet readSheet = EasyExcel.readSheet(sheetNo).build();
        excelReader.read(readSheet);
        //这里千万别忘记关闭，读的时候会创建临时文件，到时磁盘会崩的
        excelReader.finish();
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