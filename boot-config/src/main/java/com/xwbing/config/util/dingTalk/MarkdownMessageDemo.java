package com.xwbing.config.util.dingTalk;


import java.util.ArrayList;

/**
 * @author xiangwb
 */
public class MarkdownMessageDemo {
    private DingTalkClient client = new DingTalkClient();

    public MarkdownMessageDemo() {
    }

    public void testSendMarkdownMessage() throws Exception {
        MarkdownMessage message = new MarkdownMessage();
        message.setTitle("markdown message");
        message.addItem(MarkdownMessage.getHeaderText(1, "一级标题"));
        message.addItem(MarkdownMessage.getHeaderText(6, "六级标题"));
        message.addItem(MarkdownMessage.getReferenceText("引用"));
        message.addItem("正常字体");
        message.addItem(MarkdownMessage.getBoldText("加粗字体"));
        message.addItem(MarkdownMessage.getItalicText("斜体"));
        ArrayList<String> orderList = new ArrayList<>();
        orderList.add("有序列表1");
        orderList.add("有序列表2");
        message.addItem(MarkdownMessage.getOrderListText(orderList));
        ArrayList<String> unOrderList = new ArrayList<>();
        unOrderList.add("无序列表1");
        unOrderList.add("无序列表2");
        message.addItem(MarkdownMessage.getUnOrderListText(unOrderList));
        message.addItem(MarkdownMessage.getImageText("https://gw.alicdn.com/tfs/TB1ut3xxbsrBKNjSZFpXXcXhFXa-846-786.png"));
        message.addItem(MarkdownMessage.getLinkText("天气", "https://www.seniverse.com"));
        message.setAtAll(false);
        message.addAtMobile("134xxxx4170");
        SendResult result = this.client.send("https://oapi.dingtalk.com/robot/send?access_token=f41b013832ca349f45cabce7dd7e64b19f3e1aabc4414d71a1e9ee050d65e141", "SEC9f4390e8c897e118ab487b0ed5874100a7693920eb87e9a968ee8129b0874e3f", message);
        System.out.println(result);
    }
}
