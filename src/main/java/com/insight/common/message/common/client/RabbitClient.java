package com.insight.common.message.common.client;

import com.insight.util.common.ApplicationContextHolder;
import com.insight.util.pojo.Schedule;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * @author 宣炳刚
 * @date 2019-09-03
 * @remark RabbitMQ客户端
 */
public class RabbitClient {
    private static final RabbitTemplate TEMPLATE = ApplicationContextHolder.getContext().getBean(RabbitTemplate.class);

    /**
     * 发送计划任务数据到队列
     *
     * @param schedule 计划任务DTO
     */
    public static void sendTopic(String key, Schedule schedule) {
        TEMPLATE.convertAndSend("amq.topic", key, schedule);
    }
}
