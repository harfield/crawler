package com.harfield.crawler.service;

import org.springframework.amqp.core.MessageDeliveryMode;

/**
 * Created by harfield on 17/9/10.
 */
public interface MQService {


    void send(String exchangeName, String routingKey, String msg, MessageDeliveryMode mode);

}
