package com.insight.base.message.sms;

import com.insight.base.message.common.MessageCore;
import com.insight.util.Generator;
import com.insight.util.Redis;
import com.insight.util.ReplyHelper;
import com.insight.util.Util;
import com.insight.util.pojo.LoginInfo;
import com.insight.util.pojo.NormalMessage;
import com.insight.util.pojo.Reply;
import com.insight.util.pojo.SmsCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
public class SmsServiceImpl implements SmsService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final MessageCore core;

    /**
     * 构造方法
     *
     * @param core MessageCore
     */
    public SmsServiceImpl(MessageCore core) {
        this.core = core;
    }

    /**
     * 发送短信
     *
     * @param info 用户关键信息
     * @param dto  短信DTO
     * @return Reply
     */
    @Override
    public Reply sendMessage(LoginInfo info, NormalMessage dto) {
        return core.sendMessage(info, dto);
    }

    /**
     * 生成短信验证码
     *
     * @param dto  验证码DTO
     * @return Reply
     */
    @Override
    public Reply seedSmsCode(SmsCode dto) {
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

        NormalMessage message = new NormalMessage();
        message.setSceneCode(sceneCode);
        message.setAppId(dto.getAppId());
        message.setReceivers(mobile);
        message.setParams(map);
        message.setBroadcast(false);
        Reply reply = core.sendMessage(null, message);
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
        String mobile = Redis.get("VerifyCode:" + key);
        if (mobile == null || mobile.isEmpty()) {
            return ReplyHelper.fail("验证码错误");
        }

        if (isCheck) {
            return ReplyHelper.success();
        }

        // 清理已通过验证的验证码对应手机号的全部验证码
        String setKey = "VerifyCodeSet:" + mobile;
        List<String> keys = Redis.getMembers(setKey);
        for (String k : keys) {
            Redis.deleteKey("VerifyCode:" + k);
        }

        Redis.deleteKey(setKey);

        return ReplyHelper.success();
    }
}
