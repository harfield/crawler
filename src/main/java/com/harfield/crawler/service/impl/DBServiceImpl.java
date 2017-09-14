package com.harfield.crawler.service.impl;


import com.harfield.crawler.domain.Task;
import com.harfield.crawler.dao.CrawlerMapper;
import com.harfield.crawler.service.DBService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
@Service(value = "defaultDBServiceImpl")
@MapperScan("com.harfield.crawler.dao")
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

    @Override
    public void markTaskRunning(Task task) {

    }

    @Override
    public void markTaskFailed(Task task) {

    }

    @Override
    public void markTaskFinished(Task task) {

    }
}
