package com.insight.common.message.common;

import com.insight.common.message.common.dto.Schedule;
import com.insight.common.message.common.entity.InsightMessage;
import com.insight.common.message.common.entity.PushMessage;
import com.insight.common.message.common.entity.SubscribeMessage;
import com.insight.common.message.common.mapper.MessageMapper;
import com.insight.utils.Generator;
import com.insight.utils.Util;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * @author 宣炳刚
 * @date 2019/9/23
 * @remark 消息数据处理DAL
 */
@Component
public class MessageDal {
    private final MessageMapper mapper;

    /**
     * 构造方法
     *
     * @param mapper MessageMapper
     */
    public MessageDal(MessageMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * 保存消息到数据库
     *
     * @param message 消息DTO
     */
    @Transactional
    public void addMessage(InsightMessage message) {
        String id = message.getId();
        if (id == null || id.isEmpty()) {
            message.setId(Util.uuid());
        }

        message.setCreatedTime(LocalDateTime.now());
        mapper.addMessage(message);
        if (message.getBroadcast()) {
            return;
        }

        // 构造本地消息推送列表并写入数据库
        List<PushMessage> pushList = new ArrayList<>();
        message.getReceivers().forEach(i -> {
            PushMessage push = new PushMessage();
            push.setId(Util.uuid());
            push.setMessageId(message.getId());
            push.setUserId(i);
            push.setRead(false);
            pushList.add(push);
        });
        mapper.pushMessage(pushList);
    }

    /**
     * 设置消息为已读
     *
     * @param messageId   消息ID
     * @param userId      用户ID
     * @param isBroadcast 是否广播消息
     */
    @Async
    public void readMessage(String messageId, String userId, boolean isBroadcast) {
        if (isBroadcast) {
            SubscribeMessage subscribe = new SubscribeMessage();
            subscribe.setId(Util.uuid());
            subscribe.setMessageId(messageId);
            subscribe.setUserId(userId);
            subscribe.setCreatedTime(LocalDateTime.now());

            mapper.subscribeMessage(subscribe);
        } else {
            mapper.readMessage(messageId, userId);
        }
    }

    /**
     * 保存计划任务到数据库
     *
     * @param schedule 计划任务DTO
     */
    public void addSchedule(Schedule schedule) {
        mapper.addSchedule(schedule);
    }
}
