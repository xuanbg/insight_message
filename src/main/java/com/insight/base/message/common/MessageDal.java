package com.insight.base.message.common;

import com.insight.base.message.common.dto.TemplateDto;
import com.insight.base.message.common.entity.Message;
import com.insight.base.message.common.entity.PushMessage;
import com.insight.base.message.common.mapper.MessageMapper;
import com.insight.util.Generator;
import com.insight.util.pojo.Schedule;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 宣炳刚
 * @date 2019/9/23
 * @remark
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
     * 获取适用消息模板
     *
     * @param tenantId    租户ID
     * @param sceneCode   场景编码
     * @param appId       应用ID
     * @param channelCode 渠道编码
     * @return 消息模板
     */
    public TemplateDto getTemplate(String tenantId, String sceneCode, String appId, String channelCode) {
        return mapper.getTemplate(tenantId, sceneCode, appId, channelCode);
    }

    /**
     * 保存消息到数据库
     *
     * @param message 消息DTO
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean addMessage(Message message) {
        mapper.addMessage(message);
        if (message.getBroadcast()) {
            return true;
        }

        // 构造本地消息推送列表并写入数据库
        List<PushMessage> pushList = new ArrayList<>();
        message.getReceivers().forEach(i -> {
            PushMessage push = new PushMessage();
            push.setId(Generator.uuid());
            push.setMessageId(message.getId());
            push.setUserId(i);
            push.setRead(false);
            pushList.add(push);
        });
        mapper.pushMessage(pushList);

        return true;
    }

    /**
     * 保存计划任务到数据库
     *
     * @param schedule 计划任务DTO
     */
    public boolean addSchedule(Schedule<Message> schedule) {
        return true;
    }

    /**
     * 更新计划任务
     *
     * @param schedule 计划任务DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateSchedule(Schedule<Message> schedule) {

    }

    /**
     * 删除计划任务
     *
     * @param id 计划任务ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteSchedule(String id) {

    }
}
