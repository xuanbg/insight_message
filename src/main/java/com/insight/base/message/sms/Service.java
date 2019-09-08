package com.insight.base.message.sms;

import com.insight.util.pojo.Reply;

import java.util.Map;

/**
 * @author 宣炳刚
 * @date 2019-08-28
 * @remark 短信服务接口
 */
public interface Service {

    /**
     * 发送短信
     *
     * @param mobiles 手机号,多个手机号逗号分隔
     * @param scene   场景编号
     * @param channel 渠道编号
     * @param param   短信参数
     * @return Reply
     */
    Reply sendMessage(String mobiles, String scene, String channel, Map param);

    /**
     * 生成短信验证码
     *
     * @param type    验证码类型:0.验证手机号;1.注册用户账号;2.重置密码;3.修改支付密码;4.修改手机号
     * @param mobile  手机号
     * @param code    验证码
     * @param minutes 验证码有效时长(分钟)
     * @return Reply
     */
    Reply seedSmsCode(int type, String mobile, String code, int minutes);

    /**
     * 验证短信验证码
     *
     * @param key     验证参数,MD5(type + mobile + code)
     * @param isCheck 是否检验模式:true.检验模式,验证后验证码不失效;false.验证模式,验证后验证码失效
     * @return Reply
     */
    Reply verifySmsCode(String key, Boolean isCheck);
}
