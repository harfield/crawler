package com.harfield.crawler.service.impl;

import com.harfield.crawler.service.MQService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Properties;

/**
 * Created by harfield on 17/9/10.
 */
@Service(value = "defaultMQServiceImpl")
public class MQServiceImpl implements MQService {
    private static final Logger LOG = LoggerFactory.getLogger(MQServiceImpl.class);

    private  CachingConnectionFactory connectionFactory;
    private  RabbitTemplate rabbitTemplate;
    private  RabbitAdmin rabbitAdmin;

    public static final String EXCHANGE_NAME = "crawler";

    @Override
    public void send(String exchangeName, String msg, MessageDeliveryMode mode) {

    }
}
