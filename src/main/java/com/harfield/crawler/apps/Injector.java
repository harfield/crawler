package com.harfield.crawler.apps;

import com.harfield.crawler.common.Task;
import com.harfield.crawler.common.CrawlJob;

import com.harfield.crawler.service.DBService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;
import java.sql.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * Created by harfield on 2017/8/28.
 */
@SpringBootApplication

public class Injector {

    public static void main(String[] args) {
        SpringApplication.run(Injector.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        final ApplicationContext ctx0 = ctx;
        return new CommandLineRunner() {
            private final Logger LOG = LoggerFactory.getLogger(Injector.class);
            @Resource
            private DBService dbService;

            @Override
            public void run(String... args) throws Exception {
                String[] beanNames = ctx0.getBeanDefinitionNames();
                Arrays.sort(beanNames);
                for (String beanName : beanNames) {
                    System.out.println(beanName);
                }


                long l = 60000;
                if (args.length > 0) {
                    l = Long.parseLong(args[0]);
                }
                final long backoff = l;

                while (true) {
                    try {
                        LOG.info("Begin to fetch available crawlAttempts ...");
                        Date current = new Date();
                        List<Task> pendingAttempts = dbService.getPendingAttempts();
//                       Statement(DBUtils.UPDATE_CRAWL_ATTEMPT_STATUS_BY_ID_AND_VERSION_SQL);
                        int i = 0;
                        while (resultSet.next()) {
                            i++;
                            Task task = new Task();
                            task.setId(resultSet.getLong("crawlAttemptId"));
                            task.setUrl(resultSet.getString("crawlAttemptUrl"));
                            task.setReferrer(resultSet.getString("crawlAttemptReferrer"));
                            Object o = resultSet.getObject("crawlAttemptRequestMethod");
                            if (o != null) {
                                task.setRequestMethod(resultSet.getInt("crawlAttemptRequestMethod"));
                            }
                            o = resultSet.getObject("crawlAttemptRequestType");
                            if (o != null) {
                                task.setRequestType(resultSet.getInt("crawlAttemptRequestType"));
                            }
                            o = resultSet.getObject("crawlAttemptMaxTryRequest");
                            if (o != null) {
                                task.setMaxTryRequest(resultSet.getInt("crawlAttemptMaxTryRequest"));
                            }
                            o = resultSet.getObject("crawlAttemptFollowRedirects");
                            if (o != null) {
                                task.setFollowRedirects(resultSet.getInt("crawlAttemptFollowRedirects"));
                            }
                            task.setUserAgent(resultSet.getString("crawlAttemptUserAgent"));
                            o = resultSet.getObject("crawlAttemptTimeout");
                            if (o != null) {
                                task.setTimeout(resultSet.getLong("crawlAttemptTimeout"));
                            }
                            o = resultSet.getObject("crawlAttemptUseProxy");
                            if (o != null) {
                                task.setUseProxy(resultSet.getInt("crawlAttemptUseProxy"));
                            }
                            task.setHeadersByJsonStr(resultSet.getString("crawlAttemptHeaders"));
                            task.setCookiesByJsonStr(resultSet.getString("crawlAttemptCookies"));
                            task.setParamsByJsonStr(resultSet.getString("crawlAttemptParams"));
                            task.setCrawlUpdateTime(resultSet.getObject("crawlAttemptCrawlUpdateTime", Timestamp.class));
                            task.setSendMqTime(resultSet.getObject("crawlAttemptSendMqTime", Timestamp.class));
                            task.setLastCrawlTime(resultSet.getObject("crawlAttemptLastCrawlTime", Timestamp.class));
                            task.setNextCrawlTime(resultSet.getObject("crawlAttemptNextCrawlTime", Timestamp.class));
                            Integer crawlAttemptCrawlTimes = resultSet.getObject("crawlAttemptCrawlTimes", Integer.class);
                            if (crawlAttemptCrawlTimes != null) {
                                task.setCrawlTimes(crawlAttemptCrawlTimes);
                            }
                            task.setExtraDataByJsonStr(resultSet.getString("crawlAttemptExtraJsonData"));
                            task.setVersion(resultSet.getLong("crawlAttemptVersion"));

                            CrawlJob crawlJob = new CrawlJob();
                            crawlJob.setId(resultSet.getLong("crawlJobId"));
                            crawlJob.setRequestMethod(resultSet.getInt("crawlJobRequestMethod"));
                            crawlJob.setRequestType(resultSet.getInt("crawlJobRequestType"));
                            crawlJob.setMaxTryRequest(resultSet.getInt("crawlJobMaxTryRequest"));
                            crawlJob.setFollowRedirects(resultSet.getInt("crawlJobFollowRedirects"));
                            crawlJob.setUserAgent(resultSet.getString("crawlJobUserAgent"));
                            o = resultSet.getObject("crawlJobTimeout");
                            if (o != null) {
                                crawlJob.setTimeout(resultSet.getLong("crawlJobTimeout"));
                            }
                            o = resultSet.getObject("crawlJobUseProxy");
                            if (o != null) {
                                crawlJob.setUseProxy(resultSet.getInt("crawlJobUseProxy"));
                            }
                            crawlJob.setOutputClass(resultSet.getString("crawlJobOutputClass"));
                            task.setCrawlJob(crawlJob);

                            // 更新crawlAttempt状态 -> 1 待爬
                            Timestamp ts = new Timestamp(System.currentTimeMillis());
                            preparedStatement2.setTimestamp(1, ts);//crawlUpdateTime
                            preparedStatement2.setTimestamp(2, ts);//sendMqTime
                            Date lastCrawlTime = task.getLastCrawlTime();
                            if (null == lastCrawlTime) {
                                preparedStatement2.setNull(3, Types.TIMESTAMP);
                            } else {
                                preparedStatement2.setTimestamp(3, new Timestamp(lastCrawlTime.getTime()));
                            }
                            Date nextCrawlTime = task.getNextCrawlTime();
                            if (null == nextCrawlTime) {
                                preparedStatement2.setNull(4, Types.TIMESTAMP);
                            } else {
                                preparedStatement2.setTimestamp(4, new Timestamp(nextCrawlTime.getTime()));
                            }
                            preparedStatement2.setInt(5, task.getCrawlTimes());
                            preparedStatement2.setInt(6, 1);//stat
                            preparedStatement2.setNull(7, Types.VARCHAR);//remark
                            preparedStatement2.setLong(8, task.getId());
                            long version = task.getVersion();
                            preparedStatement2.setLong(9, version);

                            task.setSendMqTime(ts);
                            task.setVersion(version + 1);// 注意
                            // 先往mq发消息
                            try {
//                                MQUtils.send(MQUtils.EXCHANGE_NAME + "_" + crawlJob.getId(), JSON.toJSONString(task), MessageDeliveryMode.PERSISTENT);
                                // 再状态更新成功
                                preparedStatement2.executeUpdate();
                            } catch (Throwable t) {
                                LOG.error(t.getMessage(), t);
                            }/* finally {
                                preparedStatement2.clearParameters();
                            }*/
                        }
                        // 没有可爬的url
                        if (0 == i) {
                            LOG.info("No available crawlAttempts. Sleep {} ms.", backoff);
                            Thread.sleep(backoff);
                        } else {
                            LOG.info("Fetch {} crawlAttempts.", i);
                        }
                    } catch (Throwable e) {
                        LOG.error(e.getMessage(), e);
                    }
                }
            }

        };
    }
}
