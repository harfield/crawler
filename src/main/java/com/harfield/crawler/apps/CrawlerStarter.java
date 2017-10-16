package com.harfield.crawler.apps;

import com.harfield.crawler.domain.Job;
import com.harfield.crawler.service.DBService;
import com.harfield.crawler.service.MQService;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;
import java.util.List;

@SpringBootApplication
public class CrawlerStarter {
    public static void main(String[] args) {
        SpringApplication.run(CrawlerStarter.class, args);
    }

    @Resource
    DBService dbService;
    @Resource
    MQService mqService;


    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return new CommandLineRunner() {
            @Override
            public void run(String[] args) throws Exception {
                while (true) {
                    List<Job> jobs = dbService.getRunningJobs();
                    for(Job j : jobs){
                        mqService.send("","", MessageDeliveryMode.PERSISTENT);
                    }
                    break;
                }
            }
        };
    }

}
