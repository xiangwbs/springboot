package com.xwbing.demo;

import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author xiangwb
 * @date 2018/12/2 09:30
 * @description 线程间的通信：共享变量和消息传递
 * 编写两个线程，一个线程打印1~52，另一个线程打印字母A~Z，打印顺序为12A34B56C……5152Z
 * 多线程特性三大概念：
 * 原子性：jmm保证了对基本数据类型变量简单的读取和赋值(必须是将数字直接赋值给某个变量，变量之间相互赋值不是原子操作)是原子操作，如果要大范围操作的原子性，可以通过synchronized和lock
 * 可见性：volatile。synchronized和lock能保证同一个时刻只有一个线程获取锁然后执行同步代码，并且在释放锁之前会将对变量的修改刷新到主内存
 * 有序性：volatile。synchronized和lock保证每个时刻是有一个线程执行同步代码，相当于是让线程顺序执行同步代码
 */
public class ThreadCommunication {
    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(20, 35, 10,
            TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), new ThreadPoolExecutor.DiscardPolicy());

    static String[] buildNoArr(int max) {
        String[] noArr = new String[max];
        for (int i = 0; i < max; i++) {
            noArr[i] = Integer.toString(i + 1);
        }
        return noArr;
    }

    static String[] buildCharArr(int max) {
        String[] charArr = new String[max];
        int tmp = 65;
        for (int i = 0; i < max; i++) {
            charArr[i] = String.valueOf((char) (tmp + i));
        }
        return charArr;
    }

    static void print(String... input) {
        if (input == null)
            return;
        for (String each : input) {
            System.out.print(each);
        }
    }


    public static void main(String[] args) throws InterruptedException {
        try {
            MethodOne one = new MethodOne();
            CompletableFuture.runAsync(one.threadOne(), EXECUTOR);
            CompletableFuture.runAsync(one.threadTwo(), EXECUTOR);
            Thread.sleep(10);
            System.out.println("==synchronized、notify、wait==");
            MethodTwo two = new MethodTwo();
            CompletableFuture.runAsync(two.threadOne(), EXECUTOR);
            CompletableFuture.runAsync(two.threadTwo(), EXECUTOR);
            Thread.sleep(10);
            System.out.println("==Lock、Condition==");
            MethodThree three = new MethodThree();
            CompletableFuture.runAsync(three.threadOne(), EXECUTOR);
            CompletableFuture.runAsync(three.threadTwo(), EXECUTOR);
            Thread.sleep(10);
            System.out.println("==volatile==");
            MethodFour four = new MethodFour();
            CompletableFuture.runAsync(four.threadOne(), EXECUTOR);
            CompletableFuture.runAsync(four.threadTwo(), EXECUTOR);
            Thread.sleep(10);
            System.out.println("==AtomicInteger==");
            MethodFive five = new MethodFive();
            CompletableFuture.runAsync(five.threadOne(), EXECUTOR);
            CompletableFuture.runAsync(five.threadTwo(), EXECUTOR);
            Thread.sleep(10);
            System.out.println("==queue==");
        } finally {
            EXECUTOR.shutdown();
        }
    }
}

/**
 * 利用最基本的synchronized、notify、wait,靠共享变量来做控制
 * wait()、notify() 方法是Object方法
 * 一般在synchronized同步代码块里使用 wait()、notify()。
 */
class MethodOne {
    private final Share share = new Share();

    class Share {
        int value = 1;
    }

    Runnable threadOne() {
        final String[] inputArr = ThreadCommunication.buildNoArr(52);
        return new Runnable() {
            private String[] arr = inputArr;

            public void run() {
                try {
                    for (int i = 0; i < arr.length; i = i + 2) {
                        synchronized (share) {
                            while (share.value == 2)
                                share.wait();
                            ThreadCommunication.print(arr[i], arr[i + 1]);
                            share.value = 2;
                            share.notify();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    Runnable threadTwo() {
        final String[] inputArr = ThreadCommunication.buildCharArr(26);
        return new Runnable() {
            private String[] arr = inputArr;

            public void run() {
                try {
                    for (String anArr : arr) {
                        synchronized (share) {
                            while (share.value == 1)
                                share.wait();
                            ThreadCommunication.print(anArr);
                            share.value = 1;
                            share.notify();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}

/**
 * 利用Lock和Condition，靠共享变量来做控制
 */
class MethodTwo {
    private Lock lock = new ReentrantLock(true);
    private Condition condition = lock.newCondition();
    private final Share share = new Share();

    class Share {
        int value = 1;
    }

    Runnable threadOne() {
        final String[] inputArr = ThreadCommunication.buildNoArr(52);
        return new Runnable() {
            private String[] arr = inputArr;

            public void run() {
                for (int i = 0; i < arr.length; i = i + 2) {
                    try {
                        lock.lock();
                        while (share.value == 2)
                            condition.await();
                        ThreadCommunication.print(arr[i], arr[i + 1]);
                        share.value = 2;
                        condition.signal();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        lock.unlock();
                    }
                }
            }
        };
    }

    Runnable threadTwo() {
        final String[] inputArr = ThreadCommunication.buildCharArr(26);
        return new Runnable() {
            private String[] arr = inputArr;

            public void run() {
                for (String anArr : arr) {
                    try {
                        lock.lock();
                        while (share.value == 1)
                            condition.await();
                        ThreadCommunication.print(anArr);
                        share.value = 1;
                        condition.signal();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        lock.unlock();
                    }
                }
            }
        };
    }
}

/**
 * 利用volatile：
 * volatile修饰的变量值直接存在main memory里面，子线程对该变量的读写直接写入main memory，而不是像其它变量一样在local thread里面产生一份copy。
 * volatile能保证所修饰的变量对于多个线程可见性，即只要被修改，其它线程读到的一定是最新的值。
 */
class MethodThree {
    private volatile Share share = new Share();

    class Share {
        int value = 1;
    }

    Runnable threadOne() {
        final String[] inputArr = ThreadCommunication.buildNoArr(52);
        return new Runnable() {
            private String[] arr = inputArr;

            public void run() {
                for (int i = 0; i < arr.length; i = i + 2) {
                    while (share.value == 2) {
                    }
                    ThreadCommunication.print(arr[i], arr[i + 1]);
                    share.value = 2;
                }
            }
        };
    }

    Runnable threadTwo() {
        final String[] inputArr = ThreadCommunication.buildCharArr(26);
        return new Runnable() {
            private String[] arr = inputArr;

            public void run() {
                for (String anArr : arr) {
                    while (share.value == 1) {
                    }
                    ThreadCommunication.print(anArr);
                    share.value = 1;
                }
            }
        };
    }
}

/**
 * 利用AtomicInteger（CAS利用处理器执行指令）
 */
class MethodFour {
    private AtomicInteger share = new AtomicInteger(1);

    Runnable threadOne() {
        final String[] inputArr = ThreadCommunication.buildNoArr(52);
        return new Runnable() {
            private String[] arr = inputArr;

            public void run() {
                for (int i = 0; i < arr.length; i = i + 2) {
                    while (share.get() == 2) {
                    }
                    ThreadCommunication.print(arr[i], arr[i + 1]);
                    share.set(2);
                }
            }
        };
    }

    Runnable threadTwo() {
        final String[] inputArr = ThreadCommunication.buildCharArr(26);
        return new Runnable() {
            private String[] arr = inputArr;

            public void run() {
                for (String anArr : arr) {
                    while (share.get() == 1) {
                    }
                    ThreadCommunication.print(anArr);
                    share.set(1);
                }
            }
        };
    }
}

/**
 * 利用Queue
 */
class MethodFive {
    private final Queue<String> queue = new ArrayBlockingQueue<>(2);

    Runnable threadOne() {
        final String[] inputArr = ThreadCommunication.buildNoArr(52);
        return new Runnable() {
            private String[] arr = inputArr;

            public void run() {
                for (int i = 0; i < arr.length; i = i + 2) {
                    ThreadCommunication.print(arr[i], arr[i + 1]);
                    queue.offer("char");
                    while (!"no".equals(queue.peek())) {
                    }
                    queue.poll();
                }
            }
        };
    }

    Runnable threadTwo() {
        final String[] inputArr = ThreadCommunication.buildCharArr(26);
        return new Runnable() {
            private String[] arr = inputArr;

            public void run() {
                for (String anArr : arr) {
                    while (!"char".equals(queue.peek())) {
                    }
                    ThreadCommunication.print(anArr);
                    queue.poll();
                    queue.offer("no");
                }
            }
        };
    }
}
