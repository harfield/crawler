package com.harfield.crawler.service.impl;

import com.harfield.crawler.apps.CrawlerStarter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = CrawlerStarter.class)
public class MQServiceImplTest {
    @Resource
    private MQServiceImpl mqService;
    @Test
    public void send() throws Exception {
        System.out.println(mqService);
    }

}