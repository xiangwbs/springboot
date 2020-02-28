package com.xwbing.config.util.dingTalk;


import java.util.ArrayList;

/**
 * @author xiangwb
 */
public class TextMessageDemo {
    private DingTalkClient client = new DingTalkClient();

    public TextMessageDemo() {
    }

    public void testSendTextMessage() throws Exception {
        TextMessage message = new TextMessage("我就是我, 是不一样的烟火");
        SendResult result = this.client.send("https://oapi.dingtalk.com/robot/send?access_token=f41b013832ca349f45cabce7dd7e64b19f3e1aabc4414d71a1e9ee050d65e141", "SEC9f4390e8c897e118ab487b0ed5874100a7693920eb87e9a968ee8129b0874e3f", message);
        System.out.println(result);
    }

    public void testSendTextMessageWithAt() throws Exception {
        TextMessage message = new TextMessage("我就是我, 是不一样的烟火");
        message.addAtMobile("134xxxx4170");
        SendResult result = this.client.send("https://oapi.dingtalk.com/robot/send?access_token=f41b013832ca349f45cabce7dd7e64b19f3e1aabc4414d71a1e9ee050d65e141", "SEC9f4390e8c897e118ab487b0ed5874100a7693920eb87e9a968ee8129b0874e3f", message);
        System.out.println(result);
    }

    public void testSendTextMessageWithAtAll() throws Exception {
        TextMessage message = new TextMessage("我就是我, 是不一样的烟火");
        message.setAtAll(true);
        SendResult result = this.client.send("https://oapi.dingtalk.com/robot/send?access_token=f41b013832ca349f45cabce7dd7e64b19f3e1aabc4414d71a1e9ee050d65e141", "SEC9f4390e8c897e118ab487b0ed5874100a7693920eb87e9a968ee8129b0874e3f", message);
        System.out.println(result);
    }

    public void testSendTextMessageWithAtAndAtAll() throws Exception {
        TextMessage message = new TextMessage("我就是我, 是不一样的烟火");
        ArrayList<String> atMobiles = new ArrayList<>();
        atMobiles.add("134xxxx4170");
        message.addAtMobiles(atMobiles);
        message.setAtAll(true);
        SendResult result = this.client.send("https://oapi.dingtalk.com/robot/send?access_token=f41b013832ca349f45cabce7dd7e64b19f3e1aabc4414d71a1e9ee050d65e141", "SEC9f4390e8c897e118ab487b0ed5874100a7693920eb87e9a968ee8129b0874e3f", message);
        System.out.println(result);
    }
}
