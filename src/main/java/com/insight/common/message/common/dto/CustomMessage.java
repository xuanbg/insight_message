package com.insight.common.message.common.dto;

import com.insight.utils.Json;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Map;

/**
 * @author 宣炳刚
 * @date 2019/9/20
 * @remark 自定义消息DTO
 */
public class CustomMessage implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 消息标签
     */
    @NotEmpty(message = "消息标签不能为空")
    private String tag;

    /**
     * 发送类型:0.未定义;1.仅消息(0001);2.仅推送(0010);3.推送+消息(0011);4.仅短信(0100);8.仅邮件(1000)
     */
    @NotNull(message = "发送类型类型不能为空")
    private Integer type;

    /**
     * 接收人,多个接收人使用逗号分隔
     */
    @NotEmpty(message = "接收人不能为空")
    private String receivers;

    /**
     * 消息标题
     */
    @NotEmpty(message = "消息标题不能为空")
    private String title;

    /**
     * 消息内容
     */
    @NotEmpty(message = "消息内容不能为空")
    private String content;

    /**
     * 发送参数
     */
    private Map<String, Object> params;

    /**
     * 是否广播消息
     */
    @NotNull(message = "广播设置不能为空")
    private Boolean isBroadcast;

    /**
     * 消息失效日期
     */
    private LocalDate expireDate;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getReceivers() {
        return receivers;
    }

    public void setReceivers(String receivers) {
        this.receivers = receivers;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Boolean getBroadcast() {
        return isBroadcast;
    }

    public void setBroadcast(Boolean broadcast) {
        isBroadcast = broadcast;
    }

    public LocalDate getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
    }

    @Override
    public String toString() {
        return Json.toJson(this);
    }
}
