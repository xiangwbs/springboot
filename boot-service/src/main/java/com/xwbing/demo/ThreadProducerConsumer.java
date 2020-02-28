package com.xwbing.demo;

/**
 * 创建日期: 2017年2月20日 下午4:03:56
 * 作者: xiangwb
 */

public class ThreadProducerConsumer {
    public static void main(String[] args) {
        PublicResource resource = new PublicResource();
        new Thread(new ProducerThread(resource)).start();
        new Thread(new ConsumerThread(resource)).start();
    }
}

/**
 * 公共资源类
 */
class PublicResource {
    private int number = 9;

    /**
     * 增加公共资源
     */
    synchronized void inCreace() {
        while (number != 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        number++;
        System.out.println(number);
        notifyAll();
    }

    /**
     * 减少公共资源
     */
    synchronized void deCreace() {
        while (number == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        number--;
        System.out.println(number);
        notifyAll();
    }
}

/**
 * 生产者线程，负责生产公共资源
 */
class ProducerThread implements Runnable {
    private PublicResource resource;

    ProducerThread(PublicResource resource) {
        this.resource = resource;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep((long) (Math.random() * 1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            resource.inCreace();
        }
    }
}

/**
 * 消费者线程，负责消费公共资源
 */
class ConsumerThread implements Runnable {
    private PublicResource resource;

    ConsumerThread(PublicResource resource) {
        this.resource = resource;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep((long) (Math.random() * 1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            resource.deCreace();
        }
    }
}
