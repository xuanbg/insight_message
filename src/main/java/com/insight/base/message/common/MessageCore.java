package com.insight.base.message.common;

import com.insight.base.message.common.dto.TemplateDto;
import com.insight.base.message.common.entity.Message;
import com.insight.base.message.common.entity.Schedule;
import com.insight.util.Generator;
import com.insight.util.ReplyHelper;
import com.insight.util.pojo.CustomMessage;
import com.insight.util.pojo.LoginInfo;
import com.insight.util.pojo.NormalMessage;
import com.insight.util.pojo.Reply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

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
        String appId = dto.getAppId();
        TemplateDto template = dal.getTemplate(info.getTenantId(), dto.getSceneCode(), appId, dto.getChannelCode());
        if (template == null) {
            return ReplyHelper.fail("没有可用消息模板,请检查消息参数是否正确");
        }

        String sign = template.getSign();
        Map<String, Object> params = dto.getParams();
        if (sign != null && !sign.isEmpty()) {
            params.put("sign", sign);
        }

        // 组装消息
        Message message = new Message();
        message.setAppId(appId);
        message.setTag(template.getTag());
        message.setType(template.getType());
        message.setTitle(template.getTitle());

        Integer expire = template.getExpire();
        LocalDateTime now = LocalDateTime.now();
        if (expire != null && expire > 0) {
            message.setExpireDate(now.plusHours(expire).toLocalDate());
        }

        String content = assemblyContent(template.getContent(), dto.getParams());
        String[] receivers = dto.getReceivers().split(",");
        message.setContent(content);
        message.setReceivers(new ArrayList<>(Arrays.asList(receivers)));
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
        int type = message.getType();

        // 本地消息
        if (1 == (type & 1)) {
            message.setId(Generator.uuid());
            message.setTenantId(info.getTenantId());
            message.setDeptId(info.getDeptId());
            message.setCreator(info.getUserName());
            message.setCreatorId(info.getUserId());
            message.setCreatedTime(LocalDateTime.now());
            addMessage(message, null);
        }

        // 极光推送
        if (2 == (type & 2)) {
            pushNotice(message, null);
        }

        // 发送短信
        if (4 == (type & 4)) {
            sendSms(message, null);
        }
    }

    /**
     * 保存消息到数据库,使用补偿机制保证写入成功
     *
     * @param message  消息DTO
     * @param schedule 计划任务DTO
     */
    @Async
    public void addMessage(Message message, Schedule schedule) {
        if (schedule != null) {
            dal.deleteSchedule(schedule.getId());
        }

        // 存储消息
        if (dal.addMessage(message)) {
            return;
        }

        // 任务失败后保存计划任务数据进行补偿
        addSchedule("addMessage", message, schedule);
    }

    /**
     * 通过极光推送消息通知给用户,使用补偿机制保证推送成功
     *
     * @param message  消息DTO
     * @param schedule 计划任务DTO
     */
    @Async
    public void pushNotice(Message message, Schedule schedule) {
        if (schedule != null) {
            dal.deleteSchedule(schedule.getId());
        }

        // 推送消息

        // 任务失败后保存计划任务数据进行补偿
        addSchedule("pushNotice", message, schedule);
    }

    /**
     * 发送短信给用户,使用补偿机制保证发送成功
     *
     * @param message  消息DTO
     * @param schedule 计划任务DTO
     */
    @Async
    public void sendSms(Message message, Schedule schedule) {
        if (schedule != null) {
            dal.deleteSchedule(schedule.getId());
        }

        // 发送短信

        // 任务失败后保存计划任务数据进行补偿
        addSchedule("sendSms", message, schedule);
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
     * 保存计划任务数据
     *
     * @param method   调用方法
     * @param content  任务内容
     * @param schedule 计划任务DTO
     */
    private void addSchedule(String method, Object content, Schedule schedule) {
        if (schedule == null) {
            schedule = initSchedule(method, content);
        } else {
            updateSchedule(schedule);
        }

        if (dal.addSchedule(schedule)) {
            return;
        }

        // 保存计划任务数据失败,记录日志并发短信通知运维人员
        logger.warn("保存任务失败! 任务数据为: {}", schedule.toString());
    }

    /**
     * 构造计划任务DTO
     *
     * @param method  调用方法
     * @param content 任务内容
     * @return 计划任务DTO
     */
    private Schedule initSchedule(String method, Object content) {
        LocalDateTime now = LocalDateTime.now();
        Schedule schedule = new Schedule();
        schedule.setId(Generator.uuid());
        schedule.setMethod(method);
        schedule.setTaskTime(now.plusSeconds(10));
        schedule.setContent(content);
        schedule.setCount(0);
        schedule.setCreatedTime(now);

        return schedule;
    }

    /**
     * 更新计划任务DTO
     *
     * @param schedule 计划任务DTO
     */
    private void updateSchedule(Schedule schedule) {
        int count = schedule.getCount();
        if (count > 99) {
            schedule.setInvalid(true);
            return;
        }

        schedule.setTaskTime(LocalDateTime.now().plusSeconds((long) Math.pow(count, 2)));
        schedule.setCount(count + 1);
    }
}
