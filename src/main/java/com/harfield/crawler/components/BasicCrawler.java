package com.harfield.crawler.components;

import com.alibaba.fastjson.JSON;

import com.harfield.crawler.domain.Task;
import com.harfield.crawler.domain.WebContent;
import com.harfield.crawler.service.DBService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import javax.annotation.Resource;
import java.util.*;

/**
 * workflow
 */

public class BasicCrawler implements MessageListener{
    private static final Logger LOG = LoggerFactory.getLogger(BasicCrawler.class);

    @Resource
    DBService dbService;

    private Fetcher fetcher;
    private Parser parser;
    private Sink sink;

    public BasicCrawler(Fetcher fetcher, Parser parser, Sink sink){
        this.fetcher = fetcher;
        this.parser = parser;
        this.sink = sink;
    }

    @Override
    public void onMessage(Message message){
        Task task = null;
        try {
            String s = new String(message.getBody(), "UTF-8");
            LOG.info("Receive msg: {}", s);
            task = JSON.parseObject(s, Task.class);
            dbService.markTaskRunning(task);
            WebContent webContent = fetcher.fetch(task);
            Map<String, Object> parsedResultMap = parser.parse(webContent, task);
            sink.sink(parsedResultMap,task);
        } catch (Exception e) {
            dbService.markTaskFailed(task);
            LOG.error(e.getMessage(), e);
        } finally {
            dbService.markTaskFinished(task);
        }
    }
}
