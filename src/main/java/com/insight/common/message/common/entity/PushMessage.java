package com.insight.common.message.common.entity;

import com.insight.utils.pojo.BaseXo;

import java.time.LocalDateTime;

/**
 * @author 宣炳刚
 * @date 2019/9/21
 * @remark 推送消息DTO
 */
public class PushMessage extends BaseXo {

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
    private String userId;

    /**
     * 是否已读
     */
    private Boolean isRead;

    /**
     * 阅读时间
     */
    private LocalDateTime readTime;

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
}
