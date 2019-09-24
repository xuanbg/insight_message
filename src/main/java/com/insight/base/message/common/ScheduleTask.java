package com.insight.base.message.common;

import com.insight.base.message.common.entity.Message;
import com.insight.util.Json;
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
    @Scheduled(fixedDelay = 1000)
    public void execute() {
        for (int i = 0; i < 3; i++) {
            switch (i) {
                case 0:
                    List<Schedule> list = dal.getSchedule(i);
                    list.forEach(this::messageTask);
                    break;

                case 1:
                    break;
                case 2:
                default:
            }
        }
    }

    /**
     * 消息类型的计划任务执行
     *
     * @param s 计划任务DTO
     */
    private void messageTask(Schedule s) {
        Schedule<Message> schedule = initSchedule(s, Message.class);
        switch (s.getMethod()) {
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

    /**
     * 转换成指定类型的计划任务DTO
     *
     * @param s    泛型计划任务DTO
     * @param type 计划任务DTO类型
     * @param <T>  泛型参数
     * @return 指定类型的计划任务DTO
     */
    private <T> Schedule<T> initSchedule(Schedule s, Class<T> type) {
        Schedule<T> schedule = new Schedule<>();
        schedule.setId(s.getId());
        schedule.setType(s.getType());
        schedule.setMethod(s.getMethod());
        schedule.setTaskTime(s.getTaskTime());
        schedule.setContent(Json.clone(s.getContent(), type));
        schedule.setCount(s.getCount());
        schedule.setInvalid(s.getInvalid());
        schedule.setCreatedTime(s.getCreatedTime());

        return schedule;
    }
}
