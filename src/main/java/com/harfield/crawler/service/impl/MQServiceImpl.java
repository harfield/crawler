package com.harfield.crawler.service.impl;

import com.harfield.crawler.service.MQService;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.stereotype.Service;

/**
 * Created by harfield on 17/9/10.
 */
@Service(value = "defaultMQServiceImpl")
public class MQServiceImpl implements MQService {
    @Override
    public void send(String exchangeName, String msg, MessageDeliveryMode mode) {

    }
}
