package com.insight.base.message.common.entity;

import com.insight.util.Json;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author 宣炳刚
 * @date 2019/9/21
 * @remark 推送消息DTO
 */
public class PushMessage implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * UUID主键
     */
    private String id;

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 是否已读
     */
    private Boolean isRead;

    /**
     * 阅读时间
     */
    private LocalDateTime readTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }

    public LocalDateTime getReadTime() {
        return readTime;
    }

    public void setReadTime(LocalDateTime readTime) {
        this.readTime = readTime;
    }

    @Override
    public String toString() {
        return Json.toJson(this);
    }
}
