package com.xwbing.demo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xiangwb
 * @date 2020/3/6 17:29
 * 权重demo
 */
public class WeightLoadBalanceDemo {
    private static AtomicInteger atomicInteger = new AtomicInteger();

    public static void main(String[] args) {
        List<WeightEntity> list = new ArrayList<>();
        list.add(new WeightEntity("127.0.0.1:8080", 1));
        list.add(new WeightEntity("127.0.0.1:8081", 2));
        List<String> address = weightToRotation(list);
        System.out.println(getAddress(address));
        System.out.println(getAddress(address));
        System.out.println(getAddress(address));
        System.out.println(getAddress(address));
        System.out.println(getAddress(address));
        System.out.println(getAddress(address));
        System.out.println(getAddress(address));

    }

    private static List<String> weightToRotation(List<WeightEntity> list) {
        List<String> listAddress = new ArrayList<>();
        for (WeightEntity weightEntity : list) {
            for (int i = 0; i < weightEntity.getWeight(); i++) {
                listAddress.add(weightEntity.getAddress());
            }
        }
        return listAddress;
    }

    private static String getAddress(List<String> address) {
        int index = atomicInteger.incrementAndGet() % address.size();
        return address.get(index);
    }

    @Data
    public static class WeightEntity {
        private String address;
        private int weight;

        WeightEntity(String address, int weight) {
            this.address = address;
            this.weight = weight;
        }
    }
}
