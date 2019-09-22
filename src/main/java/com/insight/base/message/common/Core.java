package com.insight.base.message.common;

import com.insight.base.message.common.entity.Message;
import com.insight.base.message.common.entity.Template;
import com.insight.base.message.common.mapper.MessageMapper;
import com.insight.util.Generator;
import com.insight.util.ReplyHelper;
import com.insight.util.pojo.CustomMessage;
import com.insight.util.pojo.LoginInfo;
import com.insight.util.pojo.NormalMessage;
import com.insight.util.pojo.Reply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * @author 宣炳刚
 * @date 2019-08-31
 * @remark 核心类
 */
@Component
public class Core {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final MessageMapper mapper;

    /**
     * 构造方法
     *
     * @param mapper MessageMapper
     */
    public Core(MessageMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * 发送消息
     *
     * @param info 用户关键信息
     * @param dto  通用消息DTO
     * @return Reply
     */
    public Reply sendMessage(LoginInfo info, NormalMessage dto) {
        String tenantId = info.getTenantId();
        String appId = dto.getAppId();
        Template template = mapper.getTemplate(tenantId, dto.getSceneCode(), appId, dto.getChannelCode());
        if (template == null) {
            return ReplyHelper.fail("没有可用消息模板,请检查消息参数是否正确");
        }

        // 组装消息
        Message message = new Message();
        message.setId(Generator.uuid());
        message.setTenantId(tenantId);
        message.setAppId(appId);
        message.setTag(template.getTag());
        message.setType(template.getType());
        message.setTitle(template.getTitle());

        Integer expire = template.getExpire();
        LocalDateTime now = LocalDateTime.now();
        if (expire != null && expire > 0) {
            message.setExpireDate(now.plusHours(expire).toLocalDate());
        }

        String content = assemblyMessage(template.getContent(), dto.getParams());
        String[] receivers = dto.getReceivers().split(",");
        Boolean isBroadcast = dto.getBroadcast();
        message.setContent(content);
        message.setReceivers(new ArrayList<>(Arrays.asList(receivers)));
        message.setBroadcast(isBroadcast == null ? false : isBroadcast);
        message.setDeptId(info.getDeptId());
        message.setCreator(info.getUserName());
        message.setCreatorId(info.getUserId());
        message.setCreatedTime(now);

        mapper.addMessage(message);
        send(message);

        return ReplyHelper.success();
    }

    /**
     * 发送消息
     *
     * @param info 用户关键信息
     * @param dto  自定义消息DTO
     * @return Reply
     */
    public Reply sendMessage(LoginInfo info, CustomMessage dto) {
        // 组装消息
        Message message = new Message();
        message.setId(Generator.uuid());
        message.setTenantId(info.getTenantId());
        message.setAppId(dto.getAppId());
        message.setTag(dto.getTag());
        message.setType(dto.getType());

        String[] receivers = dto.getReceivers().split(",");
        message.setReceivers(new ArrayList<>(Arrays.asList(receivers)));
        message.setTitle(dto.getTitle());
        message.setContent(dto.getContent());
        message.setExpireDate(dto.getExpireDate());
        message.setBroadcast(dto.getBroadcast());
        message.setDeptId(info.getDeptId());
        message.setCreator(info.getUserName());
        message.setCreatorId(info.getUserId());
        message.setCreatedTime(LocalDateTime.now());

        mapper.addMessage(message);
        send(message);

        return ReplyHelper.success();
    }

    /**
     * 组装消息
     *
     * @param template 消息模板
     * @param params   消息参数
     * @return 消息内容
     */
    private String assemblyMessage(String template, Map params) {
        for (Object k : params.keySet()) {
            Object v = params.get(k);
            if (v == null) {
                continue;
            }

            String key = "\\{" + k.toString() + "}";
            template = template.replaceAll(key, v.toString());
        }

        return template;
    }

    /**
     * 发送短信
     *
     * @param message 消息DTO
     */
    private void send(Message message) {

    }
}
