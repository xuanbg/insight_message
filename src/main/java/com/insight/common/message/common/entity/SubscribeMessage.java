package com.insight.common.message.common.entity;

import com.insight.utils.pojo.BaseXo;

import java.time.LocalDateTime;

/**
 * @author 宣炳刚
 * @date 2019/9/21
 * @remark 订阅消息DTO
 */
public class SubscribeMessage extends BaseXo {

    /**
     * UUID主键
     */
    private Long id;

    /**
     * 消息ID
     */
    private Long messageId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }
}
