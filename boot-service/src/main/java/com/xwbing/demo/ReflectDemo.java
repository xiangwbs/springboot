package com.xwbing.demo;

import com.xwbing.domain.entity.BaseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 项目名称: boot-module-pro
 * 创建时间: 2018/5/27 20:37
 * 作者: xiangwb
 * 说明: Java反射机制是在运行状态中，对于任意一个类，都能够知道这个类的所有属性和方法；
 * 对于任意一个对象，都能够调用它的任意一个方法和属性；
 * 这种动态获取的信息以及动态调用对象的方法的功能称为Java语言的反射机制。
 * 获取方法、字段用getDeclared...（只获取当前类声明的所有...包括私有）
 * 执行私有前先setAccessible(true)
 */
public class ReflectDemo {
    public static void main(String[] args) throws Exception {
        /**
         * class
         */
        Class clazz;
        //1 直接通过类名.Class的方式得到
        clazz = Reflect.class;
        //2 通过对象的getClass()方法获取,这个使用的少（一般是传的是Object，不知道是什么类型的时候才用）
        Reflect reflect = new Reflect();
        clazz = reflect.getClass();
        //3 通过全类名获取，用的比较多，但可能抛出ClassNotFoundException异常
        clazz = Class.forName("com.xwbing.demo.Reflect");
        //创建对象
        Object obj = clazz.newInstance();
        //获取类名
        String simpleName = clazz.getSimpleName();
        String name1 = clazz.getName();

        /**
         * supper
         */
        Class superclass = clazz.getSuperclass();
        Class[] interfaces = clazz.getInterfaces();

        /**
         * method
         */
        //获取所有方法
//        Method[] publicMethods = clazz.getMethods();//得到clazz对应的类中有哪些方法,不能获取private方法
        Method[] allMethods = clazz.getDeclaredMethods();//获取所有的方法(且只获取当前类声明的方法，包括private方法）
        //获取指定方法
        Method privateMethod = clazz.getDeclaredMethod("privateMethod");
        Method method = clazz.getDeclaredMethod("setName", String.class, int.class);//第一个参数是方法名，后面的是方法里的参数
        //执行方法
        method.invoke(obj, "xwbing", 22);
        privateMethod.setAccessible(true); //执行private方法
        privateMethod.invoke(obj);

        /**
         * field
         */
        //获取所有字段
        Field[] fields = clazz.getDeclaredFields();
        //获取指定名字的字段
        Field field = clazz.getDeclaredField("name");
        field.setAccessible(true);//执行私有字段方法
        //获取指定对象的字段的值
        Object name = field.get(obj);
        //设置指定对象的字段的值
        field.set(obj, "xwjun");

        /**
         * annotation(类，方法，字段)
         */
        Annotation annotation = clazz.getAnnotation(Component.class);
        if (annotation != null) {
            if (annotation instanceof Component) {
                Component component = (Component) annotation;
                String value = component.value();
                System.out.println(value);
            }
        }
    }
}

@Slf4j
@Component("annotation")
class Reflect extends BaseEntity implements Serializable {
    private String name;
    private int age;

    public Reflect() {
        log.info("无参构造器,给反射用");
    }

    public Reflect(String name, int age) {
        log.info("有参构造器");
        this.name = name;
        this.age = age;
    }

    private void privateMethod() {
        log.info("私有方法");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setName(String name, int age) {
        this.name = name;
        this.age = age;
        System.out.println("name: " + name);
        System.out.println("age:" + age);
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}