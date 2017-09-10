package com.harfield.crawler.service.impl;


import com.harfield.crawler.domain.Task;
import com.harfield.crawler.dao.CrawlerMapper;
import com.harfield.crawler.service.DBService;
import org.mybatis.spring.annotation.MapperScan;

import javax.annotation.Resource;
import java.util.List;

@MapperScan("com.fancydsp.data.easycrawler.dao")
public class DBServiceImpl implements DBService {
    @Resource
    private CrawlerMapper crawlerMapper;

    @Override
    public List<Task> getPendingTasks() {
        return crawlerMapper.getPendingTasks();
    }

    @Override
    public void markTaskQueuing(Task task) {

    }
}
