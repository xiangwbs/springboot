package com.xwbing.web.controller.task;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xwbing.starter.util.CommonDataUtil;

import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年08月18日 下午3:52
 */
@Slf4j
@ApiIgnore
@RestController
@RequestMapping("task")
@Profile({ "dev", "prod" })
public class SchedulingTask {
    // @Lock
    @GetMapping("clearExpiryData")
    @Scheduled(cron = "0 0 6 * * ?")
    @SchedulerLock(name = "com.xwbing.web.controller.task.clearExpiryData", lockAtLeastFor = "1h", lockAtMostFor = "1h")
    public void clearExpiryData() {
        log.info("clearExpiryData start");
        CommonDataUtil.clearExpiryData();
        log.info("clearExpiryData end");
    }

    @Scheduled(cron = "* * 6 * * ?")
    @SchedulerLock(name = "com.xwbing.web.controller.task.test", lockAtLeastFor = "1s", lockAtMostFor = "5s")
    public void test() {
        log.info("test start");
        CommonDataUtil.clearExpiryData();
        log.info("test end");
    }
}