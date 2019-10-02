package com.insight.base.message.common;

import com.insight.base.message.common.client.RabbitClient;
import com.insight.base.message.common.dto.TemplateDto;
import com.insight.base.message.common.entity.InsightMessage;
import com.insight.base.message.common.mapper.MessageMapper;
import com.insight.util.Generator;
import com.insight.util.ReplyHelper;
import com.insight.util.pojo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 宣炳刚
 * @date 2019-08-31
 * @remark 消息核心类
 */
@Component
public class MessageCore {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final MessageMapper mapper;

    /**
     * 构造方法
     *
     * @param mapper MessageMapper
     */
    public MessageCore(MessageMapper mapper) {
        this.mapper = mapper;
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
        TemplateDto template = mapper.getTemplate(tenantId, dto.getSceneCode(), appId, dto.getChannelCode());
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
        InsightMessage message = new InsightMessage();
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
        InsightMessage message = new InsightMessage();
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
    public void sendMessage(LoginInfo info, InsightMessage message) {
        Schedule<InsightMessage> schedule = new Schedule<>();
        schedule.setType(0);
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
            RabbitClient.sendTopic("schedule.message", schedule);
        }

        // 极光推送
        if (2 == (type & 2)) {
            schedule.setMethod("pushNotice");
            RabbitClient.sendTopic("schedule.message", schedule);
        }

        // 发送短信
        if (4 == (type & 4)) {
            schedule.setMethod("sendSms");
            RabbitClient.sendTopic("schedule.message", schedule);
        }
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
}
