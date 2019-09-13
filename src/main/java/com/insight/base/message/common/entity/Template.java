package com.insight.base.message.common.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * @author 宣炳刚
 * @date 2019-08-28
 * @remark 消息模板
 */
public class Template implements Serializable {
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
     * 消息内容
     */
    private String content;

    /**
     * 消息有效时长(小时)
     */
    private Integer expire;

    /**
     * 自定义参数
     */
    private Map params;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否失效:0.正常;1.失效
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

    public Map getParams() {
        return params;
    }

    public void setParams(Map params) {
        this.params = params;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
}
