package com.harfield.crawler.service;


import com.harfield.crawler.common.Task;
import com.harfield.crawler.dao.CrawlerMapper;
import org.mybatis.spring.annotation.MapperScan;

import javax.annotation.Resource;
import java.util.List;

@MapperScan("com.fancydsp.data.easycrawler.dao")
public class DBServiceImpl implements DBService{
    @Resource
    private CrawlerMapper crawlerMapper;

    @Override
    public List<Task> getPendingAttempts() {
        return crawlerMapper.getPendingAttempts();
    }
}
