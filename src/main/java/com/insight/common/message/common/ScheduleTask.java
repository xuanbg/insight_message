package com.insight.common.message.common;

import com.insight.common.message.common.client.RabbitClient;
import com.insight.common.message.common.dto.ScheduleCall;
import com.insight.common.message.common.mapper.MessageMapper;
import com.insight.utils.LockHandler;
import com.insight.utils.LockParam;
import com.insight.utils.Util;
import com.insight.utils.pojo.message.InsightMessage;
import com.insight.utils.pojo.message.Schedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 宣炳刚
 * @date 2019/9/24
 * @remark 计划任务执行类
 */
@Component
public class ScheduleTask {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final LockHandler handler = new LockHandler();
    private final LockParam param = new LockParam("Message:Schedule");
    private final MessageMapper mapper;

    /**
     * 构造方法
     *
     * @param mapper MessageMapper
     */
    public ScheduleTask(MessageMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * 每间隔10秒执行一次计划任务
     */
    @Scheduled(fixedDelay = 10000)
    public void execute() {
        param.setValue(Util.uuid());
        if (handler.tryLock(param)) {
            try {
                // 执行消息类型的计划任务
                List<Schedule<InsightMessage>> messageSchedules = mapper.getMessageSchedule();
                messageSchedules.forEach(this::messageTask);

                // 执行本地调用类型的计划任务
                List<Schedule<ScheduleCall>> localSchedules = mapper.getLocalSchedule();
                localSchedules.forEach(this::localCallTask);

                // 执行远程调用类型的计划任务
                List<Schedule<ScheduleCall>> rpcSchedules = mapper.getRpcSchedule();
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
    private void messageTask(Schedule<InsightMessage> schedule) {
        mapper.deleteSchedule(schedule.getId());
        RabbitClient.sendTopic("schedule.message", schedule);
    }

    /**
     * 执行本地调用类型的计划任务
     *
     * @param schedule 计划任务DTO
     */
    private void localCallTask(Schedule<ScheduleCall> schedule) {
        mapper.deleteSchedule(schedule.getId());
        RabbitClient.sendTopic("schedule.local", schedule);
    }

    /**
     * 执行远程调用类型的计划任务
     *
     * @param schedule 计划任务DTO
     */
    private void rpcCallTask(Schedule<ScheduleCall> schedule) {
        mapper.deleteSchedule(schedule.getId());
        RabbitClient.sendTopic("schedule.remote", schedule);
    }
}
