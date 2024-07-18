package com.xwbing.service.demo;

import com.alibaba.dashscope.app.Application;
import com.alibaba.dashscope.app.ApplicationParam;
import com.alibaba.dashscope.app.ApplicationResult;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.aliyun.broadscope.bailian.sdk.AccessTokenClient;
import com.aliyun.broadscope.bailian.sdk.ApplicationClient;
import com.aliyun.broadscope.bailian.sdk.BaiLianSdkException;
import com.aliyun.broadscope.bailian.sdk.models.*;
import io.reactivex.Flowable;
import org.apache.commons.lang3.exception.ExceptionUtils;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


public class Ademo {
    public static void main(String[] args) {

    }

    public static void streamCall() throws NoApiKeyException, InputRequiredException {
        ApplicationParam param = ApplicationParam.builder()
                .apiKey("xxx")
                .appId("xxx")
                .prompt("如何做土豆炖猪脚?")
                .build();

        Application application = new Application();
        Flowable<ApplicationResult> result = application.streamCall(param);
        result.blockingForEach(data -> {
            System.out.printf("requestId: %s, text: %s, finishReason: %s\n",
                    data.getRequestId(), data.getOutput().getText(), data.getOutput().getFinishReason());

        });
    }

    /**
     * 流式响应使用示例
     */
    public static void testCompletionsWithStream() {
        String accessKeyId = "xxx";
        String accessKeySecret = "xxx";
        String agentKey = "xxx";
        String appId = "xxx";

        AccessTokenClient accessTokenClient = new AccessTokenClient(accessKeyId, accessKeySecret, agentKey);
        String token = accessTokenClient.getToken();
        ApplicationClient client = ApplicationClient.builder()
                .token(token)
                .build();

        List<ChatRequestMessage> messages = new ArrayList<>();
        messages.add(new ChatSystemMessage("你是一名天文学家, 能够帮助小学生回答宇宙与天文方面的问题"));
        messages.add(new ChatUserMessage("宇宙中为什么会存在黑洞"));

        CompletionsRequest request = new CompletionsRequest()
                .setAppId(appId)
                .setMessages(messages)
                .setParameters(new CompletionsRequest.Parameter()
                        //开启增量输出模式，后面输出不会包含已经输出的内容
                        .setIncrementalOutput(true)
                        //返回choice message结果
                        .setResultFormat("message")
                );

        CountDownLatch latch = new CountDownLatch(1);
        Flux<CompletionsResponse> response = client.streamCompletions(request);

        response.subscribe(
                data -> {
                    if (data.isSuccess()) {
                        System.out.printf("%s", data.getData().getChoices().get(0).getMessage().getContent());
                    } else {
                        System.out.printf("failed to create completion, requestId: %s, code: %s, message: %s\n",
                                data.getRequestId(), data.getCode(), data.getMessage());
                    }
                },
                err -> {
                    System.out.printf("failed to create completion, err: %s\n", ExceptionUtils.getStackTrace(err));
                    latch.countDown();
                },
                () -> {
                    System.out.println("\ncreate completion completely");
                    latch.countDown();
                }
        );

        try {
            latch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new BaiLianSdkException(e);
        }
    }
}