package com.insight.common.message.common.config;

import com.insight.utils.Json;
import com.insight.utils.common.ApplicationContextHolder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * @author 宣炳刚
 * @date 2023-1-4
 * @remark RabbitMQ客户端
 */
public class RabbitClient {
    private static final RabbitTemplate TEMPLATE = ApplicationContextHolder.getContext().getBean(RabbitTemplate.class);

    /**
     * 发送资源数据到队列
     *
     * @param data 用户DTO
     */
    public static void sendResources(Object data) {
        Object object = Json.clone(data, Object.class);
        TEMPLATE.convertAndSend("amq.topic", "hxb.resources", object);
    }
}
