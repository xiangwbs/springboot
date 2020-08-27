package com.xwbing.config.aliyun.oss;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.util.DigestUtils;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.auth.sts.AssumeRoleRequest;
import com.aliyuncs.auth.sts.AssumeRoleResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.xwbing.config.aliyun.oss.enums.ContentTypeEnum;
import com.xwbing.config.exception.ConfigException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年08月26日 下午2:23
 */
@Slf4j
public class OssService {
    private final OSSClient ossClient;
    private final DefaultAcsClient acsClient;
    private final OssProperties ossProperties;

    public OssService(OSSClient ossClient, DefaultAcsClient acsClient, OssProperties ossProperties) {
        this.ossClient = ossClient;
        this.acsClient = acsClient;
        this.ossProperties = ossProperties;
    }

    public AccessCredentialsVO getCredentials(ContentTypeEnum contentType, String suffix) {
        final AssumeRoleRequest request = new AssumeRoleRequest();
        request.setMethod(MethodType.POST);
        request.setRoleArn(ossProperties.getStsRoleArn());
        request.setRoleSessionName(ossProperties.getStsRoleSessionName());
        try {
            final AssumeRoleResponse response = acsClient.getAcsResponse(request);
            AssumeRoleResponse.Credentials credentials = response.getCredentials();
            AccessCredentialsVO credentialsVo = new AccessCredentialsVO();
            credentialsVo.setAccessKeyId(credentials.getAccessKeyId());
            credentialsVo.setAccessKeySecret(credentials.getAccessKeySecret());
            credentialsVo.setSecurityToken(credentials.getSecurityToken());
            String expiration = ZonedDateTime.parse(credentials.getExpiration())
                    .withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            credentialsVo.setExpiration(expiration);
            credentialsVo.setEndpoint(ossProperties.getEndpoint());
            credentialsVo.setBucket(ossProperties.getBucket());
            credentialsVo.setRegion(ossProperties.getRegion());
            credentialsVo.setObjectKey(generateObjectKey(contentType) + suffix);
            return credentialsVo;
        } catch (ClientException e) {
            log.error("获取SecurityToken异常:", e);
            throw new ConfigException("获取SecurityToken异常");
        }
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

    /**
     * 删除
     *
     * @param objectKey
     */
    public void deleteObject(String objectKey) {
        ossClient.deleteObject(ossProperties.getBucket(), objectKey);
    }

    /**
     * 是否存在
     *
     * @param objectKey
     *
     * @return
     */
    public boolean doesObjectExist(String objectKey) {
        return ossClient.doesObjectExist(ossProperties.getBucket(), objectKey);
    }

    /**
     * 获取字符串
     *
     * @param objectKey
     *
     * @return
     */
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

    /**
     * 获取流
     *
     * @param objectKey
     *
     * @return
     */
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
        sb.append(contentType.getCode()).append(diagonal).append(prefixDate).append(diagonal).append(md5UUID)
                .append(randomStr);
        return sb.toString();
    }
}
