package com.xwbing.web.controller.wx;

import cn.hutool.core.util.IdUtil;
import com.xwbing.service.domain.entity.vo.ScanCodeVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xwbing.starter.wx.open.app.WxOpenAppAutoConfiguration;
import com.xwbing.web.response.ApiResponse;
import com.xwbing.web.response.ApiResponseUtil;

import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.service.WxOAuth2Service;

/**
 * <a href="https://developers.weixin.qq.com/doc/oplatform/Website_App/WeChat_Login/Wechat_Login.html">网站应用微信登录开发指南</a>
 */
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/wx/open/oAuth2")
public class WxOpenOAuth2Controller {

    @ApiOperation("获取网站应用授权登录的二维码")
    @GetMapping("/web/buildAuthorizationUrl")
    public ApiResponse<ScanCodeVO> buildAuthorizationUrl(@RequestParam String appId, @RequestParam String redirectUri) {
        WxOAuth2Service openOAuth2Service = WxOpenAppAutoConfiguration.getOpenOAuth2Service(appId);
        String qrCode = openOAuth2Service.buildAuthorizationUrl(redirectUri, "snsapi_login", appId);
        String qrcodeKey = IdUtil.fastSimpleUUID();
        ScanCodeVO scanCode = ScanCodeVO.builder().qrcode(qrCode).qrcodeKey(appId + ":" + qrcodeKey).build();
        return ApiResponseUtil.success(scanCode);
    }

    @ApiOperation("网站应用扫码回调")
    @GetMapping("/web/callback")
    public void callback(@RequestParam String code, @RequestParam String state) throws WxErrorException {
        String[] split = state.split(":");
        String appId = split[0];
        String qrcodeKey = split[1];
        WxOAuth2Service openOAuth2Service = WxOpenAppAutoConfiguration.getOpenOAuth2Service(appId);
        WxOAuth2AccessToken accessToken = openOAuth2Service.getAccessToken(code);
        WxOAuth2UserInfo userInfo = openOAuth2Service.getUserInfo(accessToken, null);
        // 处理登录逻辑
    }

    @ApiOperation("获取用户信息")
    @GetMapping("/app/getUserInfo")
    public ApiResponse<WxOAuth2UserInfo> getUserInfo(@RequestParam String appId, @RequestParam String code) throws WxErrorException {
        WxOAuth2Service openOAuth2Service = WxOpenAppAutoConfiguration.getOpenOAuth2Service(appId);
        WxOAuth2AccessToken accessToken = openOAuth2Service.getAccessToken(code);
        return ApiResponseUtil.success(openOAuth2Service.getUserInfo(accessToken, null));
    }
}