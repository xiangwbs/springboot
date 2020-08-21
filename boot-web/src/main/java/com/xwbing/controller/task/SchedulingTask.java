package com.xwbing.controller.task;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xwbing.util.CommonDataUtil;

import lombok.extern.slf4j.Slf4j;
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
@Profile({ "dev" })
public class SchedulingTask {

    // @Lock
    @GetMapping("clearExpiryData")
    @Scheduled(cron = "0 0 6 * * ?")
    // @SchedulerLock(name = "com.xwbing.controller.task.clearExpiryData", lockAtLeastForString = "PT40s", lockAtMostForString = "PT60s")
    public void clearExpiryData() {
        log.info("clearExpiryData start");
        CommonDataUtil.clearExpiryData();
        log.info("clearExpiryData end");
    }
}