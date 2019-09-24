package com.insight.base.message.common;

import com.insight.base.message.common.entity.Message;
import com.insight.util.pojo.Schedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 宣炳刚
 * @date 2019/9/24
 * @remark 计划任务核心类
 */
@Component
public class ScheduleTask {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final MessageCore core;
    private final MessageDal dal;

    /**
     * 构造方法
     *
     * @param core MessageCore
     * @param dal  MessageMapper
     */
    public ScheduleTask(MessageCore core, MessageDal dal) {
        this.core = core;
        this.dal = dal;
    }

    /**
     * 执行计划任务
     */
    @Scheduled(fixedDelay = 10000)
    public void execute() {
        // 执行消息类型的计划任务
        List<Schedule<Message>> messageSchedules = dal.getMessageSchedule();
        messageSchedules.forEach(this::messageTask);

        // 执行本地调用类型的计划任务

        // 执行远程调用类型的计划任务

    }

    /**
     * 消息类型的计划任务执行
     *
     * @param schedule 计划任务DTO
     */
    private void messageTask(Schedule<Message> schedule) {
        switch (schedule.getMethod()) {
            case "addMessage":
                core.addMessage(schedule);
                return;

            case "pushNotice":
                core.pushNotice(schedule);
                return;

            case "sendSms":
                core.sendSms(schedule);
                return;

            default:
        }
    }
}
