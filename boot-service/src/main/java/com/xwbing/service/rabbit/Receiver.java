// package com.xwbing.service.rabbit;
//
// import java.text.MessageFormat;
//
// import javax.annotation.Resource;
//
// import org.springframework.amqp.rabbit.annotation.RabbitListener;
// import org.springframework.stereotype.Component;
//
// import com.xwbing.service.exception.BusinessException;
// import com.xwbing.service.service.rest.MailService;
// import com.xwbing.service.util.RestMessage;
//
// import lombok.extern.slf4j.Slf4j;
//
//
// import static com.xwbing.service.rabbit.RabbitConstant.EMAIL_QUEUE;
// import static com.xwbing.service.rabbit.RabbitConstant.MESSAGE_QUEUE;
//
// /**
//  * 创建时间: 2018/4/25 15:12
//  * 作者: xiangwb
//  * 说明: 消费者
//  */
// @Slf4j
// @Component
// public class Receiver {
//     @Resource
//     private MailService mailService;
//
//     /**
//      * 处理邮件队列信息
//      *
//      * @param msg
//      * @return
//      */
//     @RabbitListener(queues = EMAIL_QUEUE)
//     public String processEmail(String[] msg) {
//         String content = MessageFormat.format("你的用户名是:{0},密码是:{1}", msg[1], msg[2]);
//         RestMessage restMessage = new RestMessage();
//         try {
//             restMessage = mailService.sendSimpleMail( "注册成功", content,null,msg[0]);
//         } catch (BusinessException e) {
//             log.error("发送邮件给{}失败", msg[0]);
//         }
//         boolean success = restMessage.isSuccess();
//         return MessageFormat.format("成功发送邮件给{0}:{1}", msg[1], success);
//     }
//
//     /**
//      * 处理短信队列消息
//      *
//      * @param msg
//      */
//     @RabbitListener(queues = MESSAGE_QUEUE)
//     public String processMessage(String[] msg) {
//         String response = MessageFormat.format("收到{0}队列的消息:{1}", MESSAGE_QUEUE, msg);
//         return response.toUpperCase();
//     }
// }
