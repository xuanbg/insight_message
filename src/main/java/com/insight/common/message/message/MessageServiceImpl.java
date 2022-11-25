package com.insight.common.message.message;

import com.github.pagehelper.PageHelper;
import com.insight.common.message.common.MessageDal;
import com.insight.common.message.common.client.RabbitClient;
import com.insight.common.message.common.dto.*;
import com.insight.common.message.common.entity.InsightMessage;
import com.insight.common.message.common.mapper.MessageMapper;
import com.insight.utils.Redis;
import com.insight.utils.ReplyHelper;
import com.insight.utils.SnowflakeCreator;
import com.insight.utils.Util;
import com.insight.utils.pojo.SmsCode;
import com.insight.utils.pojo.auth.LoginInfo;
import com.insight.utils.pojo.base.Reply;
import com.insight.utils.pojo.base.Search;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
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
    private final MessageDal dal;
    private final MessageMapper mapper;

    /**
     * 构造方法
     *
     * @param creator 雪花算法ID生成器
     * @param dal     MessageDal
     * @param mapper  MessageMapper
     */
    public MessageServiceImpl(SnowflakeCreator creator, MessageDal dal, MessageMapper mapper) {
        this.creator = creator;
        this.dal = dal;
        this.mapper = mapper;
    }

    /**
     * 生成短信验证码
     *
     * @param info 用户关键信息
     * @param dto  验证码DTO
     * @return Reply
     */
    @Override
    public Reply seedSmsCode(LoginInfo info, SmsCode dto) {
        Integer type = dto.getType();
        String mobile = dto.getMobile();
        String smsCode = Util.randomString(dto.getLength());
        Map<String, Object> map = new HashMap<>(4);
        map.put("code", smsCode);
        map.put("minutes", dto.getMinutes());

        String sceneCode = switch (type) {
            case 0 -> "0002";
            case 1 -> "0003";
            case 2 -> "0004";
            case 3 -> "0005";
            case 4 -> "0006";
            default -> "0000";
        };

        // 组装标准消息
        NormalMessage message = new NormalMessage();
        message.setSceneCode(sceneCode);
        message.setReceivers(mobile);
        message.setParams(map);
        message.setBroadcast(false);
        Reply reply = sendNormalMessage(info, message);
        if (!reply.getSuccess()) {
            return reply;
        }

        String key = Util.md5(type + mobile + smsCode);
        Redis.set("VerifyCode:" + key, mobile, Long.valueOf(dto.getMinutes()), TimeUnit.MINUTES);
        Redis.add("VerifyCodeSet:" + mobile, key, 1800L);
        logger.info("手机号[{}]的{}类短信验证码为: {}", mobile, type, smsCode);

        return ReplyHelper.success();
    }

    /**
     * 验证短信验证码
     *
     * @param key     验证参数,MD5(type + mobile + code)
     * @param isCheck 是否检验模式:true.检验模式,验证后验证码不失效;false.验证模式,验证后验证码失效
     * @return Reply
     */
    @Override
    public Reply verifySmsCode(String key, Boolean isCheck) {
        String codeKey = "VerifyCode:" + key;
        String mobile = Redis.get(codeKey);
        if (mobile == null || mobile.isEmpty()) {
            return ReplyHelper.fail("验证码错误");
        }

        if (isCheck) {
            // 每验证一次有效时间减少15秒,以避免暴力破解
            Redis.changeExpire(codeKey, -15);
            return ReplyHelper.success();
        }

        // 清理已通过验证的验证码对应手机号的全部验证码
        String setKey = "VerifyCodeSet:" + mobile;
        List<String> keys = Redis.getMembers(setKey);
        for (String k : keys) {
            Redis.deleteKey("VerifyCode:" + k);
        }

        // 清理验证码对应手机号缓存
        Redis.deleteKey(setKey);

        return ReplyHelper.success(mobile);
    }

    /**
     * 发送送标准消息
     *
     * @param info 用户关键信息
     * @param dto  标准信息DTO
     * @return Reply
     */
    @Override
    public Reply sendNormalMessage(LoginInfo info, NormalMessage dto) {
        Long tenantId = info == null ? null : info.getTenantId();
        Long appId = info == null ? null : info.getAppId();
        TemplateDto template = mapper.getTemplate(tenantId, appId, dto.getSceneCode());
        if (template == null) {
            return ReplyHelper.fail("没有可用消息模板,请检查消息参数是否正确");
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
        InsightMessage message = new InsightMessage();
        message.setTag(template.getTag());
        message.setType(template.getType());
        message.setTitle(template.getTitle());

        String content = assemblyContent(template.getContent(), dto.getParams());
        message.setContent(content);
        message.setParams(params);

        String[] receivers = dto.getReceivers().split(",");
        message.setReceivers(new ArrayList<>(Arrays.asList(receivers)));

        Integer expire = template.getExpire();
        LocalDateTime now = LocalDateTime.now();
        if (expire != null && expire > 0) {
            message.setExpireDate(now.plusMinutes(expire).toLocalDate());
        }

        message.setBroadcast(dto.getBroadcast());
        sendMessage(info, message);

        return ReplyHelper.success();
    }

    /**
     * 发送自定义消息
     *
     * @param info 用户关键信息
     * @param dto  标准信息DTO
     * @return Reply
     */
    @Override
    public Reply sendCustomMessage(LoginInfo info, CustomMessage dto) {
        // 组装消息
        InsightMessage message = new InsightMessage();
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
     * 获取用户消息列表
     *
     * @param info   用户关键信息
     * @param search 查询实体类
     * @return Reply
     */
    @Override
    public Reply getUserMessages(LoginInfo info, Search search) {
        search.setOwnerId(info.getUserId());
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
    public Reply getUserMessage(Long messageId, Long userId) {
        UserMessageDto message = mapper.getMessage(messageId, userId);
        if (message == null) {
            return ReplyHelper.fail("ID不存在,未读取数据");
        }

        if (!message.getRead()) {
            dal.readMessage(message.getId(), userId, message.getBroadcast());
            message.setRead(true);
        }

        return ReplyHelper.success(message);
    }

    /**
     * 删除用户消息
     *
     * @param messageId 消息ID
     * @param userId    用户ID
     * @return Reply
     */
    @Override
    public Reply deleteUserMessage(Long messageId, Long userId) {
        UserMessageDto message = mapper.getMessage(messageId, userId);
        if (message == null) {
            return ReplyHelper.fail("ID不存在,未删除数据");
        }

        if (message.getBroadcast()) {
            if (!message.getRead()) {
                dal.readMessage(message.getId(), userId, true);
            }

            mapper.unsubscribeMessage(messageId, userId);
        } else {
            mapper.deleteUserMessage(messageId, userId);
        }

        return ReplyHelper.success();
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
            message.setCreator(info.getUserName());
            message.setCreatorId(info.getUserId());

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
