package com.insight.base.message.sms;

import com.insight.util.pojo.LoginInfo;
import com.insight.util.pojo.NormalMessage;
import com.insight.util.pojo.Reply;
import com.insight.util.pojo.SmsCode;

/**
 * @author 宣炳刚
 * @date 2019-08-28
 * @remark 短信服务接口
 */
public interface SmsService {

    /**
     * 发送短信
     *
     * @param info 用户关键信息
     * @param dto  短信DTO
     * @return Reply
     */
    Reply sendMessage(LoginInfo info, NormalMessage dto);

    /**
     * 生成短信验证码
     *
     * @param dto  验证码
     * @return Reply
     */
    Reply seedSmsCode(SmsCode dto);

    /**
     * 验证短信验证码
     *
     * @param key     验证参数,MD5(type + mobile + code)
     * @param isCheck 是否检验模式:true.检验模式,验证后验证码不失效;false.验证模式,验证后验证码失效
     * @return Reply
     */
    Reply verifySmsCode(String key, Boolean isCheck);
}
