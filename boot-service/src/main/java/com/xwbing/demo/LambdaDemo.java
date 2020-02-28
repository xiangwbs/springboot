package com.xwbing.demo;

import com.alibaba.fastjson.JSONObject;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Date: 2017/6/15 17:09
 * Author: xiangwb
 * 说明: 数据量大或者每个元素涉及到复杂操作的用parallelStream
 */
public class LambdaDemo {
    @Resource
    private ThreadPoolTaskExecutor taskExecutor;

    public static void main(String[] args) {
        //匿名内部类
        Thread t = new Thread(() -> System.out.println("hello,lambda"));
        t.start();
        List<String> abc = Arrays.asList("b", "c", "a");
        abc.sort(Comparator.naturalOrder());
        /**
         * stream api 高级版本的迭代器
         */
        Integer[] ints = {1, 2, 4, 2, 3, 5, 5, 6, 8, 9, 7, 10};
        List<Integer> lists = new ArrayList<>(Arrays.asList(ints));
        //获取stream
        Arrays.stream(ints);
        lists.stream();
        Stream.of(lists);
        Stream.of(ints);

        //遍历
        IntStream.rangeClosed(1, 2).parallel().forEach(System.out::println);//遍历时：对象,json等引用类型可直接转换
        //排序
        lists.sort(Comparator.comparingInt(o -> o));//升序排序，不需要收集
        System.out.println("sort:" + lists.stream().sorted((o1, o2) -> o2 - o1).collect(Collectors.toList()));//降序

        System.out.println("map:" + lists.stream().map(o1 -> o1 * 2).collect(Collectors.toList()));//转换成新元素
        List<String> words = Arrays.asList("hello welcome", "world hello", "hello world", "hello world welcome");
        List<String[]> map = words.stream().map(item -> item.split(" ")).distinct().collect(Collectors.toList());
        System.out.println("flatMap:" + words.stream().flatMap(item -> Arrays.stream(item.split(" "))).distinct().collect(Collectors.toList()));

        System.out.println("peak:" + lists.stream().peek(String::valueOf).collect(Collectors.toList()));//生成一个包含原Stream元素的新Stream
        System.out.println("distinct:" + lists.stream().distinct().collect(Collectors.toList()));//去重(去重逻辑依赖元素的equals方法)
        System.out.println("limit:" + lists.stream().limit(4).collect(Collectors.toList()));//截取
        System.out.println("skip:" + lists.stream().skip(4).collect(Collectors.toList()));//丢弃
        //去重
        listAll().stream().collect(Collectors.collectingAndThen(Collectors
                .toCollection(() -> new TreeSet<>(Comparator.comparing(SysUser::getName))), ArrayList::new));
        listAll().stream().filter(distinctByKey(SysUser::getName)).collect(Collectors.toList());

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
        //聚合(最好给默认值,不然如果list为空时,聚合计算时会报错)
        System.out.println("reduce sum:" + lists.stream().reduce((o1, o2) -> o1 + o2).orElse(0));//聚合
        System.out.println("reduce sum:" + lists.stream().reduce(0, (o1, o2) -> o1 + o2));//聚合(给定默认值)
        System.out.println("reduce ids:" + abc.stream().reduce((sum, item) -> sum + "," + item).orElse(""));//abc(a,b,c)-->a,b,c
        System.out.println("reduce ids:" + abc.stream().reduce("", (sum, item) -> sum + "," + item).substring(1));//abc(a,b,c)-->,a,b,c-->a,b,c
        String s = abc.stream().reduce("", (sum, item) -> sum + "'" + item + "',");//abc(a,b,c)-->'a','b','c',-->'a','b','c'
        System.out.println("reduce id in:" + s.substring(0, s.lastIndexOf(",")));
        //join
        System.out.println("join:" + abc.stream().collect(Collectors.joining(",")));//abc(a,b,c)-->a,b,c
        System.out.println("join:" + String.join(",", abc));
        //统计
        IntSummaryStatistics statistics = lists.stream().mapToInt(x -> x).summaryStatistics();
        System.out.println("List中最大的数字 : " + statistics.getMax());
        System.out.println("List中最小的数字 : " + statistics.getMin());
        System.out.println("List所有数字的总和: " + statistics.getSum());
        System.out.println("List所有数字的平均值: " + statistics.getAverage());
        System.out.println("List成员个数: " + statistics.getCount());
        //all example
        System.out.println("all:" + lists.stream().filter(Objects::nonNull).distinct().mapToInt(num -> num * 2).skip(2).limit(4).sum());
        //toMap 遍历list存入map里 key不能重复 value不能为null
        Map<String, SysUser> userMap = listAll().stream().collect(Collectors.toMap(SysUser::getId, Function.identity()));
        userMap = listAll().stream().collect(Collectors.toMap(SysUser::getId, sysUser -> sysUser));
        Map<String, String> nameMap = listAll().stream().collect(Collectors.toMap(SysUser::getName, SysUser::getSex));
        Map<String, String> jsonMap = getList().stream().collect(Collectors.toMap(o1 -> o1.getString(""), o2 -> o2.getString("")));
        //解决key重复 value为null
        Map<String, String> fixMap = listAll().stream().filter(sysUser -> sysUser.getSex() != null).collect(Collectors.toMap(SysUser::getName, SysUser::getSex, (sex1, sex2) -> sex1 + "," + sex2));
        //分组
        Map<String, List<SysUser>> groupMap = listAll().stream().collect(Collectors.groupingBy(SysUser::getSex));//(分组条件为key，分组成员为value)
        //非空判断
        Optional<String> optional = abc.stream().reduce((sum, item) -> sum + "," + item);
        String reduce;
        if (optional.isPresent()) {
            reduce = optional.get();
        }
        //异步回调
        List<JSONObject> sysUsers = CompletableFuture.supplyAsync(LambdaDemo::getList).join();//线程等待,效果等同于get(),会拋出CompletionException
    }

    /**
     * 遍历集合，集合里数据还要进行复杂操作，导致速度很慢,用以下操作
     */
    public List<Integer> supplyAsync() {
        List<Integer> abc = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            abc.add(i);
        }
        int size = abc.size();
        CompletableFuture[] futures = new CompletableFuture[size];
        List<Integer> finalList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            finalList.add(null);//在异步之前size+1，必须有这步，否则会下标越界
            Integer integer = abc.get(i);
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
        return Collections.EMPTY_LIST;
    }

    private static List<SysUser> listAll() {
        return Collections.EMPTY_LIST;
    }

    private String isAdmin(String id) {
        return "Y";
    }

    private class SysUser {
        private String name;
        private String sex;
        private String id;

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
    }

}
