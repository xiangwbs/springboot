package com.xwbing.demo;

import org.apache.commons.collections.CollectionUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 说明: 订单编号生成
 * 创建日期: 2017年3月24日 下午5:06:37
 * 作者: xiangwb
 */

public class CreateOrderNoDemo {
    public synchronized static String getNo() {
        String sequence;
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY_MM_DD");
        String date = sdf.format(new Date());
        StringBuilder sql = new StringBuilder("select * from order_table");
        sql.append(" where order_no like '" + date + "%'");
        sql.append(" order by create_time desc");
        List<Order> list = queryList();
        if (CollectionUtils.isNotEmpty(list)) {
            String orderNo = list.get(0).getOrderNo();
            long newOrderNo = Long.valueOf(orderNo.substring(8, 18)) + 1;
            String sequ = new DecimalFormat("0000000000").format(newOrderNo);
            sequence = date + sequ;
        } else {
            sequence = date + "0000000000";
        }
        return sequence;
    }

    class Order {//模拟订单类
        String orderNo;

        public String getOrderNo() {
            return orderNo;
        }

        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }

    }

    public static List<Order> queryList() {//模拟查询
        return null;
    }

    public static void main(String[] args) {
        long a = 12356;
        String sequ = new DecimalFormat("0000000000").format(a);
        System.out.println(sequ);
    }
}
