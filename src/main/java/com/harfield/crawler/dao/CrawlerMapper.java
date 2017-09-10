package com.harfield.crawler.dao;

import com.harfield.crawler.domain.Task;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;


public interface CrawlerMapper {

    @Select("select * from task a join job b on a.crawlAttemptCrawlJobId=b.crawlJobId where (a.crawlAttemptStat in (0,3,4) and a.crawlAttemptNextCrawlTime<=now()) or (a.crawlAttemptStat=1 and a.crawlAttemptCrawlUpdateTime<date_sub(now(),interval 7200 second)) or (a.crawlAttemptStat=2 and a.crawlAttemptCrawlUpdateTime<date_sub(now(),interval 300 second)) or a.crawlAttemptProducerUpdateTime>a.crawlAttemptSendMqTime order by a.crawlAttemptNextCrawlTime asc,a.crawlAttemptCrawlUpdateTime asc")
    @Results({
            @Result(property = "id",  column = "crawAttemptId" /*,javaType = UserSexEnum.class*/),
            @Result(property = "url", column = "crawlAttemptUrl"),
            @Result(property = "referrer", column = "crawlAttemptReferrer"),
            @Result(property = "requestMethod", column = "crawlAttemptRequestMethod"),
            @Result(property = "referrer", column = "crawlAttemptReferrer"),
            @Result(property = "referrer", column = "crawlAttemptReferrer"),
            @Result(property = "referrer", column = "crawlAttemptReferrer"),
            @Result(property = "referrer", column = "crawlAttemptReferrer"),
            @Result(property = "referrer", column = "crawlAttemptReferrer"),
    })
    List<Task> getPendingTasks();

    @Update("update crawl_attempt set crawlAttemptVersion=crawlAttemptVersion+1,crawlAttemptCrawlUpdateTime=now(),crawlAttemptSendMqTime=#{sendTime},crawlAttemptLastCrawlTime=#{lastCrawTime},crawlAttemptNextCrawlTime=#{nextCrawTime},crawlAttemptCrawlTimes=#{attemptTimes},crawlAttemptStat=#{attemptStatus},crawlAttemptRemark=#{remark} where crawlAttemptId=#{attemptId} and crawlAttemptVersion=#{attemptVersion}")
    int updateCrawlAttemptByIdAndVersion();
}
