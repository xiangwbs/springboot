package com.xwbing.demo;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class ArrayListDemo<E> extends AbstractList<E> {
    private static final int DEFAULT_CAPACITY = 10;
    private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};
    private static final Object[] EMPTY_ELEMENTDATA = {};
    private Object[] elementData;
    private int size;//包含的元素的数量

    private final Object target = new Object();
    private final Lock lock = new ReentrantLock();

    public static void main(String[] args) {
        ArrayListDemo<Integer> demo1 = new ArrayListDemo<>();//demo1.elementData.length=0,10....
        demo1.add(1);
        ArrayListDemo<Integer> demo2 = new ArrayListDemo<>(0);//demo2.elementData.length=0,1,2,3,4,6,9,13.....
        demo2.add(1);
        demo2.add(1);
        demo2.add(1);
        demo2.add(1);
        demo2.add(1);
        demo2.add(1);
        demo2.add(1);
        demo2.add(1);
        demo2.add(1);
        demo2.add(1);

        /**
         * 1.size少于理想值
         * 2.有些下标元素为null
         * 3.下标越界
         */
        int[] arrays = IntStream.rangeClosed(1, 2).toArray();
        ArrayListDemo<Integer> result = new ArrayListDemo<>();
        Arrays.stream(arrays).parallel().forEach(result::add);
        System.out.println(result.size());

        List<Integer> list = Collections.synchronizedList(new ArrayListDemo<>());
        list.add(1);
    }

    public ArrayListDemo() {
        this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
    }

    public ArrayListDemo(int initialCapacity) {
        if (initialCapacity > 0) {
            this.elementData = new Object[initialCapacity];
        } else {
            this.elementData = EMPTY_ELEMENTDATA;
        }
    }

    public boolean add(E e) {
        ensureCapacityInternal(size + 1);
        elementData[size] = e;
        size++;
//        int r1 = size;
//        int r2 = r1 + 1;
//        size = r2;
        return true;
    }

    private void ensureCapacityInternal(int minCapacity) {//计算最小容量
        int capacity;
        if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {//如果是new ArrayListDemo() 容量至少为10
            capacity = Math.max(DEFAULT_CAPACITY, minCapacity);
        } else {
            capacity = minCapacity;//按实际参数来
        }
        if (capacity - elementData.length > 0) {
            grow(capacity);
        }
    }

    private void grow(int minCapacity) {//扩容
        int oldCapacity = elementData.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);//大致为1.5倍
        if (newCapacity - minCapacity < 0) {//新容量小于最小容量 取最小容量
            newCapacity = minCapacity;
        }
        elementData = Arrays.copyOf(elementData, newCapacity);//数组拷贝
    }

    public E get(int index) {
        rangeCheck(index);
        return (E) elementData[index];
    }

    public E set(int index, E element) {
        rangeCheck(index);
        E oldValue = (E) elementData[index];
        elementData[index] = element;
        return oldValue;
    }

    public E remove(int index) {
        rangeCheck(index);
        E oldValue = (E) elementData[index];
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(elementData, index + 1, elementData, index, numMoved);//数组拷贝
        }
        elementData[--size] = null; // clear to let GC do its work
        return oldValue;
    }

    public void clear() {
        for (int i = 0; i < size; i++) {
            elementData[i] = null;
        }
        size = 0;
    }

    public boolean contains(Object var1) {
        return this.indexOf(var1) >= 0;
    }

    public int indexOf(Object o) {
        if (o == null) {
            for (int i = 0; i < size; i++) {
                if (elementData[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (o.equals(elementData[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    public int size() {
        return size;
    }


    private void rangeCheck(int index) {
        if (index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size());
        }
    }

    public boolean add1(E e) {
        synchronized (target) {//加个同步锁
            ensureCapacityInternal(size + 1);
            elementData[size++] = e;
            return true;
        }
    }

    public boolean add2(E e) {
        lock.lock();//用lock来保证原子性
        try {
            ensureCapacityInternal(size + 1);
            elementData[size++] = e;
            return true;
        } finally {
            lock.unlock();
        }
    }
}