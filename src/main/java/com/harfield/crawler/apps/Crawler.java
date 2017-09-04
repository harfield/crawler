package com.fancydsp.data.easycrawler.apps;

import com.alibaba.fastjson.JSON;
import CrawlAttempt;
import CrawlJob;
import FieldExtractRule;
import WebContent;
import Fetcher;
import DefaultFetcher;
import CrawlOutputer;
import Parser;
import DefaultParser;
import DBUtils;
import OSUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2015/12/31.
 */
public class Crawler {
    private static final Logger LOG = LoggerFactory.getLogger(Crawler.class);

    private static class MyMessageListener implements MessageListener {
        private Fetcher fetcher = new DefaultFetcher();
        private Parser parser = new DefaultParser();

        @Override
        public void onMessage(Message message) {
            long maxProcessTime = Long.MAX_VALUE;
            long beginTime = System.currentTimeMillis();
            CrawlAttempt task = null;
            try {
                String s = new String(message.getBody(), "UTF-8");
                LOG.info("Receive msg: {}", s);
                task = JSON.parseObject(s, CrawlAttempt.class);
                // 更新crawlAttempt状态 -> 2 在爬
                task.setStat(2);
                task.setCrawlTimes(task.getCrawlTimes() + 1);
                task.setLastCrawlTime(new Date());
                updateCrawlAttemptStatus(task);
                WebContent webContent = fetcher.fetch(task);
                CrawlJob crawlJob = task.getCrawlJob();
                maxProcessTime = (long) (crawlJob.getRate() * 1000);
                // 获取抽取规则
                List<FieldExtractRule> fieldExtractRules = fetchFieldExtractRules(crawlJob.getId());
                //todo webContent
                Map<String, Object> parsedResultMap = parser.parse(webContent.toString(), fieldExtractRules, task);
                CrawlOutputer outputer = (CrawlOutputer) Class.forName(crawlJob.getOutputClass()).newInstance();
                outputer.output(parsedResultMap);
                // 爬虫成功,暂时把nextCrawlTime置成null
                task.setNextCrawlTime(null);
                task.setStat(4);
            } catch (Exception e) {
                String errMsg = e.getMessage();
                if (task != null) {
                    task.setStat(3);
                    task.setRemark(errMsg);
                    LOG.error("CrawlAttempt(id={}, url={}) throw a exception", task.getId(), task.getUrl());
                }
                LOG.error(errMsg, e);
            } finally {
                if (task != null) {
                    try {
                        updateCrawlAttemptStatus(task);
                    } catch (Exception e) {
                        LOG.error(e.getMessage(), e);
                    }
                }
            }
            long backoff = System.currentTimeMillis() - beginTime - maxProcessTime;
            if (backoff > 0) {
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
    }

    public static void main(String[] args) {
        long delta = 5 * 60 * 1000L;
        if (args.length > 0) {
            delta = Long.parseLong(args[0]);
        }
        final Map<String, SimpleMessageListenerContainer> containerMap = new HashMap<String, SimpleMessageListenerContainer>();
        ScheduledExecutorService scheduledService = Executors.newSingleThreadScheduledExecutor();
        scheduledService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    // 获取CrawlJobs
                    LOG.info("Begin to fetch available crawl jobs ...");
                    List<CrawlJob> crawlJobList = fetchCrawlJobs();
                    LOG.info("Fetch {} crawl jobs.", crawlJobList.size());
                    synchronized (containerMap) {
                        if (null == crawlJobList || crawlJobList.size() == 0) {
                            for (String key : containerMap.keySet()) {
                                containerMap.get(key).stop();
                            }
                            containerMap.clear();
                            return;
                        }
                        Set<String> validKeySet = new HashSet<String>();
                        for (CrawlJob crawlJob : crawlJobList) {
                            try {
                                long crawlJobId = crawlJob.getId();
                                String queueName = MQUtils.EXCHANGE_NAME + "_" + crawlJobId;
                                SimpleMessageListenerContainer messageListenerContainer = null;
                                switch (crawlJob.getStat()) {
                                    case 0://预备
                                    case 2://进行中
                                        messageListenerContainer = containerMap.get(queueName);
                                        int burst = crawlJob.getBurst();
                                        if (messageListenerContainer != null) {
                                            LOG.info("Begin to setConcurrentConsumers MessageListenerContainer(queueName={}, size={}) ...", queueName, burst);
                                            messageListenerContainer.setConcurrentConsumers(burst);
                                            LOG.info("End to setConcurrentConsumers MessageListenerContainer(queueName={}, size={}) ...", queueName, burst);
                                        } else {
                                            // 初始化MessageListenerContainer
                                            LOG.info("Begin to start MessageListenerContainer(queueName={}, size={}) ...", queueName, burst);
                                            messageListenerContainer = MQUtils.startMessageListenerContainer(new MyMessageListener(), burst, queueName);
                                            LOG.info("End to start MessageListenerContainer(queueName={}, size={}).", queueName, burst);
                                            containerMap.put(queueName, messageListenerContainer);
                                        }
                                        validKeySet.add(queueName);
                                        break;
                                }
                            } catch (Exception e) {
                                LOG.error(e.getMessage(), e);
                            }
                        }
                        // 清理container
                        LOG.info("Begin to clean containerMap ...");
                        List<String> needRemoveKeys = new ArrayList<String>();
                        for (String key : containerMap.keySet()) {
                            if (!validKeySet.contains(key)) {
                                needRemoveKeys.add(key);
                            }
                        }
                        for (String key : needRemoveKeys) {
                            containerMap.remove(key).stop();
                        }
                        LOG.info("End to clean containerMap: {}", containerMap);
                    }
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }, 0, delta, TimeUnit.MILLISECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    synchronized(containerMap) {
                        for (String key : containerMap.keySet()) {
                            containerMap.get(key).stop();
                        }
                        containerMap.clear();
                    }
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                }
                try {
                    DBUtils.closeDataSource();
                }catch (Exception e){
                    LOG.error(e.getMessage(), e);
                }
            }
        });
    }

    private static void updateCrawlAttemptStatus(CrawlAttempt task) throws Exception {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        try {
            conn = DBUtils.getConnection();
            preparedStatement = conn.prepareStatement(DBUtils.UPDATE_CRAWL_ATTEMPT_STATUS_BY_ID_AND_VERSION_SQL);
            preparedStatement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));//crawlUpdateTime
            preparedStatement.setTimestamp(2, new Timestamp(task.getSendMqTime().getTime()));//sendMqTime
            preparedStatement.setTimestamp(3, new Timestamp(task.getLastCrawlTime().getTime()));
            Date nextCrawlTime = task.getNextCrawlTime();
            if (null == nextCrawlTime) {
                preparedStatement.setNull(4, Types.TIMESTAMP);
            } else {
                preparedStatement.setTimestamp(4, new Timestamp(nextCrawlTime.getTime()));
            }
            preparedStatement.setInt(5, task.getCrawlTimes());
            preparedStatement.setInt(6, task.getStat());
            String remark = task.getRemark();
            if (null == remark) {
                preparedStatement.setNull(7, Types.VARCHAR);
            } else {
                preparedStatement.setString(7, remark);
            }
            preparedStatement.setLong(8, task.getId());
            long version = task.getVersion();
            preparedStatement.setLong(9, version);
            preparedStatement.execute();
            task.setVersion(version + 1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            DBUtils.close(preparedStatement);
            DBUtils.close(conn);
        }
    }

    private static List<FieldExtractRule> fetchFieldExtractRules(long crawlJobId) throws Exception{
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            conn = DBUtils.getConnection();
            preparedStatement = conn.prepareStatement(DBUtils.FETCH_FIELD_EXTRACT_RULES_SQL);
            preparedStatement.setLong(1, crawlJobId);
            resultSet = preparedStatement.executeQuery();
            List<FieldExtractRule> list = new ArrayList<FieldExtractRule>();
            while (resultSet.next()) {
                FieldExtractRule fieldExtractRule = new FieldExtractRule();
                fieldExtractRule.setId(resultSet.getLong("fieldExtractRuleId"));
                fieldExtractRule.setName(resultSet.getString("fieldExtractRuleName"));
                fieldExtractRule.setExtractType(resultSet.getInt("fieldExtractRuleType"));
                fieldExtractRule.setRule(resultSet.getString("fieldExtractRule"));
                fieldExtractRule.setValueType(resultSet.getInt("fieldExtractRuleValueType"));
                fieldExtractRule.setDefaultValue(resultSet.getString("fieldExtractRuleDefaultValue"));
                fieldExtractRule.setOrd(resultSet.getInt("fieldExtractRuleOrder"));
                list.add(fieldExtractRule);
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            DBUtils.close(resultSet);
            DBUtils.close(preparedStatement);
            DBUtils.close(conn);
        }
    }

    private static List<CrawlJob> fetchCrawlJobs() throws Exception {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            conn = DBUtils.getConnection();
            preparedStatement = conn.prepareStatement(DBUtils.FETCH_CRAWL_JOBS_SQL);
            preparedStatement.setString(1, OSUtils.getLocalIP());
            resultSet = preparedStatement.executeQuery();
            List<CrawlJob> list = new ArrayList<CrawlJob>();
            while (resultSet.next()) {
                CrawlJob crawlJob = new CrawlJob();
                crawlJob.setId(resultSet.getLong("crawlJobId"));
                crawlJob.setStat(resultSet.getInt("crawlJobStat"));
                crawlJob.setBurst(resultSet.getInt("crawlJobBurst"));
                crawlJob.setRate(resultSet.getFloat("crawlJobRate"));
                list.add(crawlJob);
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            DBUtils.close(resultSet);
            DBUtils.close(preparedStatement);
            DBUtils.close(conn);
        }
    }
}
