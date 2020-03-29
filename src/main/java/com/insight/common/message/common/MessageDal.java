package com.insight.common.message.common;

import com.insight.common.message.common.entity.InsightMessage;
import com.insight.common.message.common.entity.PushMessage;
import com.insight.common.message.common.entity.SubscribeMessage;
import com.insight.common.message.common.mapper.MessageMapper;
import com.insight.util.Generator;
import com.insight.util.pojo.Log;
import com.insight.util.pojo.LoginInfo;
import com.insight.util.pojo.OperateType;
import com.insight.util.pojo.Schedule;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.insight.util.Generator.uuid;

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
     * 获取消息模板编码
     *
     * @param tenantId 租户ID
     * @return 消息模板编码
     */
    public String newCode(String tenantId) {
        String group = "Template" + (tenantId == null ? "" : ":" + tenantId);
        while (true) {
            String code = Generator.newCode("#4", group, false);
            int count = mapper.getTemplateCount(tenantId, code);
            if (count > 0) {
                continue;
            }

            return code;
        }
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
            message.setId(uuid());
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
            push.setId(uuid());
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
            subscribe.setId(uuid());
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

    /**
     * 记录操作日志
     *
     * @param info     用户关键信息
     * @param type     操作类型
     * @param business 业务名称
     * @param id       业务ID
     * @param content  日志内容
     */
    @Async
    public void writeLog(LoginInfo info, OperateType type, String business, String id, Object content) {
        Log log = new Log();
        log.setId(uuid());
        log.setTenantId(info.getTenantId());
        log.setType(type);
        log.setBusiness(business);
        log.setBusinessId(id);
        log.setContent(content);
        log.setDeptId(info.getDeptId());
        log.setCreator(info.getUserName());
        log.setCreatorId(info.getUserId());
        log.setCreatedTime(LocalDateTime.now());

        mapper.addLog(log);
    }

    /**
     * 获取操作日志列表
     *
     * @param tenantId 租户ID
     * @param business 业务类型
     * @param key      查询关键词
     * @return 操作日志列表
     */
    public List<Log> getLogs(String tenantId, String business, String key) {
        return mapper.getLogs(tenantId, business, key);
    }

    /**
     * 获取操作日志
     *
     * @param id 日志ID
     * @return 操作日志
     */
    public Log getLog(String id) {
        return mapper.getLog(id);
    }
}