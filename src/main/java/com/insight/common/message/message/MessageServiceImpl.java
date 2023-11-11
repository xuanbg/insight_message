package com.insight.common.message.message;

import com.github.pagehelper.PageHelper;
import com.insight.common.message.common.Core;
import com.insight.common.message.common.MessageDal;
import com.insight.common.message.common.client.AuthClient;
import com.insight.common.message.common.client.RabbitClient;
import com.insight.common.message.common.dto.*;
import com.insight.common.message.common.mapper.MessageMapper;
import com.insight.utils.Json;
import com.insight.utils.ReplyHelper;
import com.insight.utils.SnowflakeCreator;
import com.insight.utils.Util;
import com.insight.utils.pojo.auth.LoginInfo;
import com.insight.utils.pojo.base.BusinessException;
import com.insight.utils.pojo.base.Reply;
import com.insight.utils.pojo.base.Search;
import com.insight.utils.pojo.message.InsightMessage;
import com.insight.utils.pojo.message.Schedule;
import com.insight.utils.pojo.message.SmsCode;
import com.insight.utils.redis.Redis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author 宣炳刚
 * @date 2019-08-28
 * @remark 短信服务
 */
@Service
public class MessageServiceImpl implements MessageService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final SnowflakeCreator creator;
    private final AuthClient client;
    private final MessageDal dal;
    private final MessageMapper mapper;
    private final Core core;

    /**
     * 允许发送匿名短信
     */
    @Value("${insight.sms.allowAnonymity}")
    private boolean allowAnonymity;

    /**
     * 构造方法
     *
     * @param creator 雪花算法ID生成器
     * @param client  Feign客户端
     * @param dal     MessageDal
     * @param mapper  MessageMapper
     * @param core    计划任务异步执行核心类
     */
    public MessageServiceImpl(SnowflakeCreator creator, AuthClient client, MessageDal dal, MessageMapper mapper, Core core) {
        this.creator = creator;
        this.client = client;
        this.dal = dal;
        this.mapper = mapper;
        this.core = core;
    }

    /**
     * 发送短信验证码
     *
     * @param dto 短信DTO
     */
    @Override
    public void seedSmsCode(SmsCode dto) {
        String mobile = dto.getMobile();
        Integer type = dto.getType();
        if (type == 0 && !allowAnonymity) {
            throw new BusinessException("当前设置不允许发送匿名验证码，请联系管理员");
        }

        if (type > 1 && type < 4) {
            var data = new CodeDto(mobile);
            var reply = client.getCode(data);
            if (!reply.getSuccess()){
                throw new BusinessException(reply.getMessage());
            }
        }

        InsightMessage message = new InsightMessage();
        message.setChannel(dto.getChannel());
        message.setReceiver(mobile);
        message.setParams(dto.getParam());
        core.sendSms(message);

        String smsCode = dto.getCode();
        String key = Util.md5(type + mobile + smsCode);
        Redis.set("VerifyCode:" + key, mobile, Long.valueOf(dto.getMinutes()), TimeUnit.MINUTES);
        Redis.add("VerifyCodeSet:" + mobile, key);
        Redis.changeExpire("VerifyCodeSet:" + mobile, Long.valueOf(dto.getMinutes()) * 60);
        logger.info("手机号[{}]的{}类短信验证码为: {}", mobile, type, smsCode);
    }

    /**
     * 验证短信验证码
     *
     * @param key     验证参数,MD5(type + mobile + code)
     * @param isCheck 是否检验模式:true.检验模式,验证后验证码不失效;false.验证模式,验证后验证码失效
     * @return Reply
     */
    @Override
    public String verifySmsCode(String key, Boolean isCheck) {
        String codeKey = "VerifyCode:" + key;
        String mobile = Redis.get(codeKey);
        if (mobile == null || mobile.isEmpty()) {
            throw new BusinessException("验证码错误");
        }

        if (isCheck) {
            // 每验证一次有效时间减少15秒,以避免暴力破解
            Redis.changeExpire(codeKey, -15);
            return mobile;
        }

        // 清理已通过验证的验证码对应手机号的全部验证码
        String setKey = "VerifyCodeSet:" + mobile;
        List<String> keys = Redis.getMembers(setKey);
        for (String k : keys) {
            Redis.deleteKey("VerifyCode:" + k);
        }

        // 清理验证码对应手机号缓存
        Redis.deleteKey(setKey);

        return mobile;
    }

    /**
     * 发送送标准消息
     *
     * @param info 用户关键信息
     * @param dto  标准信息DTO
     */
    @Override
    public void sendNormalMessage(LoginInfo info, NormalMessage dto) {
        Long tenantId = info == null ? null : info.getTenantId();
        Long appId = info == null ? null : info.getAppId();
        TemplateDto template = mapper.getTemplate(tenantId, appId, dto.getSceneCode());
        if (template == null) {
            throw new BusinessException("没有可用消息模板,请检查消息参数是否正确");
        }

        // 处理签名
        String sign = template.getSign();
        Map<String, Object> params = dto.getParams();
        if (sign != null && !sign.isEmpty()) {
            if (params == null) {
                params = new HashMap<>(4);
            }

            params.put("sign", sign);
        }

        // 组装消息
        InsightMessage message = Json.clone(dto, InsightMessage.class);
        message.setTag(template.getTag());
        message.setType(template.getType());
        message.setTitle(template.getTitle());

        String content = assemblyContent(template.getContent(), dto.getParams());
        message.setContent(content);
        message.setParams(params);

        message.setExpire(template.getExpire());
        sendMessage(info, message);
    }

    /**
     * 发送自定义消息
     *
     * @param info 用户关键信息
     * @param dto  标准信息DTO
     */
    @Override
    public void sendCustomMessage(LoginInfo info, CustomMessage dto) {
        InsightMessage message = Json.clone(dto, InsightMessage.class);
        sendMessage(info, message);
    }

    /**
     * 获取用户消息列表
     *
     * @param info   用户关键信息
     * @param search 查询实体类
     * @return Reply
     */
    @Override
    public Reply getUserMessages(LoginInfo info, Search search) {
        search.setOwnerId(info.getId());
        var page = PageHelper.startPage(search.getPageNum(), search.getPageSize())
                .setOrderBy(search.getOrderBy()).doSelectPage(() -> mapper.getMessages(search));

        var total = page.getTotal();
        return total > 0 ? ReplyHelper.success(page.getResult(), total) : ReplyHelper.resultIsEmpty();
    }

    /**
     * 获取用户消息详情
     *
     * @param messageId 消息ID
     * @param userId    用户ID
     * @return Reply
     */
    @Override
    public UserMessageDto getUserMessage(Long messageId, Long userId) {
        UserMessageDto message = mapper.getMessage(messageId, userId);
        if (message == null) {
            throw new BusinessException("ID不存在,未读取数据");
        }

        if (!message.getRead()) {
            dal.readMessage(message.getId(), userId, message.getBroadcast());
            message.setRead(true);
        }

        return message;
    }

    /**
     * 删除用户消息
     *
     * @param messageId 消息ID
     * @param userId    用户ID
     */
    @Override
    public void deleteUserMessage(Long messageId, Long userId) {
        UserMessageDto message = mapper.getMessage(messageId, userId);
        if (message == null) {
            throw new BusinessException("ID不存在,未删除数据");
        }

        if (message.getBroadcast()) {
            if (!message.getRead()) {
                dal.readMessage(message.getId(), userId, true);
            }

            mapper.unsubscribeMessage(messageId, userId);
        } else {
            mapper.deleteUserMessage(messageId, userId);
        }
    }

    /**
     * 发送消息到队列
     *
     * @param info    用户关键信息
     * @param message 消息DTO
     */
    private void sendMessage(LoginInfo info, InsightMessage message) {
        Schedule<InsightMessage> schedule = new Schedule<>();
        schedule.setType(0);
        schedule.setContent(message);
        int type = message.getType();

        // 本地消息
        if (1 == (type & 1) && info != null) {
            message.setId(creator.nextId(0));
            message.setTenantId(info.getTenantId());
            message.setAppId(info.getAppId());
            message.setCreator(info.getName());
            message.setCreatorId(info.getId());

            message.setCreatedTime(LocalDateTime.now());
            schedule.setMethod("addMessage");
            RabbitClient.sendTopic("schedule.message", schedule);
        }

        // 推送通知
        if (2 == (type & 2)) {
            schedule.setMethod("pushNotice");
            RabbitClient.sendTopic("schedule.message", schedule);
        }

        // 发送短信
        if (4 == (type & 4)) {
            schedule.setMethod("sendSms");
            RabbitClient.sendTopic("schedule.message", schedule);
        }

        // 发送邮件
        if (8 == (type & 8)) {
            schedule.setMethod("sendMail");
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
