package com.insight.common.message.message;

import com.insight.common.message.common.dto.CustomMessage;
import com.insight.common.message.common.dto.NormalMessage;
import com.insight.utils.pojo.LoginInfo;
import com.insight.utils.pojo.Reply;
import com.insight.utils.pojo.SmsCode;

/**
 * @author 宣炳刚
 * @date 2019-08-28
 * @remark 短信服务接口
 */
public interface MessageService {

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
     * 发送送标准消息
     *
     * @param info 用户关键信息
     * @param dto  标准信息DTO
     * @return Reply
     */
    Reply sendNormalMessage(LoginInfo info, NormalMessage dto);

    /**
     * 发送自定义消息
     *
     * @param info 用户关键信息
     * @param dto  标准信息DTO
     * @return Reply
     */
    Reply sendCustomMessage(LoginInfo info, CustomMessage dto);

    /**
     * 获取用户消息列表
     *
     * @param info    用户关键信息
     * @param keyword 查询关键词
     * @param page    分页页码
     * @param size    每页记录数
     * @return Reply
     */
    Reply getUserMessages(LoginInfo info, String keyword, int page, int size);

    /**
     * 获取用户消息详情
     *
     * @param messageId 消息ID
     * @param userId    用户ID
     * @return Reply
     */
    Reply getUserMessage(String messageId, String userId);

    /**
     * 删除用户消息
     *
     * @param messageId 消息ID
     * @param userId    用户ID
     * @return Reply
     */
    Reply deleteUserMessage(String messageId, String userId);
}
