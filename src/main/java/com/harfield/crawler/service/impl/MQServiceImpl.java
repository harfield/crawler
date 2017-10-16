package com.harfield.crawler.service.impl;

import com.harfield.crawler.service.MQService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service(value = "defaultMQServiceImpl")
public class MQServiceImpl implements MQService {
    private static final Logger LOG = LoggerFactory.getLogger(MQServiceImpl.class);

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private RabbitAdmin rabbitAdmin;

    public static final String DEFAULT_EXCHANGE_NAME = "crawler";

    @Override
    public void send(String exchangeName, String routingKey, String msg, MessageDeliveryMode mode) {

    }
}
