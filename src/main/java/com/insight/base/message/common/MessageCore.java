package com.insight.base.message.common;

import com.insight.base.message.common.dto.TemplateDto;
import com.insight.base.message.common.entity.Message;
import com.insight.util.Generator;
import com.insight.util.ReplyHelper;
import com.insight.util.pojo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author 宣炳刚
 * @date 2019-08-31
 * @remark 消息核心类
 */
@Component
public class MessageCore {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final MessageDal dal;

    /**
     * 构造方法
     *
     * @param dal MessageMapper
     */
    public MessageCore(MessageDal dal) {
        this.dal = dal;
    }

    /**
     * 发送标准化消息,负责组装消息对象
     *
     * @param info 用户关键信息
     * @param dto  通用消息DTO
     * @return Reply
     */
    public Reply sendMessage(LoginInfo info, NormalMessage dto) {
        String tenantId = info == null ? null : info.getTenantId();
        String appId = dto.getAppId();
        TemplateDto template = dal.getTemplate(tenantId, dto.getSceneCode(), appId, dto.getChannelCode());
        if (template == null) {
            return ReplyHelper.fail("没有可用消息模板,请检查消息参数是否正确");
        }

        // 处理签名
        String sign = template.getSign();
        if (sign != null && !sign.isEmpty()) {
            Map<String, Object> params = dto.getParams();
            if (params == null) {
                params = new HashMap<>(4);
            }

            params.put("sign", sign);
        }

        // 组装消息
        Message message = new Message();
        message.setAppId(appId);
        message.setTag(template.getTag());
        message.setType(template.getType());
        message.setTitle(template.getTitle());

        String content = assemblyContent(template.getContent(), dto.getParams());
        message.setContent(content);

        String[] receivers = dto.getReceivers().split(",");
        message.setReceivers(new ArrayList<>(Arrays.asList(receivers)));

        Integer expire = template.getExpire();
        LocalDateTime now = LocalDateTime.now();
        if (expire != null && expire > 0) {
            message.setExpireDate(now.plusHours(expire).toLocalDate());
        }

        message.setBroadcast(dto.getBroadcast());
        sendMessage(info, message);

        return ReplyHelper.success();
    }

    /**
     * 发送自定义消息,负责组装消息对象
     *
     * @param info 用户关键信息
     * @param dto  自定义消息DTO
     * @return Reply
     */
    public Reply sendMessage(LoginInfo info, CustomMessage dto) {
        // 组装消息
        Message message = new Message();
        message.setAppId(dto.getAppId());
        message.setTag(dto.getTag());
        message.setType(dto.getType());

        String[] receivers = dto.getReceivers().split(",");
        message.setReceivers(new ArrayList<>(Arrays.asList(receivers)));
        message.setTitle(dto.getTitle());
        message.setContent(dto.getContent());
        message.setParams(dto.getParams());
        message.setExpireDate(dto.getExpireDate());
        message.setBroadcast(dto.getBroadcast());
        sendMessage(info, message);

        return ReplyHelper.success();
    }

    /**
     * 根据发送类型调用相应方法异步发送消息
     *
     * @param info    用户关键信息
     * @param message 消息DTO
     */
    public void sendMessage(LoginInfo info, Message message) {
        Schedule<Message> schedule = new Schedule<>();
        schedule.setContent(message);
        int type = message.getType();

        // 本地消息
        if (1 == (type & 1)) {
            message.setId(Generator.uuid());
            if (info != null) {
                message.setTenantId(info.getTenantId());
                message.setDeptId(info.getDeptId());
                message.setCreator(info.getUserName());
                message.setCreatorId(info.getUserId());
            }

            message.setCreatedTime(LocalDateTime.now());
            schedule.setMethod("addMessage");
            addMessage(schedule);
        }

        // 极光推送
        if (2 == (type & 2)) {
            schedule.setMethod("pushNotice");
            pushNotice(schedule);
        }

        // 发送短信
        if (4 == (type & 4)) {
            schedule.setMethod("sendSms");
            sendSms(schedule);
        }
    }

    /**
     * 保存消息到数据库,使用补偿机制保证写入成功
     *
     * @param schedule 计划任务DTO
     */
    @Async
    public void addMessage(Schedule<Message> schedule) {
        String id = schedule.getId();
        if (id != null && !id.isEmpty()) {
            dal.deleteSchedule(schedule.getId());
        }

        Message message = schedule.getContent();
        if (message == null) {
            return;
        }

        try {
            // 存储消息
            dal.addMessage(message);
        } catch (Exception ex) {
            // 任务失败后保存计划任务数据进行补偿
            logger.error("存储消息数据错误! 异常信息为: {}", ex.getMessage());
            addSchedule(schedule);
        }
    }

    /**
     * 通过极光推送消息通知给用户,使用补偿机制保证推送成功
     *
     * @param schedule 计划任务DTO
     */
    @Async
    public void pushNotice(Schedule<Message> schedule) {
        String id = schedule.getId();
        if (id != null && !id.isEmpty()) {
            dal.deleteSchedule(schedule.getId());
        }

        Message message = schedule.getContent();
        if (message == null || push(message)) {
            return;
        }

        addSchedule(schedule);
    }

    /**
     * 发送短信给用户,使用补偿机制保证发送成功
     *
     * @param schedule 计划任务DTO
     */
    @Async
    public void sendSms(Schedule<Message> schedule) {
        String id = schedule.getId();
        if (id != null && !id.isEmpty()) {
            dal.deleteSchedule(schedule.getId());
        }

        Message message = schedule.getContent();
        if (message == null || send(message)) {
            return;
        }

        addSchedule(schedule);
    }

    /**
     * 使用模板组装消息内容
     *
     * @param template 内容模板
     * @param params   消息参数
     * @return 消息内容
     */
    private String assemblyContent(String template, Map<String, Object> params) {
        for (String k : params.keySet()) {
            Object v = params.get(k);
            if (v == null) {
                continue;
            }

            String key = "\\{" + k + "}";
            template = template.replaceAll(key, v.toString());
        }

        return template;
    }

    /**
     * 推送消息
     *
     * @param message 消息DTO
     */
    private boolean push(Message message) {
        try {
            return true;
        } catch (Exception ex) {
            logger.error("推送消息发生错误! 异常信息为: {}", ex.getMessage());
            return false;
        }
    }

    /**
     * 发送短信
     *
     * @param message 消息DTO
     */
    private boolean send(Message message) {
        try {
            return false;
        } catch (Exception ex) {
            logger.error("发生短信发送错误! 异常信息为: {}", ex.getMessage());
            return false;
        }
    }

    /**
     * 保存计划任务数据
     *
     * @param schedule 计划任务DTO
     */
    private void addSchedule(Schedule schedule) {
        LocalDateTime now = LocalDateTime.now();
        String id = schedule.getId();
        if (id == null || id.isEmpty()) {
            schedule.setId(Generator.uuid());
            schedule.setTaskTime(now.plusSeconds(10));
            schedule.setCount(0);
            schedule.setInvalid(false);
            schedule.setCreatedTime(now);
        } else {
            int count = schedule.getCount();
            if (count > 99) {
                schedule.setInvalid(true);
            } else {
                schedule.setTaskTime(now.plusSeconds((long) Math.pow(count, 2)));
                schedule.setCount(count + 1);
            }
        }

        try {
            // 保存计划任务数据
            dal.addSchedule(schedule);
        } catch (Exception ex) {
            // 保存计划任务数据失败,记录日志并发短信通知运维人员
            logger.warn("保存任务失败! 任务数据为: {}", schedule.toString());

            List<String> list = new ArrayList<>();
            list.add("13958085903");
            Message message = new Message();
            message.setReceivers(list);
            message.setContent(now.toString() + "保存任务失败! 请尽快处理");

            send(message);
        }
    }
}
