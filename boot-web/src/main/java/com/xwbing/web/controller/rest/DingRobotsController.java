package com.xwbing.web.controller.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.xwbing.service.demo.dingtalk.DingMarkdown;
import com.xwbing.service.demo.dingtalk.DingtalkRobotHelper;
import com.xwbing.service.demo.dingtalk.DingtalkRobotMsg;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daofeng
 * @version $
 * @since 2023年12月29日 下午4:33
 */
@Slf4j
@Api(tags = "钉钉机器人")
@RestController
@RequestMapping("/ding/callback")
public class DingRobotsController {
    @RequestMapping("/happyBirth")
    public String happyBirth(@RequestBody(required = false) JSONObject msg,
            @RequestHeader(required = false) String sign, @RequestHeader(required = false) String timestamp) {
        DingtalkRobotMsg robotMsg = DingtalkRobotHelper.receiveMsg(msg, sign, timestamp);
        if (robotMsg == null) {
            return null;
        }
        String content = robotMsg.getContent();
        if (content.contains("日子")) {
            DingtalkRobotHelper.sendText(robotMsg.getClient(), false,
                    Collections.singletonList(robotMsg.getSenderStaffId()), "我家老婆的生日呀");
        } else if (content.contains("你家老婆是谁")) {
            DingtalkRobotHelper.sendText(robotMsg.getClient(), false,
                    Collections.singletonList(robotMsg.getSenderStaffId()), "彩彩呀");
        } else if (content.contains("世界上最好的老婆是谁")) {
            DingtalkRobotHelper.sendText(robotMsg.getClient(), false,
                    Collections.singletonList(robotMsg.getSenderStaffId()), "彩彩呀");
        } else if ("世界上最美的女人又是谁".contains(content)) {
            DingtalkRobotHelper.sendText(robotMsg.getClient(), false,
                    Collections.singletonList(robotMsg.getSenderStaffId()), "当然是彩彩呀");
        } else if ("芳容".equals(content)) {
            List<String> orderedList = new ArrayList<>();
            orderedList.add(DingtalkRobotHelper.dtmdLink("想"));
            orderedList.add(DingtalkRobotHelper.dtmdLink("不想"));
            DingMarkdown dingMarkdown = DingMarkdown.build().appendOrderedList(orderedList);
            DingtalkRobotHelper.sendMarkdown(robotMsg.getClient(), false,
                    Collections.singletonList(robotMsg.getSenderStaffId()), "markdown消息", dingMarkdown);
        } else if ("想".equals(content)) {
            DingMarkdown dingMarkdown = DingMarkdown.build().appendText("这么美的女人是谁呀").newLine().appendImage(
                    "https://bot-oss-test.dingtax.cn/kbs-test/image/20231229/id00deb1bc18b340a3b255d3b56e5e2bdc.jpeg");
            DingtalkRobotHelper.sendMarkdown(robotMsg.getClient(), false,
                    Collections.singletonList(robotMsg.getSenderStaffId()), "markdown消息", dingMarkdown);
        } else if ("不想".equals(content)) {
            DingMarkdown dingMarkdown = DingMarkdown.build().appendText("不想也得让你见识下").newLine().appendImage(
                    "https://bot-oss-test.dingtax.cn/kbs-test/image/20231229/id00deb1bc18b340a3b255d3b56e5e2bdc.jpeg");
            DingtalkRobotHelper.sendMarkdown(robotMsg.getClient(), false,
                    Collections.singletonList(robotMsg.getSenderStaffId()), "markdown消息", dingMarkdown);
        } else if (content.contains("生日礼物")) {
            List<String> orderedList = new ArrayList<>();
            orderedList.add(DingtalkRobotHelper.dtmdLink("一首祝福诗"));
            orderedList.add(DingtalkRobotHelper.dtmdLink("老公的手工礼物"));
            orderedList.add(DingtalkRobotHelper.dtmdLink("出生报纸"));
            DingMarkdown dingMarkdown = DingMarkdown.build().appendOrderedList(orderedList);
            DingtalkRobotHelper.sendMarkdown(robotMsg.getClient(), false,
                    Collections.singletonList(robotMsg.getSenderStaffId()), "markdown消息", dingMarkdown);
        } else if ("一首祝福诗".equals(content)) {
            DingtalkRobotHelper.sendText(robotMsg.getClient(), false,
                    Collections.singletonList(robotMsg.getSenderStaffId()),
                    "彩彩生辰笑颜开，欢歌笑语乐满怀。\n" + "年年今日共庆贺，岁岁今朝好运来。");
        } else if ("老公的手工礼物".equals(content) || "出生报纸".equals(content)) {
            DingtalkRobotHelper.sendText(robotMsg.getClient(), false,
                    Collections.singletonList(robotMsg.getSenderStaffId()), "让你老公给你吧");
        } else {
            DingtalkRobotHelper.sendText(robotMsg.getClient(), false,
                    Collections.singletonList(robotMsg.getSenderStaffId()), "本宝宝还在学习中");
        }
        return null;
    }

}
