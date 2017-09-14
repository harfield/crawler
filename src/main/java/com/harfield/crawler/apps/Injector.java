package com.harfield.crawler.apps;

import com.alibaba.fastjson.JSON;
import com.harfield.crawler.domain.Task;

import com.harfield.crawler.service.DBService;
import com.harfield.crawler.service.MQService;
import com.harfield.crawler.service.impl.DBServiceImpl;
import com.harfield.crawler.util.MQUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.Resource;
import java.util.List;

/**
 *
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.harfield.crawler.service")
public class Injector {

    public static void main(String[] args) {
        SpringApplication.run(Injector.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return new CommandLineRunner() {
            private final Logger LOG = LoggerFactory.getLogger(Injector.class);
            @Resource
            private DBService dbService;
            @Resource
            private MQService mqService;

            @Override
            public void run(String... args) throws Exception {
                long l = 60000;
                if (args.length > 0) {
                    l = Long.parseLong(args[0]);
                }
                final long backoff = l;
                while (true) {
                    LOG.info("Begin to fetch pending tasks ...");
                    List<Task> pendingTasks = dbService.getPendingTasks();

                    if (0 == pendingTasks.size()) {
                        LOG.info("No available task. Sleep {} ms.", backoff);
                        Thread.sleep(backoff);
                    } else {
                        LOG.info("Fetch {} tasks .", pendingTasks.size());
                        for (Task task : pendingTasks) {
                            try {
                                mqService.send(MQUtils.EXCHANGE_NAME + "_" + task.getJob().getId(), JSON.toJSONString(task), MessageDeliveryMode.PERSISTENT);
                                dbService.markTaskQueuing(task);
                            } catch (Exception e) {
                                LOG.error(e.getMessage(), e);
                            }
                        }
                        LOG.info("queued {} tasks .", pendingTasks.size());
                    }

                    Thread.sleep(l);
                }
            }
        };
    }
}
