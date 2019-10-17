package com.insight.base.message.message;

import com.insight.base.message.common.client.RabbitClient;
import com.insight.base.message.common.dto.TemplateDto;
import com.insight.base.message.common.entity.InsightMessage;
import com.insight.base.message.common.mapper.MessageMapper;
import com.insight.util.Generator;
import com.insight.util.Redis;
import com.insight.util.ReplyHelper;
import com.insight.util.Util;
import com.insight.util.pojo.*;
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
    private final MessageMapper mapper;

    /**
     * 构造方法
     *
     * @param mapper MessageMapper
     */
    public MessageServiceImpl(MessageMapper mapper) {
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
        String smsCode = Generator.randomInt(dto.getLength());
        Map<String, Object> map = new HashMap<>(4);
        map.put("code", smsCode);
        map.put("minutes", dto.getMinutes());

        String sceneCode;
        switch (type) {
            case 0:
                sceneCode = "0002";
                break;
            case 1:
                sceneCode = "0003";
                break;
            case 2:
                sceneCode = "0004";
                break;
            case 3:
                sceneCode = "0005";
                break;
            case 4:
                sceneCode = "0006";
                break;
            default:
                sceneCode = "0000";
        }

        // 组装标准消息
        NormalMessage message = new NormalMessage();
        message.setSceneCode(sceneCode);
        message.setAppId(dto.getAppId());
        message.setReceivers(mobile);
        message.setParams(map);
        message.setBroadcast(false);
        Reply reply = sendNormalMessage(info, message);
        if (!reply.getSuccess()) {
            return reply;
        }

        String key = Util.md5(type + mobile + smsCode);
        Redis.set("VerifyCode:" + key, mobile, dto.getMinutes(), TimeUnit.MINUTES);
        Redis.add("VerifyCodeSet:" + mobile, key);
        Redis.setExpire("VerifyCodeSet:" + mobile, 30, TimeUnit.MINUTES);
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
            long expire = Redis.getExpire(codeKey, TimeUnit.SECONDS);
            Redis.setExpire(codeKey, expire - 15, TimeUnit.SECONDS);

            return ReplyHelper.success();
        }

        // 清理验证码对应手机号缓存
        String setKey = "VerifyCodeSet:" + mobile;
        Redis.deleteKey(setKey);

        // 清理已通过验证的验证码对应手机号的全部验证码
        List<String> keys = Redis.getMembers(setKey);
        for (String k : keys) {
            Redis.deleteKey("VerifyCode:" + k);
        }

        return ReplyHelper.success();
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
        if (1 == (type & 1)) {
            message.setId(Generator.uuid());
            if (info == null) {
                message.setTenantId("2564cd559cd340f0b81409723fd8632a");
                message.setCreator("系统");
                message.setCreatorId("00000000000000000000000000000000");
            } else {
                message.setTenantId(info.getTenantId());
                message.setDeptId(info.getDeptId());
                message.setCreator(info.getUserName());
                message.setCreatorId(info.getUserId());
            }

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
