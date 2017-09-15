package com.harfield.crawler.dao;

import com.harfield.crawler.domain.Task;
import org.apache.ibatis.annotations.*;

import java.util.List;


public interface CrawlerMapper {

    @Select("select task.*,job.*  from task  join job job on job_id=task.id where job.status in (1,2) ")
    @Results({
            @Result(column = "id",  property = "id" ),
//            @Result(column = "task.url", property = "url"),
//            @Result(column = "task.referrer", property = "referrer"),
//            @Result(column = "task.send_to_mq_time", property = "sendToMqTime"),
//            @Result(column = "task.next_crawl_time", property = "nextCrawlTime"),
//            @Result(column = "task.finished_time", property = "finishTime"),
//            @Result(column = "task.finish_status", property = "finishStatus"),
//            @Result(column = "task.remaining_retry", property = "remainingRetry"),
//            @Result(column = "task.msg", property = "msg"),
            @Result(column = "update_time", property = "updateTime"),
            @Result(column = "id", property = "job.id"),
            @Result(column = "update_time", property = "job.updateTime"),
            @Result(column = "insert_time", property = "job.insertTime"),
    })
    List<Task> getPendingTasks();

}
