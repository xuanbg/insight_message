package com.insight.base.message.common.dto;

import com.insight.util.Json;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 宣炳刚
 * @date 2019-08-28
 * @remark 消息模板
 */
public class TemplateListDto implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * UUID主键
     */
    private String id;

    /**
     * 发送类型:0.未定义;1.仅消息(001);2.仅推送(010);3.推送+消息(011);4.仅短信(100);5.消息+短信(101);6.推送+短信(110);7.消息+推送+短信(111)
     */
    private Integer type;

    /**
     * 模板编号
     */
    private String code;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息有效时长(小时)
     */
    private Integer expire;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    private Date createdTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getExpire() {
        return expire;
    }

    public void setExpire(Integer expire) {
        this.expire = expire;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String toString() {
        return Json.toJson(this);
    }
}
