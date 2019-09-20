package com.insight.base.message.common;

import com.insight.util.ReplyHelper;
import com.insight.util.pojo.CustomMessage;
import com.insight.util.pojo.NormalMessage;
import com.insight.util.pojo.Reply;
import org.springframework.stereotype.Component;

/**
 * @author 宣炳刚
 * @date 2019-08-31
 * @remark 核心类
 */
@Component
public class Core {

    /**
     * 发送消息
     *
     * @param message 通用消息DTO
     * @return Reply
     */
    public Reply sendMessage(NormalMessage message) {
        return ReplyHelper.success();
    }

    /**
     * 发送消息
     *
     * @param message 自定义消息DTO
     * @return Reply
     */
    public Reply sendMessage(CustomMessage message) {
        return ReplyHelper.success();
    }
}
