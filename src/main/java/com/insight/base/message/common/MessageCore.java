package com.insight.base.message.common;

import com.insight.base.message.common.client.RabbitClient;
import com.insight.base.message.common.client.TaskClient;
import com.insight.base.message.common.dto.TemplateDto;
import com.insight.base.message.common.entity.InsightMessage;
import com.insight.util.Generator;
import com.insight.util.ReplyHelper;
import com.insight.util.pojo.*;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.rabbitmq.client.Channel;
import feign.Feign;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author 宣炳刚
 * @date 2019-08-31
 * @remark 消息核心类
 */
@Component
@Import(FeignClientsConfiguration.class)
public class MessageCore {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Decoder decoder = new JacksonDecoder();
    private Encoder encoder = new JacksonEncoder();
    private final MessageDal dal;
    private EurekaClient client;

    /**
     * 构造方法
     *
     * @param dal    MessageMapper
     * @param client EurekaClient
     */
    public MessageCore(MessageDal dal, EurekaClient client) {
        this.dal = dal;
        this.client = client;
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
     * 保存消息到数据库,使用补偿机制保证写入成功
     *
     * @param schedule 计划任务DTO
     */
    @Async
    public void addMessage(Schedule<InsightMessage> schedule, Channel channel, Message message) throws IOException {
        InsightMessage msg = schedule.getContent();
        if (msg == null) {
            return;
        }

        try {
            // 存储消息
            dal.addMessage(msg);
        } catch (Exception ex) {
            // 任务失败后保存计划任务数据进行补偿
            logger.error("存储消息发生错误! 异常信息为: {}", ex.getMessage());
            addSchedule(schedule);
        } finally {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }

    /**
     * 通过极光推送消息通知给用户,使用补偿机制保证推送成功
     *
     * @param schedule 计划任务DTO
     */
    @Async
    public void pushNotice(Schedule<InsightMessage> schedule, Channel channel, Message message) throws IOException {
        InsightMessage msg = schedule.getContent();
        if (msg != null && !push(msg)) {
            addSchedule(schedule);
        }

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 发送短信给用户,使用补偿机制保证发送成功
     *
     * @param schedule 计划任务DTO
     */
    @Async
    public void sendSms(Schedule<InsightMessage> schedule, Channel channel, Message message) throws IOException {
        InsightMessage msg = schedule.getContent();
        if (msg != null && !send(msg)) {
            addSchedule(schedule);
        }

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 本地调用,使用补偿机制保证调用成功
     *
     * @param schedule 计划任务DTO
     */
    @Async
    public void localCall(Schedule<ScheduleCall> schedule, Channel channel, Message message) throws IOException, URISyntaxException {
        ScheduleCall call = schedule.getContent();
        URI uri = new URI(call.getUrl());
        Object body = call.getBody();
        TaskClient taskClient = Feign.builder().decoder(decoder).encoder(encoder)
                .requestInterceptor(template -> {
                    Map<String, String> map = call.getHeaders();
                    for (String k : map.keySet()) {
                        String v = map.get(k);
                        template.header(k, v);
                    }
                }).target(TaskClient.class, getServiceUrl(call.getService()));
        try {
            Reply reply;
            switch (call.getMethod()) {
                case "GET":
                    reply = taskClient.get(uri);
                    break;
                case "POST":
                    reply = taskClient.post(uri, body);
                    break;
                case "PUT":
                    reply = taskClient.put(uri, body);
                    break;
                case "DELETE":
                    reply = taskClient.delete(uri, body);
                    break;
                default:
                    return;
            }
            if (!reply.getSuccess()) {
                logger.error("本地调用发生错误! 错误信息为: {}", reply.getMessage());
                addSchedule(schedule);
            }
        } catch (Exception ex) {
            logger.error("本地调用发生错误! 异常信息为: {}", ex.getMessage());
            addSchedule(schedule);
        } finally {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }

    public void remoteCall(Schedule<ScheduleCall> schedule, Channel channel, Message message) {

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
            InsightMessage message = new InsightMessage();
            message.setReceivers(list);
            message.setContent(now.toString() + "保存任务失败! 请尽快处理");

            send(message);
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

    /**
     * 推送消息
     *
     * @param message 消息DTO
     */
    private boolean push(InsightMessage message) {
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
    private boolean send(InsightMessage message) {
        try {
            return false;
        } catch (Exception ex) {
            logger.error("发送短信发生错误! 异常信息为: {}", ex.getMessage());
            return false;
        }
    }

    /**
     * 构建URI
     *
     * @param service 服务名
     * @param url     接口URL
     * @return URI
     */
    private URI buildUri(String service, String url) {
        try {
            return new URI(url);
        } catch (URISyntaxException e) {
            return null;
        }
    }

    /**
     * 根据eureka服务名获取服务地址
     *
     * @param service 服务名
     * @return URL
     */
    private String getServiceUrl(String service) {
        try {
            InstanceInfo instanceInfo = client.getNextServerFromEureka(service, false);
            String host = instanceInfo.getHomePageUrl();

            return host.substring(0, host.length() - 1);
        } catch (Exception ex) {
            logger.error("从eureka 获取服务：{}的地址出现异常：{}", service, ex.getMessage());

            return null;
        }
    }
}
