package com.nowcoder.community;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LoggerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerTest.class);


    @Test
    public void loggerTest() {

        System.out.println(LOGGER.getName());
        LOGGER.debug("debug logger");
        LOGGER.info("info logger");
        LOGGER.warn("warn logger");
        LOGGER.error("error logger");
    }
}
