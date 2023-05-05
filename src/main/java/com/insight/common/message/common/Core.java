package com.insight.common.message.common;

import com.insight.common.message.common.client.AliyunClient;
import com.insight.common.message.common.client.TaskClient;
import com.insight.common.message.common.dto.ScheduleCall;
import com.insight.utils.SnowflakeCreator;
import com.insight.utils.http.HttpUtil;
import com.insight.utils.pojo.base.BusinessException;
import com.insight.utils.pojo.base.Reply;
import com.insight.utils.pojo.message.InsightMessage;
import com.insight.utils.pojo.message.Schedule;
import com.insight.utils.redis.Redis;
import com.rabbitmq.client.Channel;
import feign.Feign;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author 宣炳刚
 * @date 2019/10/2
 * @remark 计划任务异步执行核心类
 */
@Component
@Import(FeignClientsConfiguration.class)
public class Core {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Decoder decoder = new JacksonDecoder();
    private final Encoder encoder = new JacksonEncoder();
    private final DiscoveryClient discoveryClient;
    private final JavaMailSender mailSender;
    private final SnowflakeCreator creator;
    private final MessageDal dal;

    /**
     * 邮件发件人
     */
    @Value("${insight.mail.sender}")
    private String sender;

    /**
     * 默认短信通道
     */
    @Value("${insight.sms.channel}")
    private String defaultChannel;

    /**
     * 构造方法
     *
     * @param discoveryClient DiscoveryClient
     * @param mailSender      JavaMailSender
     * @param creator         雪花算法ID生成器
     * @param dal             MessageDal
     */
    public Core(DiscoveryClient discoveryClient, JavaMailSender mailSender, SnowflakeCreator creator, MessageDal dal) {
        this.discoveryClient = discoveryClient;
        this.mailSender = mailSender;
        this.creator = creator;
        this.dal = dal;
    }

    /**
     * 保存消息到数据库,使用补偿机制保证写入成功
     *
     * @param schedule 计划任务DTO
     * @param channel  队列通道
     * @param message  队列消息
     */
    @Async
    public void addMessage(Schedule<InsightMessage> schedule, Channel channel, Message message) throws IOException {
        InsightMessage msg = schedule.getContent();
        if (msg != null && !addMessage(msg)) {
            schedule.setExpireTime(LocalDateTime.now().plusMinutes(msg.getExpire()));
            addSchedule(schedule);
        }

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 通过极光推送消息通知给用户,使用补偿机制保证推送成功
     *
     * @param schedule 计划任务DTO
     * @param channel  队列通道
     * @param message  队列消息
     */
    @Async
    public void pushNotice(Schedule<InsightMessage> schedule, Channel channel, Message message) throws IOException {
        InsightMessage msg = schedule.getContent();
        if (msg != null && !pushNotice(msg)) {
            schedule.setExpireTime(LocalDateTime.now().plusMinutes(msg.getExpire()));
            addSchedule(schedule);
        }

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 发送短信给用户,使用补偿机制保证发送成功
     *
     * @param schedule 计划任务DTO
     * @param channel  队列通道
     * @param message  队列消息
     */
    @Async
    public void sendSms(Schedule<InsightMessage> schedule, Channel channel, Message message) throws IOException {
        InsightMessage msg = schedule.getContent();
        if (msg != null && !sendSms(msg)) {
            schedule.setExpireTime(LocalDateTime.now().plusMinutes(msg.getExpire()));
            addSchedule(schedule);
        }

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 发送邮件给用户,使用补偿机制保证发送成功
     *
     * @param schedule 计划任务DTO
     * @param channel  队列通道
     * @param message  队列消息
     */
    @Async
    public void sendMail(Schedule<InsightMessage> schedule, Channel channel, Message message) throws IOException {
        InsightMessage msg = schedule.getContent();
        if (msg != null && !sendMail(msg)) {
            schedule.setExpireTime(LocalDateTime.now().plusMinutes(msg.getExpire()));
            addSchedule(schedule);
        }

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 本地调用,使用补偿机制保证调用成功
     *
     * @param schedule 计划任务DTO
     * @param channel  队列通道
     * @param message  队列消息
     */
    @Async
    public void localCall(Schedule<ScheduleCall> schedule, Channel channel, Message message) throws IOException, URISyntaxException {
        ScheduleCall call = schedule.getContent();
        String host = getServiceUrl(call.getService());
        URI uri = new URI(host + call.getUrl());
        Object body = call.getBody();
        TaskClient taskClient = Feign.builder().decoder(decoder).encoder(encoder)
                .requestInterceptor(template -> {
                    Map<String, String> map = call.getHeaders();
                    if (map != null) {
                        for (String k : map.keySet()) {
                            String v = map.get(k);
                            template.header(k, v);
                        }
                    }
                }).target(TaskClient.class, host);
        try {
            Reply reply;
            switch (call.getMethod()) {
                case "GET" -> reply = taskClient.get(uri);
                case "POST" -> reply = taskClient.post(uri, body);
                case "PUT" -> reply = taskClient.put(uri, body);
                case "DELETE" -> reply = taskClient.delete(uri, body);
                default -> {
                    return;
                }
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

    /**
     * 从队列订阅远程调用类型的计划任务
     *
     * @param schedule 计划任务DTO
     * @param channel  队列通道
     * @param message  队列消息
     */
    @Async
    public void remoteCall(Schedule<ScheduleCall> schedule, Channel channel, Message message) throws IOException {
        ScheduleCall call = schedule.getContent();
        String method = call.getMethod();
        String url = call.getUrl();
        Map<String, String> headers = call.getHeaders();
        Object body = call.getBody();
        try {
            String result;
            switch (method) {
                case "GET" -> result = HttpUtil.get(url, headers, String.class);
                case "POST" -> result = HttpUtil.post(url, body, headers, String.class);
                case "PUT" -> result = HttpUtil.put(url, body, headers, String.class);
                case "DELETE" -> result = HttpUtil.delete(url, body, headers, String.class);
                default -> {
                    return;
                }
            }
            if (result == null || result.isEmpty() || result.contains("error")) {
                logger.error("本地调用发生错误! 错误信息为: {}", result);
                addSchedule(schedule);
            }
        } catch (Exception ex) {
            logger.error("本地调用发生错误! 异常信息为: {}", ex.getMessage());
            addSchedule(schedule);
        } finally {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }

    /**
     * 保存计划任务数据
     *
     * @param schedule 计划任务DTO
     */
    private void addSchedule(Schedule schedule) {
        var expireTime = schedule.getExpireTime();
        LocalDateTime now = LocalDateTime.now();
        Long id = schedule.getId();
        if (id == null) {
            schedule.setId(creator.nextId(3));
            schedule.setTaskTime(now.plusSeconds(10));
            schedule.setCount(0);
            schedule.setInvalid(false);
            schedule.setCreatedTime(now);
        } else {
            int count = schedule.getCount();
            if (count > 99 || (expireTime != null && now.isAfter(expireTime))) {
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
            logger.warn("保存任务失败! 任务数据为: {}", schedule);

            String key = "Config:Warning";
            boolean isWarning = Redis.hasKey(key);
            if (isWarning) {
                return;
            }

            List<String> list = new ArrayList<>();
            list.add("13958085903");
            InsightMessage message = new InsightMessage();
            message.setReceivers(list);
            message.setContent(now + "保存任务失败! 请尽快处理");

            Redis.set(key, now.toString(), 24L, TimeUnit.HOURS);
            if (!sendSms(message)) {
                throw new BusinessException("短信发送失败，请稍后重试");
            }
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
            List<ServiceInstance> list = discoveryClient.getInstances(service);
            if (list == null || list.isEmpty()) {
                return null;
            }

            URI uri = list.get(0).getUri();
            return uri.toString();
        } catch (Exception ex) {
            logger.error("从eureka 获取服务：{}的地址出现异常：{}", service, ex.getMessage());

            return null;
        }
    }

    /**
     * 存储消息
     *
     * @param message 消息DTO
     * @return 是否存储成功
     */
    private boolean addMessage(InsightMessage message) {
        try {
            dal.addMessage(message);
            return true;
        } catch (Exception ex) {
            logger.error("存储消息发生错误! 异常信息为: {}", ex.getMessage());
            return false;
        }
    }

    /**
     * 推送通知
     *
     * @param message 消息DTO
     * @return 是否推送成功
     */
    private boolean pushNotice(InsightMessage message) {
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
     * @return 是否发送成功
     */
    public boolean sendSms(InsightMessage message) {
        var receivers = message.getReceivers();
        if (receivers == null || receivers.isEmpty()) {
            throw new BusinessException("短信接收人手机号不能为空");
        }

        try {
            var channel = message.getChannel() == null ? defaultChannel : message.getChannel();
            if (receivers.size() > 1) {
                // 群发
                switch (channel) {
                    case "aliyun" -> {
                    }
                    case "a" -> throw new BusinessException("");
                    default -> throw new BusinessException("不存在的短信通道");

                }
            } else {
                // 单发
                var phone = receivers.get(0);
                switch (channel) {
                    case "aliyun" -> AliyunClient.sendTemplateMessage(phone, "SMS_254755310", message.getParams(), "学堡");
                    case "a" -> throw new BusinessException("");
                    default -> throw new BusinessException("不存在的短信通道");
                }
            }
            return true;
        } catch (Exception ex) {
            logger.error("发送短信发生错误! 异常信息为: {}", ex.getMessage());
            return false;
        }
    }

    /**
     * 发送邮件
     *
     * @param message 消息DTO
     * @return 是否发送成功
     */
    private boolean sendMail(InsightMessage message) {
        try {
            List<String> list = message.getReceivers();
            String receivers = String.join(";", list);
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(sender);
            mail.setTo(receivers);
            mail.setSubject(message.getTitle());
            mail.setText(message.getContent());

            mailSender.send(mail);
            return true;
        } catch (Exception ex) {
            logger.error("发送邮件发生错误! 异常信息为: {}", ex.getMessage());
            return false;
        }
    }
}
