package com.insight.base.message.sms;


import com.insight.util.Redis;
import com.insight.util.ReplyHelper;
import com.insight.util.Util;
import com.insight.util.pojo.Reply;
import com.insight.util.pojo.Sms;
import com.insight.util.pojo.SmsCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author 宣炳刚
 * @date 2019-08-28
 * @remark 短信服务
 */
@Service
public class SmsServiceImpl implements SmsService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 发送短信
     *
     * @param sms 短信DTO
     * @return Reply
     */
    @Override
    public Reply sendMessage(Sms sms) {
        return ReplyHelper.success();
    }

    /**
     * 生成短信验证码
     *
     * @param code 验证码
     * @return Reply
     */
    @Override
    public Reply seedSmsCode(SmsCode code) {
        String key = "VerifyCode:" + Util.md5(code.getType() + code.getMobile() + code.getCode());
        Redis.set(key, "", code.getMinutes(), TimeUnit.MINUTES);

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
        key = "VerifyCode:" + key;
        if (!Redis.hasKey(key)) {
            return ReplyHelper.fail("验证码错误");
        }

        if (!isCheck) {
            Redis.deleteKey(key);
        }

        return ReplyHelper.success();
    }
}
