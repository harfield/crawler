package com.harfield.crawler.service.impl;


import com.harfield.crawler.domain.Task;
import com.harfield.crawler.dao.CrawlerMapper;
import com.harfield.crawler.service.DBService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
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
        int remainingRetry = task.getRemainingRetry();
        if (remainingRetry > 0) {
            task.setRemainingRetry(remainingRetry - 1);
        }
        task.setSendToMqTime(new Date());
        task.setNextCrawlTime(null);
        task.setFinishStatus(Task.RUNNING);
        crawlerMapper.updateTaskById(task);
    }

    @Override
    public void markTaskRunning(Task task) {
        task.setStartTime(new Date());
        crawlerMapper.updateTaskById(task);
    }

    @Override
    public void markTaskFailed(Task task) {
        task.setFinishTime(new Date());
        task.setFinishStatus(Task.FAILED);
        if (task.getJob().getMaxTry() > 0) {
            task.setRemainingRetry(task.getJob().getMaxTry());
        }else {
            task.setRemainingRetry(1);
        }
        crawlerMapper.updateTaskById(task);
    }

    @Override
    public void markTaskFinished(Task task) {
        task.setFinishStatus(Task.SUCCEED);
        task.setFinishTime(new Date());
        if (task.getJob().getMaxTry() > 0) {
            task.setRemainingRetry(task.getJob().getMaxTry());
        }else {
            task.setRemainingRetry(1);
        }
        if (task.getJob().getDuration() > 0) {
            Date nextTime = new Date(task.getJob().getDuration() * 1000 + System.currentTimeMillis());
            task.setNextCrawlTime(nextTime);
        }
        crawlerMapper.updateTaskById(task);
    }
}
