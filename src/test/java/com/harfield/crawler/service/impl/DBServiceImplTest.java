package com.harfield.crawler.service.impl;

import com.harfield.crawler.apps.CrawlerStarter;
import com.harfield.crawler.domain.Task;
import com.harfield.crawler.service.DBService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CrawlerStarter.class)
public class DBServiceImplTest {

    @Resource
    DBService dbService;

    @Test
    public void getPendingTasks() throws Exception {
        List<Task> pendingTasks = dbService.getPendingTasks();
        System.out.println(pendingTasks.toString());


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