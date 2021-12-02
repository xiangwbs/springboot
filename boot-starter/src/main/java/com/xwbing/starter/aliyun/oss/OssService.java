package com.xwbing.starter.aliyun.oss;

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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
import com.aliyuncs.vod.model.v20170321.CreateUploadVideoRequest;
import com.aliyuncs.vod.model.v20170321.CreateUploadVideoResponse;
import com.aliyuncs.vod.model.v20170321.GetPlayInfoRequest;
import com.aliyuncs.vod.model.v20170321.GetPlayInfoResponse;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthRequest;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthResponse;
import com.aliyuncs.vod.model.v20170321.RefreshUploadVideoRequest;
import com.aliyuncs.vod.model.v20170321.RefreshUploadVideoResponse;
import com.xwbing.starter.aliyun.oss.enums.ContentTypeEnum;
import com.xwbing.starter.aliyun.oss.vo.AccessCredentialsVO;
import com.xwbing.starter.aliyun.oss.vo.VideoPlayAuthVO;
import com.xwbing.starter.aliyun.oss.vo.VideoPlayInfoVO;
import com.xwbing.starter.aliyun.oss.vo.VideoUploadAuthVO;
import com.xwbing.starter.exception.ConfigException;

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

    /**
     * 获取oss资源地址
     *
     * @param objectKey
     *
     * @return
     */
    public String getUrl(String objectKey) {
        return "https://" + ossProperties.getBucket() + "." + ossProperties.getEndpoint() + "/" + objectKey;
    }

    /**
     * 获取临时访问凭证
     *
     * @param contentType
     * @param suffix 文件后缀
     *
     * @return
     */
    public AccessCredentialsVO getCredentials(ContentTypeEnum contentType, String suffix) {
        final AssumeRoleRequest request = new AssumeRoleRequest();
        request.setMethod(MethodType.POST);
        request.setRoleArn(ossProperties.getStsRoleArn());
        request.setRoleSessionName(ossProperties.getStsRoleSessionName());
        request.setDurationSeconds(3600L);
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
     * 获取视频上传地址和凭证
     *
     * @param title
     * @param fileName 完整的文件名称（带后缀名）
     *
     * @return
     */
    public VideoUploadAuthVO getVideoUploadAuth(String title, String fileName) {
        final CreateUploadVideoRequest request = new CreateUploadVideoRequest();
        request.setFileName(fileName);
        request.setTitle(title);

        // 视频分类
        // request.setCateId(1000099146L);
        try {
            final CreateUploadVideoResponse response = acsClient.getAcsResponse(request);
            return VideoUploadAuthVO.builder().videoId(response.getVideoId()).uploadAuth(response.getUploadAuth())
                    .uploadAddress(response.getUploadAddress()).build();
        } catch (ClientException e) {
            log.error("获取视频上传凭证异常:", e);
            throw new ConfigException("获取视频上传凭证异常");
        }
    }

    /**
     * 刷新视频上传凭证
     *
     * @param videoId
     *
     * @return
     */
    public VideoUploadAuthVO refreshVideoUploadAuth(String videoId) {
        final RefreshUploadVideoRequest request = new RefreshUploadVideoRequest();
        request.setVideoId(videoId);
        try {
            final RefreshUploadVideoResponse response = acsClient.getAcsResponse(request);
            return VideoUploadAuthVO.builder().requestId(response.getRequestId()).videoId(videoId)
                    .uploadAuth(response.getUploadAuth()).uploadAddress(response.getUploadAddress()).build();
        } catch (ClientException e) {
            log.error("刷新视频上传凭证异常:", e);
            throw new ConfigException("刷新视频上传凭证异常");
        }
    }

    /**
     * 获取视频播放凭证
     *
     * @param videoId
     *
     * @return
     */
    public VideoPlayAuthVO getVideoPlayAuth(String videoId) {
        final GetVideoPlayAuthRequest request = new GetVideoPlayAuthRequest();
        request.setVideoId(videoId);
        try {
            final GetVideoPlayAuthResponse response = acsClient.getAcsResponse(request);
            GetVideoPlayAuthResponse.VideoMeta videoMeta = response.getVideoMeta();
            return VideoPlayAuthVO.builder().playAuth(response.getPlayAuth()).videoMeta(
                    VideoPlayAuthVO.VideoMeta.builder().videoId(videoMeta.getVideoId())
                            .coverURL(videoMeta.getCoverURL()).status(videoMeta.getStatus())
                            .duration(videoMeta.getDuration()).title(videoMeta.getTitle()).build()).build();
        } catch (ClientException e) {
            log.error("获取播放地址和播放凭证异常:", e);
            throw new ConfigException("获取播放地址和播放凭证异常");
        }
    }

    /**
     * 获取视频播放信息
     *
     * @param videoId
     *
     * @return
     */
    public List<VideoPlayInfoVO> getVideoPlayInfo(String videoId) {
        final GetPlayInfoRequest request = new GetPlayInfoRequest();
        request.setVideoId(videoId);
        try {
            final GetPlayInfoResponse response = acsClient.getAcsResponse(request);
            return response.getPlayInfoList().stream()
                    .map(playInfo -> VideoPlayInfoVO.builder().playURL(playInfo.getPlayURL()).size(playInfo.getSize())
                            .status(playInfo.getStatus()).definition(playInfo.getDefinition())
                            .duration(Double.valueOf(playInfo.getDuration())).build()).collect(Collectors.toList());
        } catch (ClientException e) {
            log.error("获取播放地址异常:", e);
            throw new ConfigException("获取播放地址异常");
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
     * 获取富文本字符串
     *
     * @param objectKey
     *
     * @return
     */
    public String getString(String objectKey) {
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
