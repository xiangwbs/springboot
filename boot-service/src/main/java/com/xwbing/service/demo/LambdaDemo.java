package com.xwbing.service.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.alibaba.fastjson.JSONObject;
import com.xwbing.service.util.ThreadUtil;

/**
 * Date: 2017/6/15 17:09
 * Author: xiangwb
 * 说明: 数据量大或者每个元素涉及到复杂操作的用parallelStream
 */
public class LambdaDemo {
    @Resource
    private ThreadPoolTaskExecutor taskExecutor;

    public static void main(String[] args) {
        List<String> abc = Arrays.asList("b", "c", "a");
        Integer[] ints = { 1, 2, 4, 2, 3, 5, 5, 6, 8, 9, 7, 10 };
        List<Integer> lists = new ArrayList<>(Arrays.asList(ints));
        //匿名内部类
        Thread t = new Thread(() -> System.out.println("hello,lambda"));
        t.start();

        //获取stream
        Stream<Integer> stream = Arrays.stream(ints);
        stream = Stream.of(ints);
        stream = lists.stream();
        Stream<List<Integer>> streams = Stream.of(lists);

        //遍历
        IntStream.rangeClosed(1, 2).parallel().forEach(System.out::println);//遍历时：对象,json等引用类型可直接转换
        //排序
        lists.sort(Comparator.naturalOrder());//升序，不需要收集
        lists.sort(Comparator.reverseOrder());//降序，不需要收集
        lists.sort(Comparator.comparingInt(o -> o));//升序，不需要收集
        lists = lists.stream().sorted((o1, o2) -> o2 - o1).collect(Collectors.toList());// 降序，需要收集

        System.out.println("map:" + lists.stream().map(o1 -> o1 * 2).collect(Collectors.toList()));//转换成新元素
        System.out.println("flatMap:" + Stream.of("hello welcome", "world hello", "hello world", "hello world welcome")
                .flatMap(item -> Arrays.stream(item.split(" "))).distinct().collect(Collectors.toList()));
        System.out.println(
                "peak:" + lists.stream().peek(String::valueOf).collect(Collectors.toList()));//生成一个包含原Stream元素的新Stream
        System.out.println("distinct:" + lists.stream().distinct().collect(Collectors.toList()));//去重(去重逻辑依赖元素的equals方法)
        System.out.println("limit:" + lists.stream().limit(4).collect(Collectors.toList()));//截取
        System.out.println("skip:" + lists.stream().skip(4).collect(Collectors.toList()));//丢弃
        //去重
        List<SysUser> collect = listAll().stream().collect(Collectors
                .collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(SysUser::getName))),
                        ArrayList::new));
        List<SysUser> collect1 = listAll().stream().filter(distinctByKey(SysUser::getName))
                .collect(Collectors.toList());

        //匹配
        boolean b1 = lists.stream().anyMatch(o -> o == 1);
        boolean b = lists.stream().allMatch(o -> o == 1);
        boolean b2 = lists.stream().noneMatch(o -> o == 1);
        //过滤
        System.out.println("filter:" + lists.stream().filter(o1 -> o1 > 3 && o1 < 8).collect(Collectors.toList()));
        Predicate<Integer> gt = integer -> integer > 3;//函数式接口Predicate
        Predicate<Integer> lt = integer -> integer < 8;
        System.out.println("重用filter:" + lists.stream().filter(gt.and(lt)).collect(Collectors.toList()));
        //删除
        lists.removeIf(item -> item > 3);//根据条件删除，不用收集

        //聚合
        System.out
                .println("reduce sum:" + lists.stream().filter(Objects::nonNull).reduce(0, (o1, o2) -> o1 + o2));//数字聚合
        System.out.println("reduce ids:" + abc.stream().filter(Objects::nonNull)
                .reduce("", (sum, item) -> sum + "," + item));//(a,b,c)-->a,b,c
        String reduce = abc.stream().filter(Objects::nonNull).reduce("", (sum, item) -> sum + "'" + item + "',");
        reduce = StringUtils.isNotEmpty(reduce) ? reduce.substring(0, reduce.lastIndexOf(",")) : "";
        System.out.println("reduce id in:" + reduce);//(a,b,c)-->'a','b','c',-->'a','b','c'

        //join
        System.out.println("join:" + abc.stream().collect(Collectors.joining(",")));//(a,b,c)-->a,b,c
        System.out.println("join:" + String.join(",", abc));

        //统计
        IntSummaryStatistics statistics = lists.stream().mapToInt(x -> x).summaryStatistics();
        System.out.println("List中最大的数字 : " + statistics.getMax());
        System.out.println("List中最小的数字 : " + statistics.getMin());
        System.out.println("List所有数字的总和: " + statistics.getSum());
        System.out.println("List所有数字的平均值: " + statistics.getAverage());
        System.out.println("List成员个数: " + statistics.getCount());
        //all example
        System.out.println(
                "all:" + lists.stream().filter(Objects::nonNull).distinct().mapToInt(num -> num * 2).skip(2).limit(4)
                        .sum());

        //toMap
        // 遍历list存入map里 key不能重复 value不能为null
        Map<Integer, String> jsonMap = getList().stream()
                .collect(Collectors.toMap(o1 -> o1.getInteger("id"), o2 -> o2.getString("name")));
        Map<String, SysUser> userMap = listAll().stream()
                .collect(Collectors.toMap(SysUser::getId, Function.identity()));
        userMap = listAll().stream().collect(Collectors.toMap(SysUser::getId, sysUser -> sysUser));
        Map<String, String> userNameMap = listAll().stream()
                .collect(Collectors.toMap(SysUser::getId, SysUser::getName));
        //解决key重复 value为null
        Map<String, String> userSexNameMap = listAll().stream().filter(sysUser -> sysUser.getName() != null)
                .collect(Collectors.toMap(SysUser::getSex, SysUser::getName, (n1, n2) -> n1 + "," + n2));
        Map<String, SysUser> userSexMinAgeMap = listAll().stream().collect(Collectors
                .toMap(SysUser::getSex, Function.identity(),
                        BinaryOperator.minBy(Comparator.comparing(SysUser::getAge))));

        //分组
        Map<String, List<SysUser>> sexMap = listAll().stream().collect(Collectors.groupingBy(SysUser::getSex));
        Map<String, Integer> sexSumAgeMap = listAll().stream()
                .collect(Collectors.groupingBy(SysUser::getSex, Collectors.summingInt(SysUser::getAge)));
        Map<String, Long> sexCountMap = listAll().stream()
                .collect(Collectors.groupingBy(SysUser::getSex, Collectors.counting()));
        Map<String, List<String>> sexNameMap = listAll().stream().collect(
                Collectors.groupingBy(SysUser::getSex, Collectors.mapping(SysUser::getName, Collectors.toList())));
        sexNameMap = listAll().stream().collect(Collectors.groupingBy(SysUser::getSex,
                Collectors.mapping(o -> o.getName() + ":" + o.getAge(), Collectors.toList())));
        // Map<String, String> sexSingleNameMap = listAll().stream().collect(Collectors.groupingBy(SysUser::getSex,
        //         Collectors.mapping(SysUser::getName, Collectors.reducing("", (name1, name2) -> name2))));

        //异步回调
        List<JSONObject> sysUsers = CompletableFuture.supplyAsync(LambdaDemo::getList)
                .join();//线程等待,效果等同于get(),会拋出CompletionException
    }

    /**
     * 遍历集合，集合里数据还要进行复杂操作，导致速度很慢,用以下操作
     */
    public List<Integer> supplyAsync() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(i);
        }
        int size = list.size();
        CompletableFuture[] futures = new CompletableFuture[size];
        List<Integer> finalList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            finalList.add(null);//在异步之前size+1，必须有这步，否则会下标越界
            Integer integer = list.get(i);
            final int pos = i;
            futures[i] = CompletableFuture.supplyAsync(() -> finalList.set(pos, integer), taskExecutor);//按原来顺序存
        }
        CompletableFuture.allOf(futures).join();//线程等待,效果等同于get(),会拋出CompletionException
        //        CompletableFuture<Void> completableFuture = CompletableFuture.allOf(futures);
        //        try {
        //            completableFuture.get();
        //        } catch (InterruptedException | ExecutionException e) {
        //            log.error(e.getMessage());
        //            throw new BusinessException("获取数据出错");
        //        }
        return finalList;
    }

    public void supplyAsync1() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(i);
        }
        List<CompletableFuture<Void>> futureList = list.stream()
                .map(i -> CompletableFuture.runAsync(System.out::println, ThreadUtil.build().singleThreadPool()))
                .collect(Collectors.toList());
        futureList.stream().map(CompletableFuture::join).collect(Collectors.toList());
    }

    /**
     * filter
     *
     * @return
     */
    public List<SysUser> getRoleUsers() {
        if (1 == 1) {//分两步
            Predicate<SysUser> roles = sysUser -> {
                String admin = isAdmin(sysUser.getId());
                return "Y".equals(admin);
            };
            return listAll().stream().filter(roles).collect(Collectors.toList());
        } else {//一步到底
            return listAll().stream().filter(sysUser -> {
                String admin = isAdmin(sysUser.getId());
                return "Y".equals(admin);
            }).collect(Collectors.toList());
        }
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), true) == null;
    }

    /**
     * 假接口假数据-----------------------------------------------------------------------------------------------------
     */

    private static List<JSONObject> getList() {
        List<JSONObject> list = new ArrayList<>();
        JSONObject j1 = new JSONObject();
        j1.put("id", 1);
        j1.put("name", "a");
        list.add(j1);
        JSONObject j2 = new JSONObject();
        j2.put("id", 2);
        j2.put("name", "b");
        list.add(j2);
        return list;
    }

    public static List<SysUser> listAll() {
        List<SysUser> list = new ArrayList<>();
        SysUser a = new SysUser();
        a.setId("1");
        a.setName("aa");
        a.setSex("男");
        a.setAge(10);
        SysUser aa = new SysUser();
        aa.setId("2");
        aa.setName("aa");
        aa.setSex("男");
        aa.setAge(20);
        SysUser aaa = new SysUser();
        aaa.setId("3");
        aaa.setName("aa");
        aaa.setSex("男");
        aaa.setAge(30);
        SysUser bb = new SysUser();
        bb.setId("4");
        bb.setName("bb");
        bb.setSex("女");
        bb.setAge(40);
        SysUser cc = new SysUser();
        cc.setId("5");
        cc.setName("cc");
        cc.setSex("女");
        cc.setAge(50);
        list.add(a);
        list.add(aa);
        list.add(aaa);
        list.add(bb);
        list.add(cc);
        return list;
    }

    private String isAdmin(String id) {
        return "Y";
    }

    public static class SysUser {
        private String name;
        private String sex;
        private String id;
        private int age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }
}
