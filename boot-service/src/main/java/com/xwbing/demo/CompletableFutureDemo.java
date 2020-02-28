package com.xwbing.demo;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 项目名称: boot-module-pro
 * 创建时间: 2018/2/6 10:54
 * 作者: xiangwb
 * 说明: apply进行转换，有返回值。accept进行消耗，无返回值
 * supplyAsync()有返回值，runAsync()无返回值
 * 参数: Function(函数)有返回值 Consumer(消费者)无返回值
 */
public class CompletableFutureDemo {
    /**
     * 结合两个CompletionStage的结果，进行转化后返回
     *
     * @return 有返回值
     */
    public static JSONObject thenCombine() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "hello";
        }).thenCombine(CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //出现异常,异常会被限制在执行任务的线程的范围内，最终会杀死该线程，而这会导致等待 get 方法返回结果的线程永久地被阻塞。
            if (1 == 1)
                throw new RuntimeException("异常");
            return "world";
        }), (s1, s2) -> {
            JSONObject object = new JSONObject();
            object.put("s1", s1);
            object.put("s2", s2);
            return object;
        })
//                .exceptionally(e -> {//捕获CompletionException，如有全局异常处理，可省略
//                    throw new RuntimeException(e.getMessage());
//                })
                .join();
    }

    public static void main(String[] args) {
        long l = System.currentTimeMillis();
        thenCombine();
        long l1 = System.currentTimeMillis();
        System.out.println(l1 - l);
    }

    /**
     * 结合两个CompletionStage的结果，进行消耗
     *
     * @return void
     */
    public static JSONObject thenAcceptBoth() {
        JSONObject jsonObject = new JSONObject();
        CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "hello";
        }).thenAcceptBoth(CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "world";
        }), (s1, s2) -> {
            jsonObject.put("s1", s1);
            jsonObject.put("s2", s2);
        }).join();
        return jsonObject;
    }

    /**
     * 在两个CompletionStage都运行完执行
     * 不关心这两个的结果，只关心这两个执行完毕，之后在进行操作（Runnable）
     *
     * @return void
     */
    public static List<Integer> runAfterBoth() {
        List<Integer> allResult = new ArrayList<>();
        List<Integer> s1 = new ArrayList<>();
        List<Integer> s2 = new ArrayList<>();
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            s1.add(1);
        }).runAfterBothAsync(CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            s2.add(1);
        }), () -> {
            for (int i = 0; i < s1.size(); i++) {
                Integer ii = s1.get(i);
                for (int j = 0; j < s2.size(); j++) {
                    if (j == i) {
                        Integer jj = s2.get(j);
                        allResult.add(ii + jj);
                        break;
                    }
                }
            }
        }).join();
        return allResult;
    }

    /**
     * 两个CompletionStage，谁计算的快，我就用那个CompletionStage的结果进行消耗。
     *
     * @return void
     */
    public static JSONObject acceptEither() {
        JSONObject object = new JSONObject();
        CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "hello";
        }).acceptEither(CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "world";
        }), s -> object.put("s", s)).join();
        return object;
    }
}
