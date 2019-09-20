package com.insight.base.message.common.entity;

import com.insight.util.Json;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * @author 宣炳刚
 * @date 2019/9/20
 * @remark 消息DTO
 */
public class Message implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * UUID主键
     */
    private String id;

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 消息类型
     */
    private String type;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 自定义参数
     */
    private Map params;

    /**
     * 消息有效时长(小时)
     */
    private Integer expire;

    /**
     * 是否广播消息
     */
    private Boolean isBroadcast;

    /**
     * 是否失效
     */
    private Boolean isInvalid;

    /**
     * 创建人部门ID
     */
    private String deptId;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建人ID
     */
    private String creatorId;

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

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
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

    public Map getParams() {
        return params;
    }

    public void setParams(Map params) {
        this.params = params;
    }

    public Integer getExpire() {
        return expire;
    }

    public void setExpire(Integer expire) {
        this.expire = expire;
    }

    public Boolean getBroadcast() {
        return isBroadcast;
    }

    public void setBroadcast(Boolean broadcast) {
        isBroadcast = broadcast;
    }

    public Boolean getInvalid() {
        return isInvalid;
    }

    public void setInvalid(Boolean invalid) {
        isInvalid = invalid;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
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
