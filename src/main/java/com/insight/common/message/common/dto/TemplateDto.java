package com.insight.common.message.common.dto;

import com.insight.utils.pojo.base.BaseXo;

/**
 * @author 宣炳刚
 * @date 2020/11/24
 * @remark
 */
public class TemplateDto extends BaseXo {

    /**
     * 发送类型:0.未定义;1.仅消息(0001);2.仅推送(0010);3.推送+消息(0011);4.仅短信(0100);8.仅邮件(1000)
     */
    private Integer type;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息标签
     */
    private String tag;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 签名
     */
    private String sign;

    /**
     * 消息有效时长(小时)
     */
    private Integer expire;

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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Integer getExpire() {
        return expire;
    }

    public void setExpire(Integer expire) {
        this.expire = expire;
    }
}
