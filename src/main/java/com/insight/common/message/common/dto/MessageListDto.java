package com.insight.common.message.common.dto;

import com.insight.utils.pojo.BaseXo;

import java.time.LocalDateTime;

/**
 * @author 宣炳刚
 * @date 2019/9/20
 * @remark 消息DTO
 */
public class MessageListDto extends BaseXo {

    /**
     * UUID主键
     */
    private Long id;

    /**
     * 消息标签
     */
    private String tag;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 是否已读
     */
    private Boolean isRead;

    /**
     * 创建人
     */
    private String creator;

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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }
}
