package com.harfield.crawler.dao;

import com.harfield.crawler.domain.Task;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;


public interface CrawlerMapper {

    @Select("select task.*,job.* from task  join job job on job_id=task.id where job.status in (1,2) ")
    @Results({
            @Result(property = "task.id",  column = "id" /*,javaType = UserSexEnum.class*/),
            @Result(property = "task.url", column = "url"),
            @Result(property = "task.referrer", column = "referrer"),
            @Result(property = "task.send_to_mq_time", column = "sendToMqTime"),
            @Result(property = "task.next_crawl_time", column = "nextCrawlTime"),
            @Result(property = "task.finished_time", column = "finishTime"),
            @Result(property = "task.finish_status", column = "finishStatus"),
            @Result(property = "task.remaining_retry", column = "remainingRetry"),
            @Result(property = "task.msg", column = "msg"),
//            @Result(property = "job.id", column = "crawlAttemptReferrer"),
            @Result(property = "task.create_time", column = "createTime"),
            @Result(property = "task.update_time", column = "updateTime"),
    })
    List<Task> getPendingTasks();

    @Update("update crawl_attempt set crawlAttemptVersion=crawlAttemptVersion+1,crawlAttemptCrawlUpdateTime=now(),crawlAttemptSendMqTime=#{sendTime},crawlAttemptLastCrawlTime=#{lastCrawTime},crawlAttemptNextCrawlTime=#{nextCrawTime},crawlAttemptCrawlTimes=#{attemptTimes},crawlAttemptStat=#{attemptStatus},crawlAttemptRemark=#{remark} where crawlAttemptId=#{attemptId} and crawlAttemptVersion=#{attemptVersion}")
    int updateCrawlAttemptByIdAndVersion();
}
