package com.insight.common.message.common;

import com.insight.common.message.common.dto.Schedule;
import com.insight.common.message.common.dto.ScheduleCall;
import com.insight.common.message.common.entity.InsightMessage;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @author 宣炳刚
 * @date 2019-09-03
 * @remark 队列监听类
 */
@Component
public class Listener {
    private final Core core;

    /**
     * 构造方法
     *
     * @param core Core
     */
    public Listener(Core core) {
        this.core = core;
    }

    /**
     * 从队列订阅消息类型的计划任务
     *
     * @param schedule 计划任务DTO
     */
    @RabbitHandler
    @RabbitListener(queues = "schedule.message")
    public void receiveMessage(Schedule<InsightMessage> schedule, Channel channel, Message message) throws IOException {
        switch (schedule.getMethod()) {
            case "addMessage":
                core.addMessage(schedule, channel, message);
                return;

            case "pushNotice":
                core.pushNotice(schedule, channel, message);
                return;

            case "sendSms":
                core.sendSms(schedule, channel, message);
                return;

            case "sendMail":
                core.sendMail(schedule, channel, message);
                return;

            default:
        }
    }

    /**
     * 从队列订阅本地调用类型的计划任务
     *
     * @param schedule 计划任务DTO
     */
    @RabbitHandler
    @RabbitListener(queues = "schedule.local")
    public void receiveLocalCall(Schedule<ScheduleCall> schedule, Channel channel, Message message) throws IOException, URISyntaxException {
        core.localCall(schedule, channel, message);
    }

    /**
     * 从队列订阅远程调用类型的计划任务
     *
     * @param schedule 计划任务DTO
     */
    @RabbitHandler
    @RabbitListener(queues = "schedule.remote")
    public void receiveRemoteCall(Schedule<ScheduleCall> schedule, Channel channel, Message message) throws IOException {
        core.remoteCall(schedule, channel, message);
    }
}