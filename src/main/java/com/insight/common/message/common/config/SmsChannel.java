package com.insight.common.message.common.config;

import java.util.List;

/**
 * @author 宣炳刚
 * @date 2022/11/25
 * @remark 短信通道接口
 */
public interface SmsChannel {

    /**
     * 发送模版短信
     *
     * @param phone 手机号
     * @param code  模版代码
     * @param param 模版参数
     * @param sign  签名
     */
    void sendTemplateMessage(String phone, String code, Object param, String sign) throws Exception;

    /**
     * 发送自定义内容短信
     *
     * @param phone   手机号
     * @param content 内容
     * @param sign    签名
     */
    void sendFreeMessage(String phone, String content, String sign) throws Exception;

    /**
     * 群发自定义内容短信
     *
     * @param phones  手机号集合
     * @param content 内容
     * @param sign    签名
     */
    void sendMultiFreeMessage(List<String> phones, String content, String sign) throws Exception;
}
