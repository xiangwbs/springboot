package com.xwbing.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;

import java.util.*;

/**
 * 创建日期: 2017年2月16日 下午4:06:56
 * 作者: xiangwb
 * *
 * DEFAULT_CAPACITY = 10
 * Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {}
 * Object[] EMPTY_ELEMENTDATA = {}
 * Object[] elementData
 * *
 * newCapacity=oldCapacity + (oldCapacity >> 1) // 1.5倍
 * Arrays.copyOf(elementData, newCapacity)
 * *
 * ensureCapacityInternal(size + 1);
 * //1.ArrayIndexOutOfBoundsException 2.elementData[i]=null
 * elementData[size] = e;
 * //3.size++覆盖 size小于理论值
 * r1=size;
 * r2=r1+1;
 * size=r2;
 */

public class CollectionDemo {
    public static void main(String[] args) {
        /**
         * 数组
         */
        int[] arr1 = new int[4];
        int[] arr2 = new int[]{1, 3, 5, 7};
        int[] arr3 = {12, 3, 5, 6, 2, 8, 9, 4};
        //输出字符串
        System.out.println(Arrays.toString(arr3));

        for (int i = 0; i < arr3.length - 1; i++) {//选择排序
            for (int j = i + 1; j < arr3.length; j++) {
                if (arr3[i] > arr3[j]) {
                    int temp = arr3[i];
                    arr3[i] = arr3[j];
                    arr3[j] = temp;
                }
            }
        }
        for (int i = 0; i < arr3.length - 1; i++) {//冒泡排序
            for (int j = 0; j < arr3.length - 1 - i; j++) {
                if (arr3[j] > arr3[j + 1]) {
                    int t = arr3[j];
                    arr3[j] = arr3[j + 1];
                    arr3[j + 1] = t;
                }
            }
        }
        /**
         * 数组字符串操作
         */
        String[] arrays = {"1", "2", "3", "4"};
        List<String> strings = Arrays.asList(arrays);
        String arrayStr = JSONArray.toJSONString(strings);//数组字符串
        JSONArray jsonArray = JSON.parseArray(arrayStr);//转为JSONArray
        jsonArray.add("5");//相应操作
        String jsonString = JSON.toJSONString(jsonArray);//转为数组字符串

        /**
         * 数组转集合
         * 该集合表示原来的数组 对集合的操作就是对数组的操作，那么添加元素会导致原数组扩容，那么就不能表示原来的数组了,会抛出UnsupportedOperationException异常
         */
        String[] array = {"one", "two", "three", "four"};
        List<String> collection = new ArrayList<>(Arrays.asList(array));

        /**
         * 集合转数组
         */
        List<String> lsit = new ArrayList<>(2);
        lsit.add("guan");
        lsit.add("bao");
        String[] arrayy = new String[lsit.size()];// 大小为list.size()
        /*
         * toArray(T[] array): 若给定的数组可用（数组可以存放集合所有的元素）时
         * 则使用该数组，若不可用，会自动创建一个与给定数组同类型的数组
         */
        arrayy = lsit.toArray(arrayy);

        /**
         * 基本api
         */
        List<Integer> list = new ArrayList<>();// 有序
        list.add(1);
        list.add(0, 0);// 元素插入到指定位置
        list.add(2);
        list.add(3);
        list.add(5);
        list.add(4);
        list.add(6);
        System.out.println(list);
        Integer old = list.set(1, 11);// 替换元素,返回值为原位置的元素
        Integer one = list.get(0);// 获取下标元素
        list.remove(Integer.valueOf(6));
        list.remove(0);// 从集合中删除指定位置的元素，并将其返回
        int size = list.size();// 长度
        boolean isEmpty = list.isEmpty();// 是否为空
        boolean contains = list.contains(1);// 是否包含元素
        // l.clear();//清除集合元素
        Set<Integer> s = new HashSet<>();// 无序
        s.add(111);
        s.add(222);
        s.add(333);
        list.addAll(s);// 添加集合
        contains = list.containsAll(s);// 是否包含集合
        list.removeAll(s);// 清除共有的元素
        System.out.println(list);
        /**
         * ArrayList的subList结果不可强转成ArrayList 子集,含头不含尾,对子集的修改，就是原集合相应的内容
         */
        List<Integer> subList = list.subList(0, 1);
        ArrayList<Integer> arrayList = new ArrayList<>(subList);
        list.subList(0, 1).clear();// 删除集合中0-1的元素
        Collections.sort(list);// 对集合进行从小到大排序
        Collections.reverse(list);// 反转
        System.out.println(list);

        /**
         * 新循环foreach遍历集合,编译器会将它改为迭代器方式遍历， 所以在使用新循环遍历集合时，不能通过集合的方法增删元素
         */
        for (Integer o : list) {
            // list.remove(o);
        }
        /**
         * lambda表达式
         */
        list.forEach(System.out::println);
        //删除某元素
        list.removeIf(integer -> integer == 0);

        /**
         * 迭代器
         */
        Iterator<Integer> it = list.iterator();// 获取用于遍历当前集合的迭代器
        while (it.hasNext()) {
            Integer str = it.next();
            if (1 == str) {
                /*
                 * 在使用迭代器遍历集合时，不要使用集合的方法曾删元素，否则会引发异常
                 */
                // list.remove(str);
                it.remove();
            }
        }

        /**
         * 自定义排序 推荐匿名内部类形式创建比较器
         */
        Comparator<Integer> com = new Comparator<Integer>() {// 返回正数，零，负数各代表大于，等于，小于
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2 - o1;
            }
        };
        Collections.sort(list, com);
        System.out.println(list);
        //java8 lambda表达式
        list.sort(Comparator.comparingInt(o -> o));

        /**
         * 队列 队列也可以存放一组元素，但是存取元素必须 遵循：先进先出原则
         * linkedlist也实现类队列接口，因为它可以保存一组元素，并且首尾增删块快，正好符合队列特点
         */
        Queue<String> queue = new LinkedList<>();
        queue.offer("one");// 入队操作，向队尾追加一个新元素
        queue.offer("two");
        queue.offer("three");
        queue.offer("four");
        System.out.println(queue);
        String str = queue.poll();// 出队操作，从队首获取元素，获取后该元素就从队列中被删除了
        System.out.println(str);
        System.out.println(queue);
        str = queue.peek();// 引用队首元素，但是不做出队操作
        System.out.println(str);
        System.out.println(queue);
        System.out.println("遍历开始");
        System.out.println("lenth:" + queue.size());
        /*
         * 遍历从后往前 因为size一直在变，从前往后会没取完
         */
        // for (int i = queue.size(); i >0; i--) {
        // str=queue.poll();
        // System.out.println(str);
        // }
        while (queue.size() > 0) {
            str = queue.poll();
            System.out.println(str);
        }
        System.out.println("遍历结束");
        System.out.println(queue);

        /**
         * 栈 存储一组元素，但是存取元素必须遵循先进后出原则 通常为了实现后退这类功能时会使用栈
         */
        /*
         * java.util.Deque 双端队列，两端都可以进出队 当只调用从一端进出对操作时，就形成了栈结构 因此，双端队列为栈提供类两个方法：
         * push ，pop
         */
        Deque<String> stack = new LinkedList<>();
        stack.push("one");// 入栈操作，最后入栈的元素在栈顶（第一个元素位置）
        stack.push("two");
        stack.push("three");
        stack.push("four");
        System.out.println(stack);
        str = stack.poll();// 出栈操作，第一个元素先出
        System.out.println(str);
        System.out.println(stack);
        str = stack.peek();// 引用队首元素，但是不做出队操作
        System.out.println(str);
        System.out.println(stack);
    }

    /**
     * list分组
     *
     * @param list
     * @param rang
     * @return
     */
    public static List group(List list, int rang) {
        ArrayList<List> result = Lists.newArrayList();
        int size = list.size();
        for (int i = 0; i < size; i += rang) {
            if (i + rang > size) {
                rang = size - i;
            }
            result.add(list.subList(i, i + rang));
        }
        return result;
    }
}
