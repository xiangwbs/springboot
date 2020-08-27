package com.xwbing.config.aliyun.oss;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.util.DigestUtils;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.xwbing.config.aliyun.oss.enums.ContentTypeEnum;

import lombok.extern.slf4j.Slf4j;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年08月26日 下午2:23
 */
@Slf4j
public class OssService {
    private final OSSClient ossClient;
    private final OssProperties ossProperties;

    public OssService(OSSClient ossClient, OssProperties ossProperties) {
        this.ossClient = ossClient;
        this.ossProperties = ossProperties;
    }

    /**
     * 上传富文本
     *
     * @param content
     *
     * @return
     */
    public String putHtml(String content) {
        String objectKey = generateObjectKey(ContentTypeEnum.HTML);
        PutObjectRequest putObjectRequest = new PutObjectRequest(ossProperties.getBucket(), objectKey,
                new ByteArrayInputStream(content.getBytes(Charset.forName("UTF-8"))));
        ossClient.putObject(putObjectRequest);
        return objectKey;
    }

    /**
     * 上传图片
     *
     * @param inputStream
     * @param suffix .png ...
     *
     * @return
     */
    public String putImage(InputStream inputStream, String suffix) {
        String objectKey = generateObjectKey(ContentTypeEnum.IMAGE) + suffix;
        PutObjectRequest putObjectRequest = new PutObjectRequest(ossProperties.getBucket(), objectKey, inputStream);
        ossClient.putObject(putObjectRequest);
        return objectKey;
    }

    /**
     * 上传文件
     *
     * @param inputStream
     * @param contentType
     * @param suffix
     *
     * @return
     *
     * @throws IOException
     */
    public String putFile(InputStream inputStream, String contentType, String suffix) throws IOException {
        String objectKey = generateObjectKey(ContentTypeEnum.FILE) + suffix;
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        metadata.setCacheControl("no-cache");
        metadata.setHeader("Pragma", "no-cache");
        metadata.setContentLength(inputStream.available());
        metadata.setLastModified(new Date(System.currentTimeMillis()));
        metadata.setContentEncoding("utf-8");
        metadata.setContentDisposition("inline;filename=" + objectKey);
        ossClient.putObject(ossProperties.getBucket(), objectKey, inputStream, metadata);
        return objectKey;
    }

    public void deleteObject(String objectKey) {
        ossClient.deleteObject(ossProperties.getBucket(), objectKey);
    }

    public boolean doesObjectExist(String objectKey) {
        return ossClient.doesObjectExist(ossProperties.getBucket(), objectKey);
    }

    public String getObject(String objectKey) {
        try {
            OSSObject object = ossClient.getObject(ossProperties.getBucket(), objectKey);
            InputStream inputStream = object.getObjectContent();
            byte[] buffer = new byte[1024];
            int len;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            while ((len = inputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            bos.close();
            return new String(bos.toByteArray(), "utf-8");
        } catch (Exception e) {
            return null;
        }
    }

    public InputStream getInputStream(String objectKey) {
        OSSObject object = ossClient.getObject(ossProperties.getBucket(), objectKey);
        return object.getObjectContent();
    }

    private String generateObjectKey(ContentTypeEnum contentType) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        String prefixDate = sdf.format(date);
        final String diagonal = "/";
        // 5位的随机字符串
        String randomStr = RandomStringUtils.randomAlphanumeric(6).toLowerCase();
        String md5UUID = DigestUtils.md5DigestAsHex(UUID.randomUUID().toString().getBytes());
        StringBuffer sb = new StringBuffer();
        sb.append(contentType.name().toLowerCase()).append(diagonal).append(prefixDate).append(diagonal).append(md5UUID)
                .append(randomStr);
        return sb.toString();
    }
}
