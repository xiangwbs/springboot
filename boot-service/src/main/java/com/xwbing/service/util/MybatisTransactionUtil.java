package com.xwbing.service.util;

import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import javax.annotation.Resource;

/**
 * @author xiangwb
 * @date 2020/3/6 12:54
 */
@Component
public class MybatisTransactionUtil {
    @Resource(name = "mybatisTransactionManager")
    private PlatformTransactionManager platformTransactionManager;

    public TransactionStatus begin() {
        return platformTransactionManager.getTransaction(new DefaultTransactionAttribute());
    }

    public void commit(TransactionStatus transactionStatus) {
        platformTransactionManager.commit(transactionStatus);

    }

    public void rollback(TransactionStatus transactionStatus) {
        platformTransactionManager.rollback(transactionStatus);
    }
}
