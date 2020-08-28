package com.xwbing.web.controller.rest;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.xwbing.config.aliyun.oss.OssService;
import com.xwbing.config.aliyun.oss.enums.ContentTypeEnum;
import com.xwbing.config.aliyun.oss.vo.AccessCredentialsVO;
import com.xwbing.config.aliyun.oss.vo.VideoPlayAuthVO;
import com.xwbing.config.aliyun.oss.vo.VideoPlayInfoVO;
import com.xwbing.config.aliyun.oss.vo.VideoUploadAuthVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年08月27日 下午6:51
 */
@Slf4j
@Api(tags = "OssController", description = "oss相关接口")
@RestController
@RequestMapping("oss")
public class OssController {
    @Resource
    private OssService ossService;

    @ApiOperation("获取临时访问凭证")
    @GetMapping("getCredentials")
    public AccessCredentialsVO getCredentials(@RequestParam ContentTypeEnum contentType, @RequestParam String suffix) {
        return ossService.getCredentials(contentType, suffix);
    }

    @ApiOperation("获取视频上传地址和凭证")
    @GetMapping("getVideoUploadAuth")
    public VideoUploadAuthVO getVideoUploadAuth(@RequestParam String title, @RequestParam String fileName) {
        return ossService.getVideoUploadAuth(title, fileName);
    }

    @ApiOperation("刷新视频上传凭证")
    @GetMapping("refreshVideoUploadAuth")
    public VideoUploadAuthVO refreshVideoUploadAuth(@RequestParam String videoId) {
        return ossService.refreshVideoUploadAuth(videoId);
    }

    @ApiOperation("获取视频播放凭证")
    @GetMapping("getVideoPlayAuth")
    public VideoPlayAuthVO getVideoPlayAuth(@RequestParam String videoId) {
        return ossService.getVideoPlayAuth(videoId);
    }

    @ApiOperation("获取视频播放信息")
    @GetMapping("getVideoPlayInfo")
    public List<VideoPlayInfoVO> getVideoPlayInfo(@RequestParam String videoId) {
        return ossService.getVideoPlayInfo(videoId);
    }

    @ApiOperation("上传富文本")
    @GetMapping("putHtml")
    public String putHtml(@RequestParam String content) {
        return ossService.putHtml(content);
    }

    @ApiOperation("上传图片")
    @PostMapping("putImage")
    public String putImage(@RequestParam MultipartFile image) throws IOException {
        String filename = image.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf(".")).toLowerCase();
        return ossService.putImage(image.getInputStream(), suffix);
    }

    @ApiOperation("o上传文件")
    @PostMapping("putFile")
    public String putFile(@RequestParam MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf(".")).toLowerCase();
        return ossService.putFile(file.getInputStream(), file.getContentType(), suffix);
    }

    @ApiOperation("删除")
    @GetMapping("deleteObject")
    public void deleteObject(@RequestParam String objectKey) {
        ossService.deleteObject(objectKey);
    }

    @ApiOperation("是否存在")
    @GetMapping("doesObjectExist")
    public boolean doesObjectExist(@RequestParam String objectKey) {
        return ossService.doesObjectExist(objectKey);
    }
}
