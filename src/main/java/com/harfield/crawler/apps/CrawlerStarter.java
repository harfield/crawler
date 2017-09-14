package com.harfield.crawler.apps;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CrawlerStarter {
    public static void main(String[] args) {
        SpringApplication.run(CrawlerStarter.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        String[] beanDefinitionNames = ctx.getBeanDefinitionNames();
        System.out.println(String.join("\n", beanDefinitionNames));
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
//                String[] beanDefinitionNames = ctx.getBeanDefinitionNames();
//                System.out.println(String.join("\n", beanDefinitionNames));
            }
        };
    }

}
