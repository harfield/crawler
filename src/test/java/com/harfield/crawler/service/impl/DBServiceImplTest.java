package com.harfield.crawler.service.impl;

import com.harfield.crawler.apps.Injector;
import com.harfield.crawler.service.DBService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Injector.class)
public class DBServiceImplTest {

    @Resource
    ApplicationContext ctx;

    @Test
    public void getPendingTasks() throws Exception {
        System.out.println(ctx);

    }

    @Test
    public void markTaskQueuing() throws Exception {

    }

    @Test
    public void markTaskRunning() throws Exception {

    }

    @Test
    public void markTaskFailed() throws Exception {

    }

    @Test
    public void markTaskFinished() throws Exception {

    }

}