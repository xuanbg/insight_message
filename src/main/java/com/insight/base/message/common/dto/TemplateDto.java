package com.insight.base.message.common.dto;

import com.insight.util.Json;

import java.io.Serializable;

/**
 * @author 宣炳刚
 * @date 2019-08-28
 * @remark 消息模板
 */
public class TemplateDto implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 消息标签
     */
    private String tag;

    /**
     * 发送类型:0.未定义;1.仅消息(001);2.仅推送(010);3.推送+消息(011);4.仅短信(100)
     */
    private Integer type;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息有效时长(小时)
     */
    private Integer expire;

    /**
     * 短信签名
     */
    private String sign;

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

    public Integer getExpire() {
        return expire;
    }

    public void setExpire(Integer expire) {
        this.expire = expire;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return Json.toJson(this);
    }
}
