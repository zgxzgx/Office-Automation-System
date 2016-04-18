package cn.edu.shou.missive.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Created by sqhe on 15/2/4.
 */
@Component
public class AsyncTask {
    private final static Logger logger = LoggerFactory.getLogger(AsyncTask.class);

    @Async
    public void dosomething() throws InterruptedException {
        logger.info("start do something");
        Thread.sleep(5000);
        logger.info("end do something");

    }
}
