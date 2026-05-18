package com.hand.hls.mq.controllers;

import com.hand.hap.core.mq.YLRabbitMqConfiguration;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class sendDirectMessageController {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * MQ测试消息推送方法
     * @return
     */
    @PostMapping(value = {"/sendMessage"})
    public String sendDirectMessage(){
        String messageId = UUID.randomUUID().toString();
        String messageData = "test message,hello!";
        String current = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Map<String,Object> map = new HashMap<>();
        map.put("messageId",messageId);
        map.put("data",messageData);
        map.put("current",current);
        rabbitTemplate.convertAndSend(YLRabbitMqConfiguration.EXCHANGE_GT_YL, "n001", map, new CorrelationData(UUID.randomUUID().toString()));
        return "ok";
    }
}
