package com.insight.common.message.message;

import com.insight.common.message.common.dto.CustomMessage;
import com.insight.common.message.common.dto.NormalMessage;
import com.insight.common.message.common.dto.UserMessageDto;
import com.insight.utils.pojo.auth.LoginInfo;
import com.insight.utils.pojo.base.Reply;
import com.insight.utils.pojo.base.Search;
import com.insight.utils.pojo.message.SmsCode;

/**
 * @author 宣炳刚
 * @date 2019-08-28
 * @remark 短信服务接口
 */
public interface MessageService {

    /**
     * 发送短信验证码
     *
     * @param info 用户关键信息
     * @param dto  短信DTO
     */
    void seedSmsCode(LoginInfo info, SmsCode dto);

    /**
     * 发送送标准消息
     *
     * @param info 用户关键信息
     * @param dto  标准信息DTO
     */
    void sendNormalMessage(LoginInfo info, NormalMessage dto);

    /**
     * 发送自定义消息
     *
     * @param info 用户关键信息
     * @param dto  标准信息DTO
     */
    void sendCustomMessage(LoginInfo info, CustomMessage dto);

    /**
     * 获取用户消息列表
     *
     * @param info   用户关键信息
     * @param search 查询实体类
     * @return Reply
     */
    Reply getUserMessages(LoginInfo info, Search search);

    /**
     * 获取用户消息详情
     *
     * @param messageId 消息ID
     * @param userId    用户ID
     * @return Reply
     */
    UserMessageDto getUserMessage(Long messageId, Long userId);

    /**
     * 删除用户消息
     *
     * @param messageId 消息ID
     * @param userId    用户ID
     */
    void deleteUserMessage(Long messageId, Long userId);
}
