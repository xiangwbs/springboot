package com.xwbing.demo;

import com.xwbing.util.MybatisTransactionUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.TransactionStatus;

import javax.annotation.Resource;

/**
 * @author xiangwb
 * @date 2020/3/6 13:18
 * 事务原理
 * TransactionAspectSupport
 * 获取事务的属性（@Transactional注解中的配置）
 * 加载配置中的TransactionManager.
 * 获取收集事务信息TransactionInfo
 * 执行目标方法
 * 出现异常，尝试处理。
 * 清理事务相关信息
 * 提交事务
 */
@Slf4j
@Aspect
//@Component
@Scope("prototype")//避免线程安全问题
public class TransactionAopDemo {
    @Resource
    private MybatisTransactionUtil mybatisTransactionUtil;

    @Pointcut("@annotation(com.xwbing.annotation.MyTransaction)")
    public void transactionCut() {
    }

    @Around(value = "transactionCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        TransactionStatus transactionStatus = mybatisTransactionUtil.begin();
        log.info("开始事务");
        try {
            Object proceed = joinPoint.proceed();
            mybatisTransactionUtil.commit(transactionStatus);
            log.info("提交事务");
            return proceed;
        } catch (Exception e) {
            mybatisTransactionUtil.rollback(transactionStatus);
            log.info("事务回滚");
            return null;
        }
    }
}
