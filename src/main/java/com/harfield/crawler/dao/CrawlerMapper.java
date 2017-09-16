package com.harfield.crawler.dao;

import com.harfield.crawler.domain.Task;
import org.apache.ibatis.annotations.*;

import java.util.List;


public interface CrawlerMapper {

    @Select("select " +
            "task.*" +
            ",job.name job_name" +
            ",job.desc job_desc" +
            ",job.request_method request_method" +
            ",job.request_params request_params" +
            ",job.response_type response_type" +
            ",job.max_try max_try" +
            ",job.max_follow_redirects max_follow_redirects" +
            ",job.user_agent user_agent" +
            ",job.timeout timeout" +
            ",job.use_proxy use_proxy" +
            ",job.headers headers" +
            ",job.cookies cookies" +
            ",job.rate rate" +
            ",job.burst burst" +
            ",job.status job_status" +
            ",job.duration duration" +
            ",job.output_class output_class" +
            ",job.insert_time job_insert_time" +
            ",job.update_time job_update_time" +
            " from task join job on task.job_id=job.id " +
            "where job.status <= 2 and ( (max_try > 0 and finished_status > 0 ) or next_crawl_time <= now() ) limit 5000 ")
    @Results({
            @Result(column = "job_id", property = "job.id"),
            @Result(column = "job_name", property = "job.name"),
            @Result(column = "job_desc", property = "job.desc"),
            @Result(column = "request_method", property = "job.requestMethod"),
            @Result(column = "request_params", property = "job.requestParams"),
            @Result(column = "response_type", property = "job.responseType"),
            @Result(column = "max_try", property = "job.maxTry"),
            @Result(column = "max_follow_redirects", property = "job.maxFollowRedirects"),
            @Result(column = "user_agent", property = "job.userAgent"),
            @Result(column = "timeout", property = "job.timeout"),
            @Result(column = "use_proxy", property = "job.useProxy"),
            @Result(column = "headers", property = "job.headers"),
            @Result(column = "cookies", property = "job.cookies"),
            @Result(column = "rate", property = "job.rate"),
            @Result(column = "burst", property = "job.burst"),
            @Result(column = "job_status", property = "job.status"),
            @Result(column = "duration", property = "job.duration"),
            @Result(column = "output_class", property = "job.outputClass"),
            @Result(column = "job_insert_time", property = "job.insertTime"),
            @Result(column = "job_update_time", property = "job.updateTime"),
    })
    List<Task> getPendingTasks();

    @Update("update task set " +
            " finish_status = #{finishStatus} " +
            " send_to_mq_time = #{sendToMqTime} " +
            " start_time = #{startTime} " +
            " finish_time = #{finishTime} " +
            " next_crawl_time = #{nextCrawlTime} " +
            " msg = #{msg} " +
            " remaining_retry = #{remainRetry}" +
            " where id = #{id} ")
    int updateTaskById(Task task);
}
