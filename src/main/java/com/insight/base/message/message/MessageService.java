package com.insight.base.message.message;

import com.insight.util.pojo.*;

/**
 * @author 宣炳刚
 * @date 2019-08-28
 * @remark 短信服务接口
 */
public interface MessageService {

    /**
     * 发送短信
     *
     * @param info 用户关键信息
     * @param dto  标准信息DTO
     * @return Reply
     */
    Reply sendMessage(LoginInfo info, NormalMessage dto);

    /**
     * 生成短信验证码
     *
     * @param info 用户关键信息
     * @param dto  验证码
     * @return Reply
     */
    Reply seedSmsCode(LoginInfo info, SmsCode dto);

    /**
     * 验证短信验证码
     *
     * @param key     验证参数,MD5(type + mobile + code)
     * @param isCheck 是否检验模式:true.检验模式,验证后验证码不失效;false.验证模式,验证后验证码失效
     * @return Reply
     */
    Reply verifySmsCode(String key, Boolean isCheck);

    /**
     * 推送标准通知信息
     *
     * @param info 用户关键信息
     * @param dto  标准信息DTO
     * @return Reply
     */
    Reply pushNotice(LoginInfo info, NormalMessage dto);

    /**
     * 推送自定义通知信息
     *
     * @param info 用户关键信息
     * @param dto  标准信息DTO
     * @return Reply
     */
    Reply pushCustomNotice(LoginInfo info, CustomMessage dto);
}
