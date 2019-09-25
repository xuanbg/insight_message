package com.insight.base.message.common;

import com.insight.base.message.common.dto.LocalCall;
import com.insight.base.message.common.dto.RpcCall;
import com.insight.base.message.common.entity.Message;
import com.insight.util.Generator;
import com.insight.util.common.LockHandler;
import com.insight.util.pojo.Param;
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
    private final LockHandler handler = new LockHandler();
    private final MessageCore core;
    private final MessageDal dal;
    private final Param param = new Param("Message:Schedule");

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
     * 每间隔10秒执行一次计划任务
     */
    @Scheduled(fixedDelay = 10000)
    public void execute() {
        param.setValue(Generator.uuid());
        if (handler.tryLock(param)) {
            try {
                // 执行消息类型的计划任务
                List<Schedule<Message>> messageSchedules = dal.getMessageSchedule();
                messageSchedules.forEach(this::messageTask);

                // 执行本地调用类型的计划任务
                List<Schedule<LocalCall>> localSchedules = dal.getLocalSchedule();
                localSchedules.forEach(this::localCallTask);

                // 执行远程调用类型的计划任务
                List<Schedule<RpcCall>> rpcSchedules = dal.getRpcSchedule();
                rpcSchedules.forEach(this::rpcCallTask);
            } catch (Exception ex) {
                logger.error("计划任务发生错误! 异常信息为: {}", ex.getMessage());
            } finally {
                handler.releaseLock(param);
            }
        }
    }

    /**
     * 执行消息类型的计划任务
     *
     * @param schedule 计划任务DTO
     */
    private void messageTask(Schedule<Message> schedule) {
        dal.deleteSchedule(schedule.getId());
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

    /**
     * 执行本地调用类型的计划任务
     *
     * @param schedule 计划任务DTO
     */
    private void localCallTask(Schedule<LocalCall> schedule) {
        dal.deleteSchedule(schedule.getId());
    }

    /**
     * 执行远程调用类型的计划任务
     *
     * @param schedule 计划任务DTO
     */
    private void rpcCallTask(Schedule<RpcCall> schedule) {
        dal.deleteSchedule(schedule.getId());
    }
}
